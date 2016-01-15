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
        // TODO: To be implemented...
    }

    @Override
    protected void changeStyleOnOpening(final boolean open) {
        // TODO: To be implemented...
    }
}
