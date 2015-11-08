/**
 * ==================================================================
 *
 * This file is part of BCozy.
 *
 * BCozy is free software: you can redistribute it and modify
 * it under the terms of the GNU General Public License (Version 3)
 * as published by the Free Software Foundation.
 *
 * BCozy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BCozy. If not, see <http://www.gnu.org/licenses/>.
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
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.spatial.LocationConfigType;

import java.util.List;

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
        JPService.parseAndExitOnError(args);

        //try {
            launch(args);
        //} catch (InitializationException ex) {
        //    throw ExceptionPrinter
        //            .printHistoryAndReturnThrowable(ex, LOGGER, LogLevel.ERROR);
        //}
        LOGGER.info(APP_NAME + " finished.");

    }

    @Override
    public void start(final Stage primaryStage) {

        final int screenWidth = 400;
        final int screenHeight = 600;
        primaryStage.setTitle("BCozy");
        final Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(event -> LOGGER.info("Hello World!"));

        final StackPane root = new StackPane();
        root.getChildren().add(btn);
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
            LocationRegistryRemote locationRegistryRemote = new LocationRegistryRemote();

            locationRegistryRemote.init();
            locationRegistryRemote.activate();
            List<LocationConfigType.LocationConfig> list = locationRegistryRemote.getLocationConfigs();
            for (LocationConfigType.LocationConfig locationConfig : list) {
                System.out.println(locationConfig.getLabel());
                MetaConfigVariableProvider metaConfigVariableProvider =
                        new MetaConfigVariableProvider("locationConfig", locationConfig.getMetaConfig());
                try {
                    LOGGER.info("Found test entry: " + metaConfigVariableProvider.getValue("TEST_ENTRY"));
                } catch (CouldNotPerformException e) {

                }
            }
            locationRegistryRemote.shutdown();

        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
    }
}
