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

import javafx.scene.paint.Color;
import org.dc.bco.bcozy.view.Constants;

/**
 *
 */
public class TilePolygon extends LocationPolygon {

    /**
     * The Constructor for a TilePolygon.
     *
     * @param label The label of the location
     * @param id The id of the location
     * @param points The vertices of the location
     */
    public TilePolygon(final String label, final String id, final double... points) {
        super(label, id, points);
    }

    @Override
    void setLocationStyle() {
        this.setFill(Color.TRANSPARENT);
        this.setStroke(Color.WHITE);
        this.setStrokeWidth(Constants.ROOM_STROKE_WIDTH);

        if (this.isSelected()) {
            //CHECKSTYLE.OFF: MagicNumber
            this.setFill(new Color(0.8, 0.8, 0.8, 0.4));
            //CHECKSTYLE.ON: MagicNumber
        } else {
            this.setFill(Color.TRANSPARENT);
        }
    }
}
