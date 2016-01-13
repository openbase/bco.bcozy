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
package org.dc.bco.bcozy.view.mainmenupanes;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

/**
 * Created by hoestreich on 11/19/15.
 */
public abstract class PaneElement extends VBox {

    /**
     * Constructor for a Pane Element to guarantee a similar layout for all gui elements.
     * @param content the content which should be placed within this pane.
     */
    public PaneElement(final Node content) {
        this.init();
        this.getChildren().add(content);
    }

    /**
     * Default Constructor for a Pane Element to guarantee a similar layout for all gui elements.
     */
    public PaneElement() {
        this.init();
    }

    /**
     * Method to initialize all gui stuff.
     */
    private void init() {
        this.setFillWidth(true);
        this.setAlignment(Pos.CENTER);
        this.getStyleClass().addAll("floating-box");
    }

    /**
     * Method to get a status icon which muss be impelmented by all PaneElements.
     * This icon is used to be displayed in the minimized mainMenu.
     * @return a general Node (can be of different types)
     */
    public abstract Node getStatusIcon();
}
