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
package org.openbase.bco.bcozy.view;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.openbase.bco.bcozy.controller.CenterPaneController;
import org.openbase.bco.bcozy.controller.SettingsController;
import org.openbase.bco.bcozy.controller.UserSettingsController;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

/**
 * Created by hoestreich on 11/26/15.
 *
 * @author vdasilva
 */
public class CenterPane extends StackPane {

    /**
     * Application logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CenterPane.class);

    private UserSettingsController userSettingsController;

    private Pane settingsMenu;

    private final ForegroundPane foregroundPane;
    
    // switched by floating button, is bound to the layer management of the location pane
    public final ObjectProperty<CenterPaneController.State> appStateProperty;

    /**
     * Constructor for the center pane.
     */
    public CenterPane(final double height, final ForegroundPane foregroundPane) {
        this.foregroundPane = foregroundPane;

        appStateProperty = new SimpleObjectProperty<>(CenterPaneController.State.MOVEMENT);
        
        // Initializing components
        this.settingsMenu = loadSettingsMenu(height);

        // Initializing view mode switch
        final FloatingPopUp viewModes = new FloatingPopUp(Pos.BOTTOM_RIGHT);
        viewModes.addParentElement(MaterialIcon.WEEKEND, () -> {
            appStateProperty.set(CenterPaneController.State.MOVEMENT);
        }); 
        viewModes.addElement(MaterialDesignIcon.THERMOMETER_LINES, () -> {
            appStateProperty.set(CenterPaneController.State.TEMPERATURE);
        }); 
        viewModes.addElement(MaterialIcon.VISIBILITY, () -> {
            appStateProperty.set(CenterPaneController.State.SETTINGS);
        }); 
      
        //final FloatingButton settingsBtn = new FloatingButton(new SVGIcon(MaterialDesignIcon.SETTINGS, Constants.MIDDLE_ICON, true));
        //this.setAlignment(settingsBtn, Pos.TOP_RIGHT);
        //settingsBtn.setOnAction(e -> toggleSettings());

        // Styling components with CSS
        this.getStyleClass().addAll("padding-small");

        this.setPickOnBounds(false);
        this.getChildren().addAll(viewModes);

        this.setMinHeight(height);
        this.setPrefHeight(height);
    }
    


    private Pane loadSettingsMenu(final double height) {
        try {
            final URL url = getClass().getClassLoader().getResource("SettingsMenu.fxml");
            if (url == null) {
                throw new NotAvailableException("SettingsMenu.fxml");
            }

            FXMLLoader loader = new FXMLLoader(url);
            loader.setControllerFactory((clazz) -> new SettingsController(foregroundPane));

            AnchorPane anchorPane = loader.load();

            final SettingsController settingsController = (SettingsController) loader.getController();
            userSettingsController = settingsController.getUserSettingsController();

            this.setMinHeight(height);
            this.setPrefHeight(height);

            anchorPane.getStyleClass().addAll("detail-menu");
            anchorPane.setMinHeight(height);
            anchorPane.setPrefHeight(height);

            return anchorPane;
        } catch (final CouldNotPerformException | IOException ex) {
            ExceptionPrinter.printHistory("Content could not be loaded", ex, LOGGER);
            return new Pane(new ObserverLabel("unavailable"));
        }
    }

    public UserSettingsController getSettingsPane() {
        return userSettingsController;
    }

    public Pane getSettingsMenu() {
        return settingsMenu;
    }
}
