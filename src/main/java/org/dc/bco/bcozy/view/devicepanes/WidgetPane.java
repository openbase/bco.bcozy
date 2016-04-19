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
package org.dc.bco.bcozy.view.devicepanes;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.controlsfx.control.ToggleSwitch;
import org.dc.bco.bcozy.view.Constants;
import org.dc.bco.bcozy.view.ObserverText;

/**
 * Created by agatting on 10.12.15.
 */
public class WidgetPane extends VBox {

    //CHECKSTYLE.OFF: VisibilityModifier
    /**
     * Text for tooltip dependent on selected language.
     */
    protected final ObserverText observerText = new ObserverText("dummy");
    /**
     * Boolean to registry a valid one click scenario.
     */
    protected final BooleanProperty oneClick = new SimpleBooleanProperty(false);
    /**
     * ToggleSwitch Button on right side of the header Pane. Is visible via activation at
     * constructor (oneClickActivatable).
     */
    protected final ToggleSwitch toggleSwitch = new ToggleSwitch();
    /**
     * GridPane as Area on the left side of the header Pane (Icon, tooltip, ...).
     */
    protected final GridPane iconPane = new GridPane();
    /**
     * Tooltip for Icon description (current state).
     */
    protected final Tooltip tooltip = new Tooltip("");
    //CHECKSTYLE.ON: VisibilityModifier

    private final SimpleBooleanProperty isExpanded = new SimpleBooleanProperty();
    private Timeline timelineUp;
    private Timeline timelineDown;

    /**
     * Constructor for the widget pane.
     */
    public WidgetPane() {
        this.getStyleClass().add("widget-pane");
    }

    /**
     * Method creates a sliding widget pane, which includes a header(listener) and a body(sliding) with content.
     *
     * @param headContent Content of the header.
     * @param bodyContent Content of the sliding body.
     * @param oneClickActivatable Head of Widget reacts on one click activation (on/off control).
     */
    public void createWidgetPane(final BorderPane headContent, final Pane bodyContent,
                                 final boolean oneClickActivatable) {
        headPart(headContent, true, oneClickActivatable);
        bodyPart(bodyContent);

        setAnimation(headContent, bodyContent);
        isExpanded.set(false);
        isExpanded.addListener((paramObservableValue, paramT1, paramT2) -> {
            if (paramT2) {
                // To expand
                timelineDown.play();
            } else {
                // To close
                timelineUp.play();
            }
        });

        this.setMinHeight(headContent.prefHeightProperty().getValue());
        this.setMaxHeight(headContent.prefHeightProperty().getValue());
    }

    /**
     * Method creates a sliding widget pane, which includes a header without a body content.
     *
     * @param headContent Content of the header.
     * @param oneClickActivatable Head of Widget reacts on one click activation (on/off control).
     */
    public void createWidgetPane(final BorderPane headContent, final boolean oneClickActivatable) {
        headPart(headContent, false, oneClickActivatable);
        isExpanded.set(false);
    }

    private void headPart(final BorderPane headContent, final boolean withBody, final boolean oneClickActivatable) {
        toggleSwitch.setMouseTransparent(true);

        if (oneClickActivatable) {
            headContent.setRight(toggleSwitch);
        }
        headContent.getStyleClass().clear();
        headContent.getStyleClass().add("head-pane");
        headContent.setLeft(iconPane);
        observeMouseClicks(headContent, withBody, oneClickActivatable);

        Tooltip.install(iconPane, tooltip);

        if (headContent.getCenter() != null) {
            BorderPane.setMargin(headContent.getCenter(), new Insets(0.0, 0.0, 0.0, Constants.INSETS));
        }
        if (headContent.getRight() != null) {
            BorderPane.setMargin(headContent.getRight(), new Insets(0.0, Constants.INSETS, 0.0, 0.0));
        }
        this.getChildren().add(headContent);
    }

