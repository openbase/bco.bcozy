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

/**
 * Created by hoestreich on 11/26/15.
 */
public class CenterPane extends StackPane {

    private final FloatingButton popUpParent;
    private final FloatingButton popUpChildTop;
    private final FloatingButton popUpChildBottom;
    private final FloatingButton fullscreen;
    private final VBox viewSwitcher;
    private final VBox viewSwitcherPopUp;

    /**
     * Constructor for the center pane.
     */
    public CenterPane() {

        // Initializing components
        popUpParent = new FloatingButton("/icons/settings.png", Constants.MIDDLE_ICON);
        popUpChildTop = new FloatingButton("/icons/thermometer.png", Constants.SMALL_ICON);
        popUpChildBottom = new FloatingButton("/icons/observe.png", Constants.SMALL_ICON);
        fullscreen = new FloatingButton("/icons/fullscreen.png", Constants.MIDDLE_ICON);
        viewSwitcher = new VBox(Constants.INSETS);
        viewSwitcher.setMaxSize(Constants.MIDDLE_ICON,
                (Constants.MIDDLE_ICON + Constants.SMALL_ICON * 2 + Constants.INSETS * 4));
        viewSwitcher.setAlignment(Pos.BOTTOM_CENTER);
        viewSwitcherPopUp = new VBox(Constants.INSETS);
        viewSwitcherPopUp.setAlignment(Pos.CENTER);

        // Setting Alignment in Stackpane
        StackPane.setAlignment(viewSwitcher, Pos.BOTTOM_RIGHT);
        StackPane.setAlignment(fullscreen, Pos.TOP_RIGHT);
        this.setPickOnBounds(false);
        viewSwitcher.translateYProperty().set(-Constants.INSETS);
        fullscreen.translateYProperty().set(Constants.INSETS);

        // Adding components to their parents
        viewSwitcher.getChildren().addAll(popUpParent);
        viewSwitcherPopUp.getChildren().addAll(popUpChildTop, popUpChildBottom);
        this.getChildren().addAll(viewSwitcher, fullscreen);

        // Styling components with CSS
        //CHECKSTYLE.OFF: MultipleStringLiterals
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
     * @param visible value to be set
     */
    public void setViewSwitchingButtonsVisible(final boolean visible) {
        if (visible) {
            viewSwitcher.getChildren().clear();
            viewSwitcher.getChildren().addAll(viewSwitcherPopUp, popUpParent);
        } else {
            viewSwitcher.getChildren().removeAll(viewSwitcherPopUp);
        }

    }

}
