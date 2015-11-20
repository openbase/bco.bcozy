/**
 * ==================================================================
 *
 * This file is part of org.dc.bco.bcozy.
 *
 * org.dc.bco.bcozy is free software: you can redistribute it and modify
 * it under the terms of the GNU General Public License (Version 3)
 * as published by the Free Software Foundation.
 *
 * org.dc.bco.bcozy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with org.dc.bco.bcozy. If not, see <http://www.gnu.org/licenses/>.
 * ==================================================================
 */
package org.dc.bco.bcozy.view;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
//import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.scene.text.Text;


/**
 * Created by hoestreich on 11/10/15.
 */
public class MenuHeader extends HBox {

    private final Button mainMenuBtn;

    /**
     * Constructor for the MenuHeader.
     * @param height Height
     * @param width Width
     */
    public MenuHeader(final double height, final double width) {

        final Text iconText = GlyphsDude.createIcon(FontAwesomeIcon.BARS, "2em");
        this.mainMenuBtn = new Button();
        this.mainMenuBtn.setGraphic(iconText);

        this.getChildren().add(this.mainMenuBtn);

        this.setPrefHeight(height);
        this.setPrefWidth(width);
        this.getStyleClass().add("dropshadow-bottom-bg");

    }

    /**
     * Getter Method for the mainMenuBtn.
     * @return Button Instance.
     */
    public Button getMainMenuBtn() {
        return mainMenuBtn;
    }
}
