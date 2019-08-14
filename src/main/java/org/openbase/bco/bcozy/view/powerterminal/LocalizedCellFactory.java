package org.openbase.bco.bcozy.view.powerterminal;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.util.Objects;
import java.util.function.Function;

/**
 * CellFactory that binds the textproperty of created cells to a localized enum representation
 *
 * @param <T> Enum contained in the Cells
 */
public class LocalizedCellFactory<T> implements Callback<ListView<T>, ListCell<T>> {


    private final Function<T, ReadOnlyStringProperty> localization;

    /**
     * Constructor
     *
     * @param localization Function that returns a string property describing the localized enum
     */
    public LocalizedCellFactory(final Function<T, ReadOnlyStringProperty> localization) {
        this.localization = Objects.requireNonNull(localization);
    }

    @Override
    public ListCell<T> call(ListView<T> tListView) {
        return new ListCell<>() {

            @Override
            protected void updateItem(final T item, final boolean empty) {
                super.updateItem(item, empty);
                if (!empty && item != null) {
                    this.textProperty().bind(localization.apply(item));
                } else {
                    this.textProperty().unbind();
                }
            }
        };
    }
}
