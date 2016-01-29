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
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import org.dc.bco.bcozy.view.Constants;

/**
 *
 */
public class DoorPolygon extends ConnectionPolygon {

    /**
     * Constructor for the DoorPolygon.
     *
     * @param connectionLabel The label of the connection.
     * @param connectionId    The ID of the connection.
     * @param points          The vertices of the connection.
     */
    public DoorPolygon(final String connectionLabel, final String connectionId, final double... points) {
        super(connectionLabel, connectionId, points);

        final ObservableList<Double> pointList = super.getPoints();

        if (isHorizontal()) {
            for (int i = 1; i < pointList.size(); i = i + 2) {
                if (pointList.get(i) == getMinY()) {
                    pointList.set(i, getMinY() - Constants.ROOM_STROKE_WIDTH / 2);
                } else {
                    pointList.set(i, getMaxY() + Constants.ROOM_STROKE_WIDTH / 2);
                }
            }
        } else {
            for (int i = 0; i < pointList.size(); i = i + 2) {
                if (pointList.get(i) == getMinX()) {
                    pointList.set(i, getMinX() - Constants.ROOM_STROKE_WIDTH / 2);
                } else {
                    pointList.set(i, getMaxX() + Constants.ROOM_STROKE_WIDTH / 2);
                }
            }
        }
    }



    @Override
    protected void setConnectionStyle() {
        this.setMainColor(Color.TRANSPARENT);
        this.setStroke(Color.WHITE);
        this.getStrokeDashArray().addAll(Constants.DOOR_DASH_WIDTH, Constants.DOOR_DASH_WIDTH * 2);
        this.setStrokeWidth(Constants.ROOM_STROKE_WIDTH);
        this.setStrokeType(StrokeType.INSIDE);
        this.setMouseTransparent(true);
    }

    @Override
    protected void changeStyleOnOpening(final boolean open) {
        // TODO: To be implemented...
    }

    /**
     * Will be called when either the main or the custom color changes.
     * @param mainColor   The main color
     * @param customColor The custom color
     */
    @Override
    protected void onColorChange(final Color mainColor, final Color customColor) {
        if (customColor.equals(Color.TRANSPARENT)) {
            this.setFill(mainColor);
        } else {
            this.setFill(mainColor.interpolate(customColor, CUSTOM_COLOR_WEIGHT));
        }
    }
}
