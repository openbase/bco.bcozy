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

import javafx.scene.layout.BorderPane;

/**
 * Created by hoestreich on 11/10/15.
 */
public class ForegroundPane extends BorderPane {

    private final MainMenu mainMenu;
    private final ContextMenu contextMenu;
    private final MenuHeader menuHeader;
    private final InfoFooter infoFooter;

    /**
     * Constructor for the ForegroundPane.
     */
    public ForegroundPane() {
        //CHECKSTYLE.OFF: MagicNumber
        this.mainMenu = new MainMenu(400, 200);
        this.contextMenu = new ContextMenu(400, 200);
        this.menuHeader = new MenuHeader(50, 600);
        this.infoFooter = new InfoFooter(20, 600);
        //CHECKSTYLE.ON: MagicNumber

        this.setTop(this.menuHeader);
        this.setLeft(this.mainMenu);
        this.setRight(this.contextMenu);
        this.setBottom(this.infoFooter);
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
     * Getter for the menu header (top).
     * @return MenuHeader (HBox)
     */
    public MenuHeader getMenuHeader() {
        return menuHeader;
    }

    /**
     * Getter for the info footer (bottom).
     * @return InfoFooter (HBox)
     */
    public InfoFooter getInfoFooter() {
        return infoFooter;
    }
}
