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

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.openbase.bco.bcozy.BCozy;
import org.openbase.bco.bcozy.model.LanguageSelection;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.ForegroundPane;
import org.openbase.bco.bcozy.view.mainmenupanes.AvailableUsersPane;
import org.openbase.bco.bcozy.view.mainmenupanes.ConnectionPane;
import org.openbase.bco.bcozy.view.mainmenupanes.LoginPane;
import org.openbase.bco.bcozy.view.mainmenupanes.SettingsPane;
import org.openbase.bco.authentication.lib.SessionManager;

import java.io.StreamCorruptedException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import org.openbase.bco.dal.remote.unit.Units;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.NotAvailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        new Thread(this::loginUserAsync).start();
    }

    private void loginUserAsync() {
        SessionManager sessionManager = SessionManager.getInstance();

        try {
            sessionManager.login(loginPane.getNameTxt().getText(), loginPane.getPasswordField().getText());
        } catch (CouldNotPerformException | StreamCorruptedException e) {
            // ignore, thrown if wrong username
        } catch (java.lang.OutOfMemoryError error) {
            LOGGER.error(error.getMessage());
        }

        Platform.runLater(() -> {
            if (sessionManager.isLoggedIn()) {
                loginPane.resetUserOrPasswordWrong();
                loginPane.getLoggedInUserLbl().setText(loginPane.getNameTxt().getText());
                loginPane.getNameTxt().setText("");
                loginPane.getPasswordField().setText("");
                loginPane.setState(LoginPane.State.LOGOUT);
            } else {
                loginPane.indicateUserOrPasswordWrong();

            }
        });
    }

    private void resetLogin() {
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
