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

import javafx.collections.ObservableList;
import org.openbase.bco.dal.remote.layer.unit.connection.ConnectionRemote;
import org.openbase.jul.exception.InstantiationException;
import rst.domotic.unit.connection.ConnectionDataType.ConnectionData;

/**
 * A Polygon that represents different kinds of connections.
 *
 * @author julian
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public abstract class ConnectionPolygon extends AbstractUnitPolygon<ConnectionData, ConnectionRemote> {

    private final double minX;
    private final double maxX;
    private final double minY;
    private final double maxY;
    private final boolean horizontal;

    /**
     * Constructor for the ConnectionPolygon.
     *
     * @param points Points for the shape
     * @throws org.openbase.jul.exception.InstantiationException
     */
    public ConnectionPolygon(final double... points) throws InstantiationException {
        super(points);

        final ObservableList<Double> pointList = super.getPoints();

        double tempMinX = Double.MAX_VALUE;
        double tempMaxX = Double.MIN_VALUE;
        double tempMinY = Double.MAX_VALUE;
        double tempMaxY = Double.MIN_VALUE;

        for (int i = 0; i < pointList.size(); i = i + 2) {
            tempMinX = pointList.get(i) < tempMinX ? pointList.get(i) : tempMinX;
            tempMaxX = pointList.get(i) > tempMaxX ? pointList.get(i) : tempMaxX;
        }

        for (int i = 1; i < pointList.size(); i = i + 2) {
            tempMinY = pointList.get(i) < tempMinY ? pointList.get(i) : tempMinY;
            tempMaxY = pointList.get(i) > tempMaxY ? pointList.get(i) : tempMaxY;
        }

        minX = tempMinX;
        maxX = tempMaxX;
        minY = tempMinY;
        maxY = tempMaxY;
        this.horizontal = (maxX - minX > maxY - minY);

        this.setConnectionStyle();
    }

    /**
     * Getter method for the minX value.
     *
     * @return minX
     */
    protected double getMinX() {
        return minX;
    }

    /**
     * Getter method for the maxX value.
     *
     * @return maxX
     */
    protected double getMaxX() {
        return maxX;
    }

    /**
     * Getter method for the minY value.
     *
     * @return minY
     */
    protected double getMinY() {
        return minY;
    }

    /**
     * Getter method for the maxY value.
     *
     * @return maxY
     */
    protected double getMaxY() {
        return maxY;
    }

    /**
     * Getter method for the horizontal value.
     *
     * @return horizontal as a boolean value
     */
    protected boolean isHorizontal() {
        return horizontal;
    }

    /**
     * Will be called when the Polygon is constructed and can be used to apply specific styles.
     */
    protected abstract void setConnectionStyle();
}
