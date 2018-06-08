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

import com.google.protobuf.GeneratedMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.openbase.bco.dal.lib.layer.unit.UnitRemote;
import org.openbase.bco.dal.remote.unit.Units;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.unit.UnitConfigType;
import rst.domotic.unit.UnitConfigType.UnitConfig;
import rst.math.Vec3DDoubleType.Vec3DDouble;

/**
 *
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class UnitShapeTransformer extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnitShapeTransformer.class);

    @Override
    public void start(Stage primaryStage) throws CouldNotPerformException, InterruptedException, ExecutionException {
        Button resetButton = new Button();
        resetButton.setText("Reset");

        StackPane root = new StackPane();
        root.getChildren().add(resetButton);

        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("Unit Shape Transformer");
        primaryStage.setScene(scene);
        primaryStage.show();

        try {

            final UnitConfigType.UnitConfig rootLocationConfig = Registries.getUnitRegistry(true).getRootLocationConfig();

            UnitRemote<? extends GeneratedMessage> location = Units.getUnit(rootLocationConfig, true, Units.LOCATION);

            resetButton.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    try {
                        resetButton.setDisable(true);
                        // update location in registry
                        Registries.getUnitRegistry(true).updateUnitConfig(rootLocationConfig).get();
                        resetButton.setDisable(false);
                    } catch (CouldNotPerformException | ExecutionException | InterruptedException ex) {
                        ExceptionPrinter.printHistory("Could not reset unit config", ex, LOGGER);
                        resetButton.setDisable(false);
                    }
                }
            });

            scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    try {

                        double offset = 0.1d;
                        switch (event.getCode()) {
                            case UP:
                                translateUnitShape(-offset, 0, location.getConfig());
                                break;
                            case DOWN:
                                translateUnitShape(offset, 0, location.getConfig());
                                break;
                            case LEFT:
                                translateUnitShape(0, -offset, location.getConfig());
                                break;
                            case RIGHT:
                                translateUnitShape(0, offset, location.getConfig());
                                break;
                        }
                    } catch (CouldNotPerformException | InterruptedException ex) {
                        ExceptionPrinter.printHistory("Task could not be performed!", ex, LOGGER);
                    }
                }
            });

        } catch (Exception ex) {
            ExceptionPrinter.printHistory("Could not init system!", ex, LOGGER);
        }
    }

    private void translateUnitShape(final double xOffset, final double yOffset, final UnitConfig unitConfig) throws CouldNotPerformException, InterruptedException {
        // create builder
        final UnitConfig.Builder builder = unitConfig.toBuilder();

        // update location positions with given offset
        builder.getPlacementConfigBuilder().getShapeBuilder().clearFloor().addAllFloor(updatePositions(xOffset, yOffset, unitConfig.getPlacementConfig().getShape().getFloorList()));
        builder.getPlacementConfigBuilder().getShapeBuilder().clearCeiling().addAllCeiling(updatePositions(xOffset, yOffset, unitConfig.getPlacementConfig().getShape().getCeilingList()));

        try {
            // update location in registry
            Registries.getUnitRegistry().updateUnitConfig(builder.build()).get();
        } catch (ExecutionException ex) {
            throw new CouldNotPerformException("Could not translate unit shape");
        }
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
