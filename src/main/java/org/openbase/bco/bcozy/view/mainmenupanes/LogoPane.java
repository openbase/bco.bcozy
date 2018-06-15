package org.openbase.bco.bcozy.view.mainmenupanes;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import org.openbase.bco.bcozy.util.ThemeManager;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.bco.registry.unit.remote.UnitRegistryRemote;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.pattern.Remote.ConnectionState;
import org.openbase.jul.schedule.GlobalScheduledExecutorService;
import org.openbase.jul.visual.javafx.JFXConstants;
import org.openbase.jul.visual.javafx.fxml.FXMLProcessor;
import org.openbase.jul.visual.javafx.geometry.svg.SVGLogoPaneController;
import org.openbase.jul.visual.javafx.geometry.svg.SVGPathIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Contains the Application Logo.
 *
 * @author vdasilva
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class LogoPane extends HBox {

    public static final double LOGO_SIZE = 80;

    private static final Logger LOGGER = LoggerFactory.getLogger(LogoPane.class);

    private final VBox statusIcon;

    private final SVGLogoPaneController logoPaneController;

    private final SVGPathIcon smallLogoIcon;

    public LogoPane() throws InstantiationException {
        try {
            final Pair<Pane, SVGLogoPaneController> logoPanePair = FXMLProcessor.loadFxmlPaneAndControllerPair("/org/openbase/jul/visual/javafx/geometry/svg/SVGLogoPane.fxml", SVGLogoPaneController.class, getClass());
            this.logoPaneController = logoPanePair.getValue();

            smallLogoIcon = new SVGPathIcon(
                    "icons/bco_logo_simple_background_one_path.svg",
                    "icons/bco_logo_simple_one_path.svg", getClass(), JFXConstants.ICON_SIZE_MIDDLE);

            logoPaneController.setText("BCozy");
            logoPaneController.setSvgIcon(new SVGPathIcon(
                    "icons/bco_logo_simple_background_one_path.svg",
                    "icons/bco_logo_simple_one_path.svg", getClass(), LOGO_SIZE));
            logoPaneController.setSize(LOGO_SIZE);

            logoPaneController.getSVGIcon().setBackgroundIconColor(Color.WHITE);
            smallLogoIcon.setBackgroundIconColor(Color.WHITE);
            logoPaneController.getTextPane().getStyleClass().add(JFXConstants.CSS_ICON);

            // update icon state
            try {
                UnitRegistryRemote unitRegistry = Registries.getUnitRegistry(false);
                unitRegistry.addConnectionStateObserver((source, connectionState) -> updateIconState(connectionState));
                updateIconState(unitRegistry.getConnectionState());
                smallLogoIcon.addEventFilter(MouseEvent.MOUSE_PRESSED, (e -> {
                    updateIconState();
                    unitRegistry.ping();
                }));
                logoPaneController.getSVGIcon().addEventFilter(MouseEvent.MOUSE_PRESSED, (e -> {
                    updateIconState();
                    unitRegistry.ping();
                }));

                // enabled auto ping to detect connection errors
                GlobalScheduledExecutorService.scheduleAtFixedRate(() -> {
                    unitRegistry.ping();
                }, 30, 60, TimeUnit.SECONDS);


            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                // handling can be  removed in bco v2.0
            }

            // setup status icon
            statusIcon = new VBox(smallLogoIcon);
            statusIcon.setAlignment(Pos.CENTER);
            statusIcon.setSpacing(20.0);

            this.setAlignment(Pos.CENTER);
            this.getChildren().addAll(logoPanePair.getKey());
            this.setSpacing(20);


            // register for theme changes.
            ThemeManager.getInstance().themeName.addListener((observable, oldValue, newValue) -> updateIconState(false));

        } catch (CouldNotPerformException ex) {
            throw new InstantiationException(this, ex);
        }
    }

    private void updateIconState() {
        updateIconState(true);
    }

    private void updateIconState(boolean animation) {
        try {
            updateIconState(Registries.getUnitRegistry().getConnectionState(), animation);
        } catch (NotAvailableException ex) {
            ExceptionPrinter.printHistory("Could not update icon state!", ex, LOGGER);
        }
    }

    private void updateIconState(final ConnectionState connectionState) {
        updateIconState(connectionState, true);
    }
    private void updateIconState(final ConnectionState connectionState, boolean animation) {

        Color defaultColor, inversDefaultColor;
        if (ThemeManager.isDarkThemeSelected()) {
            defaultColor = Color.WHITE;
            inversDefaultColor = Color.BLACK;
        } else {
            defaultColor = Color.BLACK;
            inversDefaultColor = Color.WHITE;
        }

        logoPaneController.getSVGIcon().setForegroundIconColor(defaultColor);
        smallLogoIcon.setForegroundIconColor(defaultColor);

        switch (connectionState) {
            case CONNECTED:
                logoPaneController.getSVGIcon().setBackgroundIconColor(inversDefaultColor);
                smallLogoIcon.setBackgroundIconColor(inversDefaultColor);
                if(animation) {
                    logoPaneController.getSVGIcon().setBackgroundIconColorAnimated(Color.GREEN, 20);
                    smallLogoIcon.setBackgroundIconColorAnimated(Color.GREEN, 20);
                }
                break;
            case CONNECTING:
            case REINITIALIZING:
            case RECONNECTING:
                logoPaneController.getSVGIcon().setBackgroundIconColor(Color.ORANGE);
                smallLogoIcon.setBackgroundIconColor(Color.ORANGE);
                if(animation) {
                    logoPaneController.getSVGIcon().setBackgroundIconColorAnimated(inversDefaultColor, 20);
                    smallLogoIcon.setBackgroundIconColorAnimated(inversDefaultColor, 20);
                }
                break;
            case DISCONNECTED:
            case UNKNOWN:
            default:
                logoPaneController.getSVGIcon().setBackgroundIconColor(Color.RED);
                smallLogoIcon.setBackgroundIconColor(Color.RED);
                if(animation) {
                    logoPaneController.getSVGIcon().setBackgroundIconColorAnimated(inversDefaultColor, 20);
                    smallLogoIcon.setBackgroundIconColorAnimated(inversDefaultColor, 20);
                }
                break;
        }
    }

    //@Override
    public Node getStatusIcon() {
        return statusIcon;
    }
}
