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

package org.dc.bco.bcozy.controller;

import javafx.geometry.Point2D;
import org.dc.bco.bcozy.view.location.LocationPane;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class LocationController {

    private final LocationPane locationPane;

    /**
     * The constructor.
     * @param locationPane the location pane
     */
    public LocationController(final LocationPane locationPane) {
        this.locationPane = locationPane;

        //CHECKSTYLE.OFF: MagicNumber
        final List<Point2D> vertices = new ArrayList<>();
        vertices.add(new Point2D(10, 10));
        vertices.add(new Point2D(120, 10));
        vertices.add(new Point2D(120, 140));
        vertices.add(new Point2D(10, 140));
        //CHECKSTYLE.ON: MagicNumber

        this.locationPane.addRoom("Blub", vertices);
    }
}
