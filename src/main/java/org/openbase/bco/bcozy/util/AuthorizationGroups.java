package org.openbase.bco.bcozy.util;

import com.google.protobuf.ProtocolStringList;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.extension.type.processing.LabelProcessor;
import org.openbase.jul.pattern.Observer;
import org.openbase.jul.pattern.provider.DataProvider;
import org.openbase.jul.schedule.FutureProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openbase.type.domotic.registry.UnitRegistryDataType.UnitRegistryData;
import org.openbase.type.domotic.unit.UnitConfigType.UnitConfig;
import org.openbase.type.domotic.unit.UnitTemplateType;
import org.openbase.type.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;
import org.openbase.type.domotic.unit.authorizationgroup.AuthorizationGroupConfigType.AuthorizationGroupConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * @author vdasilva
 */
public final class AuthorizationGroups {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationGroups.class);

    private static boolean initialized = false;

    private static final ObservableList<UnitConfig> authorizationGroups = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

    /**
     * List of additional Observers, which will be informed if the groups change.
     */
    private static final List<Consumer<List<UnitConfig>>> observers = new CopyOnWriteArrayList<>();

    /**
     * UnitRegistry observer used for registry synchronization.
     */
    private static final Observer<DataProvider<UnitRegistryData>, UnitRegistryData> unitRegistryObserver = (observable, unitRegistryData) -> {
        updateAuthorizationGroups(unitRegistryData.getAuthorizationGroupUnitConfigList());
    };

    /**
     * Adds an Observer to the list of additional observers, which will be informed if the groups change.
     *
     * @param observer the consumer to add
     */
    public static void addListObserver(Consumer<List<UnitConfig>> observer) {
        observers.add(observer);
        observer.accept(getAuthorizationGroups());
    }

    /**
     * Removes an Observer from the list of additional observers.
     *
     * @param observer the consumer to remove
     */
    public static void removeListObserver(Consumer<? extends List<UnitConfig>> observer) {
        observers.remove(observer);
    }

    private static void init() {
        try {
            // register for updates
            Registries.getUnitRegistry().addDataObserver(unitRegistryObserver);

            // force update if data is available
            if (Registries.getUnitRegistry().isDataAvailable()) {
                updateAuthorizationGroups(Registries.getUnitRegistry().getUnitConfigs(UnitType.AUTHORIZATION_GROUP));
            }
            initialized = true;
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory("Could not update AuthorizationGroups!", ex, LOGGER);
        }
    }

    public static synchronized ObservableList<UnitConfig> getAuthorizationGroups() {
        if(!initialized) {
            init();
        }
        return authorizationGroups;
    }

    private static void updateAuthorizationGroups(final List<UnitConfig> authorizationGroupList) {
        Platform.runLater(() -> {
            authorizationGroups.clear();
            authorizationGroups.addAll(authorizationGroupList);
            observers.forEach(consumer -> consumer.accept(authorizationGroupList));
        });
    }

    public static StringConverter<UnitConfig> stringConverter(final List<UnitConfig> groups) {
        return new StringConverter<UnitConfig>() {
            @Override
            public String toString(final UnitConfig object) {
                try {
                    return LabelProcessor.getBestMatch(object.getLabel());
                } catch (NotAvailableException e) {
                    return "?";
                }
            }

            @Override
            public UnitConfig fromString(final String string) {
                for (final UnitConfig group : groups) {
                    if ((group.getLabel().equals(string))) {
                        return group;
                    }
                }
                return null;
            }
        };
    }

    public static void removeAuthorizationGroup(final UnitConfig group) throws InterruptedException, CouldNotPerformException {
        try {
            Registries.getUnitRegistry().removeUnitConfig(group);
        } catch (CouldNotPerformException ex) {
            throw new CouldNotPerformException("Could not remove authorization group.", ex);
        }
    }

    public static UnitConfig addAuthorizationGroup(final String groupName) throws InterruptedException, CouldNotPerformException {
        try {
            return tryAddAuthorizationGroup(groupName).get(5, TimeUnit.SECONDS);
        } catch (TimeoutException | ExecutionException ex) {
            throw new CouldNotPerformException("Could not add authorization group.", ex);
        }
    }

    public static Future<UnitConfig> tryAddAuthorizationGroup(final String groupName) {
        try {
            UnitConfig newGroup = UnitConfig.newBuilder()
                    .setLabel(LabelProcessor.buildLabel(groupName))
                    .setUnitType(UnitTemplateType.UnitTemplate.UnitType.AUTHORIZATION_GROUP)
                    .setAuthorizationGroupConfig(AuthorizationGroupConfig.newBuilder())
                    .build();

            return Registries.getUnitRegistry().registerUnitConfig(newGroup);
        } catch (NotAvailableException ex) {
            return FutureProcessor.canceledFuture(ex);
        }
    }

    public static void tryAddToGroup(final UnitConfig group, final String userId) throws CouldNotPerformException, InterruptedException {
        UnitConfig.Builder unitConfig = Registries.getUnitRegistry().getUnitConfigById(group.getId()).toBuilder();
        unitConfig.getAuthorizationGroupConfigBuilder().addMemberId(userId);
        Registries.getUnitRegistry().updateUnitConfig(unitConfig.build());
    }

    public static List<UnitConfig> getGroupsByUser(final String userId) {
        final List<UnitConfig> groupsWithUser = new ArrayList<>(authorizationGroups);
        groupsWithUser.removeIf(group -> !group.getAuthorizationGroupConfig().getMemberIdList().contains(userId));
        return groupsWithUser;
    }

    public static void tryRemoveFromGroup(final UnitConfig group, final String userId) throws CouldNotPerformException, InterruptedException {

        UnitConfig.Builder unitConfig = Registries.getUnitRegistry().getUnitConfigById(group.getId()).toBuilder();
        AuthorizationGroupConfig.Builder authorizationGroupConfig = unitConfig.getAuthorizationGroupConfigBuilder();

        ProtocolStringList members = authorizationGroupConfig.getMemberIdList();

        authorizationGroupConfig.clearMemberId();

        for (final String member : members) {
            if (!member.equals(userId)) {
                authorizationGroupConfig.addMemberId(member);
            }
        }

        Registries.getUnitRegistry().updateUnitConfig(unitConfig.build());
    }
}
