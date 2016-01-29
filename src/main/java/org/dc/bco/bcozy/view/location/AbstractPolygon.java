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
import javafx.scene.shape.Polygon;

/**
 *
 */
public abstract class AbstractPolygon extends Polygon implements Colorable {

    /**
     * The value how much the custom color will be weighted against the main color.
     */
    public static final double CUSTOM_COLOR_WEIGHT = 0.5;

    private Color mainColor;
    private Color customColor;

    private final double centerX;
    private final double centerY;
    private final String label;
    private final String uuid;

    /**
     * Creates a new instance of Polygon.
     *
     * @param label The name of the polygon
     * @param uuid The UUID of the polygon
     * @param points the coordinates of the polygon vertices
     */
    public AbstractPolygon(final String label, final String uuid, final double... points) {
        super(points);

        this.centerX = (super.getLayoutBounds().getMaxX() + super.getLayoutBounds().getMinX()) / 2;
        this.centerY = (super.getLayoutBounds().getMaxY() + super.getLayoutBounds().getMinY()) / 2;

        this.label = label;
        this.uuid = uuid;

        this.mainColor = Color.TRANSPARENT;
        this.customColor = Color.TRANSPARENT;
    }



    /**
     * Getter method for the X Coordinate of the center.
     * @return x center as a double value
     */
    public double getCenterX() {
        return centerX;
    }

    /**
     * Getter method for the Y Coordinate of the center.
     * @return y center as a double value
     */
    public double getCenterY() {
        return centerY;
    }

    /**
     * Getter method for the label.
     * @return the label as a String
     */
    public String getLabel() {
        return label;
    }

    /**
     * Getter method for the UUID.
     * @return the UUID as a String
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Setter method for the mainColor.
     * @param mainColor as a color
     */
    protected void setMainColor(final Color mainColor) {
        this.mainColor = mainColor;
        onColorChange(this.mainColor, this.customColor);
    }

    /**
     * Getter method for the mainColor.
     * @return The main color
     */
    protected Color getMainColor() {
        return this.mainColor;
    }

    /**
     * Calling the colorize method will paint the instance in the specified color.
     * @param color The specified color
     */
    @Override
    public void setCustomColor(final Color color) {
        this.customColor = color;
        onColorChange(this.mainColor, this.customColor);
    }

    /**
     * Calling the getCustomColor method will return the specified color.
     * @return The specified color
     */
    @Override
    public Color getCustomColor() {
        return this.customColor;
    }

    /**
     * Will be called when either the main or the custom color changes.
     * The initial values for both colors are Color.TRANSPARENT.
     * @param mainColor The main color
     * @param customColor The custom color
     */
    protected abstract void onColorChange(final Color mainColor, final Color customColor);

}
