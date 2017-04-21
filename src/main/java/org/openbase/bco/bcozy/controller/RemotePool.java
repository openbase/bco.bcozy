/**
 * ==================================================================
 *
 * This file is part of org.openbase.bco.bcozy.
 *
 * org.openbase.bco.bcozy is free software: you can redistribute it and modify
 * it under the terms of the GNU General Public License (Version 3) as published
 * by the Free Software Foundation.
 *
 * org.openbase.bco.bcozy is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * org.openbase.bco.bcozy. If not, see <http://www.gnu.org/licenses/>.
 * ==================================================================
 */
package org.openbase.bco.bcozy.controller; //NOPMD

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.ProgressIndicator;
import org.openbase.bco.bcozy.view.ForegroundPane;
import org.openbase.bco.dal.remote.unit.UnitRemote;
import org.openbase.bco.dal.remote.unit.UnitRemoteFactory;
import org.openbase.bco.dal.remote.unit.UnitRemoteFactoryImpl;
import org.openbase.bco.dal.remote.unit.user.UserRemote;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.openbase.jul.schedule.GlobalCachedExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rct.TransformReceiver;
import rct.TransformerFactory;
import rst.domotic.state.EnablingStateType;
import rst.domotic.unit.UnitConfigType.UnitConfig;
import rst.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by tmichalski on 25.11.15.
 *
 * @deprecated Please use Units.getUnit(...) instead
 */
