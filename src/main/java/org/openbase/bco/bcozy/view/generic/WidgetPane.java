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
package org.openbase.bco.bcozy.view.generic;

import com.jfoenix.controls.JFXToggleButton;
import org.openbase.jul.visual.javafx.iface.DynamicPane;
import de.jensd.fx.glyphs.GlyphIcons;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import java.util.concurrent.Future;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.ObserverText;
import org.openbase.bco.bcozy.view.SVGIcon;
import org.openbase.bco.bcozy.view.pane.unit.AbstractUnitPane;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author agatting
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class WidgetPane extends VBox implements DynamicPane {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    /**
     * Text for tooltip dependent on selected language.
     */
    private final ObserverText infoText = new ObserverText(Constants.DUMMY_LABEL);

    /**
     * ToggleSwitch Button on right side of the header Pane. Is visible via activation at
     * constructor (oneClickActivatable).
     */
    private final JFXToggleButton toggleSwitch = new JFXToggleButton();

    /**
     * GridPane as Area on the left side of the header Pane (Icon, tooltip, ...).
     */
    private final GridPane iconPane = new GridPane();

    /**
     * Tooltip for Icon description (current state).
     */
    private final Tooltip tooltip = new Tooltip("");

    private final SVGIcon mainIcon;

    private final Label widgetLabel;
    
    //private final Label unitCount;

    public enum DisplayMode {
        ICON_ONLY,

    }

    public void setDisplayMode(final DisplayMode displayMode) {
       // headPane.getChildren().clear();
        switch (displayMode) {
            case ICON_ONLY:
                headPane.setCenter(null);
                headPane.setRight(null);
                //remove bright hover animation
                headPane.getStyleClass().clear();
                headPane.getStyleClass().add("head-pane-2");
             //   unitCount.setTextAlignment(TextAlignment.CENTER);
             //   unitCount.setFont(new Font(12));
             //   headPane.setBottom(unitCount);
                break;
            default:
                break;
        }
    }
    
   /* public StringProperty getCountLabelProperty() {
        
        return this.unitCount.textProperty();
    }*/

    /**
     * Head content pane to visualize the main functionally.
     */
    final BorderPane headPane;

    protected final BooleanProperty primaryActivationProperty;
    protected final BooleanProperty secondaryActivationProperty;

    private final ChangeListener<Boolean> primaryActivationObserver;

    /**
     * defines if this widget can be activated e.g. by mouse click.
     */
    private final boolean activateable;

    /**
     * Constructor for the widget pane.
     *
     * @param activateable defines if this widget should be activateable e.g. via mouse click.
     */
    public WidgetPane(final boolean activateable) {
        this.activateable = activateable;
//        this.getStyleClass().add("widget-pane");
        this.headPane = new BorderPane();
        this.primaryActivationProperty = new SimpleBooleanProperty();
        this.secondaryActivationProperty = new SimpleBooleanProperty();
        this.mainIcon = new SVGIcon(MaterialDesignIcon.VECTOR_CIRCLE, Constants.SMALL_ICON, false);
        this.widgetLabel = new Label("?");

        this.primaryActivationObserver = new ChangeListener<Boolean>() {
            private Future currentTask;

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean lastActivation, Boolean newActivation) {
                try {
                    // cancel all already running tasks.
                    if (currentTask != null) {
                        currentTask.cancel(true);
                    }
                    currentTask = applyPrimaryActivationUpdate(newActivation);
                } catch (CouldNotPerformException ex) {
                    ExceptionPrinter.printHistory("Could not apply activation update " + this, ex, LOGGER);
                }
            }
        };
        primaryActivationProperty().addListener(primaryActivationObserver);
    }

    @Override
    public void initContent() {
        toggleSwitch.getStyleClass().clear();
        toggleSwitch.setMouseTransparent(true);

        Tooltip.install(iconPane, tooltip);

        initHeadPane();

        if (headPane.getCenter() != null) {
            BorderPane.setMargin(headPane.getCenter(), new Insets(0.0, 0.0, 0.0, Constants.INSETS));
        }
        if (headPane.getRight() != null) {
            BorderPane.setMargin(headPane.getRight(), new Insets(0.0, Constants.INSETS, 0.0, 0.0));
        }
        this.getChildren().add(headPane);

//        this.minHeightProperty().bind(headPane.prefHeightProperty());
//        this.maxHeightProperty().bind(headPane.prefHeightProperty());
        // setup mouse handling
        if (activateable) {
            final EventHandler<MouseEvent> mouseEventHandler = new EventHandler<MouseEvent>() {
                @Override
                public void handle(final MouseEvent event) {
                    if (!event.isStillSincePress()) {
                        return;
                    }
                    try {
                        switch (event.getButton()) {
                            case PRIMARY:
                                togglePrimaryActivation();
                                break;
                            case SECONDARY:
                                toggleSecondaryActivation();
                                break;
                        }
                    } catch (RuntimeException ex) {
                        ExceptionPrinter.printHistory("Could not handle mouse event!", ex, LOGGER);
                    }
                    event.consume();
                }
            };
            toggleSwitch.selectedProperty().bindBidirectional(primaryActivationProperty);
            toggleSwitch.setOnMouseClicked(mouseEventHandler);
            headPane.setOnMouseClicked(mouseEventHandler);
            headPane.setOnSwipeDown((event) -> {
                secondaryActivationProperty.set(true);
            });
            headPane.setOnSwipeUp((event) -> {
                secondaryActivationProperty.set(false);
            });
            headPane.setOnScrollStarted((event) -> {
                if (event.getDeltaY() > 0) {
                    secondaryActivationProperty.set(true);
                } else if (event.getDeltaY() < 0) {
                    secondaryActivationProperty.set(false);

                }
            });
        }
    }

    public void initHeadPane() {
        headPane.getStyleClass().clear();
        headPane.getStyleClass().add("head-pane");
        iconPane.getStyleClass().add(Constants.ICONS_CSS_STRING);
        iconPane.add(mainIcon, 0, 0);
        iconPane.setAlignment(Pos.CENTER);

        toggleSwitch.setBackground(Background.EMPTY);

        headPane.setLeft(iconPane);
        headPane.setAlignment(iconPane, Pos.CENTER);

        headPane.setCenter(widgetLabel);
        headPane.setAlignment(widgetLabel, Pos.CENTER_LEFT);

        if (activateable) {
            headPane.setRight(toggleSwitch);
            headPane.setAlignment(toggleSwitch, Pos.CENTER);
        }
    }

    public void setIcon(final GlyphIcons foregroundIcon, final Color foregroundIconColor, final GlyphIcons backgroundIcon, final Color backgroundIconColor) {
        mainIcon.setForegroundIcon(foregroundIcon, foregroundIconColor);
        mainIcon.setBackgroundIcon(backgroundIcon, backgroundIconColor);
    }

    public void setIcon(final GlyphIcons foregroundIcon, final GlyphIcons backgroundIcon) {
        mainIcon.setForegroundIcon(foregroundIcon);
        mainIcon.setBackgroundIcon(backgroundIcon);
    }

    public void setLabel(final String label) {
        widgetLabel.setText(label);
    }

    public SVGIcon getIcon() {
        return mainIcon;
    }

    @Override
    public void updateDynamicContent() {

    }

    public BooleanProperty primaryActivationProperty() {
        return primaryActivationProperty;
    }

    public BooleanProperty secondaryActivationProperty() {
        return secondaryActivationProperty;
    }

    public synchronized void setPrimaryActivationWithoutNotification(final Boolean activation) {
        primaryActivationProperty.removeListener(primaryActivationObserver);
        primaryActivationProperty.setValue(activation);
        primaryActivationProperty.addListener(primaryActivationObserver);
    }

    public void togglePrimaryActivation() {
        primaryActivationProperty.set(!primaryActivationProperty.getValue());
    }

    public void toggleSecondaryActivation() {
        secondaryActivationProperty.set(!secondaryActivationProperty.getValue());
    }

    public void setInfoText(final String identifier) {
        infoText.setIdentifier(identifier);
    }

    /**
     * Overwrite this method to get informed about main function updates.
     *
     * @param activation a boolean value which refers to the current function activation.
     * @return should return a future object of the triggered tasks or null if no task was triggered.
     * @throws CouldNotPerformException can be thrown if the update fails.
     */
    protected Future applyPrimaryActivationUpdate(final boolean activation) throws CouldNotPerformException {
        return null;
    }
}
