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

import javafx.scene.Group;
import javafx.scene.layout.*;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hoestreich on 11/10/15.
 */
public class LocationPane extends StackPane {

    private static final double ZOOM_PANE_WIDTH = 2000;
    private static final double ZOOM_PANE_HEIGHT = 2000;

    private final Group locationViewContent;
    private RoomPolygon selectedRoom;
    private Group scrollContent;
    private ScrollPane scroller;
    private StackPane zoomPane;
    private final ScrollPane scrollPane;
    private final List<RoomPolygon> rooms;

    /**
     * Constructor for the LocationPane.
     */
    public LocationPane() {
        super();

        final Rectangle emptyHugeRectangle = new Rectangle(-(ZOOM_PANE_WIDTH / 2),
                                                     -(ZOOM_PANE_HEIGHT / 2),
                ZOOM_PANE_WIDTH,
                ZOOM_PANE_HEIGHT);
        emptyHugeRectangle.setFill(Color.TRANSPARENT);

        //Dummy Room

        //CHECKSTYLE.OFF: MagicNumber
        selectedRoom = new RoomPolygon("none", 0.0, 0.0, 0.0, 0.0);

        final RoomPolygon room0 = new RoomPolygon("Room0",
                50.0, 50.0,
                100.0, 50.0,
                100.0, 100.0,
                80.0, 100.0,
                80.0, 80.0,
                50.0, 80.0);

        final RoomPolygon room1 = new RoomPolygon("Room1",
                -10.0, -10.0,
                -10.0, 10.0,
                30.0, 30.0,
                30.0, -10.0);

        final RoomPolygon room2 = new RoomPolygon("Room2",
                50.0, -20.0,
                100.0, -20.0,
                100.0, 30.0,
                60.0, 30.0,
                60.0, 10.0,
                50.0, 10.0);

        final RoomPolygon room3 = new RoomPolygon("Room3",
                -30.0, 50.0,
                -10.0, 70.0,
                -10.0, 90.0,
                -30.0, 110.0,
                -50.0, 110.0,
                -70.0, 90.0,
                -70.0, 70.0,
                -50.0, 50.0);

        //CHECKSTYLE.ON: MagicNumber

        locationViewContent = new Group(emptyHugeRectangle, room0, room1, room2, room3);

        //TODO: Should the rooms be part of the model?
        rooms = new ArrayList<RoomPolygon>();
        rooms.add(room0);
        rooms.add(room1);
        rooms.add(room2);
        rooms.add(room3);

        scrollPane = createZoomPane(locationViewContent);

        this.getChildren().setAll(scrollPane);

        final BackgroundImage backgroundImage = new BackgroundImage(
                new Image("backgrounds/blueprint.jpg"),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        this.setBackground(new Background(backgroundImage));

    }

    private ScrollPane createZoomPane(final Group group) {

        zoomPane = new StackPane();

        zoomPane.getChildren().add(group);

        scroller = new ScrollPane();
        scrollContent = new Group(zoomPane);
        scroller.setContent(scrollContent);
        scroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroller.getStylesheets().add("css/transparent_scrollpane.css");

        //TODO: what iiiiiis is good for?
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
    }

    /**
     * Getter for the roomList.
     * @return roomList
     */
    public List<RoomPolygon> getRooms() {
        return rooms;
    }
}
