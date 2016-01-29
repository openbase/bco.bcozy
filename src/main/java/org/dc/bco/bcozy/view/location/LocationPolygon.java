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

import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;

import java.util.List;

/**
 *  A Polygon that represents different kinds of locations.
 */
public abstract class LocationPolygon extends AbstractPolygon {

    private boolean selected;
    private Shape cuttingShape;

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
        super(locationLabel, locationId, points);
        this.childIds = childIds;
        this.selected = false;
        this.cuttingShape = this;

        this.setLocationStyle();
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
     * Will cut an additional Shape out of the polygon.
     *
     * @param additionalCuttingShape The shape to be cut out
     */
    public void addCuttingShape(final Shape additionalCuttingShape) {
        this.cuttingShape = Path.subtract(this.cuttingShape, additionalCuttingShape);
        this.setClip(this.cuttingShape);
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
