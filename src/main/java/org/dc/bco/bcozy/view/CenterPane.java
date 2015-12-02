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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;

/**
 * Created by hoestreich on 11/26/15.
 */
public class CenterPane extends StackPane {

    private final PopOver viewSwitcher;
    private final FloatingButton settings;
    private final FloatingButton temperature;
    private final FloatingButton movement;
    private final FloatingButton fullscreen;
    private final ClockLabel clock;

    private final FloatingButton activeBtn;

    /**
     * Constructor for the center pane.
     */
    public CenterPane() {

        settings = new FloatingButton("/icons/settings.png");
        temperature = new FloatingButton("/icons/thermometer.png");
        movement = new FloatingButton("/icons/observe.png");
        fullscreen = new FloatingButton("/icons/fullscreen.png");
        clock = new ClockLabel();
        this.setPickOnBounds(false);

        final VBox verticalLayout = new VBox(Constants.INSETS);
        verticalLayout.getChildren().addAll(settings, temperature, movement);
        verticalLayout.getStyleClass().addAll("padding-small");

        viewSwitcher = new PopOver(verticalLayout);
        viewSwitcher.setArrowLocation(PopOver.ArrowLocation.BOTTOM_CENTER);
        viewSwitcher.getStyleClass().addAll("floating-box");

        activeBtn = settings;
        //TODO: Implement switching action in external controller

        activeBtn.setOnAction(event -> viewSwitcher.show(activeBtn));
        this.getChildren().addAll(activeBtn, fullscreen, clock);
        this.setAlignment(activeBtn, Pos.BOTTOM_RIGHT);
        this.setAlignment(fullscreen, Pos.TOP_RIGHT);
        this.setAlignment(clock, Pos.TOP_CENTER);
        activeBtn.translateYProperty().set(-Constants.INSETS);
        fullscreen.translateYProperty().set(Constants.INSETS);
    }
}
