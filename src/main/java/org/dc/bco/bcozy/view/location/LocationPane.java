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

import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.dc.bco.bcozy.view.Constants;
import org.dc.bco.bcozy.view.ForegroundPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class LocationPane extends Pane {

    /**
     * Application logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LocationPane.class);

    private LocationPolygon selectedRoom;
    private LocationPolygon rootRoom;
    private final ForegroundPane foregroundPane;

    private final List<LocationPolygon> locationList;

    private final SimpleStringProperty selectedRoomId;

    /**
     * Constructor for the LocationPane.
     *
     * @param foregroundPane The foregroundPane
     */
    public LocationPane(final ForegroundPane foregroundPane) {
        super();

        this.foregroundPane = foregroundPane;

        locationList = new LinkedList<>();

        //Dummy Room
        selectedRoom = new ZonePolygon(Constants.DUMMY_ROOM_NAME, Constants.DUMMY_ROOM_NAME, 0.0, 0.0, 0.0, 0.0);
        selectedRoomId = new SimpleStringProperty(Constants.DUMMY_ROOM_NAME);
        rootRoom = null;
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
                rootRoom = locationPolygon; //TODO: handle the situation where several zones exist
                break;
            default:
                LOGGER.warn("The following location has an unknown LocationType and will be ignored:"
                        + "\n  UUID:  " + locationId
                        + "\n  Label: " + locationLabel
                        + "\n  Type:  " + locationType);
                return;
        }
        locationList.add(locationPolygon);
        this.getChildren().add(locationPolygon);
    }

    /**
     * Erases all locations from the locationPane.
     */
    public void clearLocations() {
        locationList.forEach(locationPolygon -> this.getChildren().remove(locationPolygon));
        locationList.clear();
    }

    /**
     * Adds a mouse eventHandler to the tile.
     *
     * @param tile The tile
     */
    public void addMouseEventHandlerToTile(final TilePolygon tile) {
        tile.setOnMouseClicked(event -> {
            event.consume();

            if (event.isStillSincePress()) {
                if (event.getClickCount() == 1) {
                    if (!selectedRoom.equals(tile)) {
                        selectedRoom.setSelected(false);
                        tile.setSelected(true);
                        setSelectedRoom(tile);
                    }
                } else if (event.getClickCount() == 2) {
                    scaleFitRoom(tile);
                    centerScrollPaneToPointAnimated(new Point2D(tile.getCenterX(), tile.getCenterY()));
                }

                foregroundPane.getContextMenu().getRoomInfo().setText(selectedRoom.getLocationLabel());
            }
        });
        tile.setOnMouseEntered(event -> {
            event.consume();
            tile.mouseEntered();
            foregroundPane.getInfoFooter().getMouseOverText().setText(tile.getLocationLabel());
        });
        tile.setOnMouseExited(event -> {
            event.consume();
            tile.mouseLeft();
            foregroundPane.getInfoFooter().getMouseOverText().setText("");
        });
    }


    //TODO: add mouseeventhandler that handles clicks out of the zones
//    private void addMouseEventHandlerToEmptyRectangle(final Rectangle emptyHugeRectangle) {
//        emptyHugeRectangle.setOnMouseClicked(event -> {
//            event.consume();
//
//            if (rootRoom != null) {
//                if (event.getClickCount() == 1) {
//                    if (!selectedRoom.equals(rootRoom)) {
//                        selectedRoom.setSelected(false);
//                        setSelectedRoom(rootRoom);
//                    }
//                } else if (event.getClickCount() == 2) {
//                    scaleFitRoom(rootRoom);
//                    centerScrollPaneToPointAnimated(new Point2D(rootRoom.getCenterX(), rootRoom.getCenterY()));
//                }
//            }
//        });
//    }

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
        centerScrollPaneToPoint(new Point2D(rootRoom.getCenterX(), rootRoom.getCenterY()));
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

    private void scaleFitRoom(final LocationPolygon room) { //TODO: Bounds in Parent? Layout Bounds?
        final double xScale =
                (getBoundsInParent().getWidth() / room.prefWidth(0)) * Constants.ZOOM_FIT_PERCENTAGE_WIDTH;
        final double yScale =
                (getBoundsInParent().getHeight() / room.prefHeight(0)) * Constants.ZOOM_FIT_PERCENTAGE_HEIGHT;
        final double scale = (xScale < yScale) ? xScale : yScale;

        this.setScaleX(scale);
        this.setScaleY(scale);
    }

    private void centerScrollPaneToPoint(final Point2D center) {
        this.setTranslateX(-center.getX() + getBoundsInParent().getWidth() / 2);
        this.setTranslateY(-center.getY() + getBoundsInParent().getHeight() / 2);
    }

    private void centerScrollPaneToPointAnimated(final Point2D center) {
        final TranslateTransition transition = new TranslateTransition(Duration.millis(500), this);
        transition.setToX(-center.getX() * this.getScaleX() + getBoundsInParent().getWidth() / 2);
        transition.setToY(-center.getY() * this.getScaleY() + getBoundsInParent().getHeight() / 2);
        transition.setCycleCount(1);
        transition.setAutoReverse(true);
        transition.play();
    }
}