@Deprecated
public class RemotePool {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemotePool.class);

    private final Map<String, UnitRemote> unitMap;
    private final Map<String, Map<String, UnitRemote>> locationMap;
    private final Map<String, UserRemote> userMap;
    private TransformReceiver transformReceiver;

    private boolean init;
    private boolean mapsFilled;

    /**
     * Constructor for the RemotePool.
     *
     * @param foregroundPane ForegroundPane
     */
    public RemotePool(final ForegroundPane foregroundPane) {
        foregroundPane.getMainMenu().addInitRemoteButtonEventHandler((final ActionEvent event) -> {
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
                    } catch (InterruptedException | CouldNotPerformException
                            | TransformerFactory.TransformerFactoryException e) {
                        ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                    }
                    return null;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    Platform.runLater(()
                            -> foregroundPane.getContextMenu().getChildren().remove(progressIndicator));
                }
            };
            GlobalCachedExecutorService.submit(task);
        });

        foregroundPane.getMainMenu().addFillHashesButtonEventHandler((final ActionEvent event) -> {
            final Task task = new Task() {
                private final ProgressIndicator progressIndicator = new ProgressIndicator(-1);

                @Override
                protected Object call() throws java.lang.Exception {
                    Platform.runLater(() -> {
                        foregroundPane.getContextMenu().getTitledPaneContainer().clearTitledPane();
                        foregroundPane.getContextMenu().getChildren().add(progressIndicator);
                    });
                    try {
                        fillUnitAndLocationMap();
                    } catch (CouldNotPerformException e) {
                        ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                        shutdownDALRemotesAndClearMaps();
                    }
                    return null;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    Platform.runLater(() -> foregroundPane.getContextMenu().getChildren().remove(progressIndicator));
                }
            };
            GlobalCachedExecutorService.execute(task);
        });

        unitMap = new HashMap<>();
        locationMap = new HashMap<>();
        userMap = new HashMap<>();

        init = false;
        mapsFilled = false;
    }

    /**
     * Initiate RegistryRemotes.
     *
     * @throws CouldNotPerformException CouldNotPerformException
     * @throws InterruptedException InterruptedException
     * @throws TransformerFactory.TransformerFactoryException
     * TransformerFactoryException
     */
    public void initRegistryRemotes() throws CouldNotPerformException, InterruptedException,
            TransformerFactory.TransformerFactoryException {
        if (init) {
            LOGGER.info("INFO: RegistryRemotes were already initialized.");
            return;

        }

        // wait for registry data
        Registries.waitForData();

        try {
            this.transformReceiver = TransformerFactory.getInstance().createTransformReceiver();
        } catch (TransformerFactory.TransformerFactoryException e) {
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
     * Fills the unit and the location hashmap with all remotes. All remotes
     * will be initialized and activated.
     *
     * @throws CouldNotPerformException CouldNotPerformException
     * @throws InterruptedException InterruptedException
     */
    public void fillUnitAndLocationMap() throws CouldNotPerformException, InterruptedException {
        checkInit();
        if (mapsFilled) {
            shutdownDALRemotesAndClearMaps();
        }
        fillUnitMap();
        fillUnitMapAgents();
        fillUnitMapApps();
        fillUnitMapScenes();

        for (final UnitConfig locationUnitConfig : Registries.getLocationRegistry().getLocationConfigs()) {
            LOGGER.debug("Loading Room[" + locationUnitConfig.getId() + "] ...");

            for (final String unitId : locationUnitConfig.getLocationConfig().getUnitIdList()) {
                LOGGER.debug("Loading Unit[" + unitId + "]");

                if (unitMap.containsKey(unitId)) {
                    final UnitRemote currentDalRemoteService = unitMap.get(unitId);

                    if (!locationMap.containsKey(locationUnitConfig.getId())) {
                        locationMap.put(locationUnitConfig.getId(), new TreeMap<>());
                    }

                    locationMap.get(locationUnitConfig.getId()).put(unitId, currentDalRemoteService);
                }
            }
        }
        mapsFilled = true;
    }

    private void fillUnitMap() throws CouldNotPerformException, InterruptedException {
        final UnitRemoteFactory unitRemoteFactoryInterface = UnitRemoteFactoryImpl.getInstance();

        for (final UnitConfig dalUnitConfig : Registries.getUnitRegistry().getDalUnitConfigs()) {
            if (dalUnitConfig.getEnablingState().getValue() != EnablingStateType.EnablingState.State.ENABLED) {
                LOGGER.debug("Skip Unit[" + dalUnitConfig.getLabel() + "] because it is not enabled!");
                continue;
            }

            UnitRemote currentDalRemoteService;

            try {
                currentDalRemoteService = unitRemoteFactoryInterface.newInstance(dalUnitConfig);
            } catch (CouldNotPerformException e) {
                ExceptionPrinter.printHistory(e, LOGGER, LogLevel.DEBUG);
                continue;
            }

            try {
                currentDalRemoteService.init(dalUnitConfig);
            } catch (InterruptedException | CouldNotPerformException e) {
                ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                continue;
            }

            try {
                currentDalRemoteService.activate();
            } catch (InterruptedException | CouldNotPerformException e) {
                ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                continue;
            }
            unitMap.put(dalUnitConfig.getId(), currentDalRemoteService);
        }
    }

    private void fillUnitMapAgents() throws CouldNotPerformException, InterruptedException {
        final UnitRemoteFactory unitRemoteFactoryInterface = UnitRemoteFactoryImpl.getInstance();

        for (final UnitConfig agentUnitConfig : Registries.getAgentRegistry().getAgentConfigs()) {
            if (agentUnitConfig.getEnablingState().getValue() != EnablingStateType.EnablingState.State.ENABLED) {
                LOGGER.debug("Skip Unit[" + agentUnitConfig.getLabel() + "] because it is not enabled!");
                continue;
            }

            UnitRemote currentDalRemoteService;

            try {
                currentDalRemoteService = unitRemoteFactoryInterface.newInstance(agentUnitConfig);
            } catch (CouldNotPerformException e) {
                ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                continue;
            }

            try {
                currentDalRemoteService.init(agentUnitConfig);
            } catch (InterruptedException | CouldNotPerformException e) {
                ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                continue;
            }

            try {
                currentDalRemoteService.activate();
            } catch (InterruptedException | CouldNotPerformException e) {
                ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                continue;
            }
            unitMap.put(agentUnitConfig.getId(), currentDalRemoteService);
        }
    }

    private void fillUnitMapApps() throws CouldNotPerformException, InterruptedException {
        final UnitRemoteFactory unitRemoteFactoryInterface = UnitRemoteFactoryImpl.getInstance();

        for (final UnitConfig appUnitConfig : Registries.getAppRegistry().getAppConfigs()) {
            if (appUnitConfig.getEnablingState().getValue() != EnablingStateType.EnablingState.State.ENABLED) {
                LOGGER.debug("Skip Unit[" + appUnitConfig.getLabel() + "] because it is not enabled!");
                continue;
            }

            UnitRemote currentDalRemoteService;

            try {
                currentDalRemoteService = unitRemoteFactoryInterface.newInstance(appUnitConfig);
            } catch (CouldNotPerformException e) {
                ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                continue;
            }

            try {
                currentDalRemoteService.init(appUnitConfig);
            } catch (InterruptedException | CouldNotPerformException e) {
                ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                continue;
            }

            try {
                currentDalRemoteService.activate();
            } catch (InterruptedException | CouldNotPerformException e) {
                ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                continue;
            }
            unitMap.put(appUnitConfig.getId(), currentDalRemoteService);
        }
    }

    private void fillUnitMapScenes() throws CouldNotPerformException, InterruptedException {
        final UnitRemoteFactory unitRemoteFactoryInterface = UnitRemoteFactoryImpl.getInstance();

        for (final UnitConfig sceneUnitConfig : Registries.getSceneRegistry().getSceneConfigs()) {
            if (sceneUnitConfig.getEnablingState().getValue() != EnablingStateType.EnablingState.State.ENABLED) {
                LOGGER.debug("Skip Unit[" + sceneUnitConfig.getLabel() + "] because it is not enabled!");
                continue;
            }

            UnitRemote currentDalRemoteService;

            try {
                currentDalRemoteService = unitRemoteFactoryInterface.newInstance(sceneUnitConfig);
            } catch (CouldNotPerformException e) {
                ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                continue;
            }

            try {
                currentDalRemoteService.init(sceneUnitConfig);
            } catch (InterruptedException | CouldNotPerformException e) {
                ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                continue;
            }

            try {
                currentDalRemoteService.activate();
            } catch (InterruptedException | CouldNotPerformException e) {
                ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                continue;
            }
            unitMap.put(sceneUnitConfig.getId(), currentDalRemoteService);
        }
    }

    /**
     * Fills the user map with all remotes. All remotes will be initialized and
     * activated.
     *
     * @throws CouldNotPerformException CouldNotPerformException
     * @throws InterruptedException InterruptedException
     */
    public void fillUserMap() throws CouldNotPerformException, InterruptedException {
        for (final UnitConfig currentUserUnitConfig : Registries.getUnitRegistry().getUnitConfigs(UnitType.USER)) {
            final UserRemote currentUserRemote = new UserRemote();
            try {
                currentUserRemote.init(currentUserUnitConfig);
                currentUserRemote.activate();
            } catch (InterruptedException e) {
                ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
            }
        }
    }
//
//    /**
//     * Returns the UnitRemote to the given unitId and class.
//     *
//     * @param unitId the unit ID
//     * @param <Remote> the corresponding class of the remote
//     * @return the UnitRemote casted to the given remote class
//     * @throws CouldNotPerformException CouldNotPerformException
//     */
//    @SuppressWarnings("unchecked")
//    public <Remote extends UnitRemote> Remote getUnitRemoteById(
//            final String unitId) throws CouldNotPerformException {
//        checkInit();
//
//        return (Remote) unitMap.get(unitId);
//    }

//    /**
//     * Returns the UnitRemote to the given unitId and
//     * locationId.
//     *
//     * @param unitId the unit ID
//     * @param locationId the location ID
//     * @param <Remote> the corresponding class of the remote
//     * @return the UnitRemote
//     * @throws CouldNotPerformException CouldNotPerformException
//     */
//    @SuppressWarnings("unchecked")
//    public <Remote extends UnitRemote> Remote getUnitRemoteByIdAndLocation(
//            final String unitId, final String locationId) throws CouldNotPerformException {
//        checkInit();
//
//        return (Remote) locationMap.get(locationId).get(unitId);
//    }

//    /**
//     * Returns a List with all Remotes of the given remote class.
//     *
//     * @param remoteClass the remote class
//     * @param <Remote> the corresponding class of the remote
//     * @return the List of DALRemoteServices
//     * @throws CouldNotPerformException CouldNotPerformException
//     */
//    @SuppressWarnings("unchecked")
//    public <Remote extends UnitRemote> List<Remote> getUnitRemoteListOfClass(
//            final Class<? extends Remote> remoteClass) throws CouldNotPerformException {
//        checkInit();
//
//        final List<Remote> unitRemoteList = new ArrayList<>();
//
//        for (final Map.Entry<String, UnitRemote> stringDALRemoteServiceEntry : unitMap.entrySet()) {
//            final UnitRemote currentDalRemoteService = stringDALRemoteServiceEntry.getValue();
//            if (currentDalRemoteService.getClass().equals(remoteClass)) {
//                unitRemoteList.add((Remote) currentDalRemoteService);
//            }
//        }
//
//        return unitRemoteList;
//    }
//
//    /**
//     * Returns a List of all DALRemoteServices to the given locationId.
//     *
//     * @param locationId the location ID
//     * @return the List of DALRemoteServices
//     * @throws CouldNotPerformException CouldNotPerformException
//     */
//    public List<UnitRemote> getUnitRemoteListOfLocation(
//            final String locationId) throws CouldNotPerformException {
//        checkInit();
//
//        final List<UnitRemote> unitRemoteList = new ArrayList<>();
//
//        if (locationMap.containsKey(locationId)) {
//            final Map<String, UnitRemote> unitRemoteHashOfLocation = locationMap.get(locationId);
//
//            for (final Map.Entry<String, UnitRemote> currentEntry : unitRemoteHashOfLocation.entrySet()) {
//                unitRemoteList.add(currentEntry.getValue());
//            }
//        }
//
//        return unitRemoteList;
//    }

    /**
     * Returns a Map of all DALRemoteServices of the given Location sorted by
     * their UnitType.
     *
     * @param locationId locationId
     * @return the Map of DALRemoteServices
     */
    public Map<UnitType, List<UnitRemote>> getUnitRemoteMapOfLocation(final String locationId) {
        if (!mapsFilled) {
            LOGGER.debug("MAPS not filled!");
        }
        final Map<UnitType, List<UnitRemote>> unitRemoteMap = new TreeMap<>();

        final UnitType[] unitTypes = UnitType.values();

        for (final UnitType type : unitTypes) {
            try {
                final Class<? extends UnitRemote> remoteClass = UnitRemoteFactoryImpl.loadUnitRemoteClass(type);
                final List<UnitRemote> unitRemoteList = getUnitRemoteListOfLocationAndClass(locationId, remoteClass);
                if (!unitRemoteList.isEmpty()) {
                    unitRemoteMap.put(type, unitRemoteList);
                }
            } catch (CouldNotPerformException e) {
                ExceptionPrinter.printHistory(new CouldNotPerformException(
                        "Unit Type[" + type.name() + "] not supported!", e), LOGGER, LogLevel.DEBUG);
            }
        }

        return unitRemoteMap;
    }

    /**
     * Returns a List of all DALRemoteServices to a given locationId and
     * inherited Class of UnitRemote.
     *
     * @param locationId the location ID
     * @param remoteClass the inherited Class of UnitRemote
     * @param <Remote> the corresponding class of the remote
     * @return the List of DALRemoteServices
     * @throws CouldNotPerformException CouldNotPerformException
     */
    @SuppressWarnings("unchecked")
    public <Remote extends UnitRemote> List<Remote> getUnitRemoteListOfLocationAndClass(
            final String locationId, final Class<? extends Remote> remoteClass) throws CouldNotPerformException {
        if (!mapsFilled) {
            LOGGER.debug("MAPS not filled!");
        }
        checkInit();

        final List<Remote> unitRemoteList = new ArrayList<>();
        if (locationMap.containsKey(locationId)) {
            final Map<String, UnitRemote> unitRemoteHashOfLocation = locationMap.get(locationId);

            for (final Map.Entry<String, UnitRemote> currentEntry : unitRemoteHashOfLocation.entrySet()) {
                if (currentEntry.getValue().getClass() == remoteClass) {
                    unitRemoteList.add((Remote) currentEntry.getValue());
                }
            }
        }
        return unitRemoteList;
    }

    /**
     * Returns a List of all UserRemotes.
     *
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
     * Shut down all DALRemotes and clear the unitMap and the locationMap.
     */
    public void shutdownDALRemotesAndClearMaps() {
        shutdownDALRemotes();
        this.unitMap.clear();
        this.locationMap.clear();

        mapsFilled = false;
    }

    /**
     * Shut down all DALRemotes.
     */
    public void shutdownDALRemotes() {
        for (final Map.Entry<String, UnitRemote> stringDALRemoteServiceEntry : unitMap.entrySet()) {
            final UnitRemote remote = stringDALRemoteServiceEntry.getValue();
            remote.shutdown();
        }
    }

    /**
     * Shut down all DALRemotes and the RegistryRemotes.
     */
    public void shutdownAllRemotes() {
        //TODO: somehow not shutting down properly?!

        for (final Map.Entry<String, UnitRemote> stringDALRemoteServiceEntry : unitMap.entrySet()) {
            final UnitRemote remote = stringDALRemoteServiceEntry.getValue();
            remote.shutdown();
        }

// TODO should be activated after rsb 16 adjustments.
//        if (transformReceiver != null) {
//            transformReceiver.shutdown();
//        }
        TransformerFactory.killInstance();

        init = false;
    }

    /**
     * Returns the TransformReceiver.
     *
     * @return TransformReceiver
     * @throws CouldNotPerformException CouldNotPerformException
     */
    public TransformReceiver getTransformReceiver() throws CouldNotPerformException {
        checkInit();

        return transformReceiver;
    }

    /**
     * Returns the information whether the remotepool is initialized or not.
     *
     * @return init
     */
    public boolean isInit() {
        return init;
    }

    /**
     * Returns the information whether the maps are filled or not.
     *
     * @return mapsFilled
     */
    public boolean isMapsFilled() {
        return mapsFilled;
    }
}
