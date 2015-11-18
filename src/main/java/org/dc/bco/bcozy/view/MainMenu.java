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

//import javafx.animation.KeyFrame;
//import javafx.animation.KeyValue;
//import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

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

        this.setMinHeight(height);
        this.setMinWidth(width);
        //this.getStyleClass().add("linear-gradient-left-to-right");
        this.getStyleClass().add("dropshadow-right-bg");

        mainButton = new Button("Test Location Registry");
        mainButton.getStyleClass().addAll("large-button", "visible-lg", "visible-md", "visible-sm", "visible-xs");
        this.getChildren().add(mainButton);
        //this.setVisible(false);
    }

    /**
     * Configure the MainButton.
     * @param eventHandler EventHandler
     */
    public void addMainButtonEventHandler(final EventHandler<ActionEvent> eventHandler) {
        mainButton.setOnAction(eventHandler);
    }

    /**
     * Method to make this menu visible.
     * Animations should be added in the future
     */
    public void showMainMenu() {
        this.setVisible(true);
        //CHECKSTYLE.OFF: MagicNumber
//        final Timeline timeline = new Timeline(
//                new KeyFrame(javafx.util.Duration.ZERO, new KeyValue(this., 0)),
//                new KeyFrame(javafx.util.Duration.millis(500), new KeyValue(this.opacityProperty(), 1))
//        );
        //CHECKSTYLE.ON: MagicNumber
        //timeline.play();
    }

    /**
     * Method to make this menu invisible.
     * Animations should be added in the future
     */
    public void hideMainMenu() {
        this.setVisible(false);
    }
}
