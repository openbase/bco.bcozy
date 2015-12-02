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

import javafx.geometry.Insets;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import org.controlsfx.control.SegmentedButton;

/**
 * Created by hoestreich on 11/20/15.
 */
public class ContextSortingPane extends SegmentedButton {

    /**
     * Constructor for a Pane with Toogle Buttons.
     * @param width width of the parent
     */
    public ContextSortingPane(final double width) {

        final ToggleGroup toggleGroup = new ToggleGroup();
        final ToggleButton locationBtn = new ToggleButton("Location");
        locationBtn.setToggleGroup(toggleGroup);
        locationBtn.setMaxWidth(Double.MAX_VALUE);
        locationBtn.setPrefWidth(width / 2);
        final ToggleButton functionBtn = new ToggleButton("Function");
        functionBtn.setToggleGroup(toggleGroup);
        functionBtn.setMaxWidth(Double.MAX_VALUE);
        functionBtn.setPrefWidth(width / 2);
        //CHECKSTYLE.OFF: MultipleStringLiterals
        locationBtn.getStyleClass().addAll("visible-lg", "visible-md", "visible-sm", "visible-xs");
        functionBtn.getStyleClass().addAll("visible-lg", "visible-md", "visible-sm", "visible-xs");
        //CHECKSTYLE.ON: MultipleStringLiterals
        this.setToggleGroup(toggleGroup);
        this.getButtons().addAll(locationBtn, functionBtn);
        this.setMaxWidth(Double.MAX_VALUE);
        //this.setPrefWidth(width);
        this.setPadding(new Insets(Constants.INSETS, Constants.INSETS, Constants.INSETS, Constants.INSETS));
    }
}
