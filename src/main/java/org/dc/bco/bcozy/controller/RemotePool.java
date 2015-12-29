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
package org.dc.bco.bcozy.controller;

import de.citec.dal.remote.unit.DALRemoteService;
import de.citec.dal.remote.unit.UnitRemoteFactory;
import de.citec.dal.remote.unit.UnitRemoteFactoryInterface;
import org.dc.bco.registry.device.remote.DeviceRegistryRemote;
import org.dc.jul.exception.CouldNotPerformException;
import org.dc.jul.exception.printer.ExceptionPrinter;
import org.dc.jul.exception.printer.LogLevel;
import org.dc.bco.registry.location.remote.LocationRegistryRemote;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import org.dc.bco.bcozy.view.ForegroundPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rct.TransformReceiver;
import rct.TransformerFactory;
import rst.homeautomation.unit.UnitConfigType;
import rst.spatial.LocationConfigType;
import rst.homeautomation.unit.UnitTemplateType.UnitTemplate.UnitType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;



/**
 * Created by tmichalski on 25.11.15.
 */
public class RemotePool {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemotePool.class);

    private final ForegroundPane foregroundPane;

    private final Map<String, DALRemoteService> deviceMap;
    private final Map<String, Map<String, DALRemoteService>> locationMap;
    private LocationRegistryRemote locationRegistryRemote = null;
    private DeviceRegistryRemote deviceRegistryRemote = null;
    private TransformReceiver transformReceiver;

    private boolean init;

    /**
     * Constructor for the Remotecontroller.
     *
     * @param foregroundPane ForegroundPane
     */
    public RemotePool(final ForegroundPane foregroundPane) {
        this.foregroundPane = foregroundPane;

        this.foregroundPane.getMainMenu().addInitRemoteButtonEventHandler(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent event) {
                try {
                    initRegistryRemotes();
                    //fillHashes();
                } catch (InterruptedException | CouldNotPerformException
                        | TransformerFactory.TransformerFactoryException e) {
                    ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                }
            }
        });

        this.foregroundPane.getMainMenu().addFillHashesButtonEventHandler(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent event) {
                try {
                    fillHashes();
                } catch (CouldNotPerformException e) {
                    ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                }
            }
        });

        deviceMap = new HashMap<>();
        locationMap = new HashMap<>();

        init = false;
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
            this.transformReceiver = TransformerFactory.getInstance().createTransformReceiver();
        } catch (TransformerFactory.TransformerFactoryException e) {
            locationRegistryRemote.shutdown();
            deviceRegistryRemote.shutdown();
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
     * @throws InterruptedException InterruptedException
     */
    public void fillHashes() throws CouldNotPerformException {
        checkInit();
        fillDeviceMap();

        final ListIterator<LocationConfigType.LocationConfig> locationConfigListIterator =
                locationRegistryRemote.getLocationConfigs().listIterator();

        while (locationConfigListIterator.hasNext()) {
            final LocationConfigType.LocationConfig currentLocationConfig = locationConfigListIterator.next();
            LOGGER.info("INFO: Room: " + currentLocationConfig.getId());

            final ListIterator<String> unitIDStringListIterator = currentLocationConfig.getUnitIdList().listIterator();

            while (unitIDStringListIterator.hasNext()) {
                final String unitId = unitIDStringListIterator.next();

                LOGGER.info("INFO: Unit: " + unitId);

                final DALRemoteService currentDalRemoteService = deviceMap.get(unitId);

                if (currentDalRemoteService != null) {
                    if (!locationMap.containsKey(currentLocationConfig.getId())) {
                        locationMap.put(currentLocationConfig.getId(), new TreeMap<>());
                    }

                    locationMap.get(currentLocationConfig.getId()).put(unitId, currentDalRemoteService);
                }
            }
        }
    }

    private void fillDeviceMap() throws CouldNotPerformException {
        final UnitRemoteFactoryInterface unitRemoteFactoryInterface = UnitRemoteFactory.getInstance();
        final Iterator<UnitConfigType.UnitConfig> unitConfigIterator = deviceRegistryRemote.getUnitConfigs().iterator();

        while (unitConfigIterator.hasNext()) {
            final UnitConfigType.UnitConfig currentUnitConfig = unitConfigIterator.next();
            DALRemoteService currentDalRemoteService = null;

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
     * Returns the DALRemoteService to the given unitId and class.
     * @param unitId the unit ID
     * @param <Remote> the corresponding class of the remote
     * @return the DALRemoteService casted to the given remote class
     * @throws CouldNotPerformException CouldNotPerformException
     */
    @SuppressWarnings("unchecked")
    public <Remote extends DALRemoteService> Remote getUnitRemoteById(
            final String unitId) throws CouldNotPerformException {
        checkInit();

        return (Remote) deviceMap.get(unitId);
    }

    /**
     * Returns the DALRemoteService to the given unitId and locationId.
     * @param unitId the unit ID
     * @param locationId the location ID
     * @param <Remote> the corresponding class of the remote
     * @return the DALRemoteService
     * @throws CouldNotPerformException CouldNotPerformException
     */
    @SuppressWarnings("unchecked")
    public <Remote extends  DALRemoteService> Remote getUnitRemoteByIdAndLocation(
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
    public <Remote extends DALRemoteService> List<Remote> getUnitRemoteListOfClass(
            final Class<? extends Remote> remoteClass) throws CouldNotPerformException {
        checkInit();

        final List<Remote> unitRemoteList = new ArrayList<>();
        final Iterator<Map.Entry<String, DALRemoteService>> unitIterator =
                deviceMap.entrySet().iterator();

        while (unitIterator.hasNext()) {
            final DALRemoteService currentDalRemoteService = unitIterator.next().getValue();
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
    public List<DALRemoteService> getUnitRemoteListOfLocation(
            final String locationId) throws CouldNotPerformException {
        checkInit();

        final List<DALRemoteService> unitRemoteList = new ArrayList<>();
        final Map<String, DALRemoteService> unitRemoteHashOfLocation = locationMap.get(locationId);

        final Iterator<Map.Entry<String, DALRemoteService>> unitIterator =
                unitRemoteHashOfLocation.entrySet().iterator();
        while (unitIterator.hasNext()) {
            final Map.Entry<String, DALRemoteService> currentEntry = unitIterator.next();
            unitRemoteList.add(currentEntry.getValue());
        }

        return unitRemoteList;
    }

    /**
     * Returns a Map of all DALRemoteServices of the given Location sorted by their UnitType.
     * @param locationId locationId
     * @return the Map of DALRemoteServices
     */
    public Map<UnitType, List<DALRemoteService>> getUnitRemoteMapOfLocation(final String locationId) {
        final Map<UnitType, List<DALRemoteService>> unitRemoteMap = new TreeMap<>();

        final UnitType[] unitTypes = UnitType.values();

        for (final UnitType type : unitTypes) {
            try {
                final Class<? extends DALRemoteService> remote = UnitRemoteFactory.loadUnitRemoteClass(type);
                final List<DALRemoteService> unitRemoteList =
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
     * Returns a List of all DALRemoteServices to a given locationId and inherited Class of DALRemoteService.
     * @param locationId the location ID
     * @param remoteClass the inherited Class of DALRemoteService
     * @param <Remote> the corresponding class of the remote
     * @return the List of DALRemoteServices
     * @throws CouldNotPerformException CouldNotPerformException
     */
    @SuppressWarnings("unchecked")
    public <Remote extends DALRemoteService> List<Remote> getUnitRemoteListOfLocationAndClass(
            final String locationId, final Class<? extends Remote> remoteClass) throws CouldNotPerformException {
        checkInit();

        final List<Remote> unitRemoteList = new ArrayList<>();
        final Map<String, DALRemoteService> unitRemoteHashOfLocation = locationMap.get(locationId);

        final Iterator<Map.Entry<String, DALRemoteService>> unitIterator =
                unitRemoteHashOfLocation.entrySet().iterator();
        while (unitIterator.hasNext()) {
            final Map.Entry<String, DALRemoteService> currentEntry = unitIterator.next();
            if (currentEntry.getValue().getClass() == remoteClass) {
                unitRemoteList.add((Remote) currentEntry.getValue());
            }
        }

        return unitRemoteList;
    }

    /**
     * Shut down all DALRemotes and the RegistryRemotes.
     */
    public void shutdownAllRemotes() {
        //TODO: somehow not shutting down properly?!

        final Iterator<Map.Entry<String, DALRemoteService>> unitIterator = deviceMap.entrySet().iterator();
        while (unitIterator.hasNext()) {
            final DALRemoteService remote = unitIterator.next().getValue();
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
     * @return isInit
     */
    public boolean isInit() {
        return init;
    }
}
