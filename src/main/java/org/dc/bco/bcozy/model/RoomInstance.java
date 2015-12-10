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

package org.dc.bco.bcozy.model;

import javafx.geometry.Point2D;

import javax.vecmath.Point3d;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class RoomInstance {

    private final List<Point2D> shape;
    private final String roomID;
    private boolean root;

    /**
     * The constructor.
     *
     * @param roomID The ID of the room
     */
    public RoomInstance(final String roomID) {
        this.shape = new ArrayList<>();
        this.roomID = roomID;
        this.root = false;
    }

    /**
     *
     * @return Returns the ID of the room.
     */
    public String getRoomID() {
        return roomID;
    }

    /**
     *
     * @return Returns the complete List of room vertices.
     */
    public List<Point2D> getVertices() {
        return shape;
    }

    /**
     * Adds a new vertex to the shape of the room.
     *
     * @param point The Coordinate
     */
    public void addVertex(final Point2D point) {
        shape.add(point);
    }

    /**
     * Adds a new vertex to the shape of the room.
     *
     * @param xPos The X Coordinate
     * @param yPos The Y Coordinate
     */
    public void addVertex(final double xPos, final double yPos) {
        shape.add(new Point2D(xPos, yPos));
    }

    /**
     * Adds a new vertex to the shape of the room.
     *
     * @param point The Coordinate (The z coordinate will be dismissed)
     */
    public void addVertex(final Point3d point) {
        shape.add(new Point2D(point.x, point.y));
    }

    /**
     * Deletes all vertices of this room.
     */
    public void deleteAllVertices() {
        shape.clear();
    }

    /**
     *
     * @return Returns the root parameter.
     */
    public boolean isRoot() {
        return root;
    }

    /**
     * Sets the root parameter.
     *
     * @param isRoot is root
     */
    public void setRoot(final boolean isRoot) {
        this.root = isRoot;
    }
}
