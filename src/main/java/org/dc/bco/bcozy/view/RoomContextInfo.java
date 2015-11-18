package org.dc.bco.bcozy.view;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

/**
 * Created by hoestreich on 11/18/15.
 */
public class RoomContextInfo extends AnchorPane{

    private final Label roomInfo;

    public RoomContextInfo() {

        roomInfo = new Label("No room selected.");

        this.getStyleClass().add("dropshadow-bottom-bg");
        this.setLeftAnchor(this, 10.0);
        this.setRightAnchor(this, 10.0);
        this.setMinHeight(roomInfo.getHeight() + 20);
    }

    public Label getRoomInfo() {
        return roomInfo;
    }
}
