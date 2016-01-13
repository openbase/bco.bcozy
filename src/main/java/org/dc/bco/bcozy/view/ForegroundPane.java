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

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.BoundingBox;
import javafx.scene.layout.BorderPane;

/**
 * Created by hoestreich on 11/10/15.
 */
public class ForegroundPane extends BorderPane {

    private final MainMenu mainMenu;
    private final ContextMenu contextMenu;
    private final CenterPane centerPane;
    private final MenuHeader menuHeader;
    private final InfoFooter infoFooter;
    /**
     * Constructor for the ForegroundPane.
     * @param height Height of the application window
     * @param width Width of the application window
     */
    public ForegroundPane(final double height, final double width) {
        //CHECKSTYLE.OFF: MagicNumber
        this.mainMenu = new MainMenu(height - 150, 300);
        this.contextMenu = new ContextMenu(height - 150, 300);
        this.menuHeader = new MenuHeader(30, width);
        this.infoFooter = new InfoFooter(20, width);
        //CHECKSTYLE.ON: MagicNumber
        this.centerPane = new CenterPane();

        //this.setTop(this.menuHeader);
        this.setLeft(this.mainMenu);
        this.setRight(this.contextMenu);
        this.setBottom(this.infoFooter);
        this.setCenter(this.centerPane);
        this.setTop(this.menuHeader);
        this.setPickOnBounds(false);
    }

    /**
     * Getter for the main menu (on the left).
     * @return MainMenu (VBox)
     */
    public MainMenu getMainMenu() {
        return mainMenu;
    }

    /**
     * Getter for the context menu (bottom or right).
     * @return ContextMenu Instance (VBox)
     */
    public ContextMenu getContextMenu() {
        return contextMenu;
    }

    /**
     * Getter for the center pane.
     * @return CenterPane
     */
    public CenterPane getCenterPane() {
        return centerPane;
    }

    /**
     * Getter for the info footer (bottom).
     * @return InfoFooter (HBox)
     */
    public InfoFooter getInfoFooter() {
        return infoFooter;
    }

    /**
     * Method to provide a bounding box within which the location should be drawn.
     * @return a bounding box with the values.
     */
    public BoundingBox getBoundingBox() {
        return new BoundingBox(this.mainMenu.getLayoutBounds().getMaxX(),
                this.menuHeader.getLayoutBounds().getMaxY(),
                this.centerPane.getWidth(),
                this.centerPane.getHeight());
    }

    /**
     * Method to provide the width property of the bounding box within which the location should be drawn.
     * @return a property of the width from the bounding box.
     */
    public ReadOnlyDoubleProperty getMainMenuWidthProperty() {
        return this.mainMenu.widthProperty();
    }
}
