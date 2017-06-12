/**
 * ==================================================================
 *
 * This file is part of org.openbase.bco.bcozy.
 *
 * org.openbase.bco.bcozy is free software: you can redistribute it and modify
 * it under the terms of the GNU General Public License (Version 3)
 * as published by the Free Software Foundation.
 *
 * org.openbase.bco.bcozy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with org.openbase.bco.bcozy. If not, see <http://www.gnu.org/licenses/>.
 * ==================================================================
 */
package com.jfoenix.skins;

import java.awt.Toolkit;
import javafx.beans.property.ObjectProperty;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Window;

/**
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class JULFXColorPicker extends HBox {

    private final JFXCustomColorPickerDialog colorPicker;

    public JULFXColorPicker(final Window owner) {
        colorPicker = new JFXCustomColorPickerDialog(owner);
        minWidthProperty().set(230);
        minHeightProperty().set(230);
        getChildren().add(colorPicker);
        colorPicker.setCurrentColor(Color.GREENYELLOW);
        colorPicker.setCurrentColor(Color.RED);
        colorPicker.setCustomColor(Color.GREEN);
        setOnMouseMoved(e -> {
            if (e.isPrimaryButtonDown()) {
                System.err.println("getCustomColor:" + colorPicker.getCustomColor());
                System.err.println("getCurrentColor:" + colorPicker.getCurrentColor());
            }
        });

        setOnMousePressed(e -> Toolkit.getDefaultToolkit().beep());
        colorPicker.customColorProperty().addListener((observable, c, nc) -> {

        });
    }

    public ObjectProperty<Color> selectedColorProperty() {
        return colorPicker.customColorProperty();
    }
}
