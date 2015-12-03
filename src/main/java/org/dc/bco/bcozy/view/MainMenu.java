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
import javafx.scene.layout.*;
import org.dc.bco.bcozy.view.mainmenupanes.UserPane;

/**
 * Created by hoestreich on 11/10/15.
 */
public class MainMenu extends StackPane {

    private final Button mainButton;
    private final Button locationButton;
    private final UserPane userPane;
    private final FloatingButton mainMenuFloatingButton;
    private final VBox verticalLayout;
    private final double height;
    private final double width;
    private boolean maximized;

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

        userPane = new UserPane();

        mainButton = new Button("Init RegistryRemotes");
        locationButton = new Button("Fetch Location");

        //CHECKSTYLE.OFF: MultipleStringLiterals
        mainButton.getStyleClass().addAll("large-button", "visible-lg", "visible-md", "visible-sm", "visible-xs");
        locationButton.getStyleClass().addAll("large-button", "visible-lg", "visible-md", "visible-sm", "visible-xs");
        //CHECKSTYLE.ON: MultipleStringLiterals

        verticalLayout.setPadding(new Insets(0.0, Constants.SMALLICON / 2, 0.0, 0.0));
        verticalLayout.getChildren().addAll(userPane, mainButton, locationButton);

        mainMenuFloatingButton = new FloatingButton("/icons/mainmenu.png");
        this.getChildren().addAll(verticalLayout, mainMenuFloatingButton);
        this.setAlignment(mainMenuFloatingButton, Pos.TOP_RIGHT);
        this.setMargin(mainMenuFloatingButton, new Insets(Constants.SMALLICON, 0, 0, 0));
        mainMenuFloatingButton.translateXProperty().set(Constants.SMALLICON);
        this.getStyleClass().addAll("dropshadow-right-bg", "floating-box", "main-menu", "padding-large");
    }

    /**
     * Configure the MainButton.
     * @param eventHandler EventHandler
     */
    public void addMainButtonEventHandler(final EventHandler<ActionEvent> eventHandler) {
        mainButton.setOnAction(eventHandler);
    }

    /**
     * Configure the LocationButton.
     * @param eventHandler EventHandler
     */
    public void addLocationButtonEventHandler(final EventHandler<ActionEvent> eventHandler) {
        locationButton.setOnAction(eventHandler);
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
        this.getChildren().clear();
        this.getChildren().addAll(verticalLayout, mainMenuFloatingButton);
    }

    /**
     * Method to make this menu invisible.
     * Animations should be added in the future
     */
    public void minimizeMainMenu() {
        this.maximized = false;
        this.setMinHeight(height / 3);
        this.setMinWidth(width / 3);
        this.getChildren().clear();
        this.getChildren().addAll(mainMenuFloatingButton);
    }
}
