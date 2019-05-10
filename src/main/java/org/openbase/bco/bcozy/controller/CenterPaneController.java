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
package org.openbase.bco.bcozy.controller;

import org.openbase.bco.bcozy.view.CenterPane;
import org.openbase.bco.bcozy.view.ForegroundPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by hoestreich on 12/2/15.
 */
public class CenterPaneController {

    /**
     * Application logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CenterPaneController.class); //NOPMD
    private final CenterPane centerPane;
    private State activeState;
    private boolean isShowing;

    /**
     * Enum to control the display state.
     */
    public enum State {
        SETTINGS, TEMPERATURE, MOVEMENT, ENERGY
    }

    /**
     * Constructor for the CenterPaneController.
     * @param foregroundPane instance of the foregroundPane which has all elements as its children.
     */
    public CenterPaneController(final ForegroundPane foregroundPane) {
        isShowing = false;
        activeState = State.SETTINGS;
        centerPane = foregroundPane.getCenterPane();
    }

    public CenterPaneController(CenterPane centerPane) {
        this.centerPane = centerPane;
        isShowing = false;
        activeState = State.SETTINGS;

    }
}
