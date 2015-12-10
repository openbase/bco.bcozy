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
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.dc.bco.bcozy.view.Constants;
import org.dc.bco.bcozy.view.ForegroundPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 *
 */
public class LocationPane extends StackPane {

    /**
     * Application logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LocationPane.class);

    private final Group locationViewContent;
    private LocationPolygon selectedRoom;
    private LocationPolygon rootRoom;
    private Group scrollContent;
    private ScrollPane scroller;
    private StackPane zoomPane;
    private final ScrollPane scrollPane;
    private final ForegroundPane foregroundPane;

    private final SimpleStringProperty selectedRoomId;

    /**
     * Constructor for the LocationPane.
     *
     * @param foregroundPane The foregroundPane
     */
    public LocationPane(final ForegroundPane foregroundPane) {
        super();

        this.foregroundPane = foregroundPane;

        final Rectangle emptyHugeRectangle = new Rectangle(-(Constants.ZOOM_PANE_WIDTH / 2),
                -(Constants.ZOOM_PANE_HEIGHT / 2),
                Constants.ZOOM_PANE_WIDTH,
                Constants.ZOOM_PANE_HEIGHT);
        emptyHugeRectangle.setFill(Color.TRANSPARENT);

        //Dummy Room
        selectedRoom = new ZonePolygon(Constants.DUMMY_ROOM_NAME, Constants.DUMMY_ROOM_NAME, 0.0, 0.0, 0.0, 0.0);
        selectedRoomId = new SimpleStringProperty(Constants.DUMMY_ROOM_NAME);

        locationViewContent = new Group(emptyHugeRectangle);
        scrollPane = createZoomPane(locationViewContent);

        this.getChildren().setAll(scrollPane);

        final BackgroundImage backgroundImage = new BackgroundImage(
                new Image("backgrounds/blueprint.jpg"),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        this.setBackground(new Background(backgroundImage));

        configureScrollContent();
        configureZoomPane();

    }

    /**
     * Adds a room to the location Pane and use the controls to add a mouse event handler.
     *
     * If a room with the same id already exists, it will be overwritten.
     *
     * @param locationId The location id
     * @param locationLabel The location label
     * @param vertices A list of vertices which defines the shape of the room
     * @param locationType The type of the location {ZONE,REGION,TILE}
     */
    public void addRoom(final String locationId, final String locationLabel,
                        final List<Point2D> vertices, final String locationType) {
        // TODO: Remove location with same ID
//        for (Node node : locationViewContent.getChildren()) {
//            if (node instanceof LocationPolygon) {
//                if (((LocationPolygon) node).getLocationId().equals(locationId)) {
//                    locationViewContent.getChildren().remove(node);
//                }
//            }
//        }

        // Fill the list of vertices into an array of points
        double[] points = new double[vertices.size() * 2];
        for (int i = 0; i < vertices.size(); i++) {
            // TODO: X and Y are swapped in the world of the csra... make it more generic...
            points[i * 2] = vertices.get(i).getY() * Constants.METER_TO_PIXEL;
            points[i * 2 + 1] = vertices.get(i).getX() * Constants.METER_TO_PIXEL;
        }

        LocationPolygon locationPolygon;

        switch (locationType) {
            case "TILE":
                locationPolygon = new TilePolygon(locationLabel, locationId, points);
                addMouseEventHandlerToTile((TilePolygon) locationPolygon);
                break;
            case "REGION":
                locationPolygon = new RegionPolygon(locationLabel, locationId, points);
                locationPolygon.setMouseTransparent(true);
                break;
            case "ZONE":
                locationPolygon = new ZonePolygon(locationLabel, locationId, points);
                locationPolygon.setMouseTransparent(true);
                rootRoom = locationPolygon;
                break;
            default:
                LOGGER.warn("The following location has an unknown LocationType and will be ignored:"
                        + "\n  UUID:  " + locationId
                        + "\n  Label: " + locationLabel
                        + "\n  Type:  " + locationType);
                return;
        }

        locationViewContent.getChildren().add(locationPolygon);
    }

    /**
     * Adds a mouse eventHandler to the tile.
     *
     * @param tile The tile
     */
    public void addMouseEventHandlerToTile(final TilePolygon tile) {
        tile.setOnMouseClicked(event -> {
            event.consume();

            //TODO: this isn't very nice yet, will be improved if we have a model with the rooms
            if (tile.isSelected()) {
                selectedRoom.toggleSelected();
                scaleFitRoom(rootRoom);
                centerScrollPaneToPointAnimated(new Point2D(rootRoom.getCenterX(), rootRoom.getCenterY()));
                setSelectedRoom(
                        new ZonePolygon(Constants.DUMMY_ROOM_NAME, Constants.DUMMY_ROOM_NAME, 0.0, 0.0, 0.0, 0.0));
                foregroundPane.getContextMenu().getRoomInfo().setText(rootRoom.getLocationLabel());
            } else {
                selectedRoom.toggleSelected();
                scaleFitRoom(tile);
                centerScrollPaneToPointAnimated(new Point2D(tile.getCenterX(), tile.getCenterY()));
                tile.toggleSelected();
                setSelectedRoom(tile);
                foregroundPane.getContextMenu().getRoomInfo().setText(selectedRoom.getLocationLabel());
            }
        });
        tile.setOnMouseEntered(event -> {
            event.consume();
            foregroundPane.getInfoFooter().getMouseOverText().setText(tile.getLocationLabel());
        });
        tile.setOnMouseExited(event -> {
            event.consume();
            foregroundPane.getInfoFooter().getMouseOverText().setText("");
        });
    }

    private ScrollPane createZoomPane(final Group group) {

        zoomPane = new StackPane();

        zoomPane.getChildren().add(group);

        scroller = new ScrollPane();
        scrollContent = new Group(zoomPane);
        scroller.setContent(scrollContent);
        scroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        scroller.viewportBoundsProperty().addListener((observable, oldValue, newValue) -> {
            zoomPane.setMinSize(newValue.getWidth(), newValue.getHeight());
        });

        //CHECKSTYLE.OFF: MagicNumber
        scroller.setPrefViewportWidth(800);
        scroller.setPrefViewportHeight(600);
        //CHECKSTYLE.ON: MagicNumber

        return scroller;
    }

    /**
     * Setter for selectedRoom.
     * @param selectedRoom Room to select
     */
    public void setSelectedRoom(final LocationPolygon selectedRoom) {
        this.selectedRoom = selectedRoom;
        this.selectedRoomId.set(selectedRoom.getLocationId());
    }

    /**
     * ZoomFits to the root if available.
     */
    public void zoomFit() {
        //TODO: handle case where no root room exists
        scaleFitRoom(rootRoom);
        centerScrollPaneToPointAnimated(new Point2D(rootRoom.getCenterX(), rootRoom.getCenterY()));
    }

    /**
     * Adds a change listener to the selectedRoomID property.
     *
     * @param changeListener The change Listener
     */
    public void addSelectedRoomIdListener(final ChangeListener<? super String> changeListener) {
        selectedRoomId.addListener(changeListener);
    }

    /**
     * Remove the specified change listener from the selectedRoomID property.
     *
     * @param changeListener The change Listener
     */
    public void removeSelectedRoomIdListener(final ChangeListener<? super String> changeListener) {
        selectedRoomId.removeListener(changeListener);
    }

    private void configureScrollContent() {
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

    private void configureZoomPane() {

        final double scaleDelta = 1.05;
        zoomPane.setOnScroll(event -> {
            event.consume();

            if (event.getDeltaY() == 0) {
                return;
            }

            final double scaleFactor = (event.getDeltaY() > 0) ? scaleDelta : 1 / scaleDelta;

            // amount of scrolling in each direction in scrollContent coordinate
            // units
            final Point2D scrollOffset = figureScrollOffset();

            locationViewContent.setScaleX(locationViewContent.getScaleX() * scaleFactor);
            locationViewContent.setScaleY(locationViewContent.getScaleY() * scaleFactor);

            // move viewport so that old center remains in the center after the
            // scaling
            repositionScroller(scaleFactor, scrollOffset);

        });
    }

    private Point2D figureScrollOffset() {
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

    private void repositionScroller(final double scaleFactor, final Point2D scrollOffset) {
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

    private void scaleFitRoom(final LocationPolygon room) {
        final Point2D scrollOffset = figureScrollOffset();

        final double xScale = (this.getWidth() / room.prefWidth(0)) * Constants.ZOOM_FIT_PERCENTAGE_WIDTH;
        final double yScale = (this.getHeight() / room.prefHeight(0)) * Constants.ZOOM_FIT_PERCENTAGE_HEIGHT;

        final double scale = (xScale < yScale) ? xScale : yScale;
        final double scaleFactor = scale / locationViewContent.getScaleX();

        locationViewContent.setScaleX(scale);
        locationViewContent.setScaleY(scale);

        repositionScroller(scaleFactor, scrollOffset);
    }

    private void centerScrollPaneToPointAnimated(final Point2D center) {
        final double realZoomPaneWidth =
                Constants.ZOOM_PANE_WIDTH - scroller.getWidth() / locationViewContent.getScaleX();
        final double realZoomPaneHeight =
                Constants.ZOOM_PANE_HEIGHT - scroller.getHeight() / locationViewContent.getScaleY();

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
}
