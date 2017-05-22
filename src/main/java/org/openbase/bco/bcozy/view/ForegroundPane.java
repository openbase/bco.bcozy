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

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.BoundingBox;
import javafx.scene.layout.BorderPane;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.iface.DefaultInitializable;

/**
 * @author hoestreich
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class ForegroundPane extends BorderPane implements DefaultInitializable {

    private final MainMenu mainMenu;
    private final ContextMenu contextMenu;
    private final CenterPane centerPane;
    private final MenuHeader menuHeader;
    private final InfoFooter infoFooter;

    /**
     * Constructor for the ForegroundPane.
     *
     * @param height Height of the application window
     * @param width Width of the application window
     */
    public ForegroundPane(final double height, final double width) throws InterruptedException {
        this.mainMenu = new MainMenu(height - 150, 300);
        this.contextMenu = new ContextMenu(height - 150, 300);
        this.menuHeader = new MenuHeader(30, width);
        this.infoFooter = new InfoFooter(20, width);
        this.centerPane = new CenterPane();

        //this.setTop(this.menuHeader);
        this.setLeft(this.mainMenu);
        this.setRight(this.contextMenu);
        this.setBottom(this.infoFooter);
        this.setCenter(this.centerPane);
        this.setTop(this.menuHeader);
        this.setPickOnBounds(false);
    }
    
    @Override
    public void init() throws InitializationException, InterruptedException {
        try {
            mainMenu.init();
        } catch (CouldNotPerformException ex) {
            new InitializationException(this, ex);
        }
    }

    /**
     * Getter for the main menu (on the left).
     *
     * @return MainMenu (VBox)
     */
    public MainMenu getMainMenu() {
        return mainMenu;
    }

    /**
     * Getter for the context menu (bottom or right).
     *
     * @return ContextMenu Instance (VBox)
     */
    public ContextMenu getContextMenu() {
        return contextMenu;
    }

    /**
     * Getter for the center pane.
     *
     * @return CenterPane
     */
    public CenterPane getCenterPane() {
        return centerPane;
    }

    /**
     * Getter for the info footer (bottom).
     *
     * @return InfoFooter (HBox)
     */
    public InfoFooter getInfoFooter() {
        return infoFooter;
    }

    /**
     * Method to provide a bounding box within which the location should be drawn.
     *
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
     *
     * @return a property of the width from the bounding box.
     */
    public ReadOnlyDoubleProperty getMainMenuWidthProperty() {
        return this.mainMenu.widthProperty();
    }
}
