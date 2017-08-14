package org.openbase.bco.bcozy.util;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import rst.domotic.unit.UnitConfigType;

import java.util.List;

/**
 * @author vdasilva
 */
public final class Groups {

    public static ObservableList<UnitConfigType.UnitConfig> getGroups() {
        ObservableList<UnitConfigType.UnitConfig> groups = FXCollections.observableArrayList();

        try {
            setGroups(Registries.getUserRegistry().getAuthorizationGroupConfigs(), groups);
        } catch (CouldNotPerformException | InterruptedException ex) {
            // not synchronized yet...
            // ex.printStackTrace();
        }

        try {
            Registries.getUserRegistry().addDataObserver((observable, userRegistryData) ->
                    setGroups(userRegistryData.getAuthorizationGroupUnitConfigList(), groups)
            );
        } catch (InterruptedException | CouldNotPerformException ex) {
            ex.printStackTrace();
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


}
