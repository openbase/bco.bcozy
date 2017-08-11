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
package org.openbase.bco.bcozy;

import com.guigarage.responsive.ResponsiveHandler;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.openbase.bco.bcozy.controller.*;
import org.openbase.bco.bcozy.view.BackgroundPane;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.ForegroundPane;
import org.openbase.bco.bcozy.view.ImageViewProvider;
import org.openbase.bco.bcozy.view.InfoPane;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.FatalImplementationErrorException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.openbase.jul.schedule.GlobalCachedExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hoestreich
 * @author timo
 * @author agatting
 * @author julian
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 *
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

    public static Stage primaryStage;

    private InfoPane infoPane;
    private ContextMenuController contextMenuController;
    private LocationPaneController locationPaneController;
    private ForegroundPane foregroundPane;
    private UnitsPaneController unitsPaneController;
    private Future initTask;

    private Scene mainScene;

    @Override
    public void start(final Stage primaryStage) throws InitializationException, InterruptedException, InstantiationException {
        BCozy.primaryStage = primaryStage;
        registerResponsiveHandler();

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
        root.getChildren().addAll(backgroundPane, foregroundPane, infoPane);

        primaryStage.setMinWidth(foregroundPane.getMainMenu().getMinWidth() + foregroundPane.getContextMenu().getMinWidth() + 300);
        primaryStage.setHeight(screenHeight);
        mainScene = new Scene(root, screenWidth, screenHeight);
        primaryStage.setScene(mainScene);
        primaryStage.getScene().getStylesheets().addAll(Constants.DEFAULT_CSS, Constants.LIGHT_THEME_CSS);

        new MainMenuController(foregroundPane);
        new CenterPaneController(foregroundPane);

        contextMenuController = new ContextMenuController(foregroundPane, backgroundPane.getLocationPane());
        locationPaneController = new LocationPaneController(backgroundPane.getLocationPane());
        unitsPaneController = new UnitsPaneController(backgroundPane.getUnitsPane(), backgroundPane.getLocationPane());

        ResponsiveHandler.addResponsiveToWindow(primaryStage);
        primaryStage.show();

        initRemotesAndLocation();
    }

    private void onInitializedRemotesAndLocation() {
        Platform.runLater(this::loadInitialRegistrationWindow);
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
                    unitsPaneController.connectUnitRemote();

                    onInitializedRemotesAndLocation();

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

    @Override
    public void stop() {
        boolean errorOccured = false;

        if (initTask != null && !initTask.isDone()) {
            initTask.cancel(true);
            try {
                initTask.get();
            } catch (InterruptedException | ExecutionException ex) {
                ExceptionPrinter.printHistory("Initialization phase canceled because of application shutdown.", ex, LOGGER, LogLevel.INFO);
                errorOccured = true;
            } catch (CancellationException ex) {
                ExceptionPrinter.printHistory("Initialization phase failed but application shutdown was initialized anyway.", ex, LOGGER, LogLevel.WARN);
            }
        }
        try {
            super.stop();
        } catch (Exception ex) { //NOPMD
            ExceptionPrinter.printHistory("Could not stop " + JPService.getApplicationName() + "!", ex, LOGGER);
            errorOccured = true;
        }

        // Call system exit to trigger all shutdown deamons.
        if (errorOccured) {
            System.exit(255);
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

    private static void registerResponsiveHandler() {
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

    private void loadInitialRegistrationWindow() {

        if (false) { //TODO: First Start?
            try {
                URL url = getClass().getClassLoader().getResource("InitialRegistration.fxml");
                if (url == null) {
                    throw new RuntimeException("InitialRegistration.fxml not found");
                }


                FXMLLoader loader = new FXMLLoader(url);

                loader.setController(new InitialRegistrationController(result -> primaryStage.setScene(mainScene)));

                AnchorPane anchorPane = loader.load();
                anchorPane.getStyleClass().addAll("detail-menu");


                primaryStage.setScene(new Scene(anchorPane));
            } catch (IOException ex) {
                ex.printStackTrace();
                ExceptionPrinter.printHistory("Content could not be loaded", ex, LOGGER);
                throw new UncheckedIOException(ex);
            }


        }
    }
}
