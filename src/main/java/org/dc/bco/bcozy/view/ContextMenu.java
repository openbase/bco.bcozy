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
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.dc.bco.bcozy.model.ShutterInstance;
import org.dc.bco.bcozy.view.devicepanes.ShutterPane;

/**
 * Created by hoestreich on 11/10/15.
 */
public class ContextMenu extends AnchorPane {

    private final ContextSortingPane contextSortingPane;
    private final Label roomInfo;


    /**
     * Constructor for the ContextMenu.
     * @param height Height
     * @param width Width
     */
    public ContextMenu(final double height, final double width) {

        this.setMinHeight(height);
        this.setMinWidth(width);
        this.setPrefHeight(height);
        //this.setPrefWidth(width);
        this.setMaxWidth(Double.MAX_VALUE);

        roomInfo = new Label("No room selected.");

        final ScrollPane verticalScrollPane = new ScrollPane();
        verticalScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        verticalScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        final VBox verticalOuterLayout = new VBox();

        final VBox verticalInnerLayout = new VBox();

        //TODO: This just won't scale on full width :((((
        contextSortingPane = new ContextSortingPane(width + Constants.INSETS * 4);
        contextSortingPane.setMaxWidth(Double.MAX_VALUE);

        //TODO: Replace with automatic content generation based on the actual selection (in the locationpane)
        final ShutterPane shutterPane = new ShutterPane(new ShutterInstance("Shutter Living", 50.0));
        final ShutterPane shutterPane1 = new ShutterPane(new ShutterInstance("Shutter Kitchen", 0.0));
        final ShutterPane shutterPane2 = new ShutterPane(new ShutterInstance("Shutter Sports", 100.0));
        final ShutterPane shutterPane3 = new ShutterPane(new ShutterInstance("Shutter Control", 100.0));

        verticalInnerLayout.getChildren().addAll(shutterPane, shutterPane1, shutterPane2, shutterPane3);
        verticalScrollPane.setContent(verticalInnerLayout);
        verticalInnerLayout.setFillWidth(true);
//        verticalInnerLayout.setPadding(
//                new Insets(Constants.INSETS, Constants.INSETS, Constants.INSETS, Constants.INSETS));
        //TODO: Somehow it won't just fill the remaining size in the VBox - Fix this somehow...
        // "However, the maximum size of a ScrollPane object is unbounded because typically you do want them to
        // grow to fill their spaces." http://docs.oracle.com/javase/8/javafx/layout-tutorial/size_align.htm#JFXLY133

        //CHECKSTYLE.OFF: MagicNumber
        verticalScrollPane.setMaxHeight(height - 200.0);
        verticalScrollPane.setMaxWidth(Double.MAX_VALUE);
        //CHECKSTYLE.ON: MagicNumber

        verticalOuterLayout.getChildren().addAll(roomInfo, contextSortingPane, verticalScrollPane);
        verticalOuterLayout.setFillWidth(true);
        verticalOuterLayout.setAlignment(Pos.CENTER);
        VBox.setVgrow(contextSortingPane, Priority.ALWAYS);
        verticalOuterLayout.setPadding(
                new Insets(Constants.INSETS, Constants.INSETS, Constants.INSETS, Constants.INSETS));


        this.getChildren().add(verticalOuterLayout);

//        this.setLeftAnchor(verticalOuterLayout, Constants.INSETS);
//        this.setRightAnchor(verticalOuterLayout, Constants.INSETS);
//        this.setTopAnchor(verticalOuterLayout, Constants.INSETS);
//        this.setBottomAnchor(verticalOuterLayout, Constants.INSETS);

        //CHECKSTYLE.OFF: MultipleStringLiterals
        verticalScrollPane.getStyleClass().add("padding");
        verticalOuterLayout.getStyleClass().add("padding");
        this.getStyleClass().add("dropshadow-left-bg");
        this.getStyleClass().add("floating-box");
        this.getStyleClass().add("context-menu");
        this.getStyleClass().add("padding");
        //CHECKSTYLE.ON: MultipleStringLiterals
    }

    /**
     * Getter Method for the Label.
     * @return label
     */
    public Label getRoomInfo() {
        return roomInfo;
    }
}
