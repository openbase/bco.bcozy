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
import org.dc.bco.bcozy.view.Constants;

/**
 *
 */
public class PassagePolygon extends ConnectionPolygon {


    /**
     * Constructor for the PassagePolygon.
     *
     * @param connectionLabel The label of the connection.
     * @param connectionId    The ID of the connection.
     * @param points          The vertices of the connection.
     */
    public PassagePolygon(final String connectionLabel, final String connectionId, final double... points) {
        super(connectionLabel, connectionId, points);

        final ObservableList<Double> pointList = super.getPoints();

        //The following Code adjusts passagePolygon to properly cut out room walls and yes...
        //It is unfortunately ugly as hell... :(
        //CHECKSTYLE.OFF: MagicNumber
        if (isHorizontal()) {
            if (pointList.get(0).equals(pointList.get(2))) { //Start Top Right or Bottom Left
                pointList.set(1, pointList.get(1) - Constants.ROOM_STROKE_WIDTH / 2);
                pointList.set(3, pointList.get(3) + Constants.ROOM_STROKE_WIDTH / 2);
                pointList.set(5, pointList.get(5) + Constants.ROOM_STROKE_WIDTH / 2);
                pointList.set(7, pointList.get(7) - Constants.ROOM_STROKE_WIDTH / 2);
                if (pointList.get(0) < pointList.get(4)) { //Start Bottom Left
                    pointList.set(0, pointList.get(0) + Constants.ROOM_STROKE_WIDTH / 2);
                    pointList.set(2, pointList.get(2) + Constants.ROOM_STROKE_WIDTH / 2);
                    pointList.set(4, pointList.get(4) - Constants.ROOM_STROKE_WIDTH / 2);
                    pointList.set(6, pointList.get(6) - Constants.ROOM_STROKE_WIDTH / 2);
                } else { // Start Top Right
                    pointList.set(0, pointList.get(0) - Constants.ROOM_STROKE_WIDTH / 2);
                    pointList.set(2, pointList.get(2) - Constants.ROOM_STROKE_WIDTH / 2);
                    pointList.set(4, pointList.get(4) + Constants.ROOM_STROKE_WIDTH / 2);
                    pointList.set(6, pointList.get(6) + Constants.ROOM_STROKE_WIDTH / 2);
                }
            } else { //Start Top Left or Bottom Right
                pointList.set(1, pointList.get(1) - Constants.ROOM_STROKE_WIDTH / 2);
                pointList.set(3, pointList.get(3) - Constants.ROOM_STROKE_WIDTH / 2);
                pointList.set(5, pointList.get(5) + Constants.ROOM_STROKE_WIDTH / 2);
                pointList.set(7, pointList.get(7) + Constants.ROOM_STROKE_WIDTH / 2);
                if (pointList.get(0) < pointList.get(4)) { //Start Top Left
                    pointList.set(0, pointList.get(0) + Constants.ROOM_STROKE_WIDTH / 2);
                    pointList.set(2, pointList.get(2) - Constants.ROOM_STROKE_WIDTH / 2);
                    pointList.set(4, pointList.get(4) - Constants.ROOM_STROKE_WIDTH / 2);
                    pointList.set(6, pointList.get(6) + Constants.ROOM_STROKE_WIDTH / 2);
                } else { // Start Bottom Right
                    pointList.set(0, pointList.get(0) - Constants.ROOM_STROKE_WIDTH / 2);
                    pointList.set(2, pointList.get(2) + Constants.ROOM_STROKE_WIDTH / 2);
                    pointList.set(4, pointList.get(4) + Constants.ROOM_STROKE_WIDTH / 2);
                    pointList.set(6, pointList.get(6) - Constants.ROOM_STROKE_WIDTH / 2);
                }
            }
        } else {
            if (pointList.get(1).equals(pointList.get(3))) { //Start Top Left or Bottom Right
                pointList.set(0, pointList.get(0) - Constants.ROOM_STROKE_WIDTH / 2);
                pointList.set(2, pointList.get(2) + Constants.ROOM_STROKE_WIDTH / 2);
                pointList.set(4, pointList.get(4) + Constants.ROOM_STROKE_WIDTH / 2);
                pointList.set(6, pointList.get(6) - Constants.ROOM_STROKE_WIDTH / 2);
                if (pointList.get(1) < pointList.get(5)) { //Start Top Left
                    pointList.set(1, pointList.get(1) + Constants.ROOM_STROKE_WIDTH / 2);
                    pointList.set(3, pointList.get(3) + Constants.ROOM_STROKE_WIDTH / 2);
                    pointList.set(5, pointList.get(5) - Constants.ROOM_STROKE_WIDTH / 2);
                    pointList.set(7, pointList.get(7) - Constants.ROOM_STROKE_WIDTH / 2);
                } else { // Start Bottom Right
                    pointList.set(1, pointList.get(1) - Constants.ROOM_STROKE_WIDTH / 2);
                    pointList.set(3, pointList.get(3) - Constants.ROOM_STROKE_WIDTH / 2);
                    pointList.set(5, pointList.get(5) + Constants.ROOM_STROKE_WIDTH / 2);
                    pointList.set(7, pointList.get(7) + Constants.ROOM_STROKE_WIDTH / 2);
                }
            } else { //Start Top Right or Bottom Left
                pointList.set(0, pointList.get(0) + Constants.ROOM_STROKE_WIDTH / 2);
                pointList.set(2, pointList.get(2) + Constants.ROOM_STROKE_WIDTH / 2);
                pointList.set(4, pointList.get(4) - Constants.ROOM_STROKE_WIDTH / 2);
                pointList.set(6, pointList.get(6) - Constants.ROOM_STROKE_WIDTH / 2);
                if (pointList.get(1) < pointList.get(5)) { //Start Top Right
                    pointList.set(1, pointList.get(1) + Constants.ROOM_STROKE_WIDTH / 2);
                    pointList.set(3, pointList.get(3) - Constants.ROOM_STROKE_WIDTH / 2);
                    pointList.set(5, pointList.get(5) - Constants.ROOM_STROKE_WIDTH / 2);
                    pointList.set(7, pointList.get(7) + Constants.ROOM_STROKE_WIDTH / 2);
                } else { // Start Bottom Left
                    pointList.set(1, pointList.get(1) - Constants.ROOM_STROKE_WIDTH / 2);
                    pointList.set(3, pointList.get(3) + Constants.ROOM_STROKE_WIDTH / 2);
                    pointList.set(5, pointList.get(5) + Constants.ROOM_STROKE_WIDTH / 2);
                    pointList.set(7, pointList.get(7) - Constants.ROOM_STROKE_WIDTH / 2);
                }
            }
        }
        //CHECKSTYLE.ON: MagicNumber
    }


    @Override
    protected void setConnectionStyle() {
        this.setMainColor(Constants.PASSAGE_FILL);
        this.setStrokeWidth(0);
        this.setMouseTransparent(true);
    }

    @Override
    protected void changeStyleOnOpening(final boolean open) {
        // nothing to do here...
    }

    /**
     * Will be called when either the main or the custom color changes.
     * The initial values for both colors are Color.TRANSPARENT.
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