    private void observeMouseClicks(final BorderPane headContent, final boolean withBody, final boolean activatable) {
        if (withBody) {
            final Duration maxTimeIntervalClick = Duration.millis(Constants.CLICK_TIME_INTERVAL_MILLI);
            final PauseTransition clickTimer = new PauseTransition(maxTimeIntervalClick);
            final IntegerProperty clickCnt = new SimpleIntegerProperty(0);

            clickTimer.setOnFinished(event -> {
                if (clickCnt.get() == 1) {
                    oneClickBoolFlag();
                }
                clickCnt.set(0); // reset
            });

            headContent.setOnMouseClicked(event -> {
                clickCnt.set(clickCnt.get() + 1);

                if (clickCnt.get() == 2) {
                    toggleVisibility();
                    clickCnt.set(0); // reset
                }
                clickTimer.playFromStart();
            });
        } else if (activatable) {
            headContent.setOnMouseClicked(event -> oneClickBoolFlag());
        }
    }

    private void bodyPart(final Pane bodyContent) {
        bodyContent.getStyleClass().clear();
        bodyContent.getStyleClass().addAll("body-pane");

        this.getChildren().add(bodyContent);
    }

    private void oneClickBoolFlag() {
        if (oneClick.get()) {
            oneClick.set(false);
        } else {
            oneClick.set(true);
        }
    }

    private void toggleVisibility() {
        if (isExpanded.get()) {
            isExpanded.set(false);
        } else {
            isExpanded.set(true);
        }
    }

    private void setAnimation(final BorderPane headContent, final Pane bodyContent) {
        final Rectangle rectangleClip = new Rectangle(Integer.MAX_VALUE, headContent.prefHeightProperty().getValue());
        this.setClip(rectangleClip);

        timelineDown = new Timeline();
        timelineUp = new Timeline();

        // animation for scroll down
        timelineDown.setCycleCount(0);
        timelineDown.setAutoReverse(true);

        final KeyValue kvDwn1 = new KeyValue(rectangleClip.heightProperty(),
                headContent.prefHeightProperty().getValue() + bodyContent.prefHeightProperty().getValue());
        final KeyValue kvDwn2 = new KeyValue(rectangleClip.translateYProperty(), 0);
        final KeyValue kvDwn3 = new KeyValue(bodyContent.prefHeightProperty(),
                bodyContent.prefHeightProperty().getValue());
        final KeyValue kvDwn4 = new KeyValue(bodyContent.translateYProperty(), 0);
        final KeyValue kvDwn5 = new KeyValue(this.maxHeightProperty(),
                headContent.prefHeightProperty().getValue() + bodyContent.prefHeightProperty().getValue());
        final KeyValue kvDwn6 = new KeyValue(this.minHeightProperty(),
                headContent.prefHeightProperty().getValue() + bodyContent.prefHeightProperty().getValue());
        final KeyFrame kfDwn = new KeyFrame(Duration.millis(Constants.ANIMATION_TIME), kvDwn1, kvDwn2, kvDwn3,
                kvDwn4, kvDwn5, kvDwn6);
        timelineDown.getKeyFrames().add(kfDwn);

        // animation for scroll up
        timelineUp.setCycleCount(1);
        timelineUp.setAutoReverse(true);

        final KeyValue kvUp1 = new KeyValue(rectangleClip.heightProperty(),
                headContent.prefHeightProperty().getValue());
        final KeyValue kvUp2 = new KeyValue(rectangleClip.translateYProperty(), 0);
        final KeyValue kvUp3 = new KeyValue(bodyContent.prefHeightProperty(), 0);
        final KeyValue kvUp4 = new KeyValue(bodyContent.translateYProperty(), 0);
        final KeyValue kvUp5 = new KeyValue(this.maxHeightProperty(), headContent.prefHeightProperty().getValue());
        final KeyValue kvUp6 = new KeyValue(this.minHeightProperty(), headContent.prefHeightProperty().getValue());
        final KeyFrame kfUp = new KeyFrame(Duration.millis(Constants.ANIMATION_TIME), kvUp1, kvUp2, kvUp3,
                kvUp4, kvUp5, kvUp6);
        timelineUp.getKeyFrames().add(kfUp);
    }

    /**
     * Enables or disables the WidgetPane.
     *
     * @param disabled disabled
     */
    public void setWidgetPaneDisable(final boolean disabled) {
        if (disabled) {
            this.setDisable(true);
            this.isExpanded.set(false);
        } else {
            this.setDisable(false);
        }
    }
}

