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
import org.dc.bco.bcozy.view.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  A Polygon that represents different kinds of connections.
 */
public abstract class ConnectionPolygon extends Polygon {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionPolygon.class);

    private boolean open;
    private final double centerX;
    private final double centerY;
    private final String connectionLabel;
    private final String connectionId;

    private final boolean horizontal;


    /**
     * Constructor for the ConnectionPolygon.
     * @param connectionLabel The name of the location
     * @param connectionId The ID of the location
     * @param points Points for the shape
     */
    public ConnectionPolygon(final String connectionLabel, final String connectionId, final double... points) {super(points);
        this.centerX = (super.getLayoutBounds().getMaxX() + super.getLayoutBounds().getMinX()) / 2;
        this.centerY = (super.getLayoutBounds().getMaxY() + super.getLayoutBounds().getMinY()) / 2;
        this.connectionLabel = connectionLabel;
        this.connectionId = connectionId;
        this.open = false;

        ObservableList<Double> pointList = super.getPoints();

        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;

        for (int i = 0; i < pointList.size(); i = i + 2) {
            minX = pointList.get(i) < minX ? pointList.get(i) : minX;
            maxX = pointList.get(i) > maxX ? pointList.get(i) : maxX;
        }

        for (int i = 1; i < pointList.size(); i = i + 2) {
            minY = pointList.get(i) < minY ? pointList.get(i) : minY;
            maxY = pointList.get(i) > maxY ? pointList.get(i) : maxY;
        }

        this.horizontal = (maxX - minX > maxY - minY);

        if (this.horizontal) {
            for (int i = 1; i < pointList.size(); i = i + 2) {
                if (pointList.get(i) == minY) {
                    pointList.set(i, minY - Constants.ROOM_STROKE_WIDTH);
                } else {
                    pointList.set(i, maxY + Constants.ROOM_STROKE_WIDTH);
                }
            }
        } else {
            for (int i = 0; i < pointList.size(); i = i + 2) {
                if (pointList.get(i) == minX) {
                    pointList.set(i, minX - Constants.ROOM_STROKE_WIDTH);
                } else {
                    pointList.set(i, maxX + Constants.ROOM_STROKE_WIDTH);
                }
            }
        }

        //TODO: Size need to be raised somehow to fully overlap room walls

        this.setConnectionStyle();
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
     * Getter for the connection label.
     * @return the label as a String
     */
    public String getLocationLabel() {
        return connectionLabel;
    }

    /**
     * Getter for the connection id.
     * @return the id as a String
     */
    public String getLocationId() {
        return connectionId;
    }

    /**
     * Getter method for the open boolean.
     * @return open as a boolean value
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * Setter method for the open boolean.
     * @param open as a boolean value
     */
    public void setSelected(final boolean open) {
        this.open = open;
        this.changeStyleOnOpening(open);
    }

    /**
     * Will be called when the open value of the Polygon has been toggled.
     * @param open boolean for the open status
     */
    protected abstract void changeStyleOnOpening(final boolean open);

    /**
     * Will be called when the Polygon is constructed and can be used to apply specific styles.
     */
    protected abstract void setConnectionStyle();
}
