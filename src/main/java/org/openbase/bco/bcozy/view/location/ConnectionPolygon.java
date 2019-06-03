/**
 * ==================================================================
 *
 * This file is part of org.openbase.bco.bcozy.
 *
 * org.openbase.bco.bcozy is free software: you can redistribute it and modify
 * it under the terms of the GNU General Public License (Version 3)
 * as published by the Free Software Foundation.
 *
 * org.openbase.bco.bcozy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with org.openbase.bco.bcozy. If not, see <http://www.gnu.org/licenses/>.
 * ==================================================================
 */
package org.openbase.bco.bcozy.view.location;

import javafx.collections.ObservableList;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.dal.remote.layer.unit.connection.ConnectionRemote;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.type.domotic.unit.UnitConfigType.UnitConfig;
import org.openbase.type.domotic.unit.connection.ConnectionDataType.ConnectionData;

/**
 * A Polygon that represents different kinds of connections.
 *
 * @author julian
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public abstract class ConnectionPolygon extends DynamicUnitPolygon<ConnectionData, ConnectionRemote> {

    // the level in the location hierarchy
    private int level;

    /**
     * Constructor for the ConnectionPolygon.
     *
     * @throws org.openbase.jul.exception.InstantiationException
     */
    public ConnectionPolygon(final LocationMap locationMap) throws InstantiationException {
        super(locationMap);
        this.setConnectionStyle();
    }

    @Override
    public void applyConfigUpdate(final UnitConfig unitConfig) throws InterruptedException {
        super.applyConfigUpdate(unitConfig);

        try {
            // calculate location level
            int level = 0;
            UnitConfig locationUnitConfig = unitConfig;

            while (!locationUnitConfig.getLocationConfig().getRoot()) {
                level++;
                locationUnitConfig = Registries.getUnitRegistry(true).getUnitConfigById(locationUnitConfig.getPlacementConfig().getLocationId());
            }
            this.level = level;
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory("Could not compute location level!", ex, LOGGER);
        }
    }

    public int getLevel() {
        return level;
    }

    /**
     * Will be called when the Polygon is constructed and can be used to apply specific styles.
     */
    protected abstract void setConnectionStyle();

        @Override
        protected void changeStyleOnSelection(boolean selected) {
//        if (selectable) {
//            this.getStrokeDashArray().addAll(Constants.REGION_DASH_WIDTH, Constants.REGION_DASH_WIDTH);
//            this.setStrokeWidth(Constants.REGION_STROKE_WIDTH);
//            this.setMouseTransparent(false);
//        } else {
//            this.getStrokeDashArray().clear();
//            this.setStrokeWidth(0.0);
//            this.setMouseTransparent(true);
//        }
    }
}
