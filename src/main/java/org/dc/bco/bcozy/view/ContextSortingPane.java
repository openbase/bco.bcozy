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

import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

/**
 * Created by hoestreich on 11/20/15.
 */
public class ContextSortingPane extends Pane {

    /**
     * Constructor for a Pane with Toogle Buttons.
     * @param width the width, of the parent
     */
    public ContextSortingPane(final double width) {

        final HBox horizontalLayout = new HBox(1.0);
        horizontalLayout.getStyleClass().add("dropshadow-bottom-bg");
        horizontalLayout.setStyle("-fx-background-color: #BDBDBD");

        final ToggleGroup toggleGroup = new ToggleGroup();
        final ToggleButton locationBtn = new ToggleButton("Location");
        locationBtn.setToggleGroup(toggleGroup);
        locationBtn.setMinWidth((width / 2) + Constants.INSETS * 2);
        final ToggleButton functionBtn = new ToggleButton("Function");
        functionBtn.setToggleGroup(toggleGroup);
        functionBtn.setMinWidth(((width / 2) + Constants.INSETS * 2) - 1.0);
        //CHECKSTYLE.OFF: MultipleStringLiterals
        locationBtn.getStyleClass().addAll("visible-lg", "visible-md", "visible-sm", "visible-xs");
        functionBtn.getStyleClass().addAll("visible-lg", "visible-md", "visible-sm", "visible-xs");
        //CHECKSTYLE.ON: MultipleStringLiterals
        horizontalLayout.getChildren().addAll(locationBtn, functionBtn);

        this.getChildren().add(horizontalLayout);
    }
}
