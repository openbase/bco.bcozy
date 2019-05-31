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

import javafx.scene.layout.StackPane;
import org.openbase.bco.bcozy.view.location.LocationMapPane;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InstantiationException;

/**
 *
 */
public class BackgroundPane extends StackPane {

    private final LocationMapPane locationMapPane;
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
            this.locationMapPane = new LocationMapPane(foregroundPane);
            this.getChildren().add(locationMapPane);

            // default layer: changing light states on the location map
            this.unitSymbolsPane = new UnitSymbolsPane();
            this.unitSymbolsPane.setPickOnBounds(false);
            this.getChildren().add(unitSymbolsPane);

            this.unitSymbolsPane.selectedUnit.bind(locationMapPane.selectedUnit);

            // layer for fast overview over maintenance-relevant units
            this.maintenanceLayerPane = new SimpleUnitSymbolsPane();
            this.maintenanceLayerPane.setPickOnBounds(false);

            // layer for editing unit positions, shows all supported units
            this.editingLayerPane = new SimpleUnitSymbolsPane();
            this.editingLayerPane.setPickOnBounds(false);

            // layer management
            foregroundPane.getAppState().addListener((observable, oldValue, newValue) -> {
                switch (newValue) {
                    case SETTINGS:
                        getChildren().clear();
                        getChildren().add(locationMapPane);
                        getChildren().add(editingLayerPane);
                        break;
                    case TEMPERATURE:
                        getChildren().clear();
                        getChildren().add(locationMapPane);
                        getChildren().add(maintenanceLayerPane);
                        break;
                    case MOVEMENT:
                        getChildren().clear();
                        getChildren().add(locationMapPane);
                        getChildren().add(unitSymbolsPane);
                        break;
                }

            });
            this.getStyleClass().add("background-pane");
            // init touch
            this.locationMapPane.initMultiTouch();
            this.onMouseClickedProperty().bindBidirectional(locationMapPane.onMouseClickedProperty());
            this.onMouseEnteredProperty().bindBidirectional(locationMapPane.onMouseEnteredProperty());
            this.onMouseExitedProperty().bindBidirectional(locationMapPane.onMouseExitedProperty());

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
    public LocationMapPane getLocationMapPane() {
        return locationMapPane;
    }
}
