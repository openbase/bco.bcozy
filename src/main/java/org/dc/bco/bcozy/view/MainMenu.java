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

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

/**
 * Created by hoestreich on 11/10/15.
 */
public class MainMenu extends Pane {

    private final Button mainButton;
    /**
     * Constructor for the MainMenu.
     * @param height Height
     * @param width Width
     */
    public MainMenu(final double height, final double width) {

        //CHECKSTYLE.OFF: MagicNumber
        final Stop[] stops = new Stop[] { new Stop(0.1f, Color.rgb(0, 0, 0, .8)),
                new Stop(0.8f, Color.rgb(0, 0, 0, .7)),
                new Stop(0.9f, Color.rgb(0, 0, 0, .4)),
                new Stop(1.0f, Color.TRANSPARENT)};
        final LinearGradient linearGradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
        //CHECKSTYLE.ON: MagicNumber
        final Rectangle background = new Rectangle(width, height);
        background.setFill(linearGradient);
        this.getChildren().add(background);
        this.setMinHeight(height);
        this.setMinWidth(width);

        mainButton = new Button("Test Location Registry");
        mainButton.getStyleClass().addAll("large-button", "visible-lg", "visible-md", "visible-sm", "visible-xs");
        this.getChildren().add(mainButton);
    }

    /**
     * Configure the MainButton.
     * @param eventHandler EventHandler
     */
    public void addMainButtonEventHandler(final EventHandler<ActionEvent> eventHandler) {
        mainButton.setOnAction(eventHandler);
    }
}
