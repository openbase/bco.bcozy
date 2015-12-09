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

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.dc.bco.bcozy.view.Constants;
import org.dc.bco.bcozy.view.ForegroundPane;

import java.util.List;

/**
 *
 */
public class LocationPane extends StackPane {

    private static final double ZOOM_PANE_WIDTH = 2000;
    private static final double ZOOM_PANE_HEIGHT = 2000;

    private final Group locationViewContent;
    private RoomPolygon selectedRoom, rootRoom;
    private Group scrollContent;
    private ScrollPane scroller;
    private StackPane zoomPane;
    private final ScrollPane scrollPane;
    private final ForegroundPane foregroundPane;
    private final LocationPaneDesktopControls locationPaneDesktopControls;

    private final SimpleStringProperty selectedRoomId;

    /**
     * Constructor for the LocationPane.
     *
     * @param foregroundPane The foregroundPane
     */
    public LocationPane(final ForegroundPane foregroundPane) {
        super();

        this.foregroundPane = foregroundPane;

        final Rectangle emptyHugeRectangle = new Rectangle(-(ZOOM_PANE_WIDTH / 2),
                -(ZOOM_PANE_HEIGHT / 2),
                ZOOM_PANE_WIDTH,
                ZOOM_PANE_HEIGHT);
        emptyHugeRectangle.setFill(Color.TRANSPARENT);

        //Dummy Room
        selectedRoom = new RoomPolygon("none", 0.0, 0.0, 0.0, 0.0);
        selectedRoomId = new SimpleStringProperty("none");

        locationViewContent = new Group(emptyHugeRectangle);
        scrollPane = createZoomPane(locationViewContent);

        this.getChildren().setAll(scrollPane);

        final BackgroundImage backgroundImage = new BackgroundImage(
                new Image("backgrounds/blueprint.jpg"),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        this.setBackground(new Background(backgroundImage));

        this.locationPaneDesktopControls = new LocationPaneDesktopControls(this, this.foregroundPane);

    }

    /**
     * Adds a room to the location Pane and use the controls to add a mouse event handler.
     *
     * If a room with the same id already exists, it will be overwritten.
     *
     * @param roomID The room id
     * @param vertices A list of vertices which defines the shape of the room
     */
    public void addRoom(final String roomID, final List<Point2D> vertices, final boolean isRoot) {
        // TODO: Remove room with same ID
//        for (Node node : locationViewContent.getChildren()) {
//            if (node instanceof RoomPolygon) {
//                if (((RoomPolygon) node).getRoomName().equals(roomID)) {
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

        // Create a new RoomPolygon, add a mouse event handler and paste it into the viewContent
        final RoomPolygon newRoom = new RoomPolygon(roomID, points);

        locationViewContent.getChildren().add(newRoom);

        if (!isRoot) {
            locationPaneDesktopControls.addMouseEventHandlerToRoom(newRoom);
        } else {
            newRoom.setMouseTransparent(true);
            this.rootRoom = newRoom;
        }

    }

    private ScrollPane createZoomPane(final Group group) {

        zoomPane = new StackPane();

        zoomPane.getChildren().add(group);

        scroller = new ScrollPane();
        scrollContent = new Group(zoomPane);
        scroller.setContent(scrollContent);
        scroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        //scroller.getStylesheets().add("css/transparent_scrollpane.css");

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
     * Getter for ZoomPaneWidth.
     * @return the width of the zoomPane
     */
    public double getZoomPaneWidth() {
        return ZOOM_PANE_WIDTH;
    }

    /**
     * Getter for ZoomPaneHeight.
     * @return the height of the zoomPane
     */
    public double getZoomPaneHeight() {
        return ZOOM_PANE_HEIGHT;
    }

    /**
     * Getter for selectedRoom.
     * @return selectedRoom
     */
    public RoomPolygon getSelectedRoom() {
        return selectedRoom;
    }

    /**
     * Getter for scrollContent.
     * @return scrollContent
     */
    public Group getScrollContent() {
        return scrollContent;
    }

    /**
     * Getter for scroller.
     * @return scroller
     */
    public ScrollPane getScroller() {
        return scroller;
    }

    /**
     * Getter for zoomPane.
     * @return zoomPane
     */
    public StackPane getZoomPane() {
        return zoomPane;
    }

    /**
     * Getter for locationViewContent.
     * @return locationViewContent
     */
    public Group getLocationViewContent() {
        return locationViewContent;
    }

    /**
     * Getter for scrollPane.
     * @return scrollPane
     */
    public ScrollPane getScrollPane() {
        return scrollPane;
    }

    /**
     * Setter for selectedRoom.
     * @param selectedRoom Room to select
     */
    public void setSelectedRoom(final RoomPolygon selectedRoom) {
        this.selectedRoom = selectedRoom;
        this.selectedRoomId.set(selectedRoom.getRoomName());
    }

    /**
     * Getter for the rootRoom.
     * @return rootRoom
     */
    public RoomPolygon getRootRoom() {
        return rootRoom;
    }

    /**
     * ZoomFits to the root if available.
     */
    public void zoomFit() {
        this.locationPaneDesktopControls.zoomFit();
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
}
