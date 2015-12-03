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

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.dc.bco.bcozy.view.devicepanes.TitledPaneContainer;

/**
 * Created by hoestreich on 11/10/15.
 */
public class ContextMenu extends VBox {

    private final ContextSortingPane contextSortingPane;
    private final Label roomInfo;
    private final TitledPaneContainer titledPaneContainer;


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

        //TODO: This just won't scale on full width :((((
        contextSortingPane = new ContextSortingPane(width + Constants.INSETS);
        contextSortingPane.setMaxWidth(Double.MAX_VALUE);

        titledPaneContainer = new TitledPaneContainer();

        verticalScrollPane.setFitToWidth(true);
        verticalScrollPane.setContent(titledPaneContainer);

        //CHECKSTYLE.OFF: MagicNumber
        //verticalScrollPane.setMaxHeight(height - 200.0);
        //CHECKSTYLE.ON: MagicNumber

        this.getChildren().addAll(roomInfo, contextSortingPane, verticalScrollPane);
        VBox.setVgrow(contextSortingPane, Priority.ALWAYS);

        //CHECKSTYLE.OFF: MultipleStringLiterals
        this.getStyleClass().addAll("dropshadow-left-bg", "context-menu", "padding-large");
        //CHECKSTYLE.ON: MultipleStringLiterals
    }

    /**
     * Getter Method for the Label.
     * @return label
     */
    public Label getRoomInfo() {
        return roomInfo;
    }

    /**
     * Getter method for the TitledPaneContainer.
     * @return TitledPaneContainer
     */
    public TitledPaneContainer getTitledPaneContainer() {
        return titledPaneContainer;
    }
}
