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
     * The name of the default css file.
     */
    public static final String DEFAULT_CSS = "/css/skin.css";

    /**
     * The name of the light theme css file.
     */
    public static final String LIGHT_THEME_CSS_NAME = "light";

    /**
     * The path of the light theme css file.
     */
    public static final String LIGHT_THEME_CSS = "/css/light.css";

    /**
     * The name of the dark theme css file.
     */
    public static final String DARK_THEME_CSS_NAME = "dark";

    /**
     * The path of the dark theme css file.
     */
    public static final String DARK_THEME_CSS = "/css/dark.css";

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
     * The size for a extra small size icon.
     */
    public static final double EXTRA_SMALL_ICON = 16.0;

    /**
     * The size for a extra extra small size icon.
     */
    public static final double EXTRA_EXTRA_SMALL_ICON = 10.0;

    /**
     * The max width for a main menu pane.
     */
    public static final double MAX_MENU_WIDTH = 280.0;

    /**
     * The max width for a main menu pane.
     */
    public static final double MAXLOGOWIDTH = 175.0;

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
    public static final double METER_TO_PIXEL = 50.0;

    /**
     * The stroke width of a location.
     */
    public static final double ROOM_STROKE_WIDTH = 1.0;

    /**
     * The stroke width of a location (mouse over).
     */
    public static final double ROOM_STROKE_WIDTH_MOUSE_OVER = 3.0;

    /**
     * The stroke width of a region.
     */
    public static final double REGION_STROKE_WIDTH = 0.5;

    /**
     * The stroke width of a region (mouse over).
     */
    public static final double REGION_STROKE_WIDTH_MOUSE_OVER = 2.0;

    /**
     * The dash width of a region.
     */
    public static final double REGION_DASH_WIDTH = 4.0;

    /**
     * The dash width of a door.
     */
    public static final double DOOR_DASH_WIDTH = 2.0;

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
    public static final double FULLY_TRANSPARENT = 0.0;

    /**
     * The opacity value for fully opaque = visible.
     */
    public static final double NO_TRANSPARENCY = 1.0;

    /**
     * The opacity value for nearly opaque style.
     */
    public static final double NEARLY_TRANSPARENT = 0.3;

    /**
     * The opacity value for half opaque style.
     */
    public static final double HALF_TRANSPARENT = 0.5;

    /**
     * The duration for a pretty fast fade animation.
     */
    public static final double FASTFADEDURATION = 100.0;

    /**
     * The duration for light change fade animation.
     */
    public static final double LIGHT_CHANGE_FADE_DURATION = 400.0;

    /**
     * The duration for a slow fade animation.
     */
    public static final double SLOW_FADE_DURATION = 2000.0;

    /**
     * The duration for a glowing fade animation.
     */
    public static final double GLOWING_FADE_DURATION = 1000.0;

    /**
     * The duration for fade animation of smoke detector.
     */
    public static final double SMOKE_DETECTOR_FADE_DURATION = 1000.0;

    /**
     * The color for a selected tile.
     */
    public static final Color TILE_SELECTION = new Color(1.0, 1.0, 1.0, 0.4);

    /**
     * The color for a region.
     */
    public static final Color REGION_FILL = new Color(1.0, 1.0, 1.0, 0.04);

    /**
     * The color for a door.
     */
    public static final Color PASSAGE_FILL = new Color(1.0, 1.0, 1.0, 0.05);

    /**
     * The color for a zone.
     */
    public static final Color ZONE_FILL = new Color(1.0, 1.0, 1.0, 0.25);

    /**
     * The color for a door.
     */
    public static final Color WINDOW_EFFECT = new Color(1.0, 1.0, 1.0, 0.40);

    /**
     * The background color.
     */
    public static final Color BACKGROUND_COLOR = new Color(0.25, 0.25, 0.25, 1.0);

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
     * The percentage of the full screen that a zoomed room will fill according to its width.
     */
    public static final double ZOOM_FIT_PERCENTAGE_WIDTH = 0.7;

    /**
     * The percentage of the full screen that a zoomed room will fill according to its width.
     */
    public static final double ZOOM_FIT_PERCENTAGE_HEIGHT = 0.7;

    /**
     * The offset to push the floating button on the border.
     */
    public static final double FLOATING_BUTTON_OFFSET = 85.0;

    /**
     * The size for the small main menu.
     */
    public static final double SMALL_MAIN_MENU_WIDTH = 60.0;

    /**
     * Divide constant for HSB color init.
     */
    public static final int ONE_HUNDRED = 100;

    /**
     * The factor for the scaling in the locationPane.
     */
    public static final double SCALE_DELTA = 1.05;

    /**
     * The amount of points for a connection.
     */
    public static final int CONNECTION_POINTS = 8;

    /**
     * Name of the language resource bundle.
     */
    public static final String LANGUAGE_RESOURCE_BUNDLE = "languages.languages";

    /**
     * Time in milliseconds for animation effects.
     */
    public static final int ANIMATION_TIME = 200;

    /**
     * Full circle angle in degree.
     */
    public static final double ROUND_ANGLE = 360.0;

    /**
     * Obtuse angle (270) in degree.
     */
    public static final int OBTUSE_ANGLE_270 = 270;

    /**
     * Right angle in degree.
     */
    public static final int RIGHT_ANGLE = 90;

    /**
     * 255 (1 byte) for rgb color.
     */
    public static final int RGB255 = 255;

    /**
     * Number Six.
     */
    public static final int SIX = 6;

    /**
     * Number ten.
     */
    public static final int TEN = 10;

    /**
     * Number fifteen.
     */
    public static final int FIFTEEN = 15;

    /**
     * text: "white".
     */
    public static final String WHITE = "white";

    /**
     * Yellow color like a lightbulb.
     */
    public static final Color LIGHTBULB_COLOR = new Color(1.0, 1.0, 0.3, 1.0);

    /**
     * Timeout value in milliseconds for fetching transformations.
     */
    public static final long TRANSFORMATION_TIMEOUT = 1000;

    /**
     * String for CSS styling of icons.
     */
    public static final String ICONS_CSS_STRING = "icons";

    /**
     * text: "UnknownID".
     */
    public static final String UNKNOWN_ID = "UnknownID";

    /**
     * The minimal temperatur for fading.
     * Has to be >= 0.
     */
    public static final double TEMPERATUR_FADING_MINIMUM = 10.0;

    /**
     * The maximal temperatur for fading.
     * Has to be > TEMPERATUR_FADING_MINIMUM.
     */
    public static final double TEMPERATUR_FADING_MAXIMUM = 25.0;

    /**
     * Thin stroke.
     */
    public static final double THIN_STROKE = 0.5;

    /**
     * Normal stroke.
     */
    public static final double NORMAL_STROKE = 1.5;

    /**
     * Highest Brightness for fading.
     */
    public static final double BRIGHTNESS_MAXIMUM = 3000;

    /**
     * Milliseconds timeout for threads.
     */
    public static final int THREAD_MILLI_TIMEOUT = 500;

    /**
     * Milliseconds interval for click counter(double click).
     */
    public static final int CLICK_TIME_INTERVAL_MILLI = 400;

    /**
     * text: "°C".
     */
    public static final String CELSIUS = "°C";

    /**
     * text: "%".
     */
    public static final String PERCENTAGE = "%";

    /**
     * Scale Factor for Weather Icons (necessary to get constant size).
     */
    public static final double WEATHER_ICONS_SCALE = 0.681;

    /**
     * String for tooltip unknown.
     */
    //public static final String UNKNOWN = "Unknown";

    /**
     * String for tooltip Watt W.
     */
    public static final String WATT = "W";

    /**
     * String for CSS styling of UserPane.
     */
    public static final String BOLD_LABEL = "bold-label";

    /**
     * String for CSS styling of AdvancedHorizontalSlider.
     */
    public static final String SMALL_LABEL = "small-label";

    /**
     * Event filter time.
     */
    public static final long FILTER_TIME = 100L;

    /**
     * Private Constructor.
     */
    private Constants() { }
}
