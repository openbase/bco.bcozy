/*
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
package org.openbase.bco.bcozy;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.extension.type.processing.LabelProcessor;
import org.openbase.jul.schedule.GlobalCachedExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openbase.type.domotic.unit.UnitConfigType.UnitConfig;
import org.openbase.type.math.Vec3DDoubleType.Vec3DDouble;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 *
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class UnitShapeTransformer extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnitShapeTransformer.class);

    private Future registryTask;
    private Label label;
    private Slider slider;
    private class LocationHolder {
        UnitConfig latestUnitConfig;
        final UnitConfig originUnitConfig;

        public LocationHolder(UnitConfig originUnitConfig) {
            this.originUnitConfig = originUnitConfig;
            this.latestUnitConfig = originUnitConfig;
            try {
                Registries.getUnitRegistry().addDataObserver((source, data) -> latestUnitConfig = Registries.getUnitRegistry().getUnitConfigById(originUnitConfig.getId()));
            } catch (NotAvailableException e) {
                e.printStackTrace();
            }
        }

        public UnitConfig getOriginUnitConfig() {
            return originUnitConfig;
        }

        public UnitConfig getLatestUnitConfig() {
            return latestUnitConfig;
        }

        @Override
        public String toString() {
            try {
                return LabelProcessor.getBestMatch(originUnitConfig.getLabel());
            } catch (NotAvailableException e) {
                return "?";
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws CouldNotPerformException, InterruptedException, ExecutionException {
        Button resetButton = new Button();
        resetButton.setText("Reset");
        CheckBox checkBox = new CheckBox();

        label = new Label("select a unit");
        slider = new Slider();

        StackPane root = new StackPane();
        final VBox main = new VBox();
        root.getChildren().add(resetButton);

        List<LocationHolder> holders = new ArrayList<>();
        for (UnitConfig unitConfig : Registries.getUnitRegistry(true).getUnitConfigs()) {
            if(unitConfig.getPlacementConfig().hasShape()) {
                holders.add(new LocationHolder(unitConfig));
            }
        }

        final ComboBox<LocationHolder> unitComboBox = new ComboBox<>();
        unitComboBox.getItems().addAll(holders);

        resetButton.disableProperty().bind(checkBox.selectedProperty());
        unitComboBox.disableProperty().bind(checkBox.selectedProperty());

        main.getChildren().addAll(label, unitComboBox, resetButton, checkBox, slider);
        root.getChildren().add(main);

        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("Unit Shape Transformer");
        primaryStage.setScene(scene);
        primaryStage.show();

        try {
            resetButton.setOnAction(event -> {
                resetButton.disableProperty().unbind();
                try {
                    resetButton.disableProperty().setValue(true);
                    // update location in registry
                    Registries.getUnitRegistry(true).updateUnitConfig(unitComboBox.getSelectionModel().getSelectedItem().getOriginUnitConfig()).get();
                    resetButton.disableProperty().setValue(false);
                } catch (CouldNotPerformException | ExecutionException | InterruptedException ex) {
                    ExceptionPrinter.printHistory("Could not reset unit config", ex, LOGGER);
                    resetButton.disableProperty().setValue(false);
                }
                resetButton.disableProperty().bind(checkBox.selectedProperty());
            });

            scene.setOnKeyPressed(event -> {
                try {

                    if(registryTask != null && !registryTask.isDone()) {
                        return;
                    }
                    double scale = slider.getValue() ;
                    System.out.println("scale: "+ scale);
                    double offset = 0.01d + scale /100;
                    System.out.println("offset: "+ offset);

                    switch (event.getCode()) {
                        case UP:
                            translateUnitShape(-offset, 0, unitComboBox.getSelectionModel().getSelectedItem().getLatestUnitConfig());
                            event.consume();
                            break;
                        case DOWN:
                            translateUnitShape(offset, 0, unitComboBox.getSelectionModel().getSelectedItem().getLatestUnitConfig());
                            event.consume();
                            break;
                        case LEFT:
                            translateUnitShape(0, -offset, unitComboBox.getSelectionModel().getSelectedItem().getLatestUnitConfig());
                            event.consume();
                            break;
                        case RIGHT:
                            translateUnitShape(0, offset, unitComboBox.getSelectionModel().getSelectedItem().getLatestUnitConfig());
                            event.consume();
                            break;
                    }
                } catch (CouldNotPerformException | InterruptedException ex) {
                    ExceptionPrinter.printHistory("Task could not be performed!", ex, LOGGER);
                }
            });

        } catch (Exception ex) {
            ExceptionPrinter.printHistory("Could not init system!", ex, LOGGER);
        }
    }

    private void translateUnitShape(final double xOffset, final double yOffset, final UnitConfig unitConfig) throws CouldNotPerformException, InterruptedException {
        registryTask = GlobalCachedExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                Platform.runLater(() -> label.setText("store transformation..."));

                // create builder
                final UnitConfig.Builder builder = unitConfig.toBuilder();

                // update location positions with given offset
                builder.getPlacementConfigBuilder().getShapeBuilder().clearFloor().addAllFloor(updatePositions(xOffset, yOffset, unitConfig.getPlacementConfig().getShape().getFloorList()));
                builder.getPlacementConfigBuilder().getShapeBuilder().clearCeiling().addAllCeiling(updatePositions(xOffset, yOffset, unitConfig.getPlacementConfig().getShape().getCeilingList()));

                try {
//             update location in registry
                    Registries.getUnitRegistry().updateUnitConfig(builder.build()).get();
                } catch (ExecutionException | CouldNotPerformException | InterruptedException ex) {
                    ExceptionPrinter.printHistory("Could not translate unit shape", ex, System.err);
                }
                Platform.runLater(() -> label.setText("ready"));
            }
        });

    }

    private List<Vec3DDouble> updatePositions(final double xOffset, final double yOffset, final List<Vec3DDouble> originalPostionList) {
        List<Vec3DDouble> updatedPostionList = new ArrayList<>();

        // update position with offset
        for (final Vec3DDouble position : originalPostionList) {
            updatedPostionList.add(position.toBuilder().setX(position.getX() + xOffset).setY(position.getY() + yOffset).build());
        }
        return updatedPostionList;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
