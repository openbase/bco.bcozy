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

import javafx.scene.layout.VBox;

/**
 * Created by hoestreich on 11/10/15.
 */
public class ContextMenu extends VBox {

    private final RoomContextInfo roomContextInfo;

    /**
     * Constructor for the ContextMenu.
     * @param height Height
     * @param width Width
     */
    public ContextMenu(final double height, final double width) {

        this.setMinHeight(height);
        this.setMinWidth(width);
        //this.getStyleClass().add("linear-gradient-right-to-left");
        this.getStyleClass().add("dropshadow-left-bg");

        roomContextInfo = new RoomContextInfo();
    }

    /**
     * Getter for the roomContextInfo Element.
     * @return Element Instance
     */
    public RoomContextInfo getRoomContextInfo() {
        return roomContextInfo;
    }
}
