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
package org.openbase.bco.bcozy.view.location;

import javafx.scene.paint.Color;
import org.openbase.bco.bcozy.view.Constants;

import org.openbase.jul.exception.InstantiationException;
import rst.domotic.unit.location.LocationDataType.LocationData;

/**
 *
 */
public class ZonePolygon extends LocationPolygon {

    /**
     * The Constructor for a ZonePolygon.
     *
     * @param points The vertices of the location
     * @throws org.openbase.jul.exception.InstantiationException
     */
    public ZonePolygon(final double... points) throws InstantiationException {
        super(points);
    }

    @Override
    public void applyDataUpdate(LocationData unitData) {
    }

    @Override
    protected void setLocationStyle() {
        this.setMainColor(Constants.ZONE_FILL);
        this.setStroke(Color.WHITE);
        this.setStrokeWidth(Constants.ROOM_STROKE_WIDTH);
        this.setMouseTransparent(true);
    }

    @Override
    protected void changeStyleOnSelection(final boolean selected) {
        /**
         * No functionality needed here for now.
         */
    }

    /**
     * Will be called when either the main or the custom color changes.
     * The initial values for both colors are Color.TRANSPARENT.
     *
     * @param mainColor The main color
     * @param customColor The custom color
     */
    @Override
    protected void onColorChange(final Color mainColor, final Color customColor) {
        if (customColor.equals(Color.TRANSPARENT)) {
            this.setFill(mainColor);
        } else {
            this.setFill(mainColor.interpolate(customColor, CUSTOM_COLOR_WEIGHT));
        }
    }
}
