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

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.HiddenSidesPane;
import org.dc.bco.bcozy.view.devicepanes.TitledPaneContainer;

/**
 * Created by hoestreich on 11/10/15.
 */
public class ContextMenu extends VBox {

    private final ContextSortingPane contextSortingPane;
    private final Label roomInfo;
    private final ScrollPane verticalScrollPane;
    private TitledPaneContainer titledPaneContainer;


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
        roomInfo.setAlignment(Pos.CENTER);

        verticalScrollPane = new ScrollPane();
        verticalScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        verticalScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        final ScrollBar scrollBar = new ScrollBar();
        scrollBar.setOrientation(Orientation.VERTICAL);
        final HiddenSidesPane hiddenSidesPane = new HiddenSidesPane();
        hiddenSidesPane.setContent(verticalScrollPane);
        hiddenSidesPane.setRight(scrollBar);
        hiddenSidesPane.setTriggerDistance(Constants.TRIGGER_DISTANCE);
        hiddenSidesPane.getStyleClass().add("hidden-sides-pane");

        scrollBar.maxProperty().bind(verticalScrollPane.vmaxProperty());
        scrollBar.minProperty().bind(verticalScrollPane.vminProperty());

        verticalScrollPane.vvalueProperty().bindBidirectional(scrollBar.valueProperty());

        //TODO: Delete completely - at the moment it is just not added to the rest
        contextSortingPane = new ContextSortingPane(width + Constants.INSETS);
        contextSortingPane.setMaxWidth(Double.MAX_VALUE);

        titledPaneContainer = new TitledPaneContainer();

        verticalScrollPane.setFitToWidth(true);
        verticalScrollPane.setContent(titledPaneContainer);
        //TODO: Find a nicer way to scale the size of the scroll bar thumb
        //CHECKSTYLE.OFF: MagicNumber
        scrollBar.setVisibleAmount(0.25);
        //CHECKSTYLE.ON: MagicNumber

        this.getChildren().addAll(roomInfo, hiddenSidesPane);
        //VBox.setVgrow(contextSortingPane, Priority.ALWAYS);

        //CHECKSTYLE.OFF: MultipleStringLiterals
        this.getStyleClass().addAll("detail-menu");
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

    /**
     * Set the new TitledPaneContainer and add it to the VerticalScrollPane.
     * @param titledPaneContainer titledPaneContainer
     */
    public void setTitledPaneContainer(final TitledPaneContainer titledPaneContainer) {
        this.titledPaneContainer = titledPaneContainer;
        verticalScrollPane.setContent(this.titledPaneContainer);
    }

    /**
     * Clears the vertical ScrollPane of the ContextMenu.
     */
    public void clearVerticalScrollPane() {
        verticalScrollPane.setContent(null);
    }
}
