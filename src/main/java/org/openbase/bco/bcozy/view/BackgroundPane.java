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

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.openbase.bco.bcozy.view.location.LocationPane;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InstantiationException;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class BackgroundPane extends StackPane {

    public static final String POWER_DRAW_PANE_FXML_LOCATION = "gorg/openbase/bco/bcozy/controller/powerterminal/PowerDrawPane.fxml";
    public static final String FXML_LOAD_EXCEPTION_MESSAGE = "Failed loading PowerDrawPane.fxml!";

    private final LocationPane locationPane;
    private final UnitSymbolsPane unitSymbolsPane;
    private final SimpleUnitSymbolsPane editingLayerPane;
    private final SimpleUnitSymbolsPane maintenanceLayerPane;
    private Pane powerDrawPane;


    Logger logger = LoggerFactory.getLogger(BackgroundPane.class);

    /**
     * The constructor for a BackgroundPane.
     *
     * @param foregroundPane The foregroundPane
     *
     * @throws org.openbase.jul.exception.InstantiationException
     * @throws java.lang.InterruptedException
     */
    public BackgroundPane(final ForegroundPane foregroundPane) throws InstantiationException, InterruptedException {
        try {
            this.locationPane = new LocationPane(foregroundPane);
            this.getChildren().add(locationPane);

            // default layer: changing light states on the location map
            this.unitSymbolsPane = new UnitSymbolsPane();
            this.unitSymbolsPane.setPickOnBounds(false);
            this.getChildren().add(unitSymbolsPane);

            this.unitSymbolsPane.selectedLocationId.bind(locationPane.selectedLocationId);

            // layer for fast overview over maintenance-relevant units
            this.maintenanceLayerPane = new SimpleUnitSymbolsPane();
            this.maintenanceLayerPane.setPickOnBounds(false);

            // layer for editing unit positions, shows all supported units
            this.editingLayerPane = new SimpleUnitSymbolsPane();
            this.editingLayerPane.setPickOnBounds(false);

            // Pane layer for fast overview over power draw
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(POWER_DRAW_PANE_FXML_LOCATION));
            try {
                this.powerDrawPane = fxmlLoader.load();
            } catch(IOException e) {
                logger.error(FXML_LOAD_EXCEPTION_MESSAGE, e);
                powerDrawPane = null;
            }

            // layer management
            foregroundPane.getAppState().addListener((observable, oldValue, newValue) -> {
                switch (newValue) {
                    case SETTINGS:
                        getChildren().clear();
                        getChildren().add(locationPane);
                        getChildren().add(editingLayerPane);
                        break;
                    case TEMPERATURE:
                        getChildren().clear();
                        getChildren().add(locationPane);
                        getChildren().add(maintenanceLayerPane);
                        break;
                    case MOVEMENT:
                        getChildren().clear();
                        getChildren().add(locationPane);
                        getChildren().add(unitSymbolsPane);
                        break;
                    case ENERGY:
                        getChildren().clear();
                        getChildren().add(powerDrawPane);
                        break;
                }
            });

            this.getStyleClass().add("background-pane");
            // init touch
            this.locationPane.initMultiTouch();
            this.onMouseClickedProperty().bindBidirectional(locationPane.onMouseClickedProperty());
            this.onMouseEnteredProperty().bindBidirectional(locationPane.onMouseEnteredProperty());
            this.onMouseExitedProperty().bindBidirectional(locationPane.onMouseExitedProperty());

        } catch (CouldNotPerformException ex) {
            throw new InstantiationException(this, ex);
        }
    }

    /**
     * @return The LocationPane.
     */
    public UnitSymbolsPane getUnitsPane() {
        return unitSymbolsPane;
    }

    /**
     * @return The LocationPane.
     */
    public SimpleUnitSymbolsPane getMaintenancePane() {
        return maintenanceLayerPane;
    }

    /**
     * @return The LocationPane.
     */
    public SimpleUnitSymbolsPane getEditingPane() {
        return editingLayerPane;
    }

    /**
     * @return The Location Pane.
     */
    public LocationPane getLocationPane() {
        return locationPane;
    }
}
