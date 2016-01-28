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

/**
 * The Colorable interface should be implemented by any class whose instances are intended to be colorized.
 */
public interface Colorable {

    /**
     * Calling the colorize method will paint the instance in the specified color.
     * @param color The specified color
     */
    void setCustomColor(final Color color);

    /**
     * Calling the getCustomColor method will return the specified color.
     * @return The specified color
     */
    Color getCustomColor();

}
