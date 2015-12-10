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

import javafx.scene.shape.Polygon;

/**
 *  A Polygon that can be selected and has a RoomName.
 */
public abstract class LocationPolygon extends Polygon {

    private final double centerX;
    private final double centerY;
    private boolean selected;
    private final String locationLabel;
    private final String locationId;

    /**
     * Constructor for the LocationPolygon.
     * @param points Points for the shape
     * @param locationLabel The name of the location
     * @param locationId The ID of the location
     */
    public LocationPolygon(final String locationLabel, final String locationId, final double... points) {
        super(points);
        this.centerX = (super.getLayoutBounds().getMaxX() + super.getLayoutBounds().getMinX()) / 2;
        this.centerY = (super.getLayoutBounds().getMaxY() + super.getLayoutBounds().getMinY()) / 2;
        this.selected = false;
        this.locationLabel = locationLabel;
        this.locationId = locationId;

        this.setLocationStyle();
    }

    /**
     * Getter method for the X Coordinate of the center.
     * @return x center as a double value
     */
    public double getCenterX() {
        return centerX;
    }

    /**
     * Getter method for the Y Coordinate of the center.
     * @return y center as a double value
     */
    public double getCenterY() {
        return centerY;
    }

    /**
     * Getter method for the selection status.
     * @return selection status as a boolean
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Toggle the selection of the LocationPolygon.
     */
    public void toggleSelected() {
        this.selected = !this.selected;
        this.setLocationStyle();
    }

    /**
     * Getter for the location label.
     * @return the label as a String
     */
    public String getLocationLabel() {
        return locationLabel;
    }

    /**
     * Getter for the location id.
     * @return the id as a String
     */
    public String getLocationId() {
        return locationId;
    }

    abstract void setLocationStyle();

}
