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
import de.citec.jul.exception.InstantiationException;
import de.citec.lm.remote.LocationRegistryRemote;
import org.dc.bco.bcozy.BCozy;
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

    private HashMap<Class, HashMap<String, DALRemoteService>> deviceHash;
    private HashMap<String, HashMap<String, DALRemoteService>> locationHash;
    private LocationRegistryRemote locationRegistryRemote;
    private DeviceRegistryRemote deviceRegistryRemote;

    /**
     * Constructor for the Remotecontroller.
     * @throws InstantiationException InstantiationException
     */
    public RemotePool() throws InstantiationException {
        deviceHash = new HashMap<>();
        locationHash = new HashMap<>();

        try {
            locationRegistryRemote = new LocationRegistryRemote();
            locationRegistryRemote.init();
            locationRegistryRemote.activate();

            deviceRegistryRemote = new DeviceRegistryRemote();
            deviceRegistryRemote.init();
            deviceRegistryRemote.activate();
        } catch (Exception e) {
            throw new de.citec.jul.exception.InstantiationException(this, e);
        }
    }

    /**
     * Fills the device and the location hashmap with all remotes. All remotes will be initialized and activated.
     * @throws CouldNotPerformException CouldNotPerformException
     * @throws InterruptedException InterruptedException
     */
    public void fillHashes() throws CouldNotPerformException, InterruptedException {
        LOGGER.info("INFO: fillDeviceHash()");

        final UnitRemoteFactoryInterface unitRemoteFactoryInterface = UnitRemoteFactory.getInstance();
        final List<LocationConfigType.LocationConfig> locationConfigs = locationRegistryRemote.getLocationConfigs();
        final ListIterator<LocationConfigType.LocationConfig> locationConfigListIterator =
                locationConfigs.listIterator();

        while (locationConfigListIterator.hasNext()) {
            final LocationConfigType.LocationConfig currentLocationConfig = locationConfigListIterator.next();
            LOGGER.info("INFO1: " + currentLocationConfig.getId());

            final List<String> unitIdList = currentLocationConfig.getUnitIdList();
            final ListIterator<String> unitIDStringListIterator = unitIdList.listIterator();

            while (unitIDStringListIterator.hasNext()) {
                final String unitId = unitIDStringListIterator.next();

                final UnitConfigType.UnitConfig currentUnitConfig = deviceRegistryRemote.getUnitConfigById(unitId);
                LOGGER.info("INFO: " + unitId);
                DALRemoteService currentDalRemoteService =
                        unitRemoteFactoryInterface.createAndInitUnitRemote(currentUnitConfig);

                if (!deviceHash.containsKey(currentDalRemoteService.getClass())) {
                    deviceHash.put(currentDalRemoteService.getClass(), new HashMap<>());
                }
                deviceHash.get(currentDalRemoteService.getClass()).put(currentUnitConfig.getId(),
                        currentDalRemoteService);

                if (!locationHash.containsKey(currentLocationConfig.getId())) {
                    locationHash.put(currentLocationConfig.getId(), new HashMap<>());
                }
                locationHash.get(currentLocationConfig.getId()).put(unitId, currentDalRemoteService);

                currentDalRemoteService.activate();
            }
        }
        LOGGER.info("Location: " + locationHash.size());
        LOGGER.info("Device: " + deviceHash.size());
    }

    /**
     * Returns the DALRemoteService to the given unitId and class.
     * @param unitId the unit ID
     * @param remoteClass the corresponding class of the remote
     * @param <Remote> the corresponding class of the remote
     * @return the DALRemoteService casted to the given remote class
     */
    public <Remote extends DALRemoteService> Remote getUnitRemoteByClassAndId(
            final String unitId, final Class<? extends Remote> remoteClass) {
        return (Remote) deviceHash.get(remoteClass).get(unitId);
    }

    /**
     * Returns the DALRemoteService to the given unitId and locationId.
     * @param unitId the unit ID
     * @param locationId the location ID
     * @param <Remote> the corresponding class of the remote
     * @return the DALRemoteService
     */
    public <Remote extends  DALRemoteService> Remote getUnitRemoteByLocationAndId(
            final String unitId, final String locationId) {
        return (Remote) locationHash.get(locationId).get(unitId);
    }

    /**
     * Returns a List of all DALRemoteServices to the given locationId.
     * @param locationId the location ID
     * @return the List of DALRemoteServices
     */
    public List<DALRemoteService> getUnitRemotesOfLocation(
            final String locationId) {
        List<DALRemoteService> unitRemoteList = new ArrayList<>();
        HashMap<String, DALRemoteService> unitRemoteHashOfLocation = locationHash.get(locationId);

        final Iterator<Map.Entry<String, DALRemoteService>> unitIterator =
                unitRemoteHashOfLocation.entrySet().iterator();
        while (unitIterator.hasNext()) {
            Map.Entry<String, DALRemoteService> currentEntry = unitIterator.next();
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
     */
    public <Remote extends DALRemoteService> List<Remote>  getUnitRemotesOfLocationByClass(
            final String locationId, final Class<? extends Remote> remoteClass) {
        List<Remote> unitRemoteList = new ArrayList<>();
        HashMap<String, DALRemoteService> unitRemoteHashOfLocation = locationHash.get(locationId);

        final Iterator<Map.Entry<String, DALRemoteService>> unitIterator =
                unitRemoteHashOfLocation.entrySet().iterator();
        while (unitIterator.hasNext()) {
            Map.Entry<String, DALRemoteService> currentEntry = unitIterator.next();
            if (currentEntry.getValue().getClass() == remoteClass) {
                unitRemoteList.add((Remote) currentEntry.getValue());
            }
        }
        return unitRemoteList;
    }
}
