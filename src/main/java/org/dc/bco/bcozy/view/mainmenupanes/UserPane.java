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

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.dc.bco.bcozy.view.Constants;
import org.dc.bco.bcozy.view.devicepanes.PaneElement;

/**
 * Created by hoestreich on 11/24/15.
 */
public class UserPane extends VBox {

    private final PaneElement userIcon;
    private final PaneElement login;
    private final PaneElement loginPane;
    private final PaneElement loggedInBottomPane;
    private final PaneElement loggedInUserPane;
    private final Button openLoginBtn;
    private final Button loginBtn;
    private final Button backBtn;
    private final Button logoutBtn;

    /**
     * Enum to control the display state.
     */
    public enum State { NOLOGIN, LOGINACTIVE, LOGIN }

    /**
     * Constructor for the UserPane.
     */
    public UserPane() {

        // Case: No user logged in
        final Image icon = new Image(getClass().getResourceAsStream("/icons/user_fa.png"));
        final ImageView userIconImageView = new ImageView(icon);
        userIconImageView.setFitHeight(Constants.BIGICON);
        userIconImageView.setFitWidth(Constants.BIGICON);
        userIcon = new PaneElement(userIconImageView);
        userIcon.setMaxWidth(Constants.MAXMENUWIDTH);
        openLoginBtn = new Button("Open Login");
        login = new PaneElement(openLoginBtn);
        login.setMaxWidth(Constants.MAXMENUWIDTH);

        // Case: Login active
        final Label nameLbl = new Label("Name:");
        final TextField nameTxt = new TextField();
        final Label pwLbl = new Label("Password:");
        final PasswordField passwordField = new PasswordField();
        loginBtn = new Button("Login");
        final Image backIcon = new Image(getClass().getResourceAsStream("/icons/back.png"));
        final ImageView imageViewBackIcon = new ImageView(backIcon);
        imageViewBackIcon.setFitWidth(Constants.SMALLICON);
        imageViewBackIcon.setFitHeight(Constants.SMALLICON);
        backBtn = new Button("", imageViewBackIcon);
        final VBox loginLayout = new VBox(Constants.INSETS);
        final BorderPane loginFirstLineLayout = new BorderPane();
        loginFirstLineLayout.setLeft(nameLbl);
        loginFirstLineLayout.setRight(backBtn);
        loginLayout.getChildren().addAll(loginFirstLineLayout, nameTxt, pwLbl, passwordField, loginBtn);
        loginPane = new PaneElement(loginLayout);
        loginPane.setMaxWidth(Constants.MAXMENUWIDTH);

        //Case: User logged in
        final Image loggedInUserIcon = new Image(getClass().getResourceAsStream("/icons/user.png"));
        final ImageView loggedInUserIconImageView = new ImageView(loggedInUserIcon);
        loggedInUserIconImageView.setFitHeight(Constants.BIGICON);
        loggedInUserIconImageView.setFitWidth(Constants.BIGICON);
        loggedInUserPane = new PaneElement(loggedInUserIconImageView);
        loggedInUserPane.setMaxWidth(Constants.MAXMENUWIDTH);
        final Label loggedInUserLbl = new Label("Timo");
        final Image logoutIcon = new Image(getClass().getResourceAsStream("/icons/logout.png"));
        final ImageView imageViewLogoutIcon = new ImageView(logoutIcon);
        imageViewLogoutIcon.setFitWidth(Constants.SMALLICON);
        imageViewLogoutIcon.setFitHeight(Constants.SMALLICON);
        logoutBtn = new Button("", imageViewLogoutIcon);
        final BorderPane loggedInBottomLayout = new BorderPane();
        loggedInBottomLayout.setLeft(loggedInUserLbl);
        loggedInBottomLayout.setRight(logoutBtn);
        loggedInBottomPane = new PaneElement(loggedInBottomLayout);
        loggedInBottomPane.setMaxWidth(Constants.MAXMENUWIDTH);

        this.setFillWidth(true);
        this.setSpacing(Constants.INSETS);

        this.getChildren().addAll(userIcon, login);

    }

    /**
     * Getter for the Button which opens the login menu.
     * @return instance of the button
     */
    public Button getOpenLoginBtn() {
        return openLoginBtn;
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
     * Getter for the logout which logs out the current user.
     * @return instance of the button
     */
    public Button getLogoutBtn() {
        return logoutBtn;
    }

    /**
     * GUI Method to switch the displayed panes.
     * @param state A state from the defined Enum
     */
    public void setState(final State state) {
        switch (state) {
            case NOLOGIN:
                this.getChildren().clear();
                this.getChildren().addAll(userIcon, login);
                break;

            case LOGINACTIVE:
                this.getChildren().clear();
                this.getChildren().addAll(loginPane);
                break;

            case LOGIN:
                this.getChildren().clear();
                this.getChildren().addAll(loggedInUserPane, loggedInBottomPane);
                break;

            default:
                this.getChildren().clear();
                this.getChildren().addAll(userIcon, login);
                break;

        }
    }
}
