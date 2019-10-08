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

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.control.HiddenSidesPane;
import org.openbase.bco.bcozy.view.pane.unit.TitledUnitPaneContainer;
import org.openbase.bco.bcozy.view.pane.unit.UnitMenuLocationPane;
import org.openbase.jul.visual.javafx.JFXConstants;
import org.openbase.jul.visual.javafx.geometry.svg.SVGGlyphIcon;

/**
 * @author hoestreich
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 * @author vdasilva
 */
public class UnitMenu extends VBox {

    private final ContextSortingPane contextSortingPane;
    private Pane powerTerminalSidebarPane;
    private UnitMenuLocationPane unitMenuLocationPane;
    private final ScrollPane verticalScrollPane;
    private TitledUnitPaneContainer titledPaneContainer;
    private FloatingButton fullscreenButton;
    private final FloatingButton settingsBtn;
    private final SVGGlyphIcon collapseIcon;
    private FloatingButton collapseBtn;
    private HBox collapseButtons;
    private HBox floatingButtons;
    private final HiddenSidesPane hiddenSidesPane;
    private final double unitMenuMaxHeight;
    private final double unitMenuMaxWidth;
    private final BooleanProperty maximizeProperty;

    /**
     * Constructor for the ContextMenu.
     *
     * @param height Height
     * @param width  Width
     */
    public UnitMenu(final double height, final double width) {

        this.setMinHeight(height);
        this.setMinWidth(width);
        this.setPrefHeight(height);
        this.setPrefWidth(width);
        this.unitMenuMaxHeight = height;
        this.unitMenuMaxWidth = width;
        this.maximizeProperty = new SimpleBooleanProperty();
        this.maximizeProperty.set(true);

        this.fullscreenButton = new FloatingButton(new SVGGlyphIcon(MaterialIcon.FULLSCREEN, JFXConstants.ICON_SIZE_MIDDLE, true));

        this.settingsBtn = new FloatingButton(new SVGGlyphIcon(MaterialDesignIcon.SETTINGS, JFXConstants.ICON_SIZE_MIDDLE, true));

        this.collapseIcon = new SVGGlyphIcon(MaterialIcon.KEYBOARD_ARROW_RIGHT, JFXConstants.ICON_SIZE_MIDDLE, true);
        this.collapseBtn = new FloatingButton(collapseIcon);
        this.collapseBtn.translateYProperty().set(-(Constants.FLOATING_BUTTON_OFFSET));

        this.collapseButtons = new HBox();
        this.collapseButtons.getChildren().add(collapseBtn);
        this.collapseButtons.setAlignment(Pos.BOTTOM_LEFT);
        this.collapseButtons.translateXProperty().set(Constants.INSETS);
        this.setVgrow(collapseButtons, Priority.ALWAYS);


        this.floatingButtons = new HBox(20.0, settingsBtn, fullscreenButton);
        this.floatingButtons.setAlignment(Pos.TOP_LEFT);
        this.floatingButtons.translateYProperty().set(Constants.FLOATING_BUTTON_OFFSET_Y);
        this.floatingButtons.translateXProperty().set(Constants.FLOATING_BUTTON_OFFSET_X);

        this.unitMenuLocationPane = new UnitMenuLocationPane();
//        this.locationLabel = new Label("Select a Room");
//        this.locationLabel.setAlignment(Pos.TOP_CENTER);
//        this.locationLabel.getStyleClass().clear();
//        this.locationLabel.getStyleClass().add("headline");

        this.verticalScrollPane = new ScrollPane();
        this.verticalScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.verticalScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        final ScrollBar scrollBar = new ScrollBar();
        scrollBar.setOrientation(Orientation.VERTICAL);
        this.hiddenSidesPane = new HiddenSidesPane();
        this.hiddenSidesPane.setContent(verticalScrollPane);
        this.hiddenSidesPane.setRight(scrollBar);
        this.hiddenSidesPane.setTriggerDistance(Constants.TRIGGER_DISTANCE);
        this.hiddenSidesPane.getStyleClass().add("hidden-sides-pane");

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

        this.getChildren().addAll(floatingButtons, unitMenuLocationPane, hiddenSidesPane);
        //VBox.setVgrow(contextSortingPane, Priority.ALWAYS);

        this.getStyleClass().addAll("detail-menu");
    }

    public UnitMenuLocationPane getUnitMenuLocationPane() {
        return unitMenuLocationPane;
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
     * Method to make this menu invisible.
     * Animations should be added in the future
     */
    public void minimizeUnitMenu() {
        maximizeProperty.set(false);
        collapseIcon.setForegroundIcon(MaterialIcon.KEYBOARD_ARROW_LEFT);
        this.getChildren().clear();
        this.getChildren().addAll(collapseButtons);
        this.setAlignment(Pos.BOTTOM_CENTER);
        this.setMinWidth(Constants.SMALL_MAIN_MENU_WIDTH);
        this.setPrefWidth(Constants.SMALL_MAIN_MENU_WIDTH_PREF);
    }

    public void removeEnergyMode() {
        this.getChildren().clear();
        setMinHeight(unitMenuMaxHeight);
        setMinWidth(unitMenuMaxWidth);
        setPrefHeight(unitMenuMaxHeight);
        setPrefWidth(unitMenuMaxWidth);
        this.getChildren().addAll(floatingButtons, unitMenuLocationPane, hiddenSidesPane);
        this.getStyleClass().addAll("detail-menu");
    }

    public void setInEnergyMode() {
        maximizeProperty.set(true);
        this.getChildren().clear();
        setMinHeight(unitMenuMaxHeight);
        setMinWidth(unitMenuMaxWidth);
        setPrefHeight(unitMenuMaxHeight);
        setPrefWidth(unitMenuMaxWidth);
        collapseIcon.setForegroundIcon(MaterialIcon.KEYBOARD_ARROW_RIGHT);
        this.getChildren().addAll(floatingButtons, unitMenuLocationPane, powerTerminalSidebarPane, collapseButtons);
        this.getStyleClass().addAll("detail-menu");

    }

    /**
     * Clears the vertical ScrollPane of the ContextMenu.
     */
    public void clearVerticalScrollPane() {
        verticalScrollPane.setContent(null);
    }

    public FloatingButton getFullscreenButton() {
        return fullscreenButton;
    }

    public FloatingButton getSettingsBtn() {
        return settingsBtn;
    }

    /**
     * Getter for the collapsed property
     *
     * @return the collapsed property
     */
    public BooleanProperty getMaximizeProperty() {
        return maximizeProperty;
    }

    public SVGGlyphIcon getCollapseIcon() {
        return collapseIcon;
    }

    public FloatingButton getCollapseBtn() {
        return collapseBtn;
    }

    public void setPowerTerminalSidebarPane(Pane powerTerminalSidebarPane) {
        this.powerTerminalSidebarPane = powerTerminalSidebarPane;
    }

}
