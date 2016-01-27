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
import javafx.animation.Timeline;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.dc.bco.bcozy.view.Constants;

/**
 * Created by agatting on 10.12.15.
 */
public class WidgetPane extends VBox {

    private BorderPane head;
    private Pane body;
    private final SimpleBooleanProperty isExpanded = new SimpleBooleanProperty();
    private Timeline timelineUp;
    private Timeline timelineDown;
    private Rectangle rectangleClip;

    /**
     * Constructor for the widget pane.
     */
    public WidgetPane() {
        //this.setMinHeight(Constants.SMALL_ICON);
        //this.setMaxHeight(Constants.SMALL_ICON);
    }

    /**
     * Method creates a sliding widget pane, which includes a header(listener) and a body(sliding) with content.
     *
     * @param headContent Content of the header.
     * @param bodyContent Content of the sliding body.
     */
    public void createWidgetPane(final BorderPane headContent, final Pane bodyContent) {
        headPart(headContent);
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
     * Method creates a sliding widget pane, which includes a header(listener) and a body(sliding) with content.
     *
     * @param headContent Content of the header.
     */
    public void createWidgetPane(final BorderPane headContent) {
        headPart(headContent);

        isExpanded.set(false);
    }

    /**
     * Method adds style and listener to the head content and integrates it to the ground pane.
     *
     * @param headContent Content of the header.
     */
    private void headPart(final BorderPane headContent) {
        head = headContent;
        head.getStyleClass().clear();
        head.getStyleClass().add("head-pane");
        head.setOnMouseClicked(paramT -> toggleVisibility());

        this.getChildren().add(head);
    }

    /**
     * Method adds style to the body content and integrates it to the ground pane.
     *
     * @param bodyContent Content of the sliding body.
     */
    private void bodyPart(final Pane bodyContent) {
        body = bodyContent;
        body.getStyleClass().clear();
        body.getStyleClass().addAll("body-pane");

        this.getChildren().add(body);
    }

    /**
     * Method changes state of sliding widget via listener of the header.
     */
    private void toggleVisibility() {
        if (isExpanded.get()) {
            isExpanded.set(false);
        } else {
            isExpanded.set(true);
        }
    }

    /**
     * Method creates animation parameters for scroll down and up.
     */
    private void setAnimation(final BorderPane headContent, final Pane bodyContent) {

        //TODO get generic width
        rectangleClip = new Rectangle(Integer.MAX_VALUE, headContent.prefHeightProperty().getValue());
        this.setClip(rectangleClip);

        timelineDown = new Timeline();
        timelineUp = new Timeline();

        // animation for scroll down
        timelineDown.setCycleCount(0);
        timelineDown.setAutoReverse(true);

        final KeyValue kvDwn1 = new KeyValue(rectangleClip.heightProperty(),
                headContent.prefHeightProperty().getValue() + bodyContent.prefHeightProperty().getValue());
        final KeyValue kvDwn2 = new KeyValue(rectangleClip.translateYProperty(), 0);
        final KeyValue kvDwn3 = new KeyValue(body.prefHeightProperty(), bodyContent.prefHeightProperty().getValue());
        final KeyValue kvDwn4 = new KeyValue(body.translateYProperty(), 0);
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
        final KeyValue kvUp3 = new KeyValue(body.prefHeightProperty(), 0);
        final KeyValue kvUp4 = new KeyValue(body.translateYProperty(), 0);
        final KeyValue kvUp5 = new KeyValue(this.maxHeightProperty(), headContent.prefHeightProperty().getValue());
        final KeyValue kvUp6 = new KeyValue(this.minHeightProperty(), headContent.prefHeightProperty().getValue());
        final KeyFrame kfUp = new KeyFrame(Duration.millis(Constants.ANIMATION_TIME), kvUp1, kvUp2, kvUp3,
                kvUp4, kvUp5, kvUp6);
        timelineUp.getKeyFrames().add(kfUp);
    }

    /**
     * Enables or disables the Widgetpane.
     *
     * @param disabled disabled
     */
    public void setWidgetPaneDisabled(final boolean disabled) {
        if (disabled) {
            this.setDisabled(true);
            this.isExpanded.set(false);
        } else {
            this.setDisabled(false);
        }
    }
}

