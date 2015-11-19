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

/**
 * Created by hoestreich on 11/19/15.
 */
public class ShutterInstance {

    private final String shutterName;
    private final double openingRatio;

    /**
     * Constuctor for an instance of a shutter.
     * @param shutterName the name of the shutter
     * @param openingRatio the opening ration of the shutter
     */
    public ShutterInstance(final String shutterName, final double openingRatio) {
        this.openingRatio = openingRatio;
        this.shutterName = shutterName;
    }

    /**
     * Getter for the shutter name.
     * @return the name as a string
     */
    public String getShutterName() {
        return shutterName;
    }

    /**
     * Getter for the opening ratio of the shutter.
     * @return the opening ratio as a double value
     */
    public double getOpeningRatio() {
        return openingRatio;
    }


}
