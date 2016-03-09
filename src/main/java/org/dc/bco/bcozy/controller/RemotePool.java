/**
 * ==================================================================
 *
 * This file is part of org.dc.bco.bcozy.
 *
 * org.dc.bco.bcozy is free software: you can redistribute it and modify
 * it under the terms of the GNU General Public License (Version 3)
 * as published by the Free Software Foundation.
 *
 * org.dc.bco.bcozy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with org.dc.bco.bcozy. If not, see <http://www.gnu.org/licenses/>.
 * ==================================================================
 */
package org.dc.bco.bcozy.controller; //NOPMD

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ProgressIndicator;
import org.dc.bco.bcozy.view.ForegroundPane;
import org.dc.jul.extension.rsb.com.AbstractIdentifiableRemote;
import org.dc.bco.dal.remote.unit.UnitRemoteFactoryImpl;
import org.dc.bco.dal.remote.unit.UnitRemoteFactory;
import org.dc.bco.manager.user.remote.UserRemote;
import org.dc.bco.registry.device.remote.DeviceRegistryRemote;
import org.dc.bco.registry.location.remote.LocationRegistryRemote;
import org.dc.bco.registry.user.remote.UserRegistryRemote;
import org.dc.jul.exception.CouldNotPerformException;
import org.dc.jul.exception.printer.ExceptionPrinter;
import org.dc.jul.exception.printer.LogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rct.TransformReceiver;
import rct.TransformerFactory;
import rst.authorization.UserConfigType;
import rst.homeautomation.unit.UnitConfigType.UnitConfig;
import rst.homeautomation.unit.UnitTemplateType.UnitTemplate.UnitType;
import rst.spatial.LocationConfigType.LocationConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;


/**
 * Created by tmichalski on 25.11.15.
 */
