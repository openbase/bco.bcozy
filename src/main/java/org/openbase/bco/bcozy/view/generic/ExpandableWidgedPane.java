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

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.openbase.bco.bcozy.view.Constants;

/**
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class ExpandableWidgedPane extends WidgetPane {

    final Pane bodyPane;
    private Timeline timelineUp;
    private Timeline timelineDown;

    /**
     * Property to define expand state.
     */
    protected final BooleanProperty expansionProperty;

    public ExpandableWidgedPane(final boolean initialExpanded, final boolean activateable) {
        super(activateable);
        this.bodyPane = new HBox();
        this.expansionProperty = new SimpleBooleanProperty(false);
        this.initContent();
    }

    private void initContent() {
        // init body pane
        bodyPane.getStyleClass().clear();
        bodyPane.getStyleClass().addAll("body-pane");
//        getChildren().add(bodyPane);

        expansionProperty.addListener((paramObservableValue, paramT1, expaneded) -> {
            bodyPane.setVisible(expaneded);
            if (expaneded) {
//                 To expand

//                timelineDown.play();
            } else {
                // To close
//                timelineUp.play();
            }
        });

        expansionProperty.set(false);

        // make sure pane is not expanded if the pane is disabled
        disableProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean disabled) -> {
            if (disabled) {
                expansionProperty.set(false);
            }
        });

        initBodyContent(bodyPane);
//        setAnimation(headPane, bodyPane);

        secondaryActivationProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            expansionProperty.set(newValue);
        });

    }

    /**
     * Should be overwritten for custom body content implementation.
     * @param bodyPane
     */
    protected void initBodyContent(final Pane bodyPane) {

    }

    private void setAnimation(final BorderPane headContent, final Pane bodyContent) {
        final Rectangle rectangleClip = new Rectangle(Integer.MAX_VALUE, headContent.prefHeightProperty().getValue());
        this.setClip(rectangleClip);

        timelineDown = new Timeline();
        timelineUp = new Timeline();

        // animation for scroll down
        timelineDown.setCycleCount(0);
        timelineDown.setAutoReverse(true);

        final KeyValue kvDwn1 = new KeyValue(rectangleClip.heightProperty(), headContent.prefHeightProperty().getValue() + bodyContent.prefHeightProperty().getValue());
        final KeyValue kvDwn2 = new KeyValue(rectangleClip.translateYProperty(), 0);
        final KeyValue kvDwn3 = new KeyValue(bodyContent.prefHeightProperty(), bodyContent.prefHeightProperty().getValue());
        final KeyValue kvDwn4 = new KeyValue(bodyContent.translateYProperty(), 0);
        final KeyValue kvDwn5 = new KeyValue(this.maxHeightProperty(), headContent.prefHeightProperty().getValue() + bodyContent.prefHeightProperty().getValue());
        final KeyValue kvDwn6 = new KeyValue(this.minHeightProperty(), headContent.prefHeightProperty().getValue() + bodyContent.prefHeightProperty().getValue());
        final KeyFrame kfDwn = new KeyFrame(Duration.millis(Constants.ANIMATION_TIME), kvDwn1, kvDwn2, kvDwn3, kvDwn4, kvDwn5, kvDwn6);
        timelineDown.getKeyFrames().add(kfDwn);

        // animation for scroll up
        timelineUp.setCycleCount(1);
        timelineUp.setAutoReverse(true);

        final KeyValue kvUp1 = new KeyValue(rectangleClip.heightProperty(), headContent.prefHeightProperty().getValue());
        final KeyValue kvUp2 = new KeyValue(rectangleClip.translateYProperty(), 0);
        final KeyValue kvUp3 = new KeyValue(bodyContent.prefHeightProperty(), 0);
        final KeyValue kvUp4 = new KeyValue(bodyContent.translateYProperty(), 0);
        final KeyValue kvUp5 = new KeyValue(this.maxHeightProperty(), headContent.prefHeightProperty().getValue());
        final KeyValue kvUp6 = new KeyValue(this.minHeightProperty(), headContent.prefHeightProperty().getValue());
        final KeyFrame kfUp = new KeyFrame(Duration.millis(Constants.ANIMATION_TIME), kvUp1, kvUp2, kvUp3, kvUp4, kvUp5, kvUp6);
        timelineUp.getKeyFrames().add(kfUp);
    }

    private void toggleVisibility() {
        if (expansionProperty.get()) {
            expansionProperty.set(false);
        } else {
            expansionProperty.set(true);
        }
    }

    public BooleanProperty expansionProperty() {
        return expansionProperty;
    }
}
