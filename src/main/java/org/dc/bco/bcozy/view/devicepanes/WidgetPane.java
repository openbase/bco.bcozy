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
        this.setMinHeight(Constants.SMALL_ICON);
        this.setMaxHeight(Constants.SMALL_ICON);
    }

    /**
     * Method creates a sliding widget pane, which includes a header(listener) and a body(sliding) with content.
     * @param headContent Content of the header.
     * @param bodyContent Content of the sliding body.
     */
    public void createWidgetPane(final BorderPane headContent, final Pane bodyContent) {
        headPart(headContent);
        bodyPart(bodyContent);

        setAnimation();
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
    }

    /**
     * Method adds style and listener to the head content and integrates it to the ground pane.
     * @param headContent Content of the header.
     */
    private void headPart(final BorderPane headContent) {
        head = headContent;
        head.getStyleClass().add("head-pane");
        head.setPrefHeight(headContent.getHeight());
        //head.setPrefWidth(100);
        head.setOnMouseClicked(paramT -> toggleVisibility());
        System.out.println(headContent.maxWidthProperty());
        System.out.println(headContent.prefWidthProperty());
        System.out.println(headContent.minWidthProperty());

        this.getChildren().add(head);
    }

    /**
     * Method adds style to the body content and integrates it to the ground pane.
     * @param bodyContent Content of the sliding body.
     */
    private void bodyPart(final Pane bodyContent) {
        body = bodyContent;
        body.setPrefHeight(50);
        //body.setPrefWidth(100);
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
    private void setAnimation() {
        rectangleClip = new Rectangle(300, Constants.SMALL_ICON);
        this.setClip(rectangleClip);

        timelineDown = new Timeline();
        timelineUp = new Timeline();

        // animation for scroll down
        timelineDown.setCycleCount((int) Constants.ONE);
        timelineDown.setAutoReverse(true);

        final KeyValue kvDwn1 = new KeyValue(rectangleClip.heightProperty(), 70);
        final KeyValue kvDwn2 = new KeyValue(rectangleClip.translateYProperty(), Constants.ZERO);
        final KeyValue kvDwn3 = new KeyValue(body.prefHeightProperty(), 50);
        final KeyValue kvDwn4 = new KeyValue(body.translateYProperty(), Constants.ZERO);
        final KeyValue kvDwn5 = new KeyValue(this.maxHeightProperty(), 70);
        final KeyValue kvDwn6 = new KeyValue(this.minHeightProperty(), 70);
        final KeyFrame kfDwn = new KeyFrame(Duration.millis(Constants.ANIMATION_TIME), kvDwn1, kvDwn2, kvDwn3, kvDwn4,
                kvDwn5, kvDwn6);
        timelineDown.getKeyFrames().add(kfDwn);

        // animation for scroll up
        timelineUp.setCycleCount((int) Constants.ONE);
        timelineUp.setAutoReverse(true);

        final KeyValue kvUp1 = new KeyValue(rectangleClip.heightProperty(), Constants.SMALL_ICON);
        final KeyValue kvUp2 = new KeyValue(rectangleClip.translateYProperty(), Constants.ZERO);
        final KeyValue kvUp3 = new KeyValue(body.prefHeightProperty(), Constants.ZERO);
        final KeyValue kvUp4 = new KeyValue(body.translateYProperty(), Constants.ZERO);
        final KeyValue kvUp5 = new KeyValue(this.maxHeightProperty(), Constants.SMALL_ICON);
        final KeyValue kvUp6 = new KeyValue(this.minHeightProperty(), Constants.SMALL_ICON);
        final KeyFrame kfUp = new KeyFrame(Duration.millis(Constants.ANIMATION_TIME), kvUp1, kvUp2, kvUp3, kvUp4,
                kvUp5, kvUp6);
        timelineUp.getKeyFrames().add(kfUp);
    }
}

/*
css setting...


.custom-titled-pane
{
    -fx-skin: "com.sun.javafx.scene.control.skin.TitledPaneSkin";
    -fx-text-fill: -fx-text-base-color;
}
.custom-titled-pane:focused
{
    -fx-text-fill: white;
}

.body-pane {
    -fx-background-color:
            -fx-box-border,
            linear-gradient(to bottom, derive(-fx-color,-02%), derive(-fx-color,65%) 12%,
            derive(-fx-color,23%) 88%, derive(-fx-color,50%) 99%, -fx-box-border);
    -fx-background-insets: 0, 0 1 1 1;
}

        .head-pane {
        -fx-background-color: -fx-box-border, -fx-inner-border, -fx-body-color;
        -fx-background-insets: 0, 1, 2;
        -fx-background-radius: 5 5 0 0, 4 4 0 0, 3 3 0 0;
        }

        .custom-titled-pane:focused > .head-pane
        {
        -fx-color: -fx-focus-color;
        }
 */
