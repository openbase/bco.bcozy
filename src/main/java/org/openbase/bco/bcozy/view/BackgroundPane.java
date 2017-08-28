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

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.StackPane;
import org.openbase.bco.bcozy.controller.CenterPaneController;
import org.openbase.bco.bcozy.view.location.LocationPane;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InstantiationException;

/**
 *
 */
public class BackgroundPane extends StackPane {

    private final LocationPane locationPane;
    private final UnitSymbolsPane unitSymbolsPane;
    private final SimpleUnitSymbolsPane editingLayerPane;
    private final SimpleUnitSymbolsPane maintenanceLayerPane;
    private double prevMouseCordX; //NOPMD
    private double prevMouseCordY; //NOPMD

    /**
     * The constructor for a BackgroundPane.
     *
     * @param foregroundPane The foregroundPane
     * @throws org.openbase.jul.exception.InstantiationException
     * @throws java.lang.InterruptedException
     */
    public BackgroundPane(final ForegroundPane foregroundPane) throws InstantiationException, InterruptedException {
        try {

            locationPane = LocationPane.getInstance(foregroundPane);
            this.getChildren().add(locationPane);

            unitSymbolsPane = new UnitSymbolsPane();
            unitSymbolsPane.setPickOnBounds(false);
            this.getChildren().add(unitSymbolsPane);

            unitSymbolsPane.selectedLocationId.bind(locationPane.selectedLocationId);

            maintenanceLayerPane = new SimpleUnitSymbolsPane();
            maintenanceLayerPane.setPickOnBounds(false);

            editingLayerPane = new SimpleUnitSymbolsPane();
            editingLayerPane.setPickOnBounds(false);

            foregroundPane.getAppState().addListener(new ChangeListener<CenterPaneController.State>() {

                @Override
                public void changed(ObservableValue<? extends CenterPaneController.State> observable, CenterPaneController.State oldValue, CenterPaneController.State newValue) {
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

                }

            });
            this.getStyleClass().add("background-pane");

            this.setOnMousePressed(event -> {
                this.prevMouseCordX = event.getX();
                this.prevMouseCordY = event.getY();
            });

            this.setOnMouseDragged(event -> {
                locationPane.setTranslateX(locationPane.getTranslateX() + (event.getX() - prevMouseCordX));
                locationPane.setTranslateY(locationPane.getTranslateY() + (event.getY() - prevMouseCordY));
                this.prevMouseCordX = event.getX();
                this.prevMouseCordY = event.getY();
            });

            this.setOnScroll(event -> {
                event.consume();

                if (event.getDeltaY() == 0) {
                    return;
                }

                final double scaleFactor = (event.getDeltaY() > 0) ? Constants.SCALE_DELTA : 1 / Constants.SCALE_DELTA;

                locationPane.setScaleX(locationPane.getScaleX() * scaleFactor);
                locationPane.setScaleY(locationPane.getScaleY() * scaleFactor);
                locationPane.setTranslateX(locationPane.getTranslateX() * scaleFactor);
                locationPane.setTranslateY(locationPane.getTranslateY() * scaleFactor);
            });

            this.setOnMouseClicked(locationPane.getOnEmptyAreaClickHandler());

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
