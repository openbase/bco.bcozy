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
import de.jensd.fx.glyphs.GlyphIcons;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.openbase.bco.bcozy.util.LabelSynchronizer;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.ObserverText;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.visual.javafx.JFXConstants;
import org.openbase.jul.visual.javafx.geometry.svg.SVGGlyphIcon;
import org.openbase.jul.visual.javafx.iface.DynamicPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.language.LabelType;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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

    private final SVGGlyphIcon mainIcon;

    private final Label widgetLabel;

    private LabelSynchronizer labelSynchronizer;
    
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
        this.labelSynchronizer = new LabelSynchronizer();
        this.primaryActivationProperty = new SimpleBooleanProperty();
        this.secondaryActivationProperty = new SimpleBooleanProperty();
        this.mainIcon = new SVGGlyphIcon(MaterialDesignIcon.VECTOR_CIRCLE, JFXConstants.ICON_SIZE_SMALL, false);
        this.widgetLabel = new Label();
        this.widgetLabel.textProperty().bind(labelSynchronizer.textProperty());

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
                    currentTask.get();
                } catch (CouldNotPerformException | ExecutionException | InterruptedException ex) {
                    ExceptionPrinter.printHistory("Could not apply activation update " + this, ex, LOGGER);

                    // If the task failed, reset the activation value.
                    setPrimaryActivationWithoutNotification(lastActivation);
                }
            }
        };
        primaryActivationProperty().addListener(primaryActivationObserver);

        // @cromankiewicz: Without this primitive dummy listener, the toggleSwitch does not seem to
        // be notified if the first try of the primary action fails. Don't ask me why.
        primaryActivationProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                // do nothing
            }
        });
    }

    @Override
    public void initContent() {
        toggleSwitch.getStyleClass().clear();
        toggleSwitch.setMouseTransparent(true);

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
//            headPane.setOnSwipeDown((event) -> {
//                secondaryActivationProperty.set(true);
//            });
//            headPane.setOnSwipeUp((event) -> {
//                secondaryActivationProperty.set(false);
//            });
//            headPane.setOnScrollStarted((event) -> {
//                if (event.getDeltaY() > 0) {
//                    secondaryActivationProperty.set(true);
//                } else if (event.getDeltaY() < 0) {
//                    secondaryActivationProperty.set(false);
//
//                }
//            });
        }
    }

    public void initHeadPane() {
        headPane.getStyleClass().clear();
        headPane.getStyleClass().add("head-pane");
        iconPane.getStyleClass().add(JFXConstants.CSS_ICON);
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

    public void setIcon(final GlyphIcons foregroundIconProvider, final Color foregroundIconColor, final GlyphIcons backgroundIconProvider, final Color backgroundIconColor) {
        mainIcon.setForegroundIcon(foregroundIconProvider, foregroundIconColor);
        mainIcon.setBackgroundIcon(backgroundIconProvider, backgroundIconColor);
    }

    public void setIcon(final GlyphIcons foregroundIconProvider, final GlyphIcons backgroundIconProvider) {
        mainIcon.setForegroundIcon(foregroundIconProvider);
        mainIcon.setBackgroundIcon(backgroundIconProvider);
    }

    public void setLabel(final LabelType.Label label) {
        labelSynchronizer.updateLabel(label);
    }

    public SVGGlyphIcon getIcon() {
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

    @Deprecated
    public void setInfoText(final String identifier) {
//        infoText.setIdentifier(identifier);
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
