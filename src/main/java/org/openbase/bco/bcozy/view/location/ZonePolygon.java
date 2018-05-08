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
import org.openbase.jul.exception.printer.ExceptionPrinter;
import rst.domotic.unit.location.LocationDataType;

/**
 *
 */
public class ZonePolygon extends LocationPolygon {

    private final LocationPane locationPane;

    /**
     * The Constructor for a ZonePolygon.
     *
     * @param points The vertices of the location
     * @throws org.openbase.jul.exception.InstantiationException
     */
    public ZonePolygon(final LocationPane locationPane, final double... points) throws InstantiationException {
        super(points);
        this.locationPane = locationPane;

        setOnMouseClicked(event -> {
            try {
                if (event.isStillSincePress()) {
                    if (event.getClickCount() == 1) {
                        locationPane.setSelectedLocation(this);
                    } else if (event.getClickCount() == 2) {
                        if (locationPane.getLastClickTarget().equals(this)) {
                            locationPane.autoFocusPolygonAnimated(this);
                        } else {
                            locationPane.getLastClickTarget().fireEvent(event.copyFor(null, locationPane.getLastClickTarget()));
                        }
                    }
                    event.consume();
                }
            } catch (CouldNotPerformException ex) {
                ExceptionPrinter.printHistory("Could not handle mouse event!", ex, LOGGER);
            }
        });

        // needed to handle background pane selection of the root pane
        setOnMouseEntered(event -> {locationPane.handleHoverUpdate(ZonePolygon.this, true);});
        setOnMouseExited(event -> {locationPane.handleHoverUpdate(ZonePolygon.this, false);});

        hoverProperty().addListener((observable,  oldValue, newValue) -> locationPane.handleHoverUpdate(ZonePolygon.this, newValue));
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
        this.setMainColor(Constants.ZONE_FILL);
        this.setStroke(Color.WHITE);
        this.setStrokeWidth(Constants.ROOM_STROKE_WIDTH);
    }

    @Override
    protected void changeStyleOnSelection(final boolean selected) {
        if (selected) {
            this.setMainColor(Constants.TILE_SELECTION);
        } else {
            this.setMainColor(Constants.ZONE_FILL);
        }
    }

    /**
     * Will be called when either the main or the custom color changes.
     * The initial values for both colors are Color.TRANSPARENT.
     *
     * @param mainColor The main color
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
