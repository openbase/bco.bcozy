/**
 * ==================================================================
 * <p>
 * This file is part of org.openbase.bco.bcozy.
 * <p>
 * org.openbase.bco.bcozy is free software: you can redistribute it and modify
 * it under the terms of the GNU General Public License (Version 3)
 * as published by the Free Software Foundation.
 * <p>
 * org.openbase.bco.bcozy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with org.openbase.bco.bcozy. If not, see <http://www.gnu.org/licenses/>.
 * ==================================================================
 */
package org.openbase.bco.bcozy.view.pane.unit;

import com.google.protobuf.Message;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.application.Platform;
import org.openbase.bco.authentication.lib.AuthorizationHelper;
import org.openbase.bco.authentication.lib.SessionManager;
import org.openbase.bco.authentication.lib.jp.JPAuthentication;
import org.openbase.bco.bcozy.view.InfoPane;
import org.openbase.jul.exception.*;
import org.openbase.jul.extension.type.processing.TimestampJavaTimeTransform;
import org.openbase.jul.pattern.controller.ConfigurableRemote;
import org.openbase.jul.pattern.controller.Remote;
import org.openbase.jul.pattern.provider.DataProvider;
import org.openbase.jul.processing.StringProcessor;
import org.openbase.jul.visual.javafx.JFXConstants;
import org.openbase.jul.visual.javafx.geometry.svg.SVGGlyphIcon;
import org.openbase.bco.bcozy.view.generic.ExpandableWidgedPane;
import org.openbase.bco.dal.lib.layer.unit.UnitRemote;
import org.openbase.bco.dal.remote.layer.unit.Units;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jps.core.JPService;
import org.openbase.jps.exception.JPNotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.extension.protobuf.IdentifiableMessage;
import org.openbase.jul.iface.Initializable;
import org.openbase.jul.iface.Shutdownable;
import org.openbase.jul.pattern.Observer;
import org.openbase.type.domotic.authentication.UserClientPairType.UserClientPair;
import org.openbase.type.domotic.state.ConnectionStateType.ConnectionState;
import org.openbase.type.domotic.unit.UnitConfigType.UnitConfig;

import java.util.Date;
import java.util.Map;

/**
 * Created by divine on 25.04.17
 *
 * @param <UR> UnitRemote
 * @param <D>  Unit Data
 */
public abstract class AbstractUnitPane<UR extends UnitRemote<D>, D extends Message> extends ExpandableWidgedPane implements Initializable<UR>, Shutdownable {

    private final Class<UR> unitRemoteClass;
    private UR unitRemote;

    private final Observer<ConfigurableRemote<String, D, UnitConfig>, UnitConfig> unitConfigObserver;
    private final Observer<DataProvider<D>,D> unitDataObserver;
    private final Observer<Remote, ConnectionState.State> unitConnectionObserver;
    private final Observer<SessionManager, UserClientPair> loginObserver;

