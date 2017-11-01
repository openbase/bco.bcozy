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
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.openbase.bco.bcozy.controller.*;
import org.openbase.bco.bcozy.view.*;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jps.core.JPService;
import org.openbase.jul.exception.*;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.openbase.jul.pattern.Observable;
import org.openbase.jul.pattern.Observer;
import org.openbase.jul.pattern.Remote;
import org.openbase.jul.pattern.Remote.ConnectionState;
import org.openbase.jul.schedule.GlobalCachedExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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

    private LoadingPane loadingPane;
    private ContextMenuController contextMenuController;
    private LocationPaneController locationPaneController;
    private ForegroundPane foregroundPane;
    private UnitsPaneController unitsPaneController;
    private MaintenanceLayerController maintenanceLayerController;
    private EditingLayerController editingLayerController;
    private Future initTask;

    private Scene mainScene;

    public static boolean baseColorIsWhite = true;

    private static Observer<ConnectionState> connectionObserver;

    public BCozy() {

        connectionObserver = new Observer<Remote.ConnectionState>() {
            @Override
            public void update(Observable<Remote.ConnectionState> source, Remote.ConnectionState data) throws Exception {
                switch (data) {
                    case CONNECTED:
                        // recover default
                        InfoPane.confirmation("connected");
                        break;
                    case CONNECTING:
                        // green
                        InfoPane.warn("connecting");
                        break;
                    case DISCONNECTED:
                        InfoPane.error("disconnected");
                        // red
                        break;
                    case UNKNOWN:
                    default:
                        // blue
                        break;
                }
            }
        };
    }

    @Override
    public void start(final Stage primaryStage) throws InitializationException, InterruptedException, InstantiationException {
        BCozy.primaryStage = primaryStage;
        registerResponsiveHandler();

        // TODO: should be removed after issue openbase/bco.registry#67 "UserRegistry blocking sync" has been fixed.
        try {
            Registries.getUnitRegistry(true);
        } catch (CouldNotPerformException ex) {

        }
        ///

        final double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        final double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
        primaryStage.setTitle("BCozy");

        final StackPane root = new StackPane();
        foregroundPane = new ForegroundPane(screenHeight, screenWidth);
        foregroundPane.setMinHeight(root.getHeight());
        foregroundPane.setMinWidth(root.getWidth());
        final BackgroundPane backgroundPane = new BackgroundPane(foregroundPane);

        loadingPane = new LoadingPane(screenHeight, screenWidth);
        loadingPane.setMinHeight(root.getHeight());
        loadingPane.setMinWidth(root.getWidth());
        root.getChildren().addAll(backgroundPane, foregroundPane, loadingPane);

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
        maintenanceLayerController = new MaintenanceLayerController(backgroundPane.getMaintenancePane(), backgroundPane.getLocationPane());
        editingLayerController = new EditingLayerController(backgroundPane.getEditingPane(), backgroundPane.getLocationPane());

        ResponsiveHandler.addResponsiveToWindow(primaryStage);
        primaryStage.show();

        InfoPane.confirmation("Welcome");
        try {
            Registries.getUnitRegistry().addConnectionStateObserver(connectionObserver);
        } catch (NotAvailableException ex) {
            ExceptionPrinter.printHistory("Could not register bco connection observer!", ex, LOGGER);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        initRemotesAndLocation();
    }

    private void initRemotesAndLocation() {
        initTask = GlobalCachedExecutorService.submit(new Task() {
            @Override
            protected Object call() throws java.lang.Exception {
                try {
                    loadingPane.setTextLabelIdentifier("waitForConnection");
                    Registries.waitForData();

                    loadingPane.setTextLabelIdentifier("fillContextMenu");
                    foregroundPane.init();

                    contextMenuController.initTitledPaneMap();

                    loadingPane.setTextLabelIdentifier("connectLocationRemote");
                    locationPaneController.connectLocationRemote();
                    unitsPaneController.connectUnitRemote();
                    maintenanceLayerController.connectUnitRemote();
                    editingLayerController.connectUnitRemote();

                    return null;
                } catch (Exception ex) {
                    loadingPane.setTextLabelIdentifier("errorDuringStartup");
                    Thread.sleep(3000);
                    Exception exx = new FatalImplementationErrorException("Could not init panes", this, ex);
                    ExceptionPrinter.printHistoryAndExit(exx, LOGGER);
                    throw exx;
                }
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                loadingPane.setVisible(false);
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
            Registries.getUnitRegistry().removeConnectionStateObserver(connectionObserver);
        } catch (NotAvailableException ex) {
            ExceptionPrinter.printHistory("Could not remove bco connection observer!", ex, LOGGER);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
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
                    primaryStage.getScene().getStylesheets().addAll(Constants.DEFAULT_CSS, Constants.DARK_THEME_CSS);
                    ImageViewProvider.colorizeIconsToWhite();
                    baseColorIsWhite = true;
                    break;
                case Constants.LIGHT_THEME_CSS:
                    primaryStage.getScene().getStylesheets().clear();
                    primaryStage.getScene().getStylesheets().addAll(Constants.DEFAULT_CSS, Constants.LIGHT_THEME_CSS);
                    ImageViewProvider.colorizeIconsToBlack();
                    baseColorIsWhite = false;
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

}
