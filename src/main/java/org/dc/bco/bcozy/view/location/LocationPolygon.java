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

import java.util.List;

/**
 *  A Polygon that can be selected and has a RoomName.
 */
public abstract class LocationPolygon extends Polygon {

    private boolean selected;
    private final double centerX;
    private final double centerY;
    private final String locationLabel;
    private final String locationId;
    private final List<String> childIds;


    /**
     * Constructor for the LocationPolygon.
     * @param locationLabel The name of the location
     * @param locationId The ID of the location
     * @param childIds The ids of the children
     * @param points Points for the shape
     */
    public LocationPolygon(final String locationLabel, final String locationId,
                           final List<String> childIds, final double... points) {
        super(points);
        this.centerX = (super.getLayoutBounds().getMaxX() + super.getLayoutBounds().getMinX()) / 2;
        this.centerY = (super.getLayoutBounds().getMaxY() + super.getLayoutBounds().getMinY()) / 2;
        this.locationLabel = locationLabel;
        this.locationId = locationId;
        this.childIds = childIds;
        this.selected = false;


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

    /**
     * Method to get all the childIds from the Tile.
     * @return A list of childIds.
     */
    public List<String> getChildIds() {
        return childIds;
    }

    /**
     * Getter method for the selected boolean.
     * @return selected as a boolean value
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Setter method for the selected boolean.
     * @param selected as a boolean value
     */
    public void setSelected(final boolean selected) {
        this.selected = selected;
        this.changeStyleOnSelection(selected);
    }

    /**
     * Will be called when the selection of the Polygon has been toggled.
     * @param selected boolean for the selection status
     */
    protected abstract void changeStyleOnSelection(final boolean selected);

    /**
     * Will be called when the Polygon is constructed and can be used to apply specific styles.
     */
    protected abstract void setLocationStyle();
}
