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
import de.citec.dm.remote.DeviceRegistryRemote;
import de.citec.jul.exception.CouldNotPerformException;
import de.citec.jul.exception.printer.ExceptionPrinter;
import de.citec.jul.exception.printer.LogLevel;
import de.citec.lm.remote.LocationRegistryRemote;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import org.dc.bco.bcozy.BCozy;
import org.dc.bco.bcozy.view.ForegroundPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.homeautomation.unit.UnitConfigType;
import rst.spatial.LocationConfigType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * Created by tmichalski on 25.11.15.
 */
public class RemotePool {

    private static final Logger LOGGER = LoggerFactory.getLogger(BCozy.class);

    private final ForegroundPane foregroundPane;

    private final Map<Class, HashMap<String, DALRemoteService>> deviceMap;
    private final Map<String, HashMap<String, DALRemoteService>> locationMap;
    private LocationRegistryRemote locationRegistryRemote;
    private DeviceRegistryRemote deviceRegistryRemote;

    private boolean isInit;

    /**
     * Constructor for the Remotecontroller.
     * @throws InstantiationException InstantiationException
     * @param foregroundPane ForegroundPane
     */
    public RemotePool(final ForegroundPane foregroundPane) {
        this.foregroundPane = foregroundPane;

        this.foregroundPane.getMainMenu().addMainButtonEventHandler(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent event) {
                try {
                    initRegistryRemotes();
                    fillHashes();
                } catch (InterruptedException | CouldNotPerformException e) {
                    ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                }
            }
        });

        deviceMap = new HashMap<>();
        locationMap = new HashMap<>();

        isInit = false;
    }

    /**
     * Initiate RegistryRemotes.
     * @throws CouldNotPerformException CouldNotPerformException
     * @throws InterruptedException InterruptedException
     */
    public void initRegistryRemotes() throws CouldNotPerformException, InterruptedException {
        if (isInit) {
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

        isInit = true;
        LOGGER.info("INFO: RegistryRemotes are initialized.");
    }

    private void checkInit() throws CouldNotPerformException {
        if (!isInit) {
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

        final UnitRemoteFactoryInterface unitRemoteFactoryInterface = UnitRemoteFactory.getInstance();
        final List<LocationConfigType.LocationConfig> locationConfigs = locationRegistryRemote.getLocationConfigs();
        final ListIterator<LocationConfigType.LocationConfig> locationConfigListIterator =
                locationConfigs.listIterator();

        while (locationConfigListIterator.hasNext()) {
            final LocationConfigType.LocationConfig currentLocationConfig = locationConfigListIterator.next();
            LOGGER.info("INFO: Room: " + currentLocationConfig.getId());

            final List<String> unitIdList = currentLocationConfig.getUnitIdList();
            final ListIterator<String> unitIDStringListIterator = unitIdList.listIterator();

            while (unitIDStringListIterator.hasNext()) {
                final String unitId = unitIDStringListIterator.next();

                UnitConfigType.UnitConfig currentUnitConfig = null;
                DALRemoteService currentDalRemoteService = null;

                try {
                    currentUnitConfig = deviceRegistryRemote.getUnitConfigById(unitId);
                    currentDalRemoteService = unitRemoteFactoryInterface.createAndInitUnitRemote(currentUnitConfig);
                } catch (CouldNotPerformException e) {
                    ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                    continue;
                }

                LOGGER.info("INFO: Unit: " + unitId);

                if (!deviceMap.containsKey(currentDalRemoteService.getClass())) {
                    deviceMap.put(currentDalRemoteService.getClass(), new HashMap<>());
                }
                deviceMap.get(currentDalRemoteService.getClass()).put(currentUnitConfig.getId(),
                        currentDalRemoteService);

                if (!locationMap.containsKey(currentLocationConfig.getId())) {
                    locationMap.put(currentLocationConfig.getId(), new HashMap<>());
                }
                locationMap.get(currentLocationConfig.getId()).put(unitId, currentDalRemoteService);

                try {
                    currentDalRemoteService.activate();
                } catch (InterruptedException | CouldNotPerformException e) {
                    ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                }
            }
        }
    }

    /**
     * Returns the DALRemoteService to the given unitId and class.
     * @param unitId the unit ID
     * @param remoteClass the corresponding class of the remote
     * @param <Remote> the corresponding class of the remote
     * @return the DALRemoteService casted to the given remote class
     * @throws CouldNotPerformException CouldNotPerformException
     */
    @SuppressWarnings("unchecked")
    public <Remote extends DALRemoteService> Remote getUnitRemoteByIdAndClass(
            final String unitId, final Class<? extends Remote> remoteClass) throws CouldNotPerformException {
        checkInit();

        return (Remote) deviceMap.get(remoteClass).get(unitId);
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
                deviceMap.get(remoteClass).entrySet().iterator();

        while (unitIterator.hasNext()) {
            unitRemoteList.add((Remote) unitIterator.next().getValue());
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
     * Returns a List of all DALRemoteServices to a given locationId and inherited Class of DALRemoteService.
     * @param locationId the location ID
     * @param remoteClass the inherited Class of DALRemoteService
     * @param <Remote> the corresponding class of the remote
     * @return the List of DALRemoteServices
     * @throws CouldNotPerformException CouldNotPerformException
     */
    @SuppressWarnings("unchecked")
    public <Remote extends DALRemoteService> List<Remote>  getUnitRemotesOfLocationAndClass(
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
        final Iterator<Map.Entry<Class, HashMap<String, DALRemoteService>>> classIterator =
                deviceMap.entrySet().iterator();
        while (classIterator.hasNext()) {
            final Iterator<Map.Entry<String, DALRemoteService>> unitIterator =
                    classIterator.next().getValue().entrySet().iterator();
            while (unitIterator.hasNext()) {
                final DALRemoteService remote = unitIterator.next().getValue();
                remote.shutdown();
            }
        }

        if (locationRegistryRemote.isActive()) {
            locationRegistryRemote.shutdown();
        }
        if (deviceRegistryRemote.isActive()) {
            deviceRegistryRemote.shutdown();
        }

        isInit = false;
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
}
