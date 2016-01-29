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

import javafx.collections.ObservableList;

/**
 * A Polygon that represents different kinds of connections.
 */
public abstract class ConnectionPolygon extends AbstractPolygon {

    private boolean open;

    private final double minX;
    private final double maxX;
    private final double minY;
    private final double maxY;
    private final boolean horizontal;

    /**
     * Constructor for the ConnectionPolygon.
     * @param connectionLabel The name of the location
     * @param connectionId    The ID of the location
     * @param points          Points for the shape
     */
    public ConnectionPolygon(final String connectionLabel, final String connectionId, final double... points) {
        super(connectionLabel, connectionId, points);
        this.open = false;

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
     * Getter method for the open boolean.
     *
     * @return open as a boolean value
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * Setter method for the open boolean.
     *
     * @param open as a boolean value
     */
    public void setOpen(final boolean open) {
        this.open = open;
        this.changeStyleOnOpening(open);
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
     * Will be called when the open value of the Polygon has been toggled.
     *
     * @param open boolean for the open status
     */
    protected abstract void changeStyleOnOpening(final boolean open);

    /**
     * Will be called when the Polygon is constructed and can be used to apply specific styles.
     */
    protected abstract void setConnectionStyle();
}
