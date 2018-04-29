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

import javafx.scene.input.TouchPoint;
import javafx.scene.layout.StackPane;
import org.openbase.bco.bcozy.view.location.LocationPane;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InstantiationException;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class BackgroundPane extends StackPane {

    private final LocationPane locationPane;
    private final UnitSymbolsPane unitSymbolsPane;
    private final SimpleUnitSymbolsPane editingLayerPane;
    private final SimpleUnitSymbolsPane maintenanceLayerPane;

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
            locationPane = new LocationPane(foregroundPane);
            this.getChildren().add(locationPane);

            // default layer: changing light states on the location map
            unitSymbolsPane = new UnitSymbolsPane();
            unitSymbolsPane.setPickOnBounds(false);
            this.getChildren().add(unitSymbolsPane);

            unitSymbolsPane.selectedLocationId.bind(locationPane.selectedLocationId);

            // layer for fast overview over maintenance-relevant units
            maintenanceLayerPane = new SimpleUnitSymbolsPane();
            maintenanceLayerPane.setPickOnBounds(false);

            // layer for editing unit positions, shows all supported units
            editingLayerPane = new SimpleUnitSymbolsPane();
            editingLayerPane.setPickOnBounds(false);

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
                }

            });
            this.getStyleClass().add("background-pane");

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
