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
package org.dc.bco.bcozy.view;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.dc.bco.bcozy.model.ShutterInstance;
import org.dc.bco.bcozy.view.devicepanes.ShutterPane;

/**
 * Created by hoestreich on 11/10/15.
 */
public class ContextMenu extends AnchorPane {

    private final RoomContextInfo roomContextInfo;
    private final ContextSortingPane contextSortingPane;

    /**
     * Constructor for the ContextMenu.
     * @param height Height
     * @param width Width
     */
    public ContextMenu(final double height, final double width) {

        this.setMinHeight(height);
        this.setMinWidth(width);

        final ScrollPane verticalScrollPane = new ScrollPane();
        verticalScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        verticalScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        final VBox verticalOuterLayout = new VBox(Constants.INSETS);

        final VBox verticalInnerLayout = new VBox(Constants.INSETS);

        roomContextInfo = new RoomContextInfo();
        contextSortingPane = new ContextSortingPane(this.getMinWidth() + (Constants.INSETS * 2));

        //TODO: Replace with automatic content generation based on the actual selection (in the locationpane)
        final ShutterPane shutterPane = new ShutterPane(new ShutterInstance("Shutter Living", 50.0));
        final ShutterPane shutterPane1 = new ShutterPane(new ShutterInstance("Shutter Kitchen", 0.0));
        final ShutterPane shutterPane2 = new ShutterPane(new ShutterInstance("Shutter Sports", 100.0));
        final ShutterPane shutterPane3 = new ShutterPane(new ShutterInstance("Shutter Control", 100.0));

        verticalInnerLayout.getChildren().addAll(shutterPane, shutterPane1, shutterPane2, shutterPane3);
        verticalScrollPane.setContent(verticalInnerLayout);
        verticalInnerLayout.setFillWidth(true);
        verticalInnerLayout.setPadding(
                new Insets(Constants.INSETS, Constants.INSETS, Constants.INSETS, Constants.INSETS));
        //TODO: Somehow it won't just fill the remaining size in the VBox - Fix this somehow...
        //CHECKSTYLE.OFF: MagicNumber
        verticalScrollPane.setPrefHeight(height - 200.0);
        //CHECKSTYLE.ON: MagicNumber
        verticalOuterLayout.getChildren().addAll(roomContextInfo, contextSortingPane, verticalScrollPane);

        this.getChildren().add(verticalOuterLayout);

        this.setLeftAnchor(verticalOuterLayout, Constants.INSETS);
        this.setRightAnchor(verticalOuterLayout, Constants.INSETS);
        this.setTopAnchor(verticalOuterLayout, Constants.INSETS);
        this.setBottomAnchor(verticalOuterLayout, Constants.INSETS);

        //CHECKSTYLE.OFF: MultipleStringLiterals
        verticalScrollPane.getStyleClass().add("dropshadow-left-bg");
        this.getStyleClass().add("dropshadow-left-bg");
        //CHECKSTYLE.ON: MultipleStringLiterals
    }

    /**
     * Getter for the roomContextInfo Element.
     * @return Element Instance
     */
    public RoomContextInfo getRoomContextInfo() {
        return roomContextInfo;
    }
}
