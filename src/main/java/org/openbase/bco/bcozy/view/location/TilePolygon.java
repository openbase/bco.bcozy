/**
 * ==================================================================
 * <p>
 * This file is part of org.openbase.bco.bcozy.
 * <p>
 * org.openbase.bco.bcozy is free software: you can redistribute it and modify
 * it under the terms of the GNU General Public License (Version 3)
 * as published by the Free Software Foundation.
 * <p>
 * org.openbase.bco.bcozy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with org.openbase.bco.bcozy. If not, see <http://www.gnu.org/licenses/>.
 * ==================================================================
 */
package org.openbase.bco.bcozy.view.location;

import javafx.scene.paint.Color;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.EnumNotSupportedException;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.type.domotic.unit.location.LocationDataType;

/**
 *
 */
public class TilePolygon extends LocationPolygon {

    /**
     * The Constructor for a TilePolygon.
     *
     * @throws org.openbase.jul.exception.InstantiationException
     */
    public TilePolygon(final LocationMap locationMap) throws InstantiationException {
        super(locationMap);

//        setOnMouseClicked(event -> {
//            try {
//                if (event.isStillSincePress()) {
//                    System.out.println("tile me: "+ event.isConsumed());
//                    if (event.getClickCount() == 1) {
//                        locationMap.setSelectedUnit(this);
//                    } else if (event.getClickCount() == 2) {
//                        locationMap.autoFocusPolygonAnimated(this);
//                    }
//                    event.consume();
//                }
//            } catch (CouldNotPerformException ex) {
//                ExceptionPrinter.printHistory("Could not handle mouse event!", ex, LOGGER);
//            }
//        });
//
//        hoverProperty().addListener((observable, oldValue, newValue) -> locationMap.handleHoverUpdate(TilePolygon.this, newValue));
    }

    @Override
    public void applyDataUpdate(LocationDataType.LocationData unitData) {
        switch (unitData.getPresenceState().getValue()) {
            case PRESENT:
                setCustomColor(Color.GREEN.brighter());
                break;
            case ABSENT:
            case UNKNOWN:
                setCustomColor(Color.TRANSPARENT);
                break;
            default:
                ExceptionPrinter.printHistory(new EnumNotSupportedException(unitData.getPresenceState().getValue(), this), LOGGER);
        }
    }

    @Override
    protected void setLocationStyle() {
        this.setMainColor(Constants.TILE_FILL);
        setStroke(Color.WHITE);
        setStrokeWidth(Constants.ROOM_STROKE_WIDTH);
    }

    @Override
    protected void changeStyleOnSelection(final boolean selected) {
        if (selected) {
            this.setMainColor(Constants.TILE_SELECTION);
        } else {
            this.setMainColor(Constants.TILE_FILL);
        }
    }

    /**
     * Will be called when either the main or the custom color changes.
     * The initial values for both colors are Color.TRANSPARENT.
     *
     * @param mainColor   The main color
     * @param customColor The custom color
     */
    @Override
    protected void onColorChange(final Color mainColor, final Color customColor) {
        if (customColor.equals(Color.TRANSPARENT)) {
            setFill(mainColor);
        } else {
            setFill(mainColor.interpolate(customColor, CUSTOM_COLOR_WEIGHT));
        }
    }
}
