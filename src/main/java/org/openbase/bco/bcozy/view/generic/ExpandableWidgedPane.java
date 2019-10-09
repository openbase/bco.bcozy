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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;

/**
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class ExpandableWidgedPane extends WidgetPane {

    private final Pane bodyPane;
    private final Timeline timeline;
    private final boolean initialExpanded;
    private final ChangeListener<Boolean> dynamicContentChangeObserver;

    /**
     * Property to define expand state.
     */
    protected final BooleanProperty expansionProperty;

    public ExpandableWidgedPane(final boolean initialExpanded, final boolean activateable) {
        super(activateable);
        this.initialExpanded = initialExpanded;
        this.bodyPane = new HBox();
        this.dynamicContentChangeObserver = new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> paramObservableValue, Boolean paramT1, Boolean expaneded) {
                update();
            }
        };
        this.timeline = new Timeline();
        this.expansionProperty = new SimpleBooleanProperty(initialExpanded);
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
        initBodyPaneAnimation(bodyPane);
        prefHeightProperty().bind(headPane.prefHeightProperty().add(bodyPane.prefHeightProperty()));

        getChildren().add(bodyPane);
        expansionProperty.bindBidirectional(secondaryActivationProperty());

        // remove if already registered.
        expansionProperty.removeListener(dynamicContentChangeObserver);

        // activate dynamic change observation.
        expansionProperty.addListener(dynamicContentChangeObserver);

        expansionProperty.set(initialExpanded);

        // make sure pane is not expanded if the pane is disabled
        disableProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean disabled) -> {
            if (disabled) {
                expansionProperty.set(false);
            }
        });
    }

    private boolean startup = true;

    @Override
    public void updateDynamicContent() {
        super.updateDynamicContent();

        // apply expantion state
        final boolean expanded = expansionProperty.get();
        bodyPane.setManaged(expanded);
        bodyPane.setVisible(expanded);

//        if (expanded) {
////            bodyPane.setManaged(true);
//        }
//
//        if (!startup) {
//            animate(expanded);
//        }
//        startup = false;
//
//        if (!expanded) {
////            bodyPane.setManaged(false);
////            bodyPane.setVisible(false);
//        }

    }

    double bodyHeight;

    private void animate(final boolean expanded) {
        // set the cycle to play the animation forwards or backwards depending on the expanded property
        Timeline timelineTest = new Timeline();

        if (expanded) {
            final KeyFrame closed = new KeyFrame(Duration.ZERO, new KeyValue(bodyPane.prefHeightProperty(), 0));
            final KeyFrame open = new KeyFrame(Duration.millis(500), new KeyValue(bodyPane.prefHeightProperty(), 230));
            timelineTest.getKeyFrames().addAll(closed, open);
        } else {
            final KeyFrame open = new KeyFrame(Duration.ZERO, new KeyValue(bodyPane.prefHeightProperty(), 230));
            final KeyFrame closed = new KeyFrame(Duration.millis(500), new KeyValue(bodyPane.prefHeightProperty(), 0));
            timelineTest.getKeyFrames().addAll(open, closed);
        }
        timelineTest.play();
    }

    /**
     * Should be overwritten for custom body content implementation.
     *
     * @param bodyPane
     * @throws org.openbase.jul.exception.CouldNotPerformException can be thrown if something went wrong.
     */
    protected void initBodyContent(final Pane bodyPane) throws CouldNotPerformException {
        // please overwrite for custom content.
    }

    private void initBodyPaneAnimation(final Pane bodyPane) {
        timeline.setAutoReverse(true);
        final KeyValue open = new KeyValue(bodyPane.prefHeightProperty(), -1);
        final KeyValue closed = new KeyValue(bodyPane.prefHeightProperty(), 0);
        final KeyFrame openCloseKeyFrame = new KeyFrame(Duration.millis(1000), closed, open);
        timeline.getKeyFrames().add(openCloseKeyFrame);
    }

    public BooleanProperty expansionProperty() {
        return expansionProperty;
    }
}
