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

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

/**
 * Created by hoestreich on 11/18/15.
 */
public class RoomContextInfo extends AnchorPane {

    private final Label roomInfo;

    /**
     * Constructor for the RoomContextInfo UI Element.
     */
    public RoomContextInfo() {

        roomInfo = new Label("No room selected.");

        this.getChildren().add(roomInfo);

        this.setLeftAnchor(roomInfo, Constants.INSETS);
        this.setRightAnchor(roomInfo, Constants.INSETS);
        this.setTopAnchor(roomInfo, Constants.INSETS);
        this.setBottomAnchor(roomInfo, Constants.INSETS);
    }

}
