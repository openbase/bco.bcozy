/**
 * ==================================================================
 *
 * This file is part of org.dc.bco.bcozy.
 *
 * org.dc.bco.bcozy is free software: you can redistribute it and modify
 * it under the terms of the GNU General Public License (Version 3)
 * as published by the Free Software Foundation.
 *
 * org.dc.bco.bcozy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with org.dc.bco.bcozy. If not, see <http://www.gnu.org/licenses/>.
 * ==================================================================
 */
package org.dc.bco.bcozy;

import de.citec.jps.core.JPService;
import de.citec.jps.preset.JPDebugMode;
import de.citec.jul.exception.CouldNotPerformException;
import de.citec.jul.exception.printer.ExceptionPrinter;
import de.citec.jul.exception.printer.LogLevel;
import de.citec.jul.extension.rst.processing.MetaConfigVariableProvider;
import de.citec.lm.remote.LocationRegistryRemote;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.homeautomation.unit.UnitConfigType;

import java.util.List;

import static rst.spatial.LocationConfigType.*;

/**
 * Main Class of the BCozy Program.
 */
public class BCozy extends Application {

    /**
     * Application name.
     */
    public static final String APP_NAME = BCozy.class.getSimpleName().toLowerCase();

    /**
     * Application logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BCozy.class);

    /**
     * Main Method starting JavaFX Environment.
     *
     * @param args Arguments from commandline.
     */
    public static void main(final String... args) {

        LOGGER.info("Start " + APP_NAME + "...");

        /* Setup JPService */
        JPService.setApplicationName(APP_NAME);
        JPService.registerProperty(JPDebugMode.class);

        try {
            JPService.parseAndExitOnError(args);
            launch(args);
        } catch (IllegalStateException ex) {
//        } catch (Exception ex) {
            ExceptionPrinter.printHistory(ex, LOGGER, LogLevel.ERROR);
            LOGGER.info(APP_NAME + " finished unexpected.");
        }
        LOGGER.info(APP_NAME + " finished.");

    }

    @Override
    public void start(final Stage primaryStage) {

        final double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        final double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
        primaryStage.setTitle("BCozy");

        final StackPane root = new StackPane();
        final ForegroundPane foregroundPane = new ForegroundPane(screenHeight, screenWidth);
        final BackgroundPane backgroundPane = new BackgroundPane();

        root.getChildren().addAll(backgroundPane, foregroundPane);
        primaryStage.setScene(new Scene(root, screenWidth, screenHeight));
        primaryStage.show();

        try {
            testLocationRegistry();
        } catch (InterruptedException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
    }

    private void testLocationRegistry() throws InterruptedException {
        try {
            final LocationRegistryRemote locationRegistryRemote = new LocationRegistryRemote();

            locationRegistryRemote.init();
            locationRegistryRemote.activate();
            final List<LocationConfig> list = locationRegistryRemote.getLocationConfigs();
            for (final LocationConfig locationConfig : list) {
                LOGGER.info(locationConfig.getLabel());
                final MetaConfigVariableProvider metaConfigVariableProvider =
                        new MetaConfigVariableProvider("locationConfig", locationConfig.getMetaConfig());
                try {
                    LOGGER.info("Found test entry: " + metaConfigVariableProvider.getValue("TEST_ENTRY"));
                } catch (CouldNotPerformException e) {
                    LOGGER.info("No test entry found");
                }
            }

            //Example: How to control a light:
            final LocationConfig controlConfig = locationRegistryRemote.getLocationConfigById("Control");


            final List<UnitConfigType.UnitConfig> ambientLight  =
                    locationRegistryRemote.getUnitConfigsByLabel("TestUnit_0", controlConfig.getId());
            LOGGER.info("INFO: " + ambientLight.get(0).getId());

            locationRegistryRemote.shutdown();

        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
    }
}
