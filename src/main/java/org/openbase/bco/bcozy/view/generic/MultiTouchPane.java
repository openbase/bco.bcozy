package org.openbase.bco.bcozy.view.generic;

import javafx.scene.Node;
import javafx.scene.input.TouchPoint;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.NotAvailableException;

public abstract class MultiTouchPane extends Pane {

    private double prevTouchCordX;
    private double prevTouchCordY;
    private double prevMouseCordX;
    private double prevMouseCordY;

    private Integer touchPointId;

    private boolean multiTouchEnabled = true;

    public MultiTouchPane() {
    }

    public MultiTouchPane(Node... children) {
        super(children);
    }

    /**
     * Method registers all mouse and touch event handler
     * It should be called after this component is assigned to any parent node.
     *
     */
    public void initMultiTouch() throws CouldNotPerformException {
        try {
            // handle node mouse translations
            getParent().setOnMousePressed(event -> {

                // filter touch events
                if (event.isSynthesized()) {
                    return;
                }

                // System.out.println("handle mouse pressed...");
                this.prevMouseCordX = event.getX();
                this.prevMouseCordY = event.getY();
                event.consume();
            });
            getParent().setOnMouseDragged(event -> {

                // filter touch events
                if (event.isSynthesized()) {
                    return;
                }

                if (!event.isPrimaryButtonDown()) {
                    return;
                }

                // System.out.println("handle mouse dragged...");
                setTranslateX(getTranslateX() + (event.getX() - prevMouseCordX));
                setTranslateY(getTranslateY() + (event.getY() - prevMouseCordY));
                prevMouseCordX = event.getX();
                prevMouseCordY = event.getY();
                event.consume();
            });

            // handle node mouse zoom
            getParent().setOnScroll(event -> {

                // filter touch events
                if (event.isDirect()) {
                    return;
                }
                // filter ended touch events
                if (event.isInertia()) {
                    return;
                }

                // filter non changes
                if (event.getDeltaY() == 0) {
                    return;
                }

                // System.out.println("handle mouse scroll...");

                // calculate scale factor
                final double scaleFactor = (event.getDeltaY() > 0) ? Constants.SCALE_DELTA : 1 / Constants.SCALE_DELTA;

                // scale
                setScaleX(getScaleX() * scaleFactor);
                setScaleY(getScaleY() * scaleFactor);
                setTranslateX(getTranslateX() * scaleFactor);
                setTranslateY(getTranslateY() * scaleFactor);
                event.consume();
            });

            // handle node touch translations
            getParent().setOnTouchPressed(event -> {

                // skip if movement is already registered
                if (isTouchMovementInProcess()) {
                    return;
                }

                // System.out.println("handle touch pressed...");
                touchPointId = event.getTouchPoint().getId();
                prevTouchCordX = event.getTouchPoint().getSceneX();
                prevTouchCordY = event.getTouchPoint().getSceneY();
                event.consume();
            });

            if(getParent() == null) {
                throw new NotAvailableException("Parent Node of " +this.getClass().getSimpleName());
            }

            getParent().setOnTouchMoved(event -> {

                // filter non related touch points
                if (!isCurrentTouchPoint(event.getTouchPoint())) {
                    return;
                }

                // filter by touch point count
                if (multiTouchEnabled) {
                    // only handle two finger interaction for multi touch support.
                    if (event.getTouchCount() != 2) {
                        return;
                    }
                } else {
                    // only handle one finger interaction because more is not supported.
                    if (event.getTouchCount() != 1) {
                        return;
                    }
                }

                // System.out.println("handle touch moved...");
                setTranslateX(getTranslateX() + (event.getTouchPoint().getSceneX() - prevTouchCordX));
                setTranslateY(getTranslateY() + (event.getTouchPoint().getSceneY() - prevTouchCordY));
                prevTouchCordX = event.getTouchPoint().getX();
                prevTouchCordY = event.getTouchPoint().getY();
                event.consume();
            });
            getParent().setOnTouchReleased(event -> {

                // filter if no movement was detected
                if (!isTouchMovementInProcess()) {
                    return;
                }

                // filter non related touch points
                if (event.getTouchPoint().getId() != touchPointId) {
                    return;
                }

                // System.out.println("handle touch released...");
                touchPointId = null;
                event.consume();
            });

            // handle map touch zoom
            getParent().setOnZoom(event -> {

                // filter ended touch events
                if (event.isInertia()) {
                    return;
                }

                // System.out.println("handle touch zoom...");
                setScaleX(getScaleX() * event.getZoomFactor());
                setScaleY(getScaleY() * event.getZoomFactor());
                setTranslateX(getTranslateX() * event.getZoomFactor());
                setTranslateY(getTranslateY() * event.getZoomFactor());
                event.consume();
            });
        } catch(CouldNotPerformException ex) {
            throw new CouldNotPerformException("Could not initialize Multitouch!", ex);
        }
    }

    private boolean isCurrentTouchPoint(final TouchPoint touchPoint) {
        return isTouchMovementInProcess() && touchPointId == touchPoint.getId();
    }

    private boolean isTouchMovementInProcess() {
        return touchPointId != null;
    }
}
