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
import de.citec.jul.exception.printer.ExceptionPrinter;
import de.citec.jul.exception.printer.LogLevel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.dc.bco.bcozy.view.ForegroundPane;
import org.dc.bco.bcozy.view.location.LocationPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        final int screenWidth = 400;
        final int screenHeight = 600;
        primaryStage.setTitle("BCozy");

        final StackPane root = new StackPane();
        final ForegroundPane foregroundPane = new ForegroundPane();
        final LocationPane backgroundPane = new LocationPane();

        root.getChildren().addAll(backgroundPane, foregroundPane);
        primaryStage.setScene(new Scene(root, screenWidth, screenHeight));
        primaryStage.show();

        new ManagerConnector(foregroundPane);
    }
}
