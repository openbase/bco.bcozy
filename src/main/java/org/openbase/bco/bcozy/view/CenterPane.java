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

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.openbase.bco.bcozy.controller.CenterPaneController;
import org.openbase.bco.bcozy.controller.SettingsController;
import org.openbase.bco.bcozy.view.mainmenupanes.SettingsPane;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
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


    private SettingsPane settingsPane;
    private Pane settingsMenu;
    private Pane permissionPaneParent;

    public ObjectProperty<CenterPaneController.State> appStateProperty;

    /**
     * Constructor for the center pane.
     */
    public CenterPane(double height) {

        appStateProperty = new SimpleObjectProperty<>(CenterPaneController.State.SETTINGS);

        // Initializing components
        this.settingsMenu = loadSettingsMenu(height);

        FloatingPopUp viewModes = new FloatingPopUp(Pos.BOTTOM_RIGHT);
        viewModes.addParentElement(MaterialIcon.SETTINGS, (Runnable) null); //TODO: Add EventHandler when needed
        viewModes.addElement(MaterialDesignIcon.THERMOMETER_LINES, (Runnable) null);//TODO: Add EventHandler when needed
        viewModes.addElement(MaterialIcon.VISIBILITY, (Runnable) null);//TODO: Add EventHandler when needed

/*        FloatingPopUp settingsModes = new FloatingPopUp(Pos.TOP_RIGHT);
        settingsModes.addParentElement(MaterialIcon.MORE_VERT, this::hideSettings);
        settingsModes.addElement(MaterialIcon.FULLSCREEN, (Runnable) null);
        settingsModes.addElement(FontAwesomeIcon.COGS, this::showSettings);*/

        FloatingButton settingsBtn = new FloatingButton(new SVGIcon(FontAwesomeIcon.COGS, Constants.MIDDLE_ICON, true));

        this.setAlignment(settingsBtn, Pos.TOP_RIGHT);

        settingsBtn.setOnAction(e -> toggleSettings());

        // Styling components with CSS
        this.getStyleClass().addAll("padding-small");


        this.setPickOnBounds(false);
        this.getChildren().addAll(viewModes, settingsBtn);

        this.setMinHeight(height);
        this.setPrefHeight(height);

    }


    private void toggleSettings() {
        if (this.getChildren().contains(settingsMenu)) {
            this.getChildren().remove(settingsMenu);
        } else {
            this.getChildren().add(0, settingsMenu);

        }
    }

    private Pane loadSettingsMenu(double height) {
        try {
            URL url = getClass().getClassLoader().getResource("SettingsMenu.fxml");
            if (url == null) {
                throw new RuntimeException("SettingsMenu.fxml not found");
            }

            FXMLLoader loader = new FXMLLoader(url);
            AnchorPane anchorPane = loader.load();

            SettingsController settingsController = (SettingsController) loader.getController();
            settingsPane = settingsController.getSettingsPane();

            this.setMinHeight(height);
            this.setPrefHeight(height);

            anchorPane.getStyleClass().addAll("detail-menu");
            anchorPane.setMinHeight(height);
            anchorPane.setPrefHeight(height);

            return anchorPane;
        } catch (IOException ex) {
            ex.printStackTrace();
            ExceptionPrinter.printHistory("Content could not be loaded", ex, LOGGER);
            throw new UncheckedIOException(ex);
        }
    }


    public SettingsPane getSettingsPane() {
        return settingsPane;
    }

}
