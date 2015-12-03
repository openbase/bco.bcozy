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

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.PopOver;

/**
 * Created by hoestreich on 11/26/15.
 */
public class CenterPane extends StackPane {

    private final PopOver viewSwitcher;
    private final FloatingButton popUpParent;
    private final FloatingButton popUpChildTop;
    private final FloatingButton popUpChildBottom;
    private final FloatingButton fullscreen;
    private final ClockLabel clock;
    private final VBox verticalLayout;

    /**
     * Constructor for the center pane.
     */
    public CenterPane() {

        popUpParent = new FloatingButton("/icons/settings.png");
        popUpChildTop = new FloatingButton("/icons/thermometer.png");
        popUpChildBottom = new FloatingButton("/icons/observe.png");
        fullscreen = new FloatingButton("/icons/fullscreen.png");
        clock = new ClockLabel();

        verticalLayout = new VBox(Constants.INSETS);
        verticalLayout.getChildren().addAll(popUpChildTop, popUpChildBottom);

        viewSwitcher = new PopOver(verticalLayout);
        viewSwitcher.setArrowLocation(PopOver.ArrowLocation.BOTTOM_CENTER);

        this.getChildren().addAll(popUpParent, fullscreen, clock);
        this.setAlignment(popUpParent, Pos.BOTTOM_RIGHT);
        this.setAlignment(fullscreen, Pos.TOP_RIGHT);
        this.setAlignment(clock, Pos.TOP_CENTER);
        popUpParent.translateYProperty().set(-Constants.INSETS);
        fullscreen.translateYProperty().set(Constants.INSETS);
        //CHECKSTYLE.OFF: MultipleStringLiterals
        verticalLayout.getStyleClass().addAll("padding-small");
        viewSwitcher.getStyleClass().addAll("floating-box");
        this.getStyleClass().addAll("padding-small");
        //CHECKSTYLE.ON: MultipleStringLiterals

    }

    /**
     * Getter for the fullscreen button.
     * @return FloatingButton instance
     */
    public FloatingButton getFullscreen() {
        return fullscreen;
    }

    /**
     * Getter for the popUpChildBottom button.
     * @return FloatingButton instance
     */
    public FloatingButton getPopUpChildBottom() {
        return popUpChildBottom;
    }

    /**
     * Getter for the popUpChildTop button.
     * @return FloatingButton instance
     */
    public FloatingButton getPopUpChildTop() {
        return popUpChildTop;
    }

    /**
     * Getter for the popUpParent button.
     * @return FloatingButton instance
     */
    public FloatingButton getPopUpParent() {
        return popUpParent;
    }

    /**
     * Getter for the PopOver pane.
     * @return PopOver instance
     */
    public PopOver getViewSwitcher() {
        return viewSwitcher;
    }

}
