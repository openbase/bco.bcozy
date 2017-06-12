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
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;

/**
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class ExpandableWidgedPane extends WidgetPane {

    final Pane bodyPane;
    private Timeline timelineUp;
    private Timeline timelineDown;
    private boolean initialExpanded;

    /**
     * Property to define expand state.
     */
    protected final BooleanProperty expansionProperty;

    public ExpandableWidgedPane(final boolean initialExpanded, final boolean activateable) {
        super(activateable);
        this.initialExpanded = initialExpanded;
        this.bodyPane = new HBox();
        this.expansionProperty = new SimpleBooleanProperty(false);
    }

    @Override
    public void initContent() {
        super.initContent();

        // init body pane style
        bodyPane.getStyleClass().clear();
        bodyPane.getStyleClass().addAll("body-pane");

        // load custom content
        try {
            initBodyContent(bodyPane);
        } catch (CouldNotPerformException | RuntimeException ex) {
            ExceptionPrinter.printHistory("Could not init body content of " + this, ex, LOGGER);
        }

        // setup animation
        animateBodyPane(headPane, bodyPane);
        prefHeightProperty().bind(headPane.prefHeightProperty().add(bodyPane.prefHeightProperty()));

        getChildren().add(bodyPane);
        expansionProperty.bindBidirectional(secondaryActivationProperty());
        expansionProperty.addListener((paramObservableValue, paramT1, expaneded) -> {
            updateDynamicContent();
        });

        expansionProperty.set(initialExpanded);

        // make sure pane is not expanded if the pane is disabled
        disableProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean disabled) -> {
            if (disabled) {
                expansionProperty.set(false);
            }
        });
    }

    @Override
    public void updateDynamicContent() {
        super.updateDynamicContent();

        // apply expantion state
        final boolean expanded = expansionProperty.get();
        bodyPane.setManaged(expanded);
        bodyPane.setVisible(expanded);

        if (expanded) {
            // To expand
            timelineDown.play();
        } else {
            // To close
            timelineUp.play();
        }
    }

    /**
     * Should be overwritten for custom body content implementation.
     *
     * @param bodyPane
     * @throws org.openbase.jul.exception.CouldNotPerformException can be thrown if something went wrong.
     */
    protected void initBodyContent(final Pane bodyPane) throws CouldNotPerformException {
        bodyPane.getChildren().add(new Label("Test"));
    }

    private void animateBodyPane(final BorderPane headPane, final Pane bodyPane) {

        //        final Rectangle rectangleClip = new Rectangle(In, headPane.prefHeightProperty().getValue());
        //        final Rectangle rectangleClip = new Rectangle(100, 100);
        //        this.setClip(rectangleClip);
        // animation for open body pane
        timelineDown = new Timeline();
        timelineDown.setCycleCount(0);
        timelineDown.setAutoReverse(true);

        //        final KeyValue kvDwn1 = new KeyValue(rectangleClip.heightProperty(), headPane.prefHeightProperty().getValue() + bodyPane.prefHeightProperty().getValue());
        //        final KeyValue kvDwn2 = new KeyValue(rectangleClip.translateYProperty(), 0);
        final KeyValue kvDwn3 = new KeyValue(bodyPane.prefHeightProperty(), -1);
        //        final KeyValue kvDwn3 = new KeyValue(bodyPane.getClprefHeightProperty(), -1);
        //        final KeyValue kvDwn4 = new KeyValue(bodyPane.translateYProperty(), 0);
        //        final KeyValue kvDwn5 = new KeyValue(maxHeightProperty(), headPane.prefHeightProperty().getValue() + bodyPane.prefHeightProperty().getValue());
        //        final KeyValue kvDwn6 = new KeyValue(minHeightProperty(), headPane.prefHeightProperty().getValue() + bodyPane.prefHeightProperty().getValue());
        //        final KeyFrame kfDwn = new KeyFrame(Duration.millis(Constants.ANIMATION_TIME), /*kvDwn1, kvDwn2,*/ kvDwn3, kvDwn4, kvDwn5, kvDwn6);
        final KeyFrame kfDwn = new KeyFrame(Duration.millis(Constants.ANIMATION_TIME), /*kvDwn1, kvDwn2,*/ kvDwn3);
        timelineDown.getKeyFrames().add(kfDwn);

        // animation for close body pane
        timelineUp = new Timeline();
        timelineUp.setCycleCount(1);
        timelineUp.setAutoReverse(true);

        //        final KeyValue kvUp1 = new KeyValue(rectangleClip.heightProperty(), headPane.prefHeightProperty().getValue());
        //        final KeyValue kvUp2 = new KeyValue(rectangleClip.translateYProperty(), 0);
        final KeyValue kvUp3 = new KeyValue(bodyPane.prefHeightProperty(), 0);
        //        final KeyValue kvUp4 = new KeyValue(bodyPane.translateYProperty(), 0);
        //        final KeyValue kvUp5 = new KeyValue(maxHeightProperty(), headPane.prefHeightProperty().getValue());
        //        final KeyValue kvUp6 = new KeyValue(minHeightProperty(), headPane.prefHeightProperty().getValue());
        //        final KeyFrame kfUp = new KeyFrame(Duration.millis(Constants.ANIMATION_TIME), /*kvUp1, kvUp2,*/ kvUp3, kvUp4, kvUp5, kvUp6);
        final KeyFrame kfUp = new KeyFrame(Duration.millis(Constants.ANIMATION_TIME), /*kvUp1, kvUp2,*/ kvUp3);
        timelineUp.getKeyFrames().add(kfUp);
    }

    public BooleanProperty expansionProperty() {
        return expansionProperty;
    }
}
