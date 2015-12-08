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
package org.dc.bco.bcozy.view;

//import javafx.animation.KeyFrame;
//import javafx.animation.KeyValue;
//import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.dc.bco.bcozy.view.mainmenupanes.ConnectionPane;
import org.dc.bco.bcozy.view.mainmenupanes.UserPane;

/**
 * Created by hoestreich on 11/10/15.
 */
public class MainMenu extends StackPane {

    private final Button initRemoteButton;
    private final Button fetchLocationButton;
    private final Button fillHashesButton;
    private final UserPane userPane;
    private final FloatingButton mainMenuFloatingButton;
    private final VBox verticalLayout;
    private final VBox verticalLayoutSmall;
    private final double height;
    private final double width;
    private boolean maximized;
    private final Image logoImage;
    private final ImageView logoView;
    private final Image logoImageSmall;
    private final ImageView logoViewSmall;
    private final ConnectionPane connectionPane;

    /**
     * Constructor for the MainMenu.
     * @param height Height
     * @param width Width
     */
    public MainMenu(final double height, final double width) {

        this.height = height;
        this.width = width;
        this.maximized = true;
        this.setMinHeight(height);
        this.setMinWidth(width);

        verticalLayout = new VBox(Constants.INSETS);
        verticalLayout.setAlignment(Pos.TOP_CENTER);

        verticalLayoutSmall = new VBox(Constants.INSETS);
        verticalLayoutSmall.setAlignment(Pos.TOP_CENTER);

        userPane = new UserPane();

        initRemoteButton = new Button("Init RegistryRemotes");
        fetchLocationButton = new Button("Fetch Location");
        fillHashesButton = new Button("Fill Hashes");

        logoImage = new Image(getClass().getResourceAsStream("/icons/bcozy.png"));
        logoView = new ImageView(logoImage);
        logoView.setSmooth(true);
        logoView.setFitWidth(Constants.MAXLOGOWIDTH);
        logoView.setPreserveRatio(true);

        logoImageSmall = new Image(getClass().getResourceAsStream("/icons/bc.png"));
        logoViewSmall = new ImageView(logoImageSmall);
        logoViewSmall.setSmooth(true);
        logoViewSmall.setFitWidth(Constants.MIDDLEICON);
        logoViewSmall.setPreserveRatio(true);

        connectionPane = new ConnectionPane();

        //verticalLayout.setPadding(new Insets(0.0, Constants.SMALLICON / 2, 0.0, 0.0));
        verticalLayout.getChildren()
                .addAll(logoView, userPane, initRemoteButton, fetchLocationButton, fillHashesButton, connectionPane);

        //verticalLayoutSmall.setPadding(new Insets(0.0, Constants.SMALLICON / 2, 0.0, 0.0));
        verticalLayoutSmall.getChildren()
                .addAll(logoViewSmall);

        mainMenuFloatingButton = new FloatingButton("/icons/mainmenu.png");

        this.getChildren().addAll(verticalLayout, mainMenuFloatingButton);
        this.setAlignment(mainMenuFloatingButton, Pos.TOP_RIGHT);
        this.setMargin(mainMenuFloatingButton, new Insets(Constants.SMALLICON, 0, 0, 0));
        mainMenuFloatingButton.translateXProperty().set(Constants.SMALLICON);

        //CHECKSTYLE.OFF: MultipleStringLiterals
        initRemoteButton.getStyleClass().addAll("large-button", "visible-lg", "visible-md", "visible-sm", "visible-xs");
        fetchLocationButton.getStyleClass()
                .addAll("large-button", "visible-lg", "visible-md", "visible-sm", "visible-xs");
        fillHashesButton.getStyleClass().addAll("large-button", "visible-lg", "visible-md", "visible-sm", "visible-xs");

        verticalLayout.getStyleClass().addAll("padding-large");
        verticalLayoutSmall.getStyleClass().addAll("padding-small");

        this.getStyleClass().addAll("dropshadow-right-bg", "floating-box", "main-menu", "padding-large");
        //CHECKSTYLE.ON: MultipleStringLiterals
    }

    /**
     * Configure the initRemoteButton.
     * @param eventHandler EventHandler
     */
    public void addInitRemoteButtonEventHandler(final EventHandler<ActionEvent> eventHandler) {
        initRemoteButton.setOnAction(eventHandler);
    }

    /**
     * Configure the fetchLocationButton.
     * @param eventHandler EventHandler
     */
    public void addFetchLocationButtonEventHandler(final EventHandler<ActionEvent> eventHandler) {
        fetchLocationButton.setOnAction(eventHandler);
    }

    /**
     * Configure the fillHashesButton.
     * @param eventHandler EventHandler
     */
    public void addFillHashesButtonEventHandler(final EventHandler<ActionEvent> eventHandler) {
        fillHashesButton.setOnAction(eventHandler);
    }

    /**
     * Getter for the main menu button.
     * @return the instance of the main menu button
     */
    public FloatingButton getMainMenuFloatingButton() {
        return mainMenuFloatingButton;
    }

    /**
     * Getter for the UserPane.
     * @return the instance of the userPane
     */
    public UserPane getUserPane() {
        return userPane;
    }

    /**
     * Getter for the current display state.
     * @return true if maximzed, false if minimized
     */
    public boolean isMaximized() {
        return maximized;
    }

    /**
     * Method to make this menu visible.
     * Animations should be added in the future
     */
    public void maximizeMainMenu() {
        this.maximized = true;
        this.setMinHeight(height);
        this.setMinWidth(width);
        this.verticalLayout.getChildren().add(connectionPane);
        this.getChildren().clear();
        this.getChildren().addAll(verticalLayout, mainMenuFloatingButton);
    }

    /**
     * Method to make this menu invisible.
     * Animations should be added in the future
     */
    public void minimizeMainMenu() {
        this.maximized = false;
        //TODO: This will be implemented soon.
        this.setMinHeight(height / 2);
        this.setMinWidth(width / 2);
        this.verticalLayoutSmall.getChildren().add(connectionPane);
        this.getChildren().clear();
        this.getChildren().addAll(verticalLayoutSmall, mainMenuFloatingButton);
    }
}
