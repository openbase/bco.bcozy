/**
 * ==================================================================
 *
 * This file is part of org.openbase.bco.bcozy.
 *
 * org.openbase.bco.bcozy is free software: you can redistribute it and modify
 * it under the terms of the GNU General Public License (Version 3)
 * as published by the Free Software Foundation.
 *
 * org.openbase.bco.bcozy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with org.openbase.bco.bcozy. If not, see <http://www.gnu.org/licenses/>.
 * ==================================================================
 */
package org.openbase.bco.bcozy.view.pane.unit;

import com.google.protobuf.GeneratedMessage;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.application.Platform;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.InfoPane;
import org.openbase.bco.bcozy.view.SVGIcon;
import org.openbase.bco.bcozy.view.generic.ExpandableWidgedPane;
import org.openbase.bco.dal.lib.layer.unit.UnitRemote;
import org.openbase.bco.dal.remote.unit.Units;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.iface.Initializable;
import org.openbase.jul.iface.Shutdownable;
import org.openbase.jul.pattern.Observable;
import org.openbase.jul.pattern.Observer;
import org.openbase.jul.pattern.Remote.ConnectionState;
import rst.domotic.unit.UnitConfigType.UnitConfig;

/**
 * Created by divine on 25.04.17
 *
 * @param <UR> UnitRemote
 * @param <D> Unit Data
 */
public abstract class AbstractUnitPane<UR extends UnitRemote<D>, D extends GeneratedMessage> extends ExpandableWidgedPane implements Initializable<UR>, Shutdownable {

    private final Class<UR> unitRemoteClass;
    private UR unitRemote;

    private final Observer<UnitConfig> unitConfigObserver;
    private final Observer<D> unitDataObserver;
    private final Observer<ConnectionState> unitConnectionObserver;

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
        this.unitConfigObserver = new Observer<UnitConfig>() {
            @Override
            public void update(Observable<UnitConfig> source, UnitConfig config) throws Exception {
                Platform.runLater(() -> {
                    try {
                        applyConfigUpdate(config);
                    } catch (CouldNotPerformException ex) {
                        ExceptionPrinter.printHistory("Could not apply config update on " + this, ex, LOGGER);
                    }
                });
            }
        };
        this.unitDataObserver = new Observer<D>() {
            @Override
            public void update(Observable<D> source, D data) throws Exception {
                Platform.runLater(() -> {
                    try {
                        applyDataUpdate(data);
                    } catch (CouldNotPerformException ex) {
                        ExceptionPrinter.printHistory("Could not apply data update on " + this, ex, LOGGER);
                    }
                });
            }
        };
        this.unitConnectionObserver = new Observer<ConnectionState>() {
            @Override
            public void update(Observable<ConnectionState> source, ConnectionState connectionState) throws Exception {
                Platform.runLater(() -> {
                    try {
                        applyConnectionStateUpdate(connectionState);
                    } catch (CouldNotPerformException ex) {
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
     * @throws InterruptedException is thrown if the current thread was externally interrupted.
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
     * @throws InterruptedException is thrown if the current thread was externally interrupted.
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
     * @throws InterruptedException is thrown if the current thread was externally interrupted.
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
    }

    @Override
    public void initContent() {
        super.initContent();

        hoverProperty().addListener((observable, oldValue, newValue) -> {
            try {
                InfoPane.info(getUnitRemote().getLabel());
            } catch (NotAvailableException ex) {
                // do nothing if not possible

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
     * @throws NotAvailableException is thrown if the data is currently not available.
     */
    public D getData() throws NotAvailableException {
        return getUnitRemote().getData();
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
     * @throws org.openbase.jul.exception.CouldNotPerformException
     */
    protected void applyConfigUpdate(final UnitConfig config) throws CouldNotPerformException {
        setLabel(config.getLabel());
    }

    /**
     * Notifies about unit data changes.
     *
     * @param data
     * @throws org.openbase.jul.exception.CouldNotPerformException
     */
    protected void applyDataUpdate(final D data) throws CouldNotPerformException {
        updateDynamicContent();
    }

    /**
     * Notifies about unit data changes.
     *
     * @param connectionState
     * @throws CouldNotPerformException
     */
    protected void applyConnectionStateUpdate(final ConnectionState connectionState) throws CouldNotPerformException {
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
    public SVGIcon getIconSymbol() {
        return new SVGIcon(MaterialDesignIcon.VECTOR_CIRCLE, Constants.SMALL_ICON, false);
    }
}
