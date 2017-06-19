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
package org.openbase.bco.bcozy;

import com.guigarage.responsive.ResponsiveHandler;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.openbase.bco.bcozy.controller.CenterPaneController;
import org.openbase.bco.bcozy.controller.ContextMenuController;
import org.openbase.bco.bcozy.controller.LocationPaneController;
import org.openbase.bco.bcozy.controller.MainMenuController;
import org.openbase.bco.bcozy.jp.JPLanguage;
import org.openbase.bco.bcozy.view.BackgroundPane;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.ForegroundPane;
import org.openbase.bco.bcozy.view.ImageViewProvider;
import org.openbase.bco.bcozy.view.InfoPane;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jps.core.JPService;
import org.openbase.jps.preset.JPDebugMode;
import org.openbase.jul.exception.FatalImplementationErrorException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.openbase.jul.schedule.GlobalCachedExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.navigation.ExecutionParametersType;

/**
 * @author hoestreich
 * @author timo
 * @author agatting
 * @author julian
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 *         <p>
 *         Main Class of the BCozy Program.
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

    private static Stage primaryStage;

    private InfoPane infoPane;
    private ContextMenuController contextMenuController;
    private LocationPaneController locationPaneController;
    private ForegroundPane foregroundPane;
    private Future initTask;

    /**
     * Main Method starting JavaFX Environment.
     *
     * @param args Arguments from commandline.
     */
    public static void main(final String... args) {

        LOGGER.info("Start " + APP_NAME + "...");

        registerListeners();
        /* Setup JPService */
        JPService.setApplicationName(APP_NAME);
        JPService.registerProperty(JPDebugMode.class);
        JPService.registerProperty(JPLanguage.class);

        try {
            JPService.parseAndExitOnError(args);
            launch(args);
        } catch (IllegalStateException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER, LogLevel.ERROR);
            LOGGER.info(APP_NAME + " finished unexpected.");
        }
        LOGGER.info(APP_NAME + " finished.");
    }

    @Override
    public void start(final Stage primaryStage) throws InitializationException, InterruptedException, InstantiationException {
        try {BCozy.primaryStage = primaryStage;
        final double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        final double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
        primaryStage.setTitle("BCozy");

        final StackPane root = new StackPane();
        foregroundPane = new ForegroundPane(screenHeight, screenWidth);
        foregroundPane.setMinHeight(root.getHeight());
        foregroundPane.setMinWidth(root.getWidth());
        final BackgroundPane backgroundPane = new BackgroundPane(foregroundPane);

        infoPane = new InfoPane(screenHeight, screenWidth);
        infoPane.setMinHeight(root.getHeight());
        infoPane.setMinWidth(root.getWidth());
        infoPane.setCloseButtonEventHandler(event -> stop());
        root.getChildren().addAll(backgroundPane, foregroundPane, infoPane);

        primaryStage.setMinWidth(foregroundPane.getMainMenu().getMinWidth() + foregroundPane.getContextMenu().getMinWidth() + 300);
        primaryStage.setHeight(screenHeight);
        primaryStage.setScene(new Scene(root, screenWidth, screenHeight));
        primaryStage.getScene().getStylesheets().addAll(Constants.DEFAULT_CSS, Constants.LIGHT_THEME_CSS);



            new MainMenuController(foregroundPane);


        new CenterPaneController(foregroundPane);

        contextMenuController = new ContextMenuController(foregroundPane, backgroundPane.getLocationPane());
        locationPaneController = new LocationPaneController(backgroundPane.getLocationPane());

        ResponsiveHandler.addResponsiveToWindow(primaryStage);
        primaryStage.show();

        initRemotesAndLocation();
        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

    private void initRemotesAndLocation() {
        initTask = GlobalCachedExecutorService.submit(new Task() {
            @Override
            protected Object call() throws java.lang.Exception {
                try {
                    infoPane.setTextLabelIdentifier("waitForConnection");
                    Registries.waitForData();

                    infoPane.setTextLabelIdentifier("fillContextMenu");
                    foregroundPane.init();

                    contextMenuController.initTitledPaneMap();

                    infoPane.setTextLabelIdentifier("connectLocationRemote");
                    locationPaneController.connectLocationRemote();
                    return null;
                } catch (Exception ex) {
                    infoPane.setTextLabelIdentifier("errorDuringStartup");
                    Thread.sleep(3000);
                    Exception exx = new FatalImplementationErrorException("Could not init panes", this, ex);
                    ExceptionPrinter.printHistoryAndExit(exx, LOGGER);
                    throw exx;
                }
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                infoPane.setVisible(false);
            }
        });
    }

    public void stop() {
        if (initTask != null && !initTask.isDone()) {
            initTask.cancel(true);
            try {
                initTask.get();
            } catch (InterruptedException | ExecutionException e) {
                ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
            }
        }
        try {
            super.stop();
        } catch (Exception e) { //NOPMD
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
        System.exit(0);
    }

    /**
     * Method to change application wide theme from other locations in the view.
     *
     * @param themeName the name of the theme to be set
     */
    public static void changeTheme(final String themeName) {
        if (primaryStage != null) {
            switch (themeName) {
                case Constants.DARK_THEME_CSS:
                    primaryStage.getScene().getStylesheets().clear();
                    primaryStage.getScene().getStylesheets()
                            .addAll(Constants.DEFAULT_CSS, Constants.DARK_THEME_CSS);
                    ImageViewProvider.colorizeIconsToWhite();
                    break;
                case Constants.LIGHT_THEME_CSS:
                    primaryStage.getScene().getStylesheets().clear();
                    primaryStage.getScene().getStylesheets()
                            .addAll(Constants.DEFAULT_CSS, Constants.LIGHT_THEME_CSS);
                    ImageViewProvider.colorizeIconsToBlack();
                    break;
                default:
                    primaryStage.getScene().getStylesheets().clear();
                    break;
            }
        }
    }

    private static void registerListeners() {
        LOGGER.info("Executing Registration of Listeners");
        ResponsiveHandler.setOnDeviceTypeChanged((over, oldDeviceType, newDeviceType) -> {
            switch (newDeviceType) {
                case LARGE:
                    adjustToLargeDevice();
                    break;
                case MEDIUM:
                    adjustToMediumDevice();
                    break;
                case SMALL:
                    adjustToSmallDevice();
                    break;
                case EXTRA_SMALL:
                    adjustToExtremeSmallDevice();
                    break;
                default:
                    break;
            }
        });
    }

    private static void adjustToLargeDevice() {
        LOGGER.info("Detected Large Device");
    }

    private static void adjustToMediumDevice() {
        LOGGER.info("Detected Medium Device");
    }

    private static void adjustToSmallDevice() {
        LOGGER.info("Detected Small Device");
    }

    private static void adjustToExtremeSmallDevice() {
        LOGGER.info("Detected Extreme Small Device");
    }
}