    /**
     * Constructor for the UnitPane.
     *
     * @param unitRemoteClass
     * @param activateable
     */
    public AbstractUnitPane(final Class<UR> unitRemoteClass, final boolean activateable) {
        super(false, activateable);
        this.unitRemoteClass = unitRemoteClass;
        //TODO: Set css styling for unitlabel
        this.unitConfigObserver = new Observer<ConfigurableRemote<String, D, UnitConfig>, UnitConfig>() {
            @Override
            public void update(ConfigurableRemote<String, D, UnitConfig> source, UnitConfig config) {
                Platform.runLater(() -> {
                    try {
                        applyConfigUpdate(config);
                    } catch (CouldNotPerformException ex) {
                        ExceptionPrinter.printHistory("Could not apply config update on " + this, ex, LOGGER);
                    }
                });
            }
        };
        this.unitDataObserver = (source, data) -> applyDataUpdate(data);
        this.unitConnectionObserver = new Observer<>() {
            @Override
            public void update(Remote source, ConnectionState.State connectionState) {
                Platform.runLater(() -> {
                    try {
                        applyConnectionStateUpdate(connectionState);
                    } catch (CouldNotPerformException ex) {
                        if (ExceptionProcessor.getInitialCause(ex) instanceof ShutdownInProgressException) {
                            // update canceled because of an application shutdown.
                            return;
                        }
                        ExceptionPrinter.printHistory("Could not apply data update on " + this, ex, LOGGER);
                    }
                });
            }
        };
        this.loginObserver = new Observer<>() {
            @Override
            public void update(SessionManager source, UserClientPair authority) throws Exception {
                Platform.runLater(() -> {
                    try {
                        applyLoginUpdate();
                    } catch (CouldNotPerformException ex) {
                        if (ExceptionProcessor.getInitialCause(ex) instanceof ShutdownInProgressException) {
                            // update canceled because of an application shutdown.
                            return;
                        }
                        ExceptionPrinter.printHistory("Could not apply data update on " + this, ex, LOGGER);
                    }
                });
            }
        };
    }

    /**
     * Method initializes this pane with a unit referred by the given id.
     *
     * @param unitId a unit id to init the pane.
     *
     * @throws InterruptedException    is thrown if the current thread was externally interrupted.
     * @throws InitializationException is thrown if the initialization has been failed.
     */
    public void init(final String unitId) throws InterruptedException, InitializationException {
        try {
            init(Units.getUnit(unitId, false, unitRemoteClass));
        } catch (CouldNotPerformException ex) {
            throw new InitializationException(this, ex);
        }
    }

    /**
     * Method initializes this pane with a unit referred by the given id.
     *
     * @param unitConfig a unit config to unit the pane.
     *
     * @throws InterruptedException    is thrown if the current thread was externally interrupted.
     * @throws InitializationException is thrown if the initialization has been failed.
     */
    public void init(final UnitConfig unitConfig) throws InterruptedException, InitializationException {
        try {
            init(Units.getUnit(unitConfig, false, unitRemoteClass));
        } catch (CouldNotPerformException ex) {
            throw new InitializationException(this, ex);
        }
    }

    /**
     * Method initializes this pane with the given unit remote.
     *
     * @param unitRemote the unit remote.
     *
     * @throws InterruptedException    is thrown if the current thread was externally interrupted.
     * @throws InitializationException is thrown if the initialization has been failed.
     */
    @Override
    public void init(final UR unitRemote) throws InterruptedException, InitializationException {
        init();
        clearRemoteObservers();

        this.unitRemote = unitRemote;

        unitRemote.addConfigObserver(unitConfigObserver);
        unitRemote.addDataObserver(unitDataObserver);
        unitRemote.addConnectionStateObserver(unitConnectionObserver);
        SessionManager.getInstance().addLoginObserver(loginObserver);

        if (!unitRemote.isConnected()) {
            setDisable(false);
        }

        try {
            applyConnectionStateUpdate(unitRemote.getConnectionState());
        } catch (CouldNotPerformException ex) {
            // skip update, config observer will handle the update later on. 
        }

        try {
            applyConfigUpdate(unitRemote.getConfig());
        } catch (CouldNotPerformException ex) {
            // skip update, config observer will handle the update later on. 
        }

        try {
            applyDataUpdate(unitRemote.getData());
        } catch (CouldNotPerformException ex) {
            // skip update, config observer will handle the update later on.
        }

        try {
            if (JPService.getProperty(JPAuthentication.class).getValue()) {
                try {
                    applyLoginUpdate();
                } catch (CouldNotPerformException ex) {
                    // skip update, config observer will handle the update later on.
                }
            }
        } catch (JPNotAvailableException ex) {
            ExceptionPrinter.printHistory("Could not access JPAuthentication property!", ex, LOGGER);
        }
    }

