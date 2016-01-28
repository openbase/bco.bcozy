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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.dc.bco.bcozy.BCozy;
import org.dc.bco.bcozy.model.LanguageSelection;
import org.dc.bco.bcozy.view.Constants;
import org.dc.bco.bcozy.view.ForegroundPane;
import org.dc.bco.bcozy.view.mainmenupanes.AvailableUsersPane;
import org.dc.bco.bcozy.view.mainmenupanes.ConnectionPane;
import org.dc.bco.bcozy.view.mainmenupanes.LoginPane;
import org.dc.bco.bcozy.view.mainmenupanes.SettingsPane;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by hoestreich on 11/24/15.
 */
public class MainMenuController {

    private final LoginPane loginPane;
    private final SettingsPane settingsPane;
    private final AvailableUsersPane availableUsersPane;
    private final ConnectionPane connectionPane;

    /**
     * Constructor for the MainMenuController.
     * @param foregroundPane The foregroundPane allows to access all necessary gui elements
     */
    public MainMenuController(final ForegroundPane foregroundPane) {
        loginPane = foregroundPane.getMainMenu().getLoginPane();
        settingsPane = foregroundPane.getMainMenu().getSettingsPane();
        availableUsersPane = foregroundPane.getMainMenu().getAvailableUsersPanePane();
        connectionPane = foregroundPane.getMainMenu().getConnectionPane();
        loginPane.getStartLoginBtn().setOnAction(event -> startLogin());
        loginPane.getLoginBtn().setOnAction(event -> loginUser());
        loginPane.getBackBtn().setOnAction(event -> resetLogin());
        loginPane.getLogoutBtn().setOnAction(event -> resetLogin());
        loginPane.getPasswordField().setOnAction(event -> loginUser());
        loginPane.getNameTxt().setOnAction(event -> loginUser());
        loginPane.getNameTxt().setOnKeyTyped(event -> resetWrongInput());
        loginPane.getPasswordField().setOnKeyTyped(event -> resetWrongInput());
        loginPane.getStatusIcon().setOnMouseClicked(event -> showHideMainMenu(foregroundPane));
        settingsPane.getStatusIcon().setOnMouseClicked(event -> showHideMainMenu(foregroundPane));
        availableUsersPane.getStatusIcon().setOnMouseClicked(event -> showHideMainMenu(foregroundPane));
        connectionPane.getStatusIcon().setOnMouseClicked(event -> showHideMainMenu(foregroundPane));
        settingsPane.getThemeChoice().setOnAction(event -> chooseTheme());
        settingsPane.getLanguageChoice().setOnAction(event -> chooseLanguage());
        //Necessary to ensure that the first change is not missed by the ChangeListener
        settingsPane.getThemeChoice().getSelectionModel().select(0);
        settingsPane.getLanguageChoice().getSelectionModel().select(0);

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
        //TODO: Initiate Login with UserRegistry
        if (loginPane.getNameTxt().getText().equals("Admin")
                && loginPane.getPasswordField().getText().equals("")) {
            loginPane.resetUserOrPasswordWrong();
            loginPane.getLoggedInUserLbl().setText(loginPane.getNameTxt().getText());
            loginPane.getNameTxt().setText("");
            loginPane.getPasswordField().setText("");
            loginPane.setState(LoginPane.State.LOGOUT);
        } else {
            loginPane.indicateUserOrPasswordWrong();
        }
    }

    private void resetLogin() {
        if (loginPane.getInputWrongLbl().isVisible()) {
            loginPane.resetUserOrPasswordWrong();
        }
        loginPane.getNameTxt().setText("");
        loginPane.getPasswordField().setText("");
        loginPane.getLoggedInUserLbl().setText("");
        loginPane.setState(LoginPane.State.LOGIN);
    }

    private void showHideMainMenu(final ForegroundPane foregroundPane) {
        //TODO: Resize the pain correctly
        if (foregroundPane.getMainMenu().isMaximized()) {
            foregroundPane.getMainMenu().minimizeMainMenu();
        } else {
            foregroundPane.getMainMenu().maximizeMainMenu();
        }

    }

    private void chooseTheme() {
        final ResourceBundle languageBundle = ResourceBundle
                .getBundle(Constants.LANGUAGE_RESOURCE_BUNDLE, Locale.getDefault());

        settingsPane.getThemeChoice().getSelectionModel().selectedIndexProperty()
                .addListener(new ChangeListener<Number>() {

            @Override
            public void changed(final ObservableValue<? extends Number> observableValue, final Number number,
                                final Number number2) {
                if (settingsPane.getAvailableThemes().get(number2.intValue())
                        .equals(languageBundle.getString(Constants.LIGHT_THEME_CSS_NAME))) {
                    BCozy.changeTheme(Constants.LIGHT_THEME_CSS);
                } else if (settingsPane.getAvailableThemes().get(number2.intValue())
                        .equals(languageBundle.getString(Constants.DARK_THEME_CSS_NAME))) {
                    BCozy.changeTheme(Constants.DARK_THEME_CSS);
                }
            }
        });
    }

    private void chooseLanguage() {
        settingsPane.getLanguageChoice().getSelectionModel().selectedIndexProperty()
                .addListener(new ChangeListener<Number>() {

                    @Override
                    public void changed(final ObservableValue<? extends Number> observableValue, final Number number,
                                        final Number number2) {
                        if (settingsPane.getAvailableLanguages().get(number2.intValue()).equals("English")) {
                            LanguageSelection.getInstance().setSelectedLocale(new Locale("en", "US"));
                        } else if (settingsPane.getAvailableLanguages().get(number2.intValue()).equals("Deutsch")) {
                            LanguageSelection.getInstance().setSelectedLocale(new Locale("de", "DE"));
                        }
                    }
                });
    }
}
