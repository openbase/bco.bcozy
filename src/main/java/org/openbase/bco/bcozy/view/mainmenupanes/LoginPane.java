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
package org.openbase.bco.bcozy.view.mainmenupanes;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openbase.bco.authentication.lib.SessionManager;
import org.openbase.bco.bcozy.controller.InitialPasswordChangeController;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.ObserverButton;
import org.openbase.bco.bcozy.view.ObserverLabel;
import org.openbase.jul.visual.javafx.JFXConstants;
import org.openbase.jul.visual.javafx.geometry.svg.SVGGlyphIcon;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.schedule.GlobalCachedExecutorService;
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

    private final ObserverButton loginBtn;
    private final ObserverButton logoutBtn;
    private final TextField nameTxt;
    private final PasswordField passwordField;
    private final ObserverLabel inputWrongLbl;
    private final Label loggedInUserLbl;
    private final VBox loginLayout;
    private final VBox logoutLayout;
    private final ObserverLabel nameLbl;
    private final ObserverLabel pwLbl;
    private final VBox statusIcon;

    /**
     * Enum to control the display state.
     */
    public enum State {
        LOGINACTIVE, LOGOUT
    }

    /**
     * Constructor for the LoginPane.
     */
    public LoginPane() {

        // Case: Login active
        nameLbl = new ObserverLabel("username");
        nameLbl.setAlignment(Pos.BOTTOM_LEFT);
        nameTxt = new TextField();
        pwLbl = new ObserverLabel("password");
        passwordField = new PasswordField();
        inputWrongLbl = new ObserverLabel("inputWrong");
        inputWrongLbl.setAlignment(Pos.TOP_LEFT);
        loginBtn = new ObserverButton("login");

        final HBox rightAlignLoginButton = new HBox(loginBtn);
        rightAlignLoginButton.setAlignment(Pos.CENTER_RIGHT);

        loginLayout = new VBox(Constants.INSETS);
        final BorderPane loginFirstLineLayout = new BorderPane();
        loginFirstLineLayout.setLeft(nameLbl);
        loginLayout.getStyleClass().clear();
        loginLayout.setAlignment(Pos.BOTTOM_LEFT);
        loginLayout.getChildren().addAll(loginFirstLineLayout, nameTxt, pwLbl, passwordField,
                rightAlignLoginButton);

        //Case: User logged in
        logoutLayout = new VBox(Constants.INSETS);
        final SVGGlyphIcon loggedInUserIcon = new SVGGlyphIcon(MaterialDesignIcon.ACCOUNT_CIRCLE, JFXConstants.ICON_SIZE_SMALL, true);
        loggedInUserLbl = new Label();
        logoutBtn = new ObserverButton("logout");
        final HBox rightAlignLogoutButton = new HBox(logoutBtn);
        rightAlignLogoutButton.setAlignment(Pos.CENTER_RIGHT);

        logoutLayout.getStyleClass().clear();
        logoutLayout.setAlignment(Pos.TOP_CENTER);
        logoutLayout.getChildren().addAll(loggedInUserIcon, loggedInUserLbl, rightAlignLogoutButton);

        //Setting styles
        //CHECKSTYLE.OFF: MultipleStringLiterals
        nameLbl.getStyleClass().clear();
        nameLbl.getStyleClass().add("small-label");
        inputWrongLbl.getStyleClass().clear();
        inputWrongLbl.getStyleClass().add("wrong-input-indicator");
        pwLbl.getStyleClass().clear();
        pwLbl.getStyleClass().add("small-label");
        loginBtn.getStyleClass().clear();
        loginBtn.getStyleClass().add("transparent-button");
        logoutBtn.getStyleClass().clear();
        logoutBtn.getStyleClass().add("transparent-button");
        //CHECKSTYLE.ON: MultipleStringLiterals

        this.setPrefHeight(100);
        this.setMaxHeight(100);

        this.statusIcon = new VBox(new SVGGlyphIcon(MaterialDesignIcon.LOGIN, JFXConstants.ICON_SIZE_MIDDLE, true));
        setState(State.LOGINACTIVE);

        getLoginBtn().setOnAction(event -> loginUser());
        getLogoutBtn().setOnAction(event -> resetLogin());
        getPasswordField().setOnAction(event -> loginUser());
        getNameTxt().setOnAction(event -> loginUser());
        getNameTxt().setOnKeyTyped(event -> resetWrongInput());
        getPasswordField().setOnKeyTyped(event -> resetWrongInput());

    }

    /**
     * Getter for the login button which initiates the user login.
     *
     * @return instance of the button
     */
    public ObserverButton getLoginBtn() {
        return loginBtn;
    }


    /**
     * Getter for the name textfield.
     *
     * @return instance of the textfield
     */
    public TextField getNameTxt() {
        return nameTxt;
    }

    /**
     * Getter for the passwordfield.
     *
     * @return instance of the passwordfield
     */
    public PasswordField getPasswordField() {
        return passwordField;
    }

    /**
     * Getter for the logoutBtn.
     *
     * @return instance of the logoutBtn
     */
    public ObserverButton getLogoutBtn() {
        return logoutBtn;
    }

    /**
     * Getter for the inputWrongLabel.
     *
     * @return instance of the inputWrongLbl
     */
    public ObserverLabel getInputWrongLbl() {
        return inputWrongLbl;
    }

    /**
     * Getter for the loggedInUserLbl.
     *
     * @return instance of the loggedInUserLbl
     */
    public Label getLoggedInUserLbl() {
        return loggedInUserLbl;
    }

    /**
     * Getter for the pwLbl.
     *
     * @return instance of the pwLbl
     */
    public ObserverLabel getPwLbl() {
        return pwLbl;
    }

    /**
     * Getter for the nameLbl.
     *
     * @return instance of the nameLbl
     */
    public ObserverLabel getNameLbl() {
        return nameLbl;
    }

    /**
     * Change CSS Style to indicate that at least one of the informations
     * password or the name were wrong.
     */
    public void indicateUserOrPasswordWrong() {
        if (!loginLayout.getChildren().contains(inputWrongLbl)) {
            passwordField.getStyleClass().add("password-field-wrong");
            nameTxt.getStyleClass().add("text-field-wrong");
            loginLayout.getChildren().add(loginLayout.getChildren().size() - 1, inputWrongLbl);
        }
    }

    /**
     * Reset CSS Style if name or password are corrected.
     */
    public void resetUserOrPasswordWrong() {
        passwordField.getStyleClass().clear();
        nameTxt.getStyleClass().clear();
        passwordField.getStyleClass().add("password-field");
        nameTxt.getStyleClass().add("text-field");
        if (loginLayout.getChildren().contains(inputWrongLbl)) {
            loginLayout.getChildren().remove(inputWrongLbl);
        }
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
    public void setState(final State state) {
        switch (state) {

            case LOGINACTIVE:
                this.getChildren().clear();
                this.getChildren().addAll(loginLayout);
                this.statusIcon.getChildren().clear();
                this.statusIcon.getChildren().addAll(new SVGGlyphIcon(MaterialDesignIcon.LOGIN, JFXConstants.ICON_SIZE_MIDDLE,
                        true));
                break;

            case LOGOUT:
                this.getChildren().clear();
                this.getChildren().addAll(logoutLayout);
                this.statusIcon.getChildren().clear();
                this.statusIcon.getChildren().addAll(new SVGGlyphIcon(MaterialDesignIcon.LOGOUT, JFXConstants.ICON_SIZE_MIDDLE,
                        true));
                break;

        }
    }

    private void startLogin() {
        setState(LoginPane.State.LOGINACTIVE);
    }

    private void resetWrongInput() {
        if (getInputWrongLbl().isVisible()) {
            resetUserOrPasswordWrong();
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
            final String password = getPasswordField().getText();
            sessionManager.login(Registries.getUserRegistry().getUserIdByUserName(getNameTxt().getText()), password);
            Platform.runLater(() -> {
                resetUserOrPasswordWrong();
                getLoggedInUserLbl().setText(getNameTxt().getText());
                getNameTxt().setText("");
                getPasswordField().setText("");
                setState(LoginPane.State.LOGOUT);
            });

            if (password.equals(INITIAL_PASSWORD)) {
                showChangeInitialPassword();
            }
        } catch (CouldNotPerformException ex) {
            Platform.runLater(() -> {
                indicateUserOrPasswordWrong();
            });
        } catch (java.lang.OutOfMemoryError error) {
            LOGGER.error(error.getMessage());
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

        if (getInputWrongLbl().isVisible()) {
            resetUserOrPasswordWrong();
        }
        getNameTxt().setText("");
        getPasswordField().setText("");
        getLoggedInUserLbl().setText("");
        setState(LoginPane.State.LOGINACTIVE);
    }
}
