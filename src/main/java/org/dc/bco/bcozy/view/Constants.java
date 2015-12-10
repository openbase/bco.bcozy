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
package org.dc.bco.bcozy.view;

import javafx.scene.paint.Color;

/**
 * Created by hoestreich on 11/18/15.
 */
public final class Constants {

    /**
     * The size of the images used.
     */
    public static final double IMAGE_SIZE = 512.0;

    /**
     * Inset used for UI Elements.
     */
    public static final double INSETS = 10.0;

    /**
     * The size for a big size icon.
     */
    public static final double BIG_ICON = 128.0;

    /**
     * The size for a mid size icon.
     */
    public static final double MIDDLE_ICON = 48.0;

    /**
     * The size for a small size icon.
     */
    public static final double SMALL_ICON = 32.0;

    /**
     * The max width for a main menu pane.
     */
    public static final double MAX_MENU_WIDTH = 280.0;

    /**
     * The max width for a main menu pane.
     */
    public static final double MAXLOGOWIDTH = 175.0;

    /**
     * The insets for a icon.
     */
    public static final double ICON_INSETS = 15.0;

    /**
     * The value for a full rotation.
     */
    public static final double FULL_ROTATION = 360.0;

    /**
     * The value for a full rotation.
     */
    public static final double TRIGGER_DISTANCE = 200.0;

    /**
     * The relation between meters and pixels.
     */
    public static final double METER_TO_PIXEL = 30.0;

    /**
     * The stroke width of the rooms.
     */
    public static final double ROOM_STROKE_WIDTH = 1.0;

    /**
     * The standard text size to be used when loading a new font.
     */
    public static final double STANDARD_TEXT_SIZE = 14.0;

    /**
     * The relation factor of middle icon and image size.
     */
    public static final double MIDDLE_ICON_SCALE_FACTOR = MIDDLE_ICON / IMAGE_SIZE;

    /**
     * The relation factor of small icon and image size.
     */
    public static final double SMALL_ICON_SCALE_FACTOR = SMALL_ICON / IMAGE_SIZE;

    /**
     * The opacity value for fully transparent = invisible.
     */
    public static final double FULLYTRANSPARENT = 0.0;

    /**
     * The opacity value for fully opaque = visible.
     */
    public static final double NOTRANSPARENCY = 1.0;

    /**
     * The opacity value for nearly opaque style.
     */
    public static final double NEARLYTRANSPARENT = 0.3;

    /**
     * The duration for a pretty fast fade animation.
     */
    public static final double FASTFADEDURATION = 100.0;

    /**
     * The duration for a glowing fade animation.
     */
    public static final double GLOWINGFADEDURATION = 1000.0;

    /**
     * The color for a selected tile.
     */
    public static final Color TILE_SELECTION = new Color(1.0, 1.0, 1.0, 0.4);

    /**
     * The color for a region.
     */
    public static final Color REGION_FILL = new Color(1.0, 1.0, 1.0, 0.4);

    /**
     * The width of the zoomPane in the locationPane.
     */
    public static final double ZOOM_PANE_WIDTH = 2000;

    /**
     * The height of the zoomPane in the locationPane.
     */
    public static final double ZOOM_PANE_HEIGHT = 2000;

    /**
     * The name of a dummy room.
     */
    public static final String DUMMY_ROOM_NAME = "DUMMY";

    /**
     * The offset to push the floating button on the border.
     */
    public static final double FLOATING_BUTTON_OFFSET = 85.0;

    /**
     * The size for the small main menu.
     */
    public static final double SMALL_MAIN_MENU_WIDTH = 60.0;

    /**
     * Private Constructor.
     */
    private Constants() { }
}
