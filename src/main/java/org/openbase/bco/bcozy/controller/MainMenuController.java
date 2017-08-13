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

import javafx.application.Platform;
import org.openbase.bco.bcozy.view.ForegroundPane;
import org.openbase.bco.bcozy.view.mainmenupanes.AvailableUsersPane;
import org.openbase.bco.bcozy.view.mainmenupanes.ConnectionPane;
import org.openbase.bco.bcozy.view.mainmenupanes.LoginPane;
import org.openbase.bco.bcozy.view.mainmenupanes.SettingsPane;
import org.openbase.bco.authentication.lib.SessionManager;

import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InvalidStateException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.schedule.GlobalCachedExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.unit.UnitConfigType;

/**
 * Created by hoestreich on 11/24/15.
 */
public class MainMenuController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainMenuController.class);

    private LoginPane loginPane;
    private SettingsPane settingsPane;
    private AvailableUsersPane availableUsersPane;
    private ConnectionPane connectionPane;

    public MainMenuController() {
    }

    /**
     * Constructor for the MainMenuController.
     *
     * @param foregroundPane The foregroundPane allows to access all necessary gui elements
     */
    public MainMenuController(final ForegroundPane foregroundPane) {
        init(foregroundPane);
    }

    public void init(final ForegroundPane foregroundPane) {
        loginPane = foregroundPane.getMainMenu().getLoginPane();
        settingsPane = foregroundPane.getCenterPane().getSettingsPane();
        availableUsersPane = foregroundPane.getMainMenu().getAvailableUsersPanePane();
        connectionPane = foregroundPane.getMainMenu().getConnectionPane();
        loginPane.getLoginBtn().setOnAction(event -> loginUser());
        loginPane.getLogoutBtn().setOnAction(event -> resetLogin());
        loginPane.getPasswordField().setOnAction(event -> loginUser());
        loginPane.getNameTxt().setOnAction(event -> loginUser());
        loginPane.getNameTxt().setOnKeyTyped(event -> resetWrongInput());
        loginPane.getPasswordField().setOnKeyTyped(event -> resetWrongInput());
        loginPane.getStatusIcon().setOnMouseClicked(event -> showHideMainMenu(foregroundPane));
        settingsPane.getStatusIcon().setOnMouseClicked(event -> showHideMainMenu(foregroundPane));
        availableUsersPane.getStatusIcon().setOnMouseClicked(event -> showHideMainMenu(foregroundPane));
        connectionPane.getStatusIcon().setOnMouseClicked(event -> showHideMainMenu(foregroundPane));

        foregroundPane.getMainMenu().getMainMenuFloatingButton().setOnAction(event -> showHideMainMenu(foregroundPane));

    }

    private void startLogin() {
        loginPane.setState(LoginPane.State.LOGINACTIVE);
    }

    private void resetWrongInput() {
        if (loginPane.getInputWrongLbl().isVisible()) {
            loginPane.resetUserOrPasswordWrong();
        }
    }

    private void loginUser() {
        GlobalCachedExecutorService.submit(() -> {
            try {
                loginUserAsync();
            } catch (InterruptedException ex) {
                ExceptionPrinter.printHistory("Could not login!", ex, LOGGER);
            }
        });
    }

    private void loginUserAsync() throws InterruptedException {
        SessionManager sessionManager = SessionManager.getInstance();

        try {

            // sessionManager.login(Registries.getUserRegistry().getUserIdByUserName(loginPane.getNameTxt().getText()),
            // ##### reimplemented because "getUserIdByUserName" not included in current master api.
            final String username = loginPane.getNameTxt().getText();
            String userUnitId = null;
            for (final UnitConfigType.UnitConfig userUnitConfig : Registries.getUserRegistry().getUserConfigs()) {
                if (userUnitConfig.getUserConfig().getUserName().equals(username)) {
                    userUnitId = userUnitConfig.getId();
                }
            }

            if (userUnitId == null) {
                throw new InvalidStateException("username does not exists!");
            }
            // #####

            sessionManager.login(userUnitId, loginPane.getPasswordField().getText());

            Platform.runLater(() -> {
                loginPane.resetUserOrPasswordWrong();
                loginPane.getLoggedInUserLbl().setText(loginPane.getNameTxt().getText());
                loginPane.getNameTxt().setText("");
                loginPane.getPasswordField().setText("");
                loginPane.setState(LoginPane.State.LOGOUT);
            });
        } catch (CouldNotPerformException ex) {
            Platform.runLater(() -> {
                loginPane.indicateUserOrPasswordWrong();
            });
        } catch (java.lang.OutOfMemoryError error) {
            LOGGER.error(error.getMessage());
        }
    }

    private void resetLogin() {
        SessionManager.getInstance().logout();

        if (loginPane.getInputWrongLbl().isVisible()) {
            loginPane.resetUserOrPasswordWrong();
        }
        loginPane.getNameTxt().setText("");
        loginPane.getPasswordField().setText("");
        loginPane.getLoggedInUserLbl().setText("");
        loginPane.setState(LoginPane.State.LOGINACTIVE);
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
