package org.openbase.bco.bcozy.view.powerterminal;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import org.openbase.bco.bcozy.jp.JPLanguage;
import org.openbase.bco.bcozy.model.LanguageSelection;
import org.openbase.bco.dal.lib.layer.unit.Unit;
import org.openbase.type.domotic.unit.UnitConfigType;

import java.util.Map;

/**
 * CellFactory that binds the textproperty of created cells to a localized enum representation
 * @param <T> Enum contained in the Cells
 */
public class LocalizedUnitCellFactory<T extends UnitConfigType.UnitConfig> implements Callback<ListView<T>, ListCell<T>> {

    public final static Map<String, String> LOCATION_ALIAS_LOCALIZATION_IDENTIFIER_MAP = Map.of(
            "Location-20", "living",
            "Location-26", "bathroom",
            "Location-12", "robotRoom",
            "Location-1", "hallway",
            "Location-15", "kitchen"
    );


    /**
     * Constructor
     */
    public LocalizedUnitCellFactory() {
    }

    @Override
    public ListCell<T> call(ListView<T> tListView) {
        return new ListCell<>() {

            @Override
            protected void updateItem(final T item, final boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    this.textProperty().bind(LanguageSelection.getProperty(
                            LOCATION_ALIAS_LOCALIZATION_IDENTIFIER_MAP.get(item.getAlias(0))));
                } else {
                    this.textProperty().unbind();
                }
            }
        };
    }
}
