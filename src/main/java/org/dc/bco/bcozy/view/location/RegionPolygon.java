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
public class RegionPolygon extends LocationPolygon {

    private boolean selectable;

    /**
     * The Constructor for a RegionPolygon.
     *
     * @param locationLabel The label of the location
     * @param locationId The id of the location
     * @param points The vertices of the location
     */
    public RegionPolygon(final String locationLabel, final String locationId, final double... points) {
        super(locationLabel, locationId, points);
        this.selectable = false;
    }

    @Override
    protected void setLocationStyle() {
        this.setFill(Constants.REGION_FILL);
        this.setStroke(Color.WHITE);
        this.setStrokeWidth(0);
        this.setMouseTransparent(true);
    }

    @Override
    protected void changeStyleOnSelection(final boolean selected) {
        /**
         * No functionality needed here for now.
         */
    }

    /**
     * This method should be called to change the selectable status.
     *
     * @param selectable Whether the Region should be selectable or not.
     */
    public void changeStyleOnSelectable(final boolean selectable) {
        if (selectable) {
            this.selectable = false;
            this.setMouseTransparent(false);
        } else {
            this.selectable = true;
            this.setMouseTransparent(true);
        }
    }

    /**
     * Getter for the selectable status.
     *
     * @return The selectable status.
     */
    public boolean isSelectable() {
        return selectable;
    }

    /**
     * This method should be called when the mouse enters the polygon.
     */
    public void mouseEntered() {
        this.setStrokeWidth(Constants.ROOM_STROKE_WIDTH_MOUSE_OVER);
    }

    /**
     * This method should be called when the mouse leaves the polygon.
     */
    public void mouseLeft() {
        this.setStrokeWidth(Constants.ROOM_STROKE_WIDTH);
    }
}
