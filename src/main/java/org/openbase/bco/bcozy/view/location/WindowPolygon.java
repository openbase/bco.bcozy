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

import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.jul.exception.EnumNotSupportedException;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.unit.connection.ConnectionDataType;

/**
 *
 */
public class WindowPolygon extends ConnectionPolygon {

    private static final Logger LOGGER = LoggerFactory.getLogger(WindowPolygon.class);
    
    /**
     * Constructor for the WindowPolygon.
     *
     * @param points The vertices of the connection.
     * @throws org.openbase.jul.exception.InstantiationException
     */
    public WindowPolygon(final double... points) throws InstantiationException {
        super(points);
    }

    @Override
    public void applyDataUpdate(ConnectionDataType.ConnectionData unitData) {
        switch (unitData.getDoorState().getValue()) {
            case CLOSED:
                setCustomColor(Color.GREEN.brighter());
                break;
            case IN_BETWEEN:
            case OPEN:
                setCustomColor(Color.BLUE.brighter());
                break;
            case UNKNOWN:
                setCustomColor(Color.ORANGE.brighter());
                break;
            default:
                ExceptionPrinter.printHistory(new EnumNotSupportedException(unitData.getDoorState().getValue(), this), LOGGER);
        }
    }

    @Override
    protected void setConnectionStyle() {
        this.setStroke(Color.WHITE);
        this.setStrokeWidth(Constants.ROOM_STROKE_WIDTH);
        this.setMouseTransparent(true);
        this.setMainColor(Color.TRANSPARENT);
    }

    @Override
    protected void changeStyleOnOpening(final boolean open) {
        // TODO: To be implemented...
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
        final Color color = mainColor.interpolate(customColor, CUSTOM_COLOR_WEIGHT);

        final Stop[] stops = new Stop[]{new Stop(0, color),
            new Stop(0.2, color),
            new Stop(0.5, Constants.WINDOW_EFFECT),
            new Stop(0.8, color),
            new Stop(1, color)};
        final LinearGradient lg1 = new LinearGradient(0, 0, 6, 6, false, CycleMethod.REPEAT, stops);
        this.setFill(lg1);
    }

}
