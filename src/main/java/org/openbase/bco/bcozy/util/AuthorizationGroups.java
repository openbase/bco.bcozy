package org.openbase.bco.bcozy.util;

import com.google.protobuf.ProtocolStringList;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.pattern.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.registry.UserRegistryDataType;
import rst.domotic.unit.UnitConfigType;
import rst.domotic.unit.UnitTemplateType;
import rst.domotic.unit.authorizationgroup.AuthorizationGroupConfigType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * @author vdasilva
 */
public final class AuthorizationGroups {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationGroups.class);

    private static final ObservableList<UnitConfigType.UnitConfig> authorizationGroups =
            FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

    /**
     * List of additional Observers, which will be informed if the groups change.
     */
    private static final List<Consumer<List<UnitConfigType.UnitConfig>>> observers = new
            CopyOnWriteArrayList<>();

    /**
     * Adds an Observer to the list of additional observers, which will be informed if the groups change.
     *
     * @param observer the consumer to add
     */
    public static void addListObserver(Consumer<List<UnitConfigType.UnitConfig>> observer) {
        observers.add(observer);
        List<UnitConfigType.UnitConfig> unitConfigs = getAuthorizationGroups();
        observer.accept(unitConfigs);
    }

    /**
     * Removes an Observer from the list of additional observers.
     *
     * @param observer the consumer to remove
     */
    public static void removeListObserver(Consumer<? extends List<UnitConfigType.UnitConfig>> observer) {
        observers.remove(observer);
    }

    private static final Observer<UserRegistryDataType.UserRegistryData> observer = (observable, userRegistryData) ->
            setAuthorizationGroups(userRegistryData.getAuthorizationGroupUnitConfigList(), authorizationGroups);

    public static synchronized ObservableList<UnitConfigType.UnitConfig> getAuthorizationGroups() {
        if (Registries.isDataAvailable()) {
            try {
                setAuthorizationGroups(Registries.getUserRegistry().getAuthorizationGroupConfigs(), authorizationGroups);
            } catch (CouldNotPerformException ex) {
                ExceptionPrinter.printHistory(ex, LOGGER);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }

        try {
            Registries.getUserRegistry().addDataObserver(observer);
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        return authorizationGroups;

    }

    private static void setAuthorizationGroups(List<UnitConfigType.UnitConfig> newGroups,
                                               ObservableList<UnitConfigType.UnitConfig> groups) {

        Platform.runLater(() -> {
            groups.clear();
            groups.addAll(newGroups);
            observers.forEach(consumer -> consumer.accept(newGroups));
        });

    }

    public static StringConverter<UnitConfigType.UnitConfig> stringConverter(
            List<UnitConfigType.UnitConfig> groups) {

        return new StringConverter<UnitConfigType.UnitConfig>() {
            @Override
            public String toString(UnitConfigType.UnitConfig object) {
                return object.getLabel();
            }

            @Override
            public UnitConfigType.UnitConfig fromString(String string) {
                for (UnitConfigType.UnitConfig group : groups) {
                    if ((group.getLabel().equals(string))) {
                        return group;
                    }
                }
                return null;
            }
        };
    }

    public static void removeAuthorizationGroup(UnitConfigType.UnitConfig group) throws InterruptedException, CouldNotPerformException {
        try {
            Registries.getUserRegistry().removeAuthorizationGroupConfig(group);
        } catch (CouldNotPerformException ex) {
            throw new CouldNotPerformException("Could not remove authorization group.", ex);
        }
    }

    public static UnitConfigType.UnitConfig addAuthorizationGroup(String groupName) throws InterruptedException, CouldNotPerformException {
        try {
            UnitConfigType.UnitConfig addedGroup = tryAddAuthorizationGroup(groupName).get(5, TimeUnit.SECONDS);
            return addedGroup;
        } catch (CouldNotPerformException | TimeoutException | ExecutionException ex) {
            throw new CouldNotPerformException("Could not add authorization group.", ex);
        }
    }

    public static Future<UnitConfigType.UnitConfig> tryAddAuthorizationGroup(String groupName)
            throws CouldNotPerformException, InterruptedException {

        UnitConfigType.UnitConfig newGroup = UnitConfigType.UnitConfig.newBuilder()
                .setLabel(groupName)
                .setType(UnitTemplateType.UnitTemplate.UnitType.AUTHORIZATION_GROUP)
                .setAuthorizationGroupConfig(AuthorizationGroupConfigType.AuthorizationGroupConfig.newBuilder())
                .build();

        return Registries.getUserRegistry().registerAuthorizationGroupConfig(newGroup);
    }

    public static void tryAddToGroup(UnitConfigType.UnitConfig group, String userId) throws CouldNotPerformException,
            InterruptedException {
        UnitConfigType.UnitConfig.Builder unitConfig = Registries.getUserRegistry().getAuthorizationGroupConfigById(group.getId()).toBuilder();
        AuthorizationGroupConfigType.AuthorizationGroupConfig.Builder authorizationGroupConfig = unitConfig.getAuthorizationGroupConfigBuilder();
        authorizationGroupConfig.addMemberId(userId);
        Registries.getUserRegistry().updateAuthorizationGroupConfig(unitConfig.build());
    }

    public static List<UnitConfigType.UnitConfig> getGroupsByUser(String userId) {
        List<UnitConfigType.UnitConfig> groupsWithUser = new ArrayList<>(authorizationGroups);
        groupsWithUser.removeIf(group -> !group.getAuthorizationGroupConfig().getMemberIdList().contains(userId));
        return groupsWithUser;
    }

    public static void tryRemoveFromGroup(UnitConfigType.UnitConfig group, String userId) throws CouldNotPerformException,
            InterruptedException {

        UnitConfigType.UnitConfig.Builder unitConfig = Registries.getUserRegistry().getAuthorizationGroupConfigById(group.getId()).toBuilder();
        AuthorizationGroupConfigType.AuthorizationGroupConfig.Builder authorizationGroupConfig = unitConfig.getAuthorizationGroupConfigBuilder();

        ProtocolStringList members = authorizationGroupConfig.getMemberIdList();

        authorizationGroupConfig.clearMemberId();

        for (String member : members) {
            if (!member.equals(userId)) {
                authorizationGroupConfig.addMemberId(member);
            }
        }

        Registries.getUserRegistry().updateAuthorizationGroupConfig(unitConfig.build());
    }
}
