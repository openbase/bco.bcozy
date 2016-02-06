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

package org.dc.bco.bcozy.view.location;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import org.dc.bco.bcozy.view.SVGIcon;

/**
 *
 */
public class UnitButton extends Button {

    /**
     * Creates a button with an empty string for its label.
     * @param svgIcon The Icon displayed in the button
     * @param onActionHandler The ActionHandler that gets active when the button is clicked
     */
    public UnitButton(final SVGIcon svgIcon, final EventHandler<ActionEvent> onActionHandler) {
        this.setGraphic(svgIcon);
        this.setOnAction(onActionHandler);
    }
}
