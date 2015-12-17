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
package org.dc.bco.bcozy.view.mainmenupanes;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.dc.bco.bcozy.view.Constants;
import org.dc.bco.bcozy.view.ImageViewProvider;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by hoestreich on 11/24/15.
 */
public class UserPane extends VBox {

    private final PaneElement loginPane;
    private final PaneElement startLoginPane;
    private final PaneElement logoutPane;
    private final Button startLoginBtn;
    private final Button loginBtn;
    private final Button backBtn;
    private final Button logoutBtn;
    private final TextField nameTxt;
    private final PasswordField passwordField;
    private final Label inputWrongLbl;
    private final Label loggedInUserLbl;
    /**
     * Enum to control the display state.
     */
    public enum State { LOGINACTIVE, LOGIN, LOGOUT }

    /**
     * Constructor for the UserPane.
     */
    public UserPane() {

        final ResourceBundle languageBundle = ResourceBundle
                .getBundle("languages.languages", new Locale("en", "US"));

        // Case: Before login
        final ImageView addUserIconImageView = ImageViewProvider
                .createImageView("/icons/login.png", Constants.SMALL_ICON);
        startLoginBtn = new Button("", addUserIconImageView);
        startLoginBtn.getStyleClass().clear();
        startLoginPane = new PaneElement(startLoginBtn);

        // Case: Login active
        final Label nameLbl = new Label(languageBundle.getString("username"));
        nameLbl.setAlignment(Pos.BOTTOM_LEFT);
        nameTxt = new TextField();
        final Label pwLbl = new Label(languageBundle.getString("password"));
        passwordField = new PasswordField();
        inputWrongLbl = new Label(languageBundle.getString("inputWrong"));
        inputWrongLbl.setAlignment(Pos.TOP_LEFT);
        inputWrongLbl.setVisible(false);
        loginBtn = new Button(languageBundle.getString("login"));
        final HBox rightAlignLoginButton = new HBox(loginBtn);
        rightAlignLoginButton.setAlignment(Pos.CENTER_RIGHT);

        final ImageView imageViewBackIcon = ImageViewProvider
                .createImageView("/icons/back.png", Constants.EXTRA_SMALL_ICON);
        backBtn = new Button("", imageViewBackIcon);
        backBtn.getStyleClass().clear();

        final VBox loginLayout = new VBox(Constants.INSETS);
        final BorderPane loginFirstLineLayout = new BorderPane();
        loginFirstLineLayout.setLeft(nameLbl);
        loginFirstLineLayout.setRight(backBtn);
        loginLayout.getStyleClass().clear();
        loginLayout.setAlignment(Pos.BOTTOM_LEFT);
        loginLayout.getChildren().addAll(loginFirstLineLayout, nameTxt, pwLbl, passwordField, inputWrongLbl,
                rightAlignLoginButton);
        loginPane = new PaneElement(loginLayout);
        loginPane.setMaxWidth(Constants.MAX_MENU_WIDTH);

        //Case: User logged in
        final VBox logoutLayout = new VBox(Constants.INSETS);
        final ImageView loggedInUserIcon = ImageViewProvider
                .createImageView("/icons/user_fa.png", Constants.SMALL_ICON);
        loggedInUserLbl = new Label();
        logoutBtn = new Button(languageBundle.getString("logout"));
        final HBox rightAlignLogoutButton = new HBox(logoutBtn);
        rightAlignLogoutButton.setAlignment(Pos.CENTER_RIGHT);

        logoutLayout.getStyleClass().clear();
        logoutLayout.setAlignment(Pos.TOP_CENTER);
        logoutLayout.getChildren().addAll(loggedInUserIcon, loggedInUserLbl, rightAlignLogoutButton);
        logoutPane = new PaneElement(logoutLayout);
        logoutPane.setMaxWidth(Constants.MAX_MENU_WIDTH);

        this.setFillWidth(true);
        this.setSpacing(Constants.INSETS);

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

        this.getChildren().addAll(startLoginPane);
    }

    /**
     * Getter for the startLogin button which starts the user login.
     * @return instance of the button
     */
    public Button getStartLoginBtn() {
        return startLoginBtn;
    }

    /**
     * Getter for the login button which initiates the user login.
     * @return instance of the button
     */
    public Button getLoginBtn() {
        return loginBtn;
    }

    /**
     * Getter for the back button to abort a login.
     * @return instance of the button
     */
    public Button getBackBtn() {
        return backBtn;
    }

    /**
     * Getter for the name textfield.
     * @return instance of the textfield
     */
    public TextField getNameTxt() {
        return nameTxt;
    }

    /**
     * Getter for the passwordfield.
     * @return instance of the passwordfield
     */
    public PasswordField getPasswordField() {
        return passwordField;
    }

    /**
     * Getter for the logoutBtn.
     * @return instance of the logoutBtn
     */
    public Button getLogoutBtn() {
        return logoutBtn;
    }

    /**
     * Getter for the inputWrongLabel.
     * @return instance of the inputWrongLbl
     */
    public Label getInputWrongLbl() {
        return inputWrongLbl;
    }
    /**
     * Getter for the loggedInUserLbl.
     * @return instance of the loggedInUserLbl
     */
    public Label getLoggedInUserLbl() {
        return loggedInUserLbl;
    }

    public void indicateUserOrPasswordWrong() {
        passwordField.setStyle("-fx-border-color: transparent transparent red transparent;");
        nameTxt.setStyle("-fx-border-color: transparent transparent red transparent;");
        inputWrongLbl.setVisible(true);
    }

    public void resetUserOrPasswordWrong() {
        passwordField.setStyle("-fx-border-color: transparent transparent black transparent;");
        nameTxt.setStyle("-fx-border-color: transparent transparent black transparent;");
        inputWrongLbl.setVisible(false);
    }
    /**
     * GUI Method to switch the displayed panes.
     * @param state A state from the defined Enum
     */
    public void setState(final State state) {
        switch (state) {

            case LOGINACTIVE:
                this.getChildren().clear();
                this.getChildren().addAll(loginPane);
                break;

            case LOGIN:
                this.getChildren().clear();
                this.getChildren().addAll(startLoginPane);
                break;

            case LOGOUT:
                this.getChildren().clear();
                this.getChildren().addAll(logoutPane);
                break;

            default:
                this.getChildren().clear();
                this.getChildren().addAll(startLoginPane);
                break;

        }
    }
}
