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

    private final UserPane userPane;
    /**
     * Constructor for the MainMenuController.
     * @param foregroundPane The foregroundPane allows to access all necessary gui elements
     */
    public MainMenuController(final ForegroundPane foregroundPane) {
        userPane = foregroundPane.getMainMenu().getUserPane();
        userPane.getStartLoginBtn().setOnAction(event -> startLogin());
        userPane.getLoginBtn().setOnAction(event -> loginUser());
        userPane.getBackBtn().setOnAction(event -> resetLogin());
        userPane.getLogoutBtn().setOnAction(event -> resetLogin());
        userPane.getPasswordField().setOnAction(event -> loginUser());
        userPane.getNameTxt().setOnAction(event -> loginUser());
        userPane.getNameTxt().setOnKeyTyped(event -> resetWrongInput());
        userPane.getPasswordField().setOnKeyTyped(event -> resetWrongInput());
        foregroundPane.getMainMenu().getMainMenuFloatingButton().setOnAction(event -> showHideMainMenu(foregroundPane));
    }


    private void startLogin() {
        userPane.setState(UserPane.State.LOGINACTIVE);
    }

    private void resetWrongInput() {
        if(userPane.getInputWrongLbl().isVisible()){
            userPane.resetUserOrPasswordWrong();
        }
    }

    private void loginUser() {
        //TODO: Initiate Login with UserRegistry
        if (userPane.getNameTxt().getText().equals("Admin") &&
                userPane.getPasswordField().getText().equals("")) {
            userPane.resetUserOrPasswordWrong();
            userPane.getLoggedInUserLbl().setText(userPane.getNameTxt().getText());
            userPane.getNameTxt().setText("");
            userPane.getPasswordField().setText("");
            userPane.setState(UserPane.State.LOGOUT);
        } else {
            userPane.indicateUserOrPasswordWrong();
        }
    }

    private void resetLogin() {
        if(userPane.getInputWrongLbl().isVisible()){
            userPane.resetUserOrPasswordWrong();
        }
        userPane.getNameTxt().setText("");
        userPane.getPasswordField().setText("");
        userPane.getLoggedInUserLbl().setText("");
        userPane.setState(UserPane.State.LOGIN);
    }
    private void showHideMainMenu(final ForegroundPane foregroundPane) {
        //TODO: Resize the pain correctly
        if (foregroundPane.getMainMenu().isMaximized()) {
            foregroundPane.getMainMenu().minimizeMainMenu();
        } else {
            foregroundPane.getMainMenu().maximizeMainMenu();
        }

    }
}
