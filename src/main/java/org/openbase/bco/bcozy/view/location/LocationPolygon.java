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

import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;

import java.util.List;
import org.openbase.bco.dal.remote.unit.location.LocationRemote;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.exception.NotAvailableException;
import rst.domotic.unit.location.LocationDataType.LocationData;

/**
 * A Polygon that represents different kinds of locations.
 */
public abstract class LocationPolygon extends AbstractUnitPolygon<LocationData, LocationRemote> {

    private boolean selected;
    private Shape cuttingShape;

    /**
     * Constructor for the LocationPolygon.
     *
     * @param points Points for the shape
     * @throws org.openbase.jul.exception.InstantiationException
     */
    public LocationPolygon(final double... points) throws InstantiationException {
        super(points);
        this.selected = false;
        this.cuttingShape = this;
        this.setLocationStyle();
    }

    /**
     * Method to get all the childIds from the Tile.
     *
     * @return A list of childIds.
     * @throws org.openbase.jul.exception.NotAvailableException
     */
    public List<String> getChildIds() throws NotAvailableException {
        try {
            return getUnitRemote().getConfig().getLocationConfig().getChildIdList();
        } catch (NullPointerException ex) {
            throw new NotAvailableException("ChildIdList", ex);
        }
    }

    /**
     * Getter method for the selected boolean.
     *
     * @return selected as a boolean value
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Setter method for the selected boolean.
     *
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
     *
     * @param selected boolean for the selection status
     */
    protected abstract void changeStyleOnSelection(final boolean selected);

    /**
     * Will be called when the Polygon is constructed and can be used to apply specific styles.
     */
    protected abstract void setLocationStyle();

}
