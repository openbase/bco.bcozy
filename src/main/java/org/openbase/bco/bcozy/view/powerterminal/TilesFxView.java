package org.openbase.bco.bcozy.view.powerterminal;

import eu.hansolo.tilesfx.Tile;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import org.openbase.bco.bcozy.model.LanguageSelection;

public class TilesFxView extends Tile {

    public static final int TILE_WIDTH = (int) Screen.getPrimary().getVisualBounds().getWidth();
    public static final int TILE_HEIGHT = (int) Screen.getPrimary().getVisualBounds().getHeight();

    public TilesFxView(String header, SkinType skinType, ReadOnlyStringProperty text) {
        super();
        setPrefSize(TILE_WIDTH, TILE_HEIGHT);
        setTitle(LanguageSelection.getLocalized(header));
        setSkinType(skinType);
        setTextAlignment(TextAlignment.RIGHT);
        textProperty().bind(text);
    }

}
