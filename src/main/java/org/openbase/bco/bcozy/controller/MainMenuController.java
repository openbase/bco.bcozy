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
package org.openbase.bco.bcozy.controller;

import org.openbase.bco.bcozy.view.ForegroundPane;
import org.openbase.bco.bcozy.view.mainmenupanes.AvailableUsersPane;

/**
 * Created by hoestreich on 11/24/15.
 */
public class MainMenuController {

    /**
     * Constructor for the MainMenuController.
     *
     * @param foregroundPane The foregroundPane allows to access all necessary gui elements
     */
    public MainMenuController(final ForegroundPane foregroundPane) {
        final AvailableUsersPane availableUsersPane = foregroundPane.getMainMenu().getAvailableUsersPanePane();
        availableUsersPane.getStatusIcon().setOnMouseClicked(event -> showHideMainMenu(foregroundPane));
        foregroundPane.getMainMenu().getMainMenuFloatingButton().setOnAction(event -> showHideMainMenu(foregroundPane));
    }

    private void showHideMainMenu(final ForegroundPane foregroundPane) {
        if (foregroundPane.getMainMenu().isMaximized()) {
            foregroundPane.getMainMenu().minimizeMainMenu();
        } else {
            foregroundPane.getMainMenu().maximizeMainMenu();
        }
    }
}
