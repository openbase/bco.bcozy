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
package org.dc.bco.bcozy;

import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Created by hoestreich on 11/10/15.
 */
public class InfoFooter extends HBox {

    /**
     * Constructor for the InfoFooter.
     * @param height Height
     * @param width Width
     */
    public InfoFooter(final double height, final double width) {
        final Rectangle placeholder;
        placeholder = new Rectangle(width, height);
        placeholder.setFill(Color.CADETBLUE);
        this.getChildren().add(placeholder);
    }
}
