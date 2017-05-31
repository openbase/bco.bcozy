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
package org.openbase.bco.bcozy.view.mainmenupanes;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

/**
 * @author hoestreich
 * @author vdasilva
 */
public abstract class PaneElement extends VBox {

    /**
     * Constructor for a Pane Element to guarantee a similar layout for all gui elements.
     * @param content the content which should be placed within this pane.
     */
    public PaneElement(final Node content) {
        this();
        this.getChildren().add(content);
    }

    /**
     * Default Constructor for a Pane Element to guarantee a similar layout for all gui elements.
     */
    public PaneElement() {
        this(false);
    }


    /**
     * Constructor for a Pane Element to guarantee a similar layout for all gui elements.
     */
    public PaneElement(boolean plain) {
        this.init(plain);
    }

    /**
     * Method to initialize all gui stuff.
     */
    private void init(boolean plain) {
        this.setFillWidth(true);
        this.setAlignment(Pos.CENTER);
        if (plain) {
            this.getStyleClass().addAll("floating-box-plain");
        } else {
            this.getStyleClass().addAll("floating-box");

        }
    }

    /**
     * Method to get a status icon which muss be impelmented by all PaneElements.
     * This icon is used to be displayed in the minimized mainMenu.
     * @return a general Node (can be of different types)
     */
    public abstract Node getStatusIcon();
}
