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
import javafx.scene.shape.Polygon;

/**
 * A Polygon that represents different kinds of connections.
 */
public abstract class ConnectionPolygon extends Polygon {

    private boolean open;
    private final double centerX;
    private final double centerY;
    private final String connectionLabel;
    private final String connectionId;

    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    private final boolean horizontal;


    /**
     * Constructor for the ConnectionPolygon.
     *
     * @param connectionLabel The name of the location
     * @param connectionId    The ID of the location
     * @param points          Points for the shape
     */
    public ConnectionPolygon(final String connectionLabel, final String connectionId, final double... points) {
        super(points);
        this.centerX = (super.getLayoutBounds().getMaxX() + super.getLayoutBounds().getMinX()) / 2;
        this.centerY = (super.getLayoutBounds().getMaxY() + super.getLayoutBounds().getMinY()) / 2;
        this.connectionLabel = connectionLabel;
        this.connectionId = connectionId;
        this.open = false;

        final ObservableList<Double> pointList = super.getPoints();

        minX = Double.MAX_VALUE;
        maxX = Double.MIN_VALUE;
        minY = Double.MAX_VALUE;
        maxY = Double.MIN_VALUE;

        for (int i = 0; i < pointList.size(); i = i + 2) {
            minX = pointList.get(i) < minX ? pointList.get(i) : minX;
            maxX = pointList.get(i) > maxX ? pointList.get(i) : maxX;
        }

        for (int i = 1; i < pointList.size(); i = i + 2) {
            minY = pointList.get(i) < minY ? pointList.get(i) : minY;
            maxY = pointList.get(i) > maxY ? pointList.get(i) : maxY;
        }

        this.horizontal = (maxX - minX > maxY - minY);

        this.setConnectionStyle();
    }

    /**
     * Getter method for the X Coordinate of the center.
     *
     * @return x center as a double value
     */
    public double getCenterX() {
        return centerX;
    }

    /**
     * Getter method for the Y Coordinate of the center.
     *
     * @return y center as a double value
     */
    public double getCenterY() {
        return centerY;
    }

    /**
     * Getter for the connection label.
     *
     * @return the label as a String
     */
    public String getLocationLabel() {
        return connectionLabel;
    }

    /**
     * Getter for the connection id.
     *
     * @return the id as a String
     */
    public String getLocationId() {
        return connectionId;
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
     * Setter method for the open boolean.
     *
     * @param open as a boolean value
     */
    public void setSelected(final boolean open) {
        this.open = open;
        this.changeStyleOnOpening(open);
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
