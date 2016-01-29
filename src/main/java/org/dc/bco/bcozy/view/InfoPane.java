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

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * Created by tmichalski on 14.01.16.
 */
public class InfoPane extends BorderPane {

    private final VBox centerPane;
    private final ObserverLabel textLabel;
    private final ProgressIndicator progressIndicator;
    private final ObserverButton closeButton;

    /**
     * Constructor for the ForegroundPane.
     * @param height Height of the application window
     * @param width Width of the application window
     */
    public InfoPane(final double height, final double width) {
        this.setPrefSize(width, height);
        this.getStyleClass().add("info-pane");

        this.textLabel = new ObserverLabel("initRemotes");
        this.progressIndicator = new ProgressIndicator(-1);
        this.closeButton = new ObserverButton("close");
        this.closeButton.getStyleClass().add("transparent-button");

        this.centerPane = new VBox(Constants.INSETS);
        this.centerPane.setAlignment(Pos.CENTER);
        this.centerPane.getChildren().addAll(this.textLabel, this.progressIndicator, this.closeButton);

        this.setCenter(this.centerPane);
    }

    /**
     * Sets the new identifier for the TextLabel.
     * @param identifier identifier
     */
    public void setTextLabelIdentifier(final String identifier) {
        Platform.runLater(() -> {
                this.textLabel.setIdentifier(identifier);
            }
        );
    }

    /**
     * Sets the eventHandler for the closeButton.
     * @param eventHandler eventHandler
     */
    public void setCloseButtonEventHandler(final EventHandler<ActionEvent> eventHandler) {
        this.closeButton.setOnAction(eventHandler);
    }
}
