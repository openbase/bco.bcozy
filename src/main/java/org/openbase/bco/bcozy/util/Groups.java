package org.openbase.bco.bcozy.util;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.unit.UnitConfigType;
import rst.domotic.unit.UnitTemplateType;
import rst.domotic.unit.authorizationgroup.AuthorizationGroupConfigType;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author vdasilva
 */
public final class Groups {

    private static final Logger LOGGER = LoggerFactory.getLogger(Groups.class);

    public static ObservableList<UnitConfigType.UnitConfig> getGroups() {
        ObservableList<UnitConfigType.UnitConfig> groups = FXCollections.observableArrayList();

        if (Registries.isDataAvailable()) {
            try {
                setGroups(Registries.getUserRegistry().getAuthorizationGroupConfigs(), groups);
            } catch (CouldNotPerformException ex) {
                ExceptionPrinter.printHistory(ex, LOGGER);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }

        try {
            Registries.getUserRegistry().addDataObserver((observable, userRegistryData) ->
                    setGroups(userRegistryData.getAuthorizationGroupUnitConfigList(), groups)
            );
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        return groups;

    }

    private static void setGroups(List<UnitConfigType.UnitConfig> newGroups,
                                  ObservableList<UnitConfigType.UnitConfig> groups) {

        Platform.runLater(() -> {
            groups.clear();
            groups.addAll(newGroups);
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

    public static void removeGroup(UnitConfigType.UnitConfig group) throws InterruptedException {
        try {
            Registries.getUserRegistry().removeAuthorizationGroupConfig(group);
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER);
        }
    }

    public static boolean addGroup(String groupName) throws InterruptedException {
        try {
            tryAddGroup(groupName).get(5, TimeUnit.SECONDS);
            return true;
        } catch (CouldNotPerformException | TimeoutException | ExecutionException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER);
        }
        return false;
    }

    public static Future<UnitConfigType.UnitConfig> tryAddGroup(String groupName)
            throws CouldNotPerformException, InterruptedException {

        UnitConfigType.UnitConfig newGroup = UnitConfigType.UnitConfig.newBuilder()
                .setLabel(groupName)
                .setType(UnitTemplateType.UnitTemplate.UnitType.AUTHORIZATION_GROUP)
                .setAuthorizationGroupConfig(AuthorizationGroupConfigType.AuthorizationGroupConfig.newBuilder())
                .build();

        return Registries.getUserRegistry().registerAuthorizationGroupConfig(newGroup);
    }
}
