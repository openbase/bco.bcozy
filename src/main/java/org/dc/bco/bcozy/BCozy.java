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

import com.guigarage.responsive.ResponsiveHandler;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.dc.bco.bcozy.controller.CenterPaneController;
import org.dc.bco.bcozy.controller.ContextMenuController;
import org.dc.bco.bcozy.controller.LocationController;
import org.dc.bco.bcozy.controller.MainMenuController;
import org.dc.bco.bcozy.controller.RemotePool;
import org.dc.bco.bcozy.jp.JPLanguage;
import org.dc.bco.bcozy.view.BackgroundPane;
import org.dc.bco.bcozy.view.Constants;
import org.dc.bco.bcozy.view.ForegroundPane;
import org.dc.bco.bcozy.view.ImageViewProvider;
import org.dc.bco.bcozy.view.InfoPane;
import org.dc.jps.core.JPService;
import org.dc.jps.exception.JPNotAvailableException;
import org.dc.jps.preset.JPDebugMode;
import org.dc.jul.exception.printer.ExceptionPrinter;
import org.dc.jul.exception.printer.LogLevel;
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

    private static Stage primaryStage;

    private InfoPane infoPane;
    private RemotePool remotePool;
    private ContextMenuController contextMenuController;
    private LocationController locationController;
    private Thread initThread;

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
    public void start(final Stage primaryStage) {

        BCozy.primaryStage = primaryStage;
        final double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        final double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
        primaryStage.setTitle("BCozy");

        final StackPane root = new StackPane();
        final ForegroundPane foregroundPane = new ForegroundPane(screenHeight, screenWidth);
        foregroundPane.setMinHeight(root.getHeight());
        foregroundPane.setMinWidth(root.getWidth());
        final BackgroundPane backgroundPane = new BackgroundPane(foregroundPane);
        infoPane = new InfoPane(screenHeight, screenWidth);
        infoPane.setMinHeight(root.getHeight());
        infoPane.setMinWidth(root.getWidth());
        infoPane.setCloseButtonEventHandler(event -> stop());

        root.getChildren().addAll(backgroundPane, foregroundPane, infoPane);

        //CHECKSTYLE.OFF: MagicNumber
        primaryStage.setMinWidth(foregroundPane.getMainMenu().getMinWidth()
                + foregroundPane.getContextMenu().getMinWidth() + 300);
        primaryStage.setHeight(screenHeight);
        //CHECKSTYLE.ON: MagicNumber

        primaryStage.setScene(new Scene(root, screenWidth, screenHeight));

        primaryStage.getScene().getStylesheets().addAll(Constants.DEFAULT_CSS, Constants.LIGHT_THEME_CSS);
        ResponsiveHandler.addResponsiveToWindow(primaryStage);
        primaryStage.show();

        new MainMenuController(foregroundPane);
        new CenterPaneController(foregroundPane);

        //instantiate RemotePool
        remotePool = new RemotePool(foregroundPane);
        contextMenuController = new ContextMenuController(foregroundPane, backgroundPane.getLocationPane(), remotePool);

        //instantiate LocationController
        locationController = new LocationController(foregroundPane, backgroundPane.getLocationPane(), remotePool);

        try {
            if (JPService.getProperty(JPDebugMode.class).getValue()) {
                infoPane.setVisible(false);
            } else {
                this.initRemotesAndLocation();
            }
        } catch (JPNotAvailableException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
            this.initRemotesAndLocation();
        }
    }

    private void initRemotesAndLocation() {
         initThread = new Thread(new Task() {
            @Override
            protected Object call() throws java.lang.Exception {
                infoPane.setTextLabelIdentifier("initRemotes");
                remotePool.initRegistryRemotes();

                infoPane.setTextLabelIdentifier("fillDeviceAndLocationMap");
                remotePool.fillDeviceAndLocationMap();

                infoPane.setTextLabelIdentifier("fillUserMap");
                remotePool.fillUserMap();

                infoPane.setTextLabelIdentifier("fillContextMenu");
                contextMenuController.initTitledPaneMap();

                infoPane.setTextLabelIdentifier("connectLocationRemote");
                locationController.connectLocationRemote();

                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                infoPane.setVisible(false);
            }
        });
        initThread.start();
    }

    @Override
    public void stop() {
        if (initThread != null && initThread.isAlive()) {
            initThread.interrupt();
            try {
                initThread.join();
            } catch (InterruptedException e) {
                ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
            }
        }
        try {
            super.stop();
        } catch (Exception e) { //NOPMD
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
        remotePool.shutdownAllRemotes(); //TODO mpohling: not shutting down properly
        System.exit(0);
    }

    /**
     * Method to change application wide theme from other locations in the view.
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