public class RemotePool {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemotePool.class);

    private final Map<String, AbstractIdentifiableRemote> deviceMap;
    private final Map<String, Map<String, AbstractIdentifiableRemote>> locationMap;
    private final Map<String, UserRemote> userMap;
    private LocationRegistryRemote locationRegistryRemote;
    private DeviceRegistryRemote deviceRegistryRemote;
    private UserRegistryRemote userRegistryRemote;
    private TransformReceiver transformReceiver;

    private boolean init;
    private boolean mapsFilled;

    /**
     * Constructor for the Remotecontroller.
     *
     * @param foregroundPane ForegroundPane
     */
    public RemotePool(final ForegroundPane foregroundPane) {
        foregroundPane.getMainMenu().addInitRemoteButtonEventHandler(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent event) {
                final Task task = new Task() {
                    private final ProgressIndicator progressIndicator = new ProgressIndicator(-1);
                    @Override
                    protected Object call() throws java.lang.Exception {
                        Platform.runLater(() -> {
                            foregroundPane.getContextMenu().getTitledPaneContainer().clearTitledPane();
                            foregroundPane.getContextMenu().getChildren().add(progressIndicator);
                        });
                        try {
                            initRegistryRemotes();
                        }  catch (InterruptedException | CouldNotPerformException
                                | TransformerFactory.TransformerFactoryException e) {
                            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                        }
                        return null;
                    }
                    @Override
                    protected void succeeded() {
                        super.succeeded();
                        Platform.runLater(() ->
                                foregroundPane.getContextMenu().getChildren().remove(progressIndicator));
                    }
                };
                new Thread(task).start();
            }
        });

        foregroundPane.getMainMenu().addFillHashesButtonEventHandler(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent event) {
                final Task task = new Task() {
                    private final ProgressIndicator progressIndicator = new ProgressIndicator(-1);
                    @Override
                    protected Object call() throws java.lang.Exception {
                        Platform.runLater(() -> {
                            foregroundPane.getContextMenu().getTitledPaneContainer().clearTitledPane();
                            foregroundPane.getContextMenu().getChildren().add(progressIndicator);
                        });
                        try {
                            fillDeviceAndLocationMap();
                        } catch (CouldNotPerformException e) {
                            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                            shutdownDALRemotesAndClearMaps();
                        }
                        return null;
                    }
                    @Override
                    protected void succeeded() {
                        super.succeeded();
                        Platform.runLater(() ->
                                foregroundPane.getContextMenu().getChildren().remove(progressIndicator));
                    }
                };
                new Thread(task).start();
            }
        });

        deviceMap = new HashMap<>();
        locationMap = new HashMap<>();
        userMap = new HashMap<>();

        init = false;
        mapsFilled = false;
    }

    /**
     * Initiate RegistryRemotes.
     * @throws CouldNotPerformException CouldNotPerformException
     * @throws InterruptedException InterruptedException
     * @throws TransformerFactory.TransformerFactoryException TransformerFactoryException
     */
    public void initRegistryRemotes() throws CouldNotPerformException, InterruptedException,
            TransformerFactory.TransformerFactoryException {
        if (init) {
            LOGGER.info("INFO: RegistryRemotes were already initialized.");
            return;
        }

        locationRegistryRemote = new LocationRegistryRemote();
        locationRegistryRemote.init();
        locationRegistryRemote.activate();

        try {
            deviceRegistryRemote = new DeviceRegistryRemote();
            deviceRegistryRemote.init();
            deviceRegistryRemote.activate();
        } catch (CouldNotPerformException | InterruptedException e) {
            locationRegistryRemote.shutdown();
            throw e;
        }

        try {
            userRegistryRemote = new UserRegistryRemote();
            userRegistryRemote.init();
            userRegistryRemote.activate();
        } catch (CouldNotPerformException | InterruptedException e) {
            locationRegistryRemote.shutdown();
            deviceRegistryRemote.shutdown();
            throw e;
        }

        try {
            this.transformReceiver = TransformerFactory.getInstance().createTransformReceiver();
        } catch (TransformerFactory.TransformerFactoryException e) {
            locationRegistryRemote.shutdown();
            deviceRegistryRemote.shutdown();
            userRegistryRemote.shutdown();
            throw e;
        }

        init = true;
        LOGGER.info("INFO: RegistryRemotes are initialized.");
    }

    private void checkInit() throws CouldNotPerformException {
        if (!init) {
            throw new CouldNotPerformException("RegistryRemotes are not initialized.");
        }
    }

    /**
     * Fills the device and the location hashmap with all remotes. All remotes will be initialized and activated.
     * @throws CouldNotPerformException CouldNotPerformException
     */
    public void fillDeviceAndLocationMap() throws CouldNotPerformException {
        checkInit();
        if (mapsFilled) {
            shutdownDALRemotesAndClearMaps();
        }
        fillDeviceMap();

        for (final LocationConfig currentLocationConfig : locationRegistryRemote.getLocationConfigs()) {
            LOGGER.info("INFO: Room: " + currentLocationConfig.getId());

            for (final String unitId : currentLocationConfig.getUnitIdList()) {
                LOGGER.info("INFO: Unit: " + unitId);

                if (deviceMap.containsKey(unitId)) {
                    final AbstractIdentifiableRemote currentDalRemoteService = deviceMap.get(unitId);

                    if (!locationMap.containsKey(currentLocationConfig.getId())) {
                        locationMap.put(currentLocationConfig.getId(), new TreeMap<>());
                    }

                    locationMap.get(currentLocationConfig.getId()).put(unitId, currentDalRemoteService);
                }
            }
        }
        mapsFilled = true;
    }

    private void fillDeviceMap() throws CouldNotPerformException {
        final UnitRemoteFactory unitRemoteFactoryInterface = UnitRemoteFactoryImpl.getInstance();

        for (final UnitConfig currentUnitConfig : deviceRegistryRemote.getUnitConfigs()) {
            AbstractIdentifiableRemote currentDalRemoteService;

            try {
                currentDalRemoteService = unitRemoteFactoryInterface.createAndInitUnitRemote(currentUnitConfig);
            } catch (CouldNotPerformException e) {
                ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                continue;
            }

            try {
                currentDalRemoteService.activate();
            } catch (InterruptedException | CouldNotPerformException e) {
                ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                continue;
            }

            deviceMap.put(currentUnitConfig.getId(), currentDalRemoteService);
        }
    }

    /**
     * Fills the user map with all remotes. All remotes will be initialized and activated.
     * @throws CouldNotPerformException CouldNotPerformException
     */
    public void fillUserMap() throws CouldNotPerformException {
        for (final UserConfigType.UserConfig currentUserConfig : userRegistryRemote.getUserConfigs()) {
            final UserRemote currentUserRemote = new UserRemote();
            try {
                currentUserRemote.init(currentUserConfig);
                currentUserRemote.activate();
            } catch (InterruptedException e) {
                ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                continue;
            }
        }
    }

    /**
     * Returns the AbstractIdentifiableRemote to the given unitId and class.
     * @param unitId the unit ID
     * @param <Remote> the corresponding class of the remote
     * @return the AbstractIdentifiableRemote casted to the given remote class
     * @throws CouldNotPerformException CouldNotPerformException
     */
    @SuppressWarnings("unchecked")
    public <Remote extends AbstractIdentifiableRemote> Remote getUnitRemoteById(
            final String unitId) throws CouldNotPerformException {
        checkInit();

        return (Remote) deviceMap.get(unitId);
    }

    /**
     * Returns the AbstractIdentifiableRemote to the given unitId and locationId.
     * @param unitId the unit ID
     * @param locationId the location ID
     * @param <Remote> the corresponding class of the remote
     * @return the AbstractIdentifiableRemote
     * @throws CouldNotPerformException CouldNotPerformException
     */
    @SuppressWarnings("unchecked")
    public <Remote extends  AbstractIdentifiableRemote> Remote getUnitRemoteByIdAndLocation(
            final String unitId, final String locationId) throws CouldNotPerformException {
        checkInit();

        return (Remote) locationMap.get(locationId).get(unitId);
    }

    /**
     * Returns a List with all Remotes of the given remote class.
     * @param remoteClass the remote class
     * @param <Remote> the corresponding class of the remote
     * @return the List of DALRemoteServices
     * @throws CouldNotPerformException CouldNotPerformException
     */
    @SuppressWarnings("unchecked")
    public <Remote extends AbstractIdentifiableRemote> List<Remote> getUnitRemoteListOfClass(
            final Class<? extends Remote> remoteClass) throws CouldNotPerformException {
        checkInit();

        final List<Remote> unitRemoteList = new ArrayList<>();

        for (final Map.Entry<String, AbstractIdentifiableRemote> stringDALRemoteServiceEntry : deviceMap.entrySet()) {
            final AbstractIdentifiableRemote currentDalRemoteService = stringDALRemoteServiceEntry.getValue();
            if (currentDalRemoteService.getClass().equals(remoteClass)) {
                unitRemoteList.add((Remote) currentDalRemoteService);
            }
        }

        return unitRemoteList;
    }

    /**
     * Returns a List of all DALRemoteServices to the given locationId.
     * @param locationId the location ID
     * @return the List of DALRemoteServices
     * @throws CouldNotPerformException CouldNotPerformException
     */
    public List<AbstractIdentifiableRemote> getUnitRemoteListOfLocation(
            final String locationId) throws CouldNotPerformException {
        checkInit();

        final List<AbstractIdentifiableRemote> unitRemoteList = new ArrayList<>();

        if (locationMap.containsKey(locationId)) {
            final Map<String, AbstractIdentifiableRemote> unitRemoteHashOfLocation = locationMap.get(locationId);

            for (final Map.Entry<String,
                    AbstractIdentifiableRemote> currentEntry : unitRemoteHashOfLocation.entrySet()) {
                unitRemoteList.add(currentEntry.getValue());
            }
        }

        return unitRemoteList;
    }

    /**
     * Returns a Map of all DALRemoteServices of the given Location sorted by their UnitType.
     * @param locationId locationId
     * @return the Map of DALRemoteServices
     */
    public Map<UnitType, List<AbstractIdentifiableRemote>> getUnitRemoteMapOfLocation(final String locationId) {
        final Map<UnitType, List<AbstractIdentifiableRemote>> unitRemoteMap = new TreeMap<>();

        final UnitType[] unitTypes = UnitType.values();

        for (final UnitType type : unitTypes) {
            try {
                final Class<? extends AbstractIdentifiableRemote> remote
                        = UnitRemoteFactoryImpl.loadUnitRemoteClass(type);
                final List<AbstractIdentifiableRemote> unitRemoteList =
                        this.getUnitRemoteListOfLocationAndClass(locationId, remote);
                if (!unitRemoteList.isEmpty()) {
                    unitRemoteMap.put(type, unitRemoteList);
                }
            } catch (CouldNotPerformException e) {
                ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
            }
        }

        return unitRemoteMap;
    }

    /**
     * Returns a List of all DALRemoteServices to a given locationId and inherited Class of AbstractIdentifiableRemote.
     * @param locationId the location ID
     * @param remoteClass the inherited Class of AbstractIdentifiableRemote
     * @param <Remote> the corresponding class of the remote
     * @return the List of DALRemoteServices
     * @throws CouldNotPerformException CouldNotPerformException
     */
    @SuppressWarnings("unchecked")
    public <Remote extends AbstractIdentifiableRemote> List<Remote> getUnitRemoteListOfLocationAndClass(
            final String locationId, final Class<? extends Remote> remoteClass) throws CouldNotPerformException {
        checkInit();

        final List<Remote> unitRemoteList = new ArrayList<>();
        if (locationMap.containsKey(locationId)) {
            final Map<String, AbstractIdentifiableRemote> unitRemoteHashOfLocation = locationMap.get(locationId);

            for (final Map.Entry<String,
                    AbstractIdentifiableRemote> currentEntry : unitRemoteHashOfLocation.entrySet()) {
                if (currentEntry.getValue().getClass() == remoteClass) {
                    unitRemoteList.add((Remote) currentEntry.getValue());
                }
            }
        }
        return unitRemoteList;
    }

    /**
     * Returns a List of all UserRemotes.
     * @return the List of UserRemotes
     * @throws CouldNotPerformException CouldNotPerformException
     */
    public List<UserRemote> getUserRemoteList() throws CouldNotPerformException {
        checkInit();

        final List<UserRemote> userRemoteList
                = userMap.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());

        return userRemoteList;
    }

    /**
     * Shut down all DALRemotes and clear the deviceMap and the locationMap.
     */
    public void shutdownDALRemotesAndClearMaps() {
        shutdownDALRemotes();
        this.deviceMap.clear();
        this.locationMap.clear();

        mapsFilled = false;
    }

    /**
     * Shut down all DALRemotes.
     */
    public void shutdownDALRemotes() {
        for (final Map.Entry<String, AbstractIdentifiableRemote> stringDALRemoteServiceEntry : deviceMap.entrySet()) {
            final AbstractIdentifiableRemote remote = stringDALRemoteServiceEntry.getValue();
            remote.shutdown();
        }
    }

    /**
     * Shut down all DALRemotes and the RegistryRemotes.
     */
    public void shutdownAllRemotes() {
        //TODO: somehow not shutting down properly?!

        for (final Map.Entry<String, AbstractIdentifiableRemote> stringDALRemoteServiceEntry : deviceMap.entrySet()) {
            final AbstractIdentifiableRemote remote = stringDALRemoteServiceEntry.getValue();
            remote.shutdown();
        }

        if (locationRegistryRemote != null) {
            LOGGER.info("Shutting down locationRegistryRemote...");
            locationRegistryRemote.shutdown();
        }

        if (deviceRegistryRemote != null) {
            LOGGER.info("Shutting down deviceRegistryRemote...");
            deviceRegistryRemote.shutdown();
        }

        TransformerFactory.killInstance(); //TODO mpohling: how to shutdown transformer factory?
        init = false;
    }

    /**
     * Returns the DeviceRegistryRemote.
     * @return DeviceRegistryRemote
     * @throws CouldNotPerformException CouldNotPerformException
     */
    public DeviceRegistryRemote getDeviceRegistryRemote() throws CouldNotPerformException {
        checkInit();

        return deviceRegistryRemote;
    }

    /**
     * Returns the LocationRegistryRemote.
     * @return LocationRegistryRemote
     * @throws CouldNotPerformException CouldNotPerformException
     */
    public LocationRegistryRemote getLocationRegistryRemote() throws CouldNotPerformException {
        checkInit();

        return locationRegistryRemote;
    }

    /**
     * Returns the TransformReceiver.
     * @return TransformReceiver
     * @throws CouldNotPerformException CouldNotPerformException
     */
    public TransformReceiver getTransformReceiver() throws CouldNotPerformException {
        checkInit();

        return transformReceiver;
    }

    /**
     * Returns the information whether the remotepool is initialized or not.
     * @return init
     */
    public boolean isInit() {
        return init;
    }

    /**
     * Returns the information whether the maps are filled or not.
     * @return mapsFilled
     */
    public boolean isMapsFilled() {
        return mapsFilled;
    }
}
