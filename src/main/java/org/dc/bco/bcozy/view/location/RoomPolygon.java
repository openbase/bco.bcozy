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
import javafx.scene.shape.Polygon;

/**
 * Created by julian on 05.11.15.
 */
public class RoomPolygon extends Polygon {

    private final double centerX;
    private final double centerY;
    private boolean selected;
    private String roomName;

    /**
     * Constructor for the RoomPolygon.
     * @param points Points for the shape
     */
    public RoomPolygon(final String roomName, final double... points) {
        super(points);
        this.setFill(Color.TRANSPARENT);
        this.setStroke(Color.WHITE);
        //CHECKSTYLE.OFF: MagicNumber
        this.setStrokeWidth(2.5);
        //CHECKSTYLE.ON: MagicNumber
        this.centerX = (super.getLayoutBounds().getMaxX() + super.getLayoutBounds().getMinX()) / 2;
        this.centerY = (super.getLayoutBounds().getMaxY() + super.getLayoutBounds().getMinY()) / 2;
        this.selected = false;
        this.roomName = roomName;
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
     * Toggle the selection of the RoomPolygon.
     */
    public void toggleSelected() {
        if (this.selected) {
            this.setFill(Color.TRANSPARENT);
            this.selected = false;
        } else {
            //CHECKSTYLE.OFF: MagicNumber
            this.setFill(new Color(0.8, 0.8, 0.8, 0.4));
            //CHECKSTYLE.ON: MagicNumber
            this.selected = true;
        }
    }

    /**
     * Getter for the roomName.
     * @return the roomName as a String
     */
    public String getRoomName() {
        return roomName;
    }
}
