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
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import org.dc.bco.bcozy.view.Constants;

/**
 *
 */
public class WindowPolygon extends ConnectionPolygon {


    /**
     * Constructor for the WindowPolygon.
     *
     * @param connectionLabel The label of the connection.
     * @param connectionId    The ID of the connection.
     * @param points          The vertices of the connection.
     */
    public WindowPolygon(final String connectionLabel, final String connectionId, final double... points) {
        super(connectionLabel, connectionId, points);
    }

    @Override
    protected void setConnectionStyle() {
        this.setStroke(Color.WHITE);
        this.setStrokeWidth(Constants.ROOM_STROKE_WIDTH);
        this.setMouseTransparent(true);

        final Stop[] stops = new Stop[]{new Stop(0, Color.TRANSPARENT),
                new Stop(0.2, Color.TRANSPARENT),
                new Stop(0.5, Constants.WINDOW_EFFECT),
                new Stop(0.8, Color.TRANSPARENT),
                new Stop(1, Color.TRANSPARENT)};
        final LinearGradient lg1 = new LinearGradient(0, 0, 6, 6, false, CycleMethod.REPEAT, stops);
        this.setFill(lg1);
    }

    @Override
    protected void changeStyleOnOpening(final boolean open) {
        // TODO: To be implemented...
    }
}
