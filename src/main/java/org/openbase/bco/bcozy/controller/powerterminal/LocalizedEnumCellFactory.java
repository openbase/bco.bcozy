package org.openbase.bco.bcozy.controller.powerterminal;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.util.Objects;
import java.util.function.Function;

/**
 * CellFactory that binds the textproperty of created cells to a localized enum representation
 * @param <T> Enum contained in the Cells
 */
public class LocalizedEnumCellFactory<T extends Enum> implements Callback<ListView<T>, ListCell<T>> {


    private final Function<String, ReadOnlyStringProperty> localization;

    /**
     * Constructor
     * @param localization Function that returns a string property describing the localized enum
     */
    public LocalizedEnumCellFactory(final Function<String, ReadOnlyStringProperty> localization) {
        this.localization = Objects.requireNonNull(localization);
    }

    @Override
    public ListCell<T> call(ListView<T> tListView) {
        return new ListCell<>() {

            @Override
            protected void updateItem(final T item, final boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    this.textProperty().bind(localization.apply(item.name()));
                } else {
                    this.textProperty().unbind();
                }
            }
        };
    }
}
