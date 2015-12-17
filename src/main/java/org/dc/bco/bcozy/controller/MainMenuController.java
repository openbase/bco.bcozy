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
import org.dc.bco.bcozy.view.Constants;
import org.dc.bco.bcozy.view.ForegroundPane;
import org.dc.bco.bcozy.view.mainmenupanes.SettingsPane;
import org.dc.bco.bcozy.view.mainmenupanes.UserPane;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by hoestreich on 11/24/15.
 */
public class MainMenuController {

    private final UserPane userPane;
    private final SettingsPane settingsPane;
    /**
     * Constructor for the MainMenuController.
     * @param foregroundPane The foregroundPane allows to access all necessary gui elements
     */
    public MainMenuController(final ForegroundPane foregroundPane) {
        userPane = foregroundPane.getMainMenu().getUserPane();
        settingsPane = foregroundPane.getMainMenu().getSettingsPane();
        userPane.getStartLoginBtn().setOnAction(event -> startLogin());
        userPane.getLoginBtn().setOnAction(event -> loginUser());
        userPane.getBackBtn().setOnAction(event -> resetLogin());
        userPane.getLogoutBtn().setOnAction(event -> resetLogin());
        userPane.getPasswordField().setOnAction(event -> loginUser());
        userPane.getNameTxt().setOnAction(event -> loginUser());
        userPane.getNameTxt().setOnKeyTyped(event -> resetWrongInput());
        userPane.getPasswordField().setOnKeyTyped(event -> resetWrongInput());
        settingsPane.getThemeChoice().setOnAction(event -> chooseTheme());
        settingsPane.getLanguageChoice().setOnAction(event -> chooseLanguage());
        //Necessary to ensure that the first change is not missed by the ChangeListener
        settingsPane.getThemeChoice().getSelectionModel().select(0);

        foregroundPane.getMainMenu().getMainMenuFloatingButton().setOnAction(event -> showHideMainMenu(foregroundPane));
    }


    private void startLogin() {
        userPane.setState(UserPane.State.LOGINACTIVE);
    }

    private void resetWrongInput() {
        if (userPane.getInputWrongLbl().isVisible()) {
            userPane.resetUserOrPasswordWrong();
        }
    }

    private void loginUser() {
        //TODO: Initiate Login with UserRegistry
        if (userPane.getNameTxt().getText().equals("Admin")
                && userPane.getPasswordField().getText().equals("")) {
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
        if (userPane.getInputWrongLbl().isVisible()) {
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
        final ResourceBundle languageBundle = ResourceBundle
                .getBundle(Constants.LANGUAGE_RESOURCE_BUNDLE, Locale.getDefault());
        settingsPane.getLanguageChoice().getSelectionModel().selectedIndexProperty()
                .addListener(new ChangeListener<Number>() {

                    @Override
                    public void changed(final ObservableValue<? extends Number> observableValue, final Number number,
                                        final Number number2) {
                        if (settingsPane.getAvailableLanguages().get(number2.intValue()).equals("English")) {
                            Locale.setDefault(new Locale("en", "US"));
                        } else if (settingsPane.getAvailableLanguages().get(number2.intValue()).equals("Deutsch")) {
                            Locale.setDefault(new Locale("de", "DE"));
                        }
                    }
                });
        userPane.getInputWrongLbl().setText(languageBundle.getString("inputWrong"));
        userPane.getNameLbl().setText(languageBundle.getString("username"));
        userPane.getPwLbl().setText(languageBundle.getString("password"));
        userPane.getLoginBtn().setText(languageBundle.getString("login"));
        userPane.getLogoutBtn().setText(languageBundle.getString("logout"));

        settingsPane.getSettingsLbl().setText(languageBundle.getString("settings"));
    }
}
