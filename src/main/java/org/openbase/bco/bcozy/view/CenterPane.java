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
import org.openbase.bco.bcozy.view.mainmenupanes.SettingsPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;

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
    private final Pane settingsMenu;

    public final ObjectProperty<CenterPaneController.State> appStateProperty;

    /**
     * Constructor for the center pane.
     */
    public CenterPane(final double height) {

        appStateProperty = new SimpleObjectProperty<>(CenterPaneController.State.SETTINGS);
        
        appStateProperty.addListener(new ChangeListener<CenterPaneController.State> () {
            @Override
            public void changed(ObservableValue<? extends CenterPaneController.State> observable, CenterPaneController.State oldValue, CenterPaneController.State newValue) {
                System.out.println(newValue);
            }
        });
        // Initializing components
        this.settingsMenu = loadSettingsMenu(height);

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
        final FloatingButton settingsBtn = new FloatingButton(new SVGIcon(MaterialDesignIcon.SETTINGS, Constants.MIDDLE_ICON, true));

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

    private Pane loadSettingsMenu(final double height) {
        try {
            final URL url = getClass().getClassLoader().getResource("SettingsMenu.fxml");
            if (url == null) {
                throw new NotAvailableException("SettingsMenu.fxml");
            }

            FXMLLoader loader = new FXMLLoader(url);
            AnchorPane anchorPane = loader.load();

            final SettingsController settingsController = (SettingsController) loader.getController();
            settingsPane = settingsController.getSettingsPane();

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

    public SettingsPane getSettingsPane() {
        return settingsPane;
    }
}
