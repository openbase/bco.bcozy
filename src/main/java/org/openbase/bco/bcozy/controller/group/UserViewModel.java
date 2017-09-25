package org.openbase.bco.bcozy.controller.group;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import rst.domotic.unit.UnitConfigType;

/**
 * ViewModel, used to display user in tables.
 *
 * @author vdasilva
 */
public class UserViewModel {
    private final ReadOnlyStringProperty id;
    private final ReadOnlyStringProperty name;

    public UserViewModel(UnitConfigType.UnitConfig unitConfig) {
        this.id = new SimpleStringProperty(unitConfig.getId());
        this.name = new SimpleStringProperty(unitConfig.getUserConfig().getUserName());
    }

    public String getId() {
        return id.get();
    }

    public ReadOnlyStringProperty idProperty() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public ReadOnlyStringProperty nameProperty() {
        return name;
    }
}
