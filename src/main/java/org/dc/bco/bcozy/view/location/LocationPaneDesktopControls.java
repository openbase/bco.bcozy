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
package org.dc.bco.bcozy.view.location;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.dc.bco.bcozy.view.ForegroundPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class LocationPaneDesktopControls {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocationPaneDesktopControls.class);

    private final LocationPane locationPane;
    private final ForegroundPane foregroundPane;

    /**
     * Constructor for the LocationPaneDesktopControls.
     * @param locationPane the Pane to control.
     * @param foregroundPane the foreground Pane with the menu structure.
     */
    public LocationPaneDesktopControls(final LocationPane locationPane, final ForegroundPane foregroundPane) {
        this.locationPane = locationPane;
        this.foregroundPane = foregroundPane;

        configureScrollContent(locationPane.getScrollContent(), locationPane.getScroller());
        configureZoomPane(locationPane.getZoomPane(), locationPane.getLocationViewContent(),
                locationPane.getScrollContent(), locationPane.getScroller());


        this.centerScrollPaneToPoint(locationPane.getScrollPane(),
                this.getCenterOfGroup(locationPane.getLocationViewContent(), true),
                locationPane.getLocationViewContent(),
                locationPane.getZoomPaneWidth(), locationPane.getZoomPaneHeight());

//        this.addMouseEventHandlerToRoom(locationPane.getScrollPane(), locationPane,
//                locationPane.getLocationViewContent(), locationPane.getZoomPaneWidth(),
//                locationPane.getZoomPaneHeight(), locationPane.getRooms(),
//                foregroundPane);
    }

    /**
     * Configures the Scroll Content.
     * @param scrollContent to configure
     * @param scroller object
     */
    private void configureScrollContent(final Group scrollContent, final ScrollPane scroller) {
        // Panning via drag....
        final ObjectProperty<Point2D> lastMouseCoordinates = new SimpleObjectProperty<>();
        scrollContent.setOnMousePressed(event -> lastMouseCoordinates.set(new Point2D(event.getX(), event.getY())));

        scrollContent.setOnMouseDragged(event -> {
            final double deltaX = event.getX() - lastMouseCoordinates.get().getX();
            final double extraWidth = scrollContent.getLayoutBounds().getWidth()
                    - scroller.getViewportBounds().getWidth();
            final double deltaH = deltaX * (scroller.getHmax() - scroller.getHmin()) / extraWidth;
            final double desiredH = scroller.getHvalue() - deltaH;
            scroller.setHvalue(Math.max(0, Math.min(scroller.getHmax(), desiredH)));

            final double deltaY = event.getY() - lastMouseCoordinates.get().getY();
            final double extraHeight = scrollContent.getLayoutBounds().getHeight()
                    - scroller.getViewportBounds().getHeight();
            final double deltaV = deltaY * (scroller.getHmax() - scroller.getHmin()) / extraHeight;
            final double desiredV = scroller.getVvalue() - deltaV;
            scroller.setVvalue(Math.max(0, Math.min(scroller.getVmax(), desiredV)));
        });
    }

    private void configureZoomPane(final StackPane zoomPane, final Group group, final Group scrollContent,
                                   final ScrollPane scroller) {

        final double scaleDelta = 1.05;
        zoomPane.setOnScroll(event -> {
            event.consume();

            if (event.getDeltaY() == 0) {
                return;
            }

            final double scaleFactor = (event.getDeltaY() > 0) ? scaleDelta : 1 / scaleDelta;

            // amount of scrolling in each direction in scrollContent coordinate
            // units
            final Point2D scrollOffset = figureScrollOffset(scrollContent, scroller);

            group.setScaleX(group.getScaleX() * scaleFactor);
            group.setScaleY(group.getScaleY() * scaleFactor);

            // move viewport so that old center remains in the center after the
            // scaling
            repositionScroller(scrollContent, scroller, scaleFactor, scrollOffset);

        });
    }

    private void scaleFitRoom(final StackPane zoomPane, final Group group, final Group scrollContent,
                              final ScrollPane scroller, final RoomPolygon room) {
        final Point2D scrollOffset = figureScrollOffset(scrollContent, scroller);

        double xScale = (this.locationPane.getWidth() / room.prefWidth(0)) * 0.3;
        double yScale = (this.locationPane.getHeight() / room.prefHeight(0)) * 0.5;

        double scale = (xScale < yScale) ? xScale : yScale;
        double scaleFactor = scale / group.getScaleX();

        group.setScaleX(scale);
        group.setScaleY(scale);

        repositionScroller(scrollContent, scroller, scaleFactor, scrollOffset);
    }

    private Point2D figureScrollOffset(final Node scrollContent, final ScrollPane scroller) {
        final double extraWidth = scrollContent.getLayoutBounds().getWidth() - scroller.getViewportBounds().getWidth();
        final double hScrollProportion = (scroller.getHvalue() - scroller.getHmin())
                / (scroller.getHmax() - scroller.getHmin());
        final double scrollXOffset = hScrollProportion * Math.max(0, extraWidth);
        final double extraHeight = scrollContent.getLayoutBounds().getHeight()
                - scroller.getViewportBounds().getHeight();
        final double vScrollProportion = (scroller.getVvalue() - scroller.getVmin())
                / (scroller.getVmax() - scroller.getVmin());
        final double scrollYOffset = vScrollProportion * Math.max(0, extraHeight);
        return new Point2D(scrollXOffset, scrollYOffset);
    }

    private void repositionScroller(final Node scrollContent, final ScrollPane scroller,
                                    final double scaleFactor, final Point2D scrollOffset) {
        final double scrollXOffset = scrollOffset.getX();
        final double scrollYOffset = scrollOffset.getY();
        final double extraWidth = scrollContent.getLayoutBounds().getWidth() - scroller.getViewportBounds().getWidth();
        if (extraWidth > 0) {
            final double halfWidth = scroller.getViewportBounds().getWidth() / 2;
            final double newScrollXOffset = (scaleFactor - 1) *  halfWidth + scaleFactor * scrollXOffset;
            scroller.setHvalue(scroller.getHmin()
                    + newScrollXOffset * (scroller.getHmax() - scroller.getHmin()) / extraWidth);
        } else {
            scroller.setHvalue(scroller.getHmin());
        }
        final double extraHeight = scrollContent.getLayoutBounds().getHeight()
                - scroller.getViewportBounds().getHeight();
        if (extraHeight > 0) {
            final double halfHeight = scroller.getViewportBounds().getHeight() / 2;
            final double newScrollYOffset = (scaleFactor - 1) * halfHeight + scaleFactor * scrollYOffset;
            scroller.setVvalue(scroller.getVmin()
                    + newScrollYOffset * (scroller.getVmax() - scroller.getVmin()) / extraHeight);
        } else {
            scroller.setHvalue(scroller.getHmin());
        }
    }

    /**
     * ZoomFit the locationPane to the root.
     */
    public void zoomFit() {
        //TODO: handle case where no root room exists
        scaleFitRoom(locationPane.getZoomPane(), locationPane.getLocationViewContent(),
                locationPane.getScrollContent(), locationPane.getScroller(), locationPane.getRootRoom());
        centerScrollPaneToPointAnimated(locationPane.getScrollPane(),
                new Point2D(locationPane.getRootRoom().getCenterX(), locationPane.getRootRoom().getCenterY()),
                locationPane.getLocationViewContent(), locationPane.getZoomPaneWidth(),
                locationPane.getZoomPaneHeight());
    }


    /**
     * Adds a mouse eventHandler to the room.
     *
     * @param room The room
     */
    public void addMouseEventHandlerToRoom(final RoomPolygon room) {
        final ScrollPane scrollPane = this.locationPane.getScrollPane();
        final Group locationViewContent = this.locationPane.getLocationViewContent();
        final double zoomPaneWidth = this.locationPane.getZoomPaneWidth();
        final double zoomPaneHeight = this.locationPane.getZoomPaneHeight();


        room.setOnMouseClicked(event -> {
            event.consume();

            //TODO: this isn't very nice yet, will be improved if we have a model with the rooms
            if (room.isSelected()) {
                locationPane.getSelectedRoom().toggleSelected();
                scaleFitRoom(locationPane.getZoomPane(), locationPane.getLocationViewContent(),
                        locationPane.getScrollContent(), locationPane.getScroller(), locationPane.getRootRoom());
                centerScrollPaneToPointAnimated(scrollPane,
                        new Point2D(locationPane.getRootRoom().getCenterX(), locationPane.getRootRoom().getCenterY()),
                        locationViewContent, zoomPaneWidth, zoomPaneHeight);
                locationPane.setSelectedRoom(new RoomPolygon("none", 0.0, 0.0, 0.0, 0.0));
                foregroundPane.getContextMenu().getRoomInfo()
                        .setText(locationPane.getRootRoom().getRoomName());
            } else {
                locationPane.getSelectedRoom().toggleSelected();
                scaleFitRoom(locationPane.getZoomPane(), locationPane.getLocationViewContent(),
                        locationPane.getScrollContent(), locationPane.getScroller(), room);
                centerScrollPaneToPointAnimated(scrollPane,
                        new Point2D(room.getCenterX(), room.getCenterY()),
                        locationViewContent, zoomPaneWidth, zoomPaneHeight);
                room.toggleSelected();
                locationPane.setSelectedRoom(room);
                foregroundPane.getContextMenu().getRoomInfo()
                        .setText(locationPane.getSelectedRoom().getRoomName());
            }
        });
        room.setOnMouseEntered(event -> {
            event.consume();
            foregroundPane.getInfoFooter().getMouseOverText().setText(room.getRoomName());
        });
        room.setOnMouseExited(event -> {
            event.consume();
            foregroundPane.getInfoFooter().getMouseOverText().setText("");
        });
    }


    private void centerScrollPaneToPoint(final ScrollPane scroller, final Point2D center,
                                         final Group locationViewContent,
                                         final double zoomPaneWidth, final double zoomPaneHeight) {
        final double realZoomPaneWidth = zoomPaneWidth - scroller.getWidth() / locationViewContent.getScaleX();
        final double realZoomPaneHeight = zoomPaneHeight - scroller.getHeight() / locationViewContent.getScaleY();
        scroller.setHvalue((center.getX() + (realZoomPaneWidth / 2.0)) / realZoomPaneWidth);
        scroller.setVvalue((center.getY() + (realZoomPaneHeight / 2.0)) / realZoomPaneHeight);
    }

    private void centerScrollPaneToPointAnimated(final ScrollPane scroller, final Point2D center,
                                                 final Group locationViewContent, final double zoomPaneWidth,
                                                 final double zoomPaneHeight) {
        final double realZoomPaneWidth = zoomPaneWidth - scroller.getWidth() / locationViewContent.getScaleX();
        final double realZoomPaneHeight = zoomPaneHeight - scroller.getHeight() / locationViewContent.getScaleY();

        final Timeline timeline = new Timeline();
        timeline.setCycleCount(1);

        final KeyValue keyValueX = new KeyValue(scroller.hvalueProperty(),
                (center.getX() + (realZoomPaneWidth / 2.0)) / realZoomPaneWidth,
                Interpolator.EASE_BOTH);
        final KeyValue keyValueY = new KeyValue(scroller.vvalueProperty(),
                (center.getY() + (realZoomPaneHeight / 2.0)) / realZoomPaneHeight,
                Interpolator.EASE_BOTH);
        final KeyFrame keyFrame = new KeyFrame(Duration.millis(500), keyValueX, keyValueY);

        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    private Point2D getCenterOfGroup(final Group group, final boolean skipFirst) {
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;

        int start;
        if (skipFirst) {
            start = 1;
        } else {
            start = 0;
        }

        for (int i = start; i < group.getChildren().size(); i++) {
            if (group.getChildren().get(i).getLayoutBounds().getMaxX() > maxX) {
                maxX = group.getChildren().get(i).getLayoutBounds().getMaxX();
            }
            if (group.getChildren().get(i).getLayoutBounds().getMaxY() > maxY) {
                maxY = group.getChildren().get(i).getLayoutBounds().getMaxY();
            }
            if (group.getChildren().get(i).getLayoutBounds().getMinX() < minX) {
                minX = group.getChildren().get(i).getLayoutBounds().getMinX();
            }
            if (group.getChildren().get(i).getLayoutBounds().getMinY() < minY) {
                minY = group.getChildren().get(i).getLayoutBounds().getMinY();
            }
        }

        return new Point2D((minX + maxX) / 2, (minY + maxY) / 2);
    }
}