    @Override
    public void initContent() {
        super.initContent();

        hoverProperty().addListener((observable, oldValue, newValue) -> {
            try {
                InfoPane.info(getUnitStateDescription());
            } catch (NotAvailableException ex) {
                ExceptionPrinter.printHistory("Could not print unit state description!", ex, LOGGER);
            }
        });
    }

    private void clearRemoteObservers() {
        if (this.unitRemote != null) {
            this.unitRemote.removeConfigObserver(unitConfigObserver);
            this.unitRemote.removeDataObserver(unitDataObserver);
            this.unitRemote.removeConnectionStateObserver(unitConnectionObserver);
        }
    }

    /**
     * Returns the UnitRemote.
     *
     * @return UnitRemote which is connected to these unit pane.
     *
     * @throws NotAvailableException if the pane is not connected to any unit remote.
     */
    public UR getUnitRemote() throws NotAvailableException {
        if (unitRemote == null) {
            throw new NotAvailableException("UnitRemote");
        }
        return unitRemote;
    }

    /**
     * Method returns the data object of the linked unit.
     *
     * @return the data object.
     *
     * @throws NotAvailableException is thrown if the data is currently not available.
     */
    public D getData() throws NotAvailableException {
        return getUnitRemote().getData();
    }

    public String getUnitStateDescription() throws NotAvailableException {
        return getUnitRemote().getLabel();
    }

    /**
     * Shutdown resets the remote observation.
     */
    @Override
    public void shutdown() {
        clearRemoteObservers();
    }

    /**
     * Notifies about unit data changes.
     *
     * @param config
     *
     * @throws org.openbase.jul.exception.CouldNotPerformException
     */
    protected void applyConfigUpdate(final UnitConfig config) throws CouldNotPerformException {
        setLabel(config.getLabel());
    }

    /**
     * Notifies about unit data changes.
     *
     * @param data
     *
     */
    protected void applyDataUpdate(final D data) {
        update();
    }

    /**
     * Notifies about unit data changes.
     *
     * @param connectionState
     *
     * @throws CouldNotPerformException
     */
    protected void applyConnectionStateUpdate(final ConnectionState.State connectionState) throws CouldNotPerformException {
        switch (connectionState) {
            case CONNECTED:
                setDisable(false);
                break;
            case CONNECTING:
            case DISCONNECTED:
            case UNKNOWN:
            default:
                setDisable(true);
                break;
        }
    }

    /**
     * Checks the permissions for the unit when the login state changes.
     * Sets the disableProperty accordingly to the user's/client's write permissions.
     *
     * @throws CouldNotPerformException
     */
    protected void applyLoginUpdate() throws CouldNotPerformException {
        UserClientPair userClientPair = UserClientPair.getDefaultInstance();
        Map<String, IdentifiableMessage<String, UnitConfig, UnitConfig.Builder>> groups = null;
        Map<String, IdentifiableMessage<String, UnitConfig, UnitConfig.Builder>> locations = Registries.getUnitRegistry().getLocationUnitConfigRemoteRegistry().getEntryMap();

        if (SessionManager.getInstance().isLoggedIn()) {
            userClientPair = SessionManager.getInstance().getUserClientPair();
            groups = Registries.getUnitRegistry().getAuthorizationGroupUnitConfigRemoteRegistry().getEntryMap();
        }

        disableProperty().set(!AuthorizationHelper.canAccess(AbstractUnitPane.this.unitRemote.getConfig(), userClientPair, groups, locations));
    }

    /**
     * Method returns a unit pane description.
     *
     * @return a description as string.
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + (unitRemote != null ? unitRemote : "?") + "]";
    }

    /**
     * Returns a new Icon object according to the type of icon used in this class.
     *
     * @return
     */
    public SVGGlyphIcon getIconSymbol() {
        return new SVGGlyphIcon(MaterialDesignIcon.VECTOR_CIRCLE, JFXConstants.ICON_SIZE_SMALL, false);
    }
}
