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
package org.openbase.bco.bcozy.view;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import javafx.geometry.Pos;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.openbase.bco.authentication.lib.jp.JPAuthentication;
import org.openbase.jul.visual.javafx.JFXConstants;
import org.openbase.jul.visual.javafx.geometry.svg.SVGGlyphIcon;
import org.openbase.bco.bcozy.view.mainmenupanes.AvailableUsersPane;
import org.openbase.bco.bcozy.view.mainmenupanes.LoginPane;
import org.openbase.bco.bcozy.view.mainmenupanes.LogoPane;
import org.openbase.bco.bcozy.view.mainmenupanes.ObserverTitledPane;
import org.openbase.jps.core.JPService;
import org.openbase.jps.exception.JPNotAvailableException;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.iface.VoidInitializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hoestreich
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 * @author vdasilva
 */
public class MainMenu extends StackPane implements VoidInitializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainMenu.class);

    private final LoginPane loginPane;
    private final FloatingButton mainMenuFloatingButton;
    private final VBox verticalLayout;
    private final VBox verticalLayoutSmall;
    private final double height;
    private final double width;
    private final AvailableUsersPane availableUsersPane;
    private final LogoPane logoPane;
    private boolean maximized;
    private TitledPane loginContainer;
    private final SVGGlyphIcon menuStateIcon;


    /**
     * Constructor for the MainMenu.
     *
     * @param height Height
     * @param width  Width
     */
    public MainMenu(final double height, final double width) throws InstantiationException {
        try {
            // Initializing the container (StackPane)
            this.height = height;
            this.width = width;
            this.maximized = true;
            this.setMinHeight(height);
            this.setMinWidth(width);
            this.setPrefWidth(width);

            // Initializing components
            this.verticalLayout = new VBox(Constants.INSETS);
            this.verticalLayout.setAlignment(Pos.TOP_CENTER);
            this.verticalLayoutSmall = new VBox(Constants.INSETS * 2);
            this.verticalLayoutSmall.setAlignment(Pos.TOP_CENTER);
            this.logoPane = new LogoPane();
            this.loginPane = new LoginPane();
            this.availableUsersPane = new AvailableUsersPane();

            this.menuStateIcon = new SVGGlyphIcon(MaterialIcon.KEYBOARD_ARROW_LEFT, JFXConstants.ICON_SIZE_MIDDLE, true);
            this.mainMenuFloatingButton = new FloatingButton(menuStateIcon);

            loginContainer = new ObserverTitledPane("login");
            loginContainer.getStyleClass().addAll("login-titled-pane");
            loginContainer.setGraphic(new SVGGlyphIcon(MaterialDesignIcon.LOGIN, JFXConstants.ICON_SIZE_EXTRA_SMALL, true));
            loginContainer.setContent(loginPane);

            // Setting Alignment in Stackpane
            StackPane.setAlignment(mainMenuFloatingButton, Pos.BOTTOM_RIGHT);
            StackPane.setAlignment(verticalLayout, Pos.TOP_CENTER);
            StackPane.setAlignment(verticalLayoutSmall, Pos.TOP_CENTER);
            this.mainMenuFloatingButton.translateYProperty().set(-(Constants.FLOATING_BUTTON_OFFSET));

            // Adding components to their parents
            try {
                if (JPService.getProperty(JPAuthentication.class).getValue()) {
                    this.verticalLayout.getChildren().addAll(logoPane, loginContainer, availableUsersPane);
                } else {
                    this.verticalLayout.getChildren().addAll(logoPane, availableUsersPane);
                }
            } catch (JPNotAvailableException e) {
                // just ignore is property is not available
                this.verticalLayout.getChildren().addAll(logoPane, availableUsersPane);
            }

            this.getChildren().addAll(verticalLayout, mainMenuFloatingButton);

            // Styling components with CSS
            this.getStyleClass().addAll("main-menu");
        } catch (CouldNotPerformException ex) {
            throw new InstantiationException(this, ex);
        }
    }

    @Override
    public void init() throws InitializationException, InterruptedException {
        try {
            availableUsersPane.init();
        } catch (CouldNotPerformException ex) {
            new InitializationException(this, ex);
        }
    }

    /**
     * Getter for the main menu button.
     *
     * @return the instance of the main menu button
     */
    public FloatingButton getMainMenuFloatingButton() {
        return mainMenuFloatingButton;
    }

    /**
     * Getter for the LoginPane.
     *
     * @return the instance of the loginPane
     */
    public LoginPane getLoginPane() {
        return loginPane;
    }


    /**
     * Getter for the availableUsersPane.
     *
     * @return the instance of the availableUsersPane
     */
    public AvailableUsersPane getAvailableUsersPanePane() {
        return availableUsersPane;
    }

    /**
     * Getter for the current display state.
     *
     * @return true if maximized, false if minimized
     */
    public boolean isMaximized() {
        return maximized;
    }

    /**
     * Method to make this menu visible.
     * Animations should be added in the future
     */
    public void maximizeMainMenu() {
        maximized = true;
        menuStateIcon.setForegroundIcon(MaterialIcon.KEYBOARD_ARROW_LEFT);
        setMinHeight(height);
        setMinWidth(width);
        setPrefHeight(height);
        setPrefWidth(width);
        getChildren().clear();
        StackPane.setAlignment(mainMenuFloatingButton, Pos.BOTTOM_RIGHT);
        mainMenuFloatingButton.translateYProperty().set(-(Constants.FLOATING_BUTTON_OFFSET));
        getChildren().addAll(verticalLayout, mainMenuFloatingButton);
    }

    /**
     * Method to make this menu invisible.
     * Animations should be added in the future
     */
    public void minimizeMainMenu() {
        maximized = false;
        menuStateIcon.setForegroundIcon(MaterialIcon.KEYBOARD_ARROW_RIGHT);
        setMinHeight(height);
        setPrefHeight(height);
        setMinWidth(Constants.SMALL_MAIN_MENU_WIDTH);
        setPrefWidth(Constants.SMALL_MAIN_MENU_WIDTH_PREF);
        StackPane.setAlignment(mainMenuFloatingButton, Pos.BOTTOM_CENTER);
        mainMenuFloatingButton.translateYProperty().set(-(Constants.FLOATING_BUTTON_OFFSET));
        verticalLayoutSmall.getChildren().clear();
        verticalLayoutSmall.getChildren().addAll(logoPane.getStatusIcon(), loginPane.getStatusIcon(), availableUsersPane.getStatusIcon());
        getChildren().clear();
        getChildren().addAll(verticalLayoutSmall, mainMenuFloatingButton);
    }
}
