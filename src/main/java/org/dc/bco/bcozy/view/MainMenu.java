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

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.dc.bco.bcozy.view.mainmenupanes.AvailableUsersPane;
import org.dc.bco.bcozy.view.mainmenupanes.ConnectionPane;
import org.dc.bco.bcozy.view.mainmenupanes.SettingsPane;
import org.dc.bco.bcozy.view.mainmenupanes.UserPane;

/**
 * Created by hoestreich on 11/10/15.
 */
public class MainMenu extends StackPane {


    private final Button initRemoteButton;
    private final Button fetchLocationButton;
    private final Button fillHashesButton;
    private final Button fillContextMenuButton;
    private final UserPane userPane;
    private final FloatingButton mainMenuFloatingButton;
    private final VBox verticalLayout;
    private final VBox verticalLayoutSmall;
    private final double height;
    private final double width;
    private boolean maximized;
    private final ConnectionPane connectionPane;
    private final AvailableUsersPane availableUsersPanePane;
    private final SettingsPane settingsPane;

    /**
     * Constructor for the MainMenu.
     * @param height Height
     * @param width Width
     */
    public MainMenu(final double height, final double width) {

        // Initializing the container (StackPane)
        this.height = height;
        this.width = width;
        this.maximized = true;
        this.setMinHeight(height);
        this.setMinWidth(width);

        // Initializing components
        verticalLayout = new VBox(Constants.INSETS);
        verticalLayout.setAlignment(Pos.TOP_CENTER);

        verticalLayoutSmall = new VBox(Constants.INSETS);
        verticalLayoutSmall.setAlignment(Pos.TOP_CENTER);

        userPane = new UserPane();

        initRemoteButton = new Button("Init RegistryRemotes");
        fetchLocationButton = new Button("Fetch Location");
        fillHashesButton = new Button("Fill Hashes");
        fillContextMenuButton = new Button("Fill ContextMenu");

        final ImageView logoView = ImageViewProvider
                .createImageView("/icons/bcozy.png", Constants.MAXLOGOWIDTH, Double.MAX_VALUE);

        final ImageView logoViewSmall = ImageViewProvider.createImageView("/icons/bc.png", Constants.MIDDLE_ICON);

        connectionPane = new ConnectionPane();

        availableUsersPanePane = new AvailableUsersPane();

        settingsPane = new SettingsPane();

        mainMenuFloatingButton = new FloatingButton("/icons/mainmenu.png", Constants.MIDDLE_ICON);

        // Setting Alignment in Stackpane
        StackPane.setAlignment(mainMenuFloatingButton, Pos.TOP_RIGHT);
        StackPane.setAlignment(verticalLayout, Pos.TOP_CENTER);
        StackPane.setAlignment(verticalLayoutSmall, Pos.TOP_CENTER);
        mainMenuFloatingButton.translateYProperty().set(-(Constants.FLOATING_BUTTON_OFFSET));

        // Adding components to their parents
        verticalLayout.getChildren()
                .addAll(logoView, initRemoteButton, fetchLocationButton, fillHashesButton,
                        fillContextMenuButton, connectionPane, userPane, availableUsersPanePane, settingsPane);
        verticalLayoutSmall.getChildren().addAll(logoViewSmall);
        this.getChildren().addAll(verticalLayout, mainMenuFloatingButton);

        // Styling components with CSS
        //CHECKSTYLE.OFF: MultipleStringLiterals
        this.getStyleClass().addAll("main-menu");
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
     * Configure the fillContextMenuButton.
     * @param eventHandler EventHandler
     */
    public void addFillContextMenuButtonEventHandler(final EventHandler<ActionEvent> eventHandler) {
        fillContextMenuButton.setOnAction(eventHandler);
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
        StackPane.setAlignment(mainMenuFloatingButton, Pos.TOP_RIGHT);
        mainMenuFloatingButton.translateYProperty().set(-(Constants.FLOATING_BUTTON_OFFSET));
        this.getChildren().addAll(verticalLayout, mainMenuFloatingButton);
    }

    /**
     * Method to make this menu invisible.
     * Animations should be added in the future
     */
    public void minimizeMainMenu() {
        this.maximized = false;
        this.setMinHeight(height);
        this.setMinWidth(Constants.SMALL_MAIN_MENU_WIDTH);
        this.verticalLayoutSmall.getChildren().addAll(connectionPane);
        StackPane.setAlignment(mainMenuFloatingButton, Pos.TOP_CENTER);
        mainMenuFloatingButton.translateYProperty().set(-(Constants.FLOATING_BUTTON_OFFSET));
        this.getChildren().clear();
        this.getChildren().addAll(verticalLayoutSmall, mainMenuFloatingButton);
    }
}
