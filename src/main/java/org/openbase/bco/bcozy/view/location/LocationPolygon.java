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

import org.openbase.bco.dal.remote.layer.unit.location.LocationRemote;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openbase.type.domotic.unit.UnitConfigType.UnitConfig;
import org.openbase.type.domotic.unit.location.LocationDataType.LocationData;

import java.util.List;

/**
 * A Polygon that represents different kinds of locations.
 */
public abstract class LocationPolygon extends DynamicUnitPolygon<LocationData, LocationRemote> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(TilePolygon.class);

    // the level in the location hierarchy
    private int level;

    /**
     * Constructor for the LocationPolygon.
     *
     * @throws org.openbase.jul.exception.InstantiationException
     */
    public LocationPolygon(final LocationMap locationMap) throws InstantiationException {
        super(locationMap);
        this.setLocationStyle();
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
     * Method to get all the childIds from the Tile.
     *
     * @return A list of childIds.
     * @throws org.openbase.jul.exception.NotAvailableException
     */
    public List<String> getChildIds() throws NotAvailableException {
        try {
            return getUnitRemote().getConfig().getLocationConfig().getChildIdList();
        } catch (NullPointerException ex) {
            throw new NotAvailableException("ChildIdList", ex);
        }
    }

    /**
     * Will be called when the Polygon is constructed and can be used to apply specific styles.
     */
    protected abstract void setLocationStyle();

}
