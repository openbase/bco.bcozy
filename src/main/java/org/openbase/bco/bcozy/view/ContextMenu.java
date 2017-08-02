/**
 * ==================================================================
 * <p>
 * This file is part of org.openbase.bco.bcozy.
 * <p>
 * org.openbase.bco.bcozy is free software: you can redistribute it and modify
 * it under the terms of the GNU General Public License (Version 3)
 * as published by the Free Software Foundation.
 * <p>
 * org.openbase.bco.bcozy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with org.openbase.bco.bcozy. If not, see <http://www.gnu.org/licenses/>.
 * ==================================================================
 */
package org.openbase.bco.bcozy.view;

import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.HiddenSidesPane;
import org.openbase.bco.bcozy.view.pane.unit.TitledUnitPaneContainer;

/**
 * @author hoestreich
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class ContextMenu extends VBox {

    private final ContextSortingPane contextSortingPane;
    private final Label roomInfo;
    private final ScrollPane verticalScrollPane;
    private TitledUnitPaneContainer titledPaneContainer;
    private FloatingButton fullscreenBtn;


    /**
     * Constructor for the ContextMenu.
     *
     * @param height Height
     * @param width  Width
     */
    public ContextMenu(final double height, final double width) {

        this.setMinHeight(height);
        this.setMinWidth(width);
        this.setPrefHeight(height);
        this.setPrefWidth(width);

        fullscreenBtn = new FloatingButton(new SVGIcon(MaterialIcon.FULLSCREEN, Constants.MIDDLE_ICON, true));
        fullscreenBtn.setAlignment(Pos.TOP_RIGHT);
        fullscreenBtn.translateYProperty().set(Constants.FLOATING_BUTTON_FULLSCREEN_OFFSET_Y);
        fullscreenBtn.translateXProperty().set(Constants.FLOATING_BUTTON_FULLSCREEN_OFFSET_X);

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

        titledPaneContainer = new TitledUnitPaneContainer();

        verticalScrollPane.setFitToWidth(true);
        verticalScrollPane.setContent(titledPaneContainer);
        //TODO: Find a nicer way to scale the size of the scroll bar thumb
        scrollBar.setVisibleAmount(0.25);

        this.getChildren().addAll(fullscreenBtn, roomInfo, hiddenSidesPane);
        //VBox.setVgrow(contextSortingPane, Priority.ALWAYS);

        this.getStyleClass().addAll("detail-menu");
    }

    /**
     * Getter Method for the Label.
     *
     * @return label
     */
    public Label getRoomInfo() {
        return roomInfo;
    }

    /**
     * Getter method for the TitledUnitPaneContainer.
     *
     * @return TitledUnitPaneContainer
     */
    public TitledUnitPaneContainer getTitledPaneContainer() {
        return titledPaneContainer;
    }

    /**
     * Set the new TitledUnitPaneContainer and add it to the VerticalScrollPane.
     *
     * @param titledPaneContainer titledPaneContainer
     */
    public void setTitledPaneContainer(final TitledUnitPaneContainer titledPaneContainer) {
        this.titledPaneContainer = titledPaneContainer;
        verticalScrollPane.setContent(this.titledPaneContainer);
    }

    /**
     * Clears the vertical ScrollPane of the ContextMenu.
     */
    public void clearVerticalScrollPane() {
        verticalScrollPane.setContent(null);
    }

    public FloatingButton getFullscreen() {
        return fullscreenBtn;
    }

}
