/**
 * ==================================================================
 *
 * This file is part of org.openbase.bco.bcozy.
 *
 * org.openbase.bco.bcozy is free software: you can redistribute it and modify
 * it under the terms of the GNU General Public License (Version 3)
 * as published by the Free Software Foundation.
 *
 * org.openbase.bco.bcozy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with org.openbase.bco.bcozy. If not, see <http://www.gnu.org/licenses/>.
 * ==================================================================
 */
package org.openbase.bco.bcozy.view;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import org.controlsfx.control.HiddenSidesPane;
import org.openbase.bco.bcozy.controller.CenterPaneController;
import org.openbase.bco.bcozy.controller.ContextMenuController;
import org.openbase.jul.visual.javafx.JFXConstants;
import org.openbase.jul.visual.javafx.geometry.svg.SVGGlyphIcon;
import org.openbase.bco.bcozy.view.pane.unit.TitledUnitPaneContainer;

import java.awt.*;
import java.awt.Menu;

/**
 * @author hoestreich
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 * @author vdasilva
 */
public class UnitMenu extends VBox {

    private final ContextSortingPane contextSortingPane;
    private final Label roomInfo;
    private final Label energyInfo;
    private final ScrollPane verticalScrollPane;
    private TitledUnitPaneContainer titledPaneContainer;
    private FloatingButton fullscreenBtn;
    private final FloatingButton settingsBtn;
    private final SVGGlyphIcon collapseIcon;
    private FloatingButton collapseBtn;
    private HBox collapseButtons;
    private boolean maximized;
    private HBox floatingButtons;
    private MenuButton graphChoice;
    private final HiddenSidesPane hiddenSidesPane;
    private final double height;
    private final double width;
    public final ObjectProperty<ContextMenuController.energyChart> energyChartProperty;


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
        this.height = height;
        this.width = width;
        this.maximized = true;


        fullscreenBtn = new FloatingButton(new SVGGlyphIcon(MaterialIcon.FULLSCREEN, JFXConstants.ICON_SIZE_MIDDLE, true));

        settingsBtn = new FloatingButton(new SVGGlyphIcon(MaterialDesignIcon.SETTINGS, JFXConstants.ICON_SIZE_MIDDLE, true));

        collapseIcon = new SVGGlyphIcon(MaterialIcon.KEYBOARD_ARROW_RIGHT, JFXConstants.ICON_SIZE_MIDDLE, true);
        collapseBtn = new FloatingButton(collapseIcon);
        collapseBtn.translateYProperty().set(-(Constants.FLOATING_BUTTON_OFFSET));

        collapseButtons = new HBox();
        collapseButtons.getChildren().add(collapseBtn);
        collapseButtons.setAlignment(Pos.BOTTOM_LEFT);
        collapseButtons.translateXProperty().set(Constants.INSETS);
        this.setVgrow(collapseButtons, Priority.ALWAYS);


        floatingButtons = new HBox(20.0, settingsBtn, fullscreenBtn);
        floatingButtons.setAlignment(Pos.TOP_LEFT);
        floatingButtons.translateYProperty().set(Constants.FLOATING_BUTTON_OFFSET_Y);
        floatingButtons.translateXProperty().set(Constants.FLOATING_BUTTON_OFFSET_X);


        roomInfo = new Label("Select a Room");
        roomInfo.setAlignment(Pos.TOP_CENTER);
        roomInfo.getStyleClass().clear();
        roomInfo.getStyleClass().add("headline");

        energyInfo = new Label("Select your Energy");
        energyInfo.setAlignment(Pos.TOP_CENTER);
        energyInfo.getStyleClass().clear();
        energyInfo.getStyleClass().add("headline");

        energyChartProperty = new SimpleObjectProperty<>(ContextMenuController.energyChart.BAR);


        MenuItem barChart = new MenuItem("Bar Chart");
        barChart.setOnAction(event -> {
            energyChartProperty.set(ContextMenuController.energyChart.BAR);
        });

        MenuItem pieChart = new MenuItem("Pie Chart");
        pieChart.setOnAction(event -> {
           energyChartProperty.set(ContextMenuController.energyChart.PIE);
        });

        MenuItem webView = new MenuItem("WebView");
        webView.setOnAction(event -> {
            energyChartProperty.set(ContextMenuController.energyChart.WEBVIEW);
        });

        graphChoice = new MenuButton("Charts");
        graphChoice.getItems().addAll(barChart, pieChart, webView);


        verticalScrollPane = new ScrollPane();
        verticalScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        verticalScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        final ScrollBar scrollBar = new ScrollBar();
        scrollBar.setOrientation(Orientation.VERTICAL);
        this.hiddenSidesPane = new HiddenSidesPane();
        hiddenSidesPane.setContent(verticalScrollPane);
        hiddenSidesPane.setRight(scrollBar);
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

        this.getChildren().addAll(floatingButtons, roomInfo, hiddenSidesPane);
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
     * Method to make this menu invisible.
     * Animations should be added in the future
     */
    public void minimizeUnitMenu() {
        maximized = false;
        collapseIcon.setForegroundIcon(MaterialIcon.KEYBOARD_ARROW_LEFT);
        this.getChildren().clear();
        this.getChildren().addAll(collapseButtons);
        this.setAlignment(Pos.BOTTOM_CENTER);
        this.setMinWidth(Constants.SMALL_MAIN_MENU_WIDTH);
        this.setPrefWidth(Constants.SMALL_MAIN_MENU_WIDTH_PREF);
    }

    public void removeEnergyMode () {
        this.getChildren().clear();
        setMinHeight(height);
        setMinWidth(width);
        setPrefHeight(height);
        setPrefWidth(width);
        this.getChildren().addAll(floatingButtons, roomInfo, hiddenSidesPane);
        this.getStyleClass().addAll("detail-menu");
    }

    public void setInEnergyMode () {
        maximized = true;
        this.getChildren().clear();
        setMinHeight(height);
        setMinWidth(width);
        setPrefHeight(height);
        setPrefWidth(width);
        collapseIcon.setForegroundIcon(MaterialIcon.KEYBOARD_ARROW_RIGHT);
        this.getChildren().addAll(floatingButtons, roomInfo, graphChoice, collapseButtons);
        this.getStyleClass().addAll("detail-menu");

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

    public FloatingButton getSettingsBtn() {
        return settingsBtn;
    }

    /**
     * Getter for the current display state.
     *
     * @return true if maximized, false if minimized
     */
    public boolean isMaximized() {
        return maximized;
    }

    public SVGGlyphIcon getCollapseIcon() {
        return collapseIcon;
    }

    public ObjectProperty<ContextMenuController.energyChart> getEnergyChartProperty() {
        return this.energyChartProperty;
    }

    public FloatingButton getCollapseBtn() {
        return collapseBtn;
    }
}
