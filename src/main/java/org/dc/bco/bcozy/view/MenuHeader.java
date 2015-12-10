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

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

/**
 * Created by hoestreich on 11/10/15.
 */
public class MenuHeader extends HBox {

    private final ClockLabel clock;

    /**
     * Constructor for the MenuHeader.
     * @param height Height
     * @param width Width
     */
    public MenuHeader(final double height, final double width) {

        this.clock = new ClockLabel();
        this.setPickOnBounds(false);

        this.getChildren().add(this.clock);
        this.setAlignment(Pos.CENTER);

        this.setPrefHeight(height);
        this.setPrefWidth(width);

    }

}
