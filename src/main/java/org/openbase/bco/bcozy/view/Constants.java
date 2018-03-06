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
package org.openbase.bco.bcozy.view;

import javafx.scene.paint.Color;

/**
 * @author hoestreich
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public final class Constants {
    /**
     * Inset used for UI Elements.
     */
    public static final double INSETS = 10.0;

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
     * The name of a dummy label.
     */
    public static final String DUMMY_LABEL = "DUMMY";

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
    public static final double FLOATING_BUTTON_OFFSET = 55.0;

    /**
     * Offset to position floatingbutton on outer right corner.
     */
    public static final double FLOATING_BUTTON_OFFSET_X = 85.0;

    /**
     * Offset to position buttons above context menu.
     */
    public static final double FLOATING_BUTTON_OFFSET_Y = -46.0;

    /**
     * The size for the small main menu.
     */
    public static final double SMALL_MAIN_MENU_WIDTH = 60.0;

    /**
     * The pref width for the small main menu.
     */
    public static final double SMALL_MAIN_MENU_WIDTH_PREF = 98.0;


    /**
     * The factor for the scaling in the locationPane.
     */
    public static final double SCALE_DELTA = 1.05;

    /**
     * Name of the language resource bundle.
     */
    public static final String LANGUAGE_RESOURCE_BUNDLE = "languages.languages";

    /**
     * text: "white".
     */
    public static final String WHITE = "white";

    /**
     * Yellow color like a lightbulb.
     */
    public static final Color LIGHTBULB_OFF_COLOR = Color.GRAY.darker();

    /**
     * Timeout value in milliseconds for fetching transformations.
     */
    public static final long TRANSFORMATION_TIMEOUT = 10000;

    /**
     * The minimal temperatur for fading.
     * Has to be &gt;= 0.
     */
    public static final double TEMPERATUR_FADING_MINIMUM = 10.0;

    /**
     * The maximal temperatur for fading.
     * Has to be &gt; TEMPERATUR_FADING_MINIMUM.
     */
    public static final double TEMPERATUR_FADING_MAXIMUM = 25.0;

    /**
     * Milliseconds timeout for recurrenceEventFilter.
     */
    public static final int RECURRENCE_EVENT_FILTER_MILLI_TIMEOUT = 300;

    /**
     * text: "°C".
     */
    public static final String CELSIUS = "°C";

    /**
     * String for CSS styling of UserPane.
     */
    public static final String BOLD_LABEL = "bold-label";

    /**
     * String for CSS styling of AdvancedHorizontalSlider.
     */
    public static final String SMALL_LABEL = "small-label";


    /**
     * Private Constructor.
     */
    private Constants() {
    }
}
