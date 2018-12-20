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
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.jul.exception.EnumNotSupportedException;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openbase.type.domotic.unit.connection.ConnectionDataType.ConnectionData;

/**
 *
 */
public class DoorPolygon extends ConnectionPolygon {

    /**
     * Application logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DoorPolygon.class);

    /**
     * Constructor for the DoorPolygon.
     *
     * @param points The vertices of the connection.
     * @throws org.openbase.jul.exception.InstantiationException
     */
    public DoorPolygon(final double... points) throws InstantiationException {
        super(points);

        final ObservableList<Double> pointList = super.getPoints();

        if (isHorizontal()) {
            for (int i = 1; i < pointList.size(); i = i + 2) {
                if (pointList.get(i) == getMinY()) {
                    pointList.set(i, getMinY() - Constants.ROOM_STROKE_WIDTH / 2);
                } else {
                    pointList.set(i, getMaxY() + Constants.ROOM_STROKE_WIDTH / 2);
                }
            }
        } else {
            for (int i = 0; i < pointList.size(); i = i + 2) {
                if (pointList.get(i) == getMinX()) {
                    pointList.set(i, getMinX() - Constants.ROOM_STROKE_WIDTH / 2);
                } else {
                    pointList.set(i, getMaxX() + Constants.ROOM_STROKE_WIDTH / 2);
                }
            }
        }
    }

    @Override
    public void applyDataUpdate(ConnectionData unitData) {
        switch (unitData.getDoorState().getValue()) {
            case CLOSED:
            setMainColor(Color.WHITE);
            setCustomColor(Color.WHITE);
            getStrokeDashArray().clear();
            getStrokeDashArray().addAll(Constants.DOOR_DASH_WIDTH, Constants.DOOR_DASH_WIDTH);
            break;
            case IN_BETWEEN:
            case OPEN:
                setMainColor(Color.TRANSPARENT);
                setCustomColor(Color.TRANSPARENT);
                getStrokeDashArray().clear();
                getStrokeDashArray().addAll(Constants.DOOR_DASH_WIDTH, Constants.DOOR_DASH_WIDTH * 3);
                break;
            case UNKNOWN:
                setMainColor(Color.TRANSPARENT);
                setCustomColor(Color.YELLOW);
                break;
            default:
                ExceptionPrinter.printHistory(new EnumNotSupportedException(unitData.getDoorState().getValue(), this), LOGGER);
        }
    }

    @Override
    protected void setConnectionStyle() {
        this.setMainColor(Color.TRANSPARENT);
        this.setStroke(Color.WHITE);
        this.getStrokeDashArray().addAll(Constants.DOOR_DASH_WIDTH, Constants.DOOR_DASH_WIDTH);
        this.setStrokeWidth(Constants.ROOM_STROKE_WIDTH);
        this.setStrokeType(StrokeType.INSIDE);
        this.setMouseTransparent(true);
    }

    /**
     * Will be called when either the main or the custom color changes.
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
