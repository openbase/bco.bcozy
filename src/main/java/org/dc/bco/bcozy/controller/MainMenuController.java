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
package org.dc.bco.bcozy.controller;

import org.dc.bco.bcozy.view.ForegroundPane;
import org.dc.bco.bcozy.view.mainmenupanes.UserPane;

/**
 * Created by hoestreich on 11/24/15.
 */
public class MainMenuController {

    /**
     * Constructor for the MainMenuController.
     * @param foregroundPane The foregroundPane allows to access all necessary gui elements
     */
    public MainMenuController(final ForegroundPane foregroundPane) {

        foregroundPane.getMainMenu().getUserPane().getOpenLoginBtn().setOnAction(event -> setLoginPane(foregroundPane));
        foregroundPane.getMainMenu().getUserPane().getLoginBtn().setOnAction(event -> loginUser(foregroundPane));
        foregroundPane.getMainMenu().getUserPane().getLogoutBtn().setOnAction(event -> logoutUser(foregroundPane));
        foregroundPane.getMainMenu().getUserPane().getBackBtn().setOnAction(event -> logoutUser(foregroundPane));
        foregroundPane.getMainMenu().getMainMenuFloatingButton().setOnAction(event -> showHideMainMenu(foregroundPane));
    }

    private void setLoginPane(final ForegroundPane foregroundPane) {
        foregroundPane.getMainMenu().getUserPane().setState(UserPane.State.LOGINACTIVE);
    }

    private void loginUser(final ForegroundPane foregroundPane) {
        foregroundPane.getMainMenu().getUserPane().setState(UserPane.State.LOGIN);
    }

    private void logoutUser(final ForegroundPane foregroundPane) {
        foregroundPane.getMainMenu().getUserPane().setState(UserPane.State.NOLOGIN);
    }
    private void showHideMainMenu(final ForegroundPane foregroundPane) {
        //TODO: Resize the pain correctly
        if (foregroundPane.getMainMenu().isVisible()) {
            foregroundPane.getMainMenu().hideMainMenu();
        } else {
            foregroundPane.getMainMenu().showMainMenu();
        }

    }
}
