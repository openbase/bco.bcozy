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
package org.openbase.bco.bcozy.view.mainmenupanes;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openbase.bco.authentication.lib.SessionManager;
import org.openbase.bco.bcozy.controller.InitialPasswordChangeController;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.ObserverButton;
import org.openbase.bco.bcozy.view.ObserverLabel;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.schedule.GlobalCachedExecutorService;
import org.openbase.jul.visual.javafx.JFXConstants;
import org.openbase.jul.visual.javafx.geometry.svg.SVGGlyphIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


/**
 * @author hoestreich
 * @author vdasilva
 */
public class LoginPane extends PaneElement {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginPane.class);

    private static final String INITIAL_PASSWORD = "admin";

    private final ObserverButton loginButton;
    private final ObserverButton logoutButton;
    private final TextField userNameTextField;
    private final PasswordField passwordField;
    private final ObserverLabel inputWrongLabel;
    private final Label userLabel;
    private final VBox loginLayout;
    private final VBox userInfoLayout;
    private final ObserverLabel nameLabel;
    private final ObserverLabel passwordLabel;
    private final VBox statusIcon;

    /**
     * Constructor for the LoginPane.
     */
    public LoginPane() {
        nameLabel = new ObserverLabel("username");
        nameLabel.setAlignment(Pos.BOTTOM_LEFT);
        userNameTextField = new TextField();
        passwordLabel = new ObserverLabel("password");
        passwordField = new PasswordField();
        inputWrongLabel = new ObserverLabel("inputWrong");
        inputWrongLabel.setAlignment(Pos.TOP_LEFT);
        loginButton = new ObserverButton("login");

        final HBox rightAlignLoginButton = new HBox(loginButton);
        rightAlignLoginButton.setAlignment(Pos.CENTER_RIGHT);

        loginLayout = new VBox(Constants.INSETS);
        final BorderPane loginFirstLineLayout = new BorderPane();
        loginFirstLineLayout.setLeft(nameLabel);
        loginLayout.getStyleClass().clear();
        loginLayout.setAlignment(Pos.BOTTOM_LEFT);
        loginLayout.getChildren().addAll(loginFirstLineLayout, userNameTextField, passwordLabel, passwordField,
                rightAlignLoginButton);

        //Case: User logged in
        userInfoLayout = new VBox(Constants.INSETS);
        final SVGGlyphIcon loggedInUserIcon = new SVGGlyphIcon(MaterialDesignIcon.ACCOUNT_CIRCLE, JFXConstants.ICON_SIZE_SMALL, true);
        userLabel = new Label();
        logoutButton = new ObserverButton("logout");
        final HBox rightAlignLogoutButton = new HBox(logoutButton);
        rightAlignLogoutButton.setAlignment(Pos.CENTER_RIGHT);

        userInfoLayout.getStyleClass().clear();
        userInfoLayout.setAlignment(Pos.TOP_CENTER);
        userInfoLayout.getChildren().addAll(loggedInUserIcon, userLabel, rightAlignLogoutButton);

        //Setting styles
        nameLabel.getStyleClass().clear();
        nameLabel.getStyleClass().add("small-label");
        inputWrongLabel.getStyleClass().clear();
        inputWrongLabel.getStyleClass().add("wrong-input-indicator");
        passwordLabel.getStyleClass().clear();
        passwordLabel.getStyleClass().add("small-label");
        loginButton.getStyleClass().clear();
        loginButton.getStyleClass().add("transparent-button");
        logoutButton.getStyleClass().clear();
        logoutButton.getStyleClass().add("transparent-button");

        this.setPrefHeight(100);
        this.setMaxHeight(100);

        this.statusIcon = new VBox(new SVGGlyphIcon(MaterialDesignIcon.LOGIN, JFXConstants.ICON_SIZE_MIDDLE, true));

        loginButton.setOnAction(event -> loginUser());
        logoutButton.setOnAction(event -> resetLogin());
        passwordField.setOnAction(event -> loginUser());
        userNameTextField.setOnAction(event -> loginUser());
        userNameTextField.setOnKeyTyped(event -> resetWrongInput());
        passwordField.setOnKeyTyped(event -> resetWrongInput());

        SessionManager.getInstance().addLoginObserver((observable, userAtClientId) -> onLoggedInChanged());
        onLoggedInChanged();
    }

    /**
     * Change CSS Style to indicate that at least one of the informations
     * password or the name were wrong.
     */
    private void indicateUserOrPasswordWrong() {
        if (!loginLayout.getChildren().contains(inputWrongLabel)) {
            passwordField.getStyleClass().add("password-field-wrong");
            userNameTextField.getStyleClass().add("text-field-wrong");
            loginLayout.getChildren().add(loginLayout.getChildren().size() - 1, inputWrongLabel);
        }
    }

    /**
     * Reset CSS Style if name or password are corrected.
     */
    private void resetUserOrPasswordWrong() {
        passwordField.getStyleClass().clear();
        userNameTextField.getStyleClass().clear();
        passwordField.getStyleClass().add("password-field");
        userNameTextField.getStyleClass().add("text-field");
        loginLayout.getChildren().remove(inputWrongLabel);
    }

    @Override
    public Node getStatusIcon() {
        return this.statusIcon;
    }

    /**
     * GUI Method to switch the displayed panes.
     *
     * @param state A state from the defined Enum
     */
    private void setState(final State state) {
        switch (state) {
            case SHOW_LOGIN:
                this.getChildren().clear();
                this.getChildren().addAll(loginLayout);
                this.statusIcon.getChildren().clear();
                this.statusIcon.getChildren().addAll(new SVGGlyphIcon(MaterialDesignIcon.LOGIN, JFXConstants.ICON_SIZE_MIDDLE, true));
                break;

            case SHOW_USER_INFO:
                this.getChildren().clear();
                this.getChildren().addAll(userInfoLayout);
                this.statusIcon.getChildren().clear();
                this.statusIcon.getChildren().addAll(new SVGGlyphIcon(MaterialDesignIcon.LOGOUT, JFXConstants.ICON_SIZE_MIDDLE, true));
                break;
        }
    }

    private void resetWrongInput() {
        if (inputWrongLabel.isVisible()) {
            resetUserOrPasswordWrong();
        }
    }

    private void loginUser() {
        GlobalCachedExecutorService.submit(this::loginUserAsync);
    }

    private void loginUserAsync() {
        try {
            final String password = passwordField.getText();
            final String userName = userNameTextField.getText();
            final String userId = Registries.getUnitRegistry().getUserUnitIdByUserName(userName);
            SessionManager.getInstance().login(userId, password);

            if (password.equals(INITIAL_PASSWORD)) {
                showChangeInitialPassword();
            }
        } catch (CouldNotPerformException ex) {
            Platform.runLater(this::indicateUserOrPasswordWrong);
        }
    }

    private void showChangeInitialPassword() {
        Platform.runLater(() -> {
            try {
                InitialPasswordChangeController.loadModalStage().getKey().show();
            } catch (IOException ioe) {
                ExceptionPrinter.printHistory(ioe, LOGGER);
            }
        });
    }

    private void resetLogin() {
        SessionManager.getInstance().logout();

        if (inputWrongLabel.isVisible()) {
            resetUserOrPasswordWrong();
        }
        userNameTextField.setText("");
        passwordField.setText("");
        userLabel.setText("");
    }

    private void onLoggedInChanged() {
        if (SessionManager.getInstance().isLoggedIn()) {
            // load user name from registry if possible.
            String displayedUserName;
            try {
                displayedUserName = Registries.getUnitRegistry().getUnitConfigById(SessionManager.getInstance().getUserId()).getUserConfig().getUserName();
            } catch (CouldNotPerformException ex) {
                displayedUserName = userNameTextField.getText();
            }

            final String userName = displayedUserName;

            Platform.runLater(() -> {
                resetUserOrPasswordWrong();

                userLabel.setText(userName);
                userNameTextField.setText("");
                passwordField.setText("");
                setState(State.SHOW_USER_INFO);
            });
        } else {
            Platform.runLater(() -> {
                if (inputWrongLabel.isVisible()) {
                    resetUserOrPasswordWrong();
                }
                userNameTextField.setText("");
                passwordField.setText("");
                userLabel.setText("");
                setState(LoginPane.State.SHOW_LOGIN);
            });
        }
    }

    /**
     * Enum to control the display state.
     */
    public enum State {
        SHOW_LOGIN, SHOW_USER_INFO
    }
}
