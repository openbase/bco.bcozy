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
package org.openbase.bco.bcozy.view;

import com.jfoenix.controls.JFXSpinner;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * Created by tmichalski on 14.01.16.
 */
public class InfoPane extends BorderPane {

    private final VBox centerPane;
    private final ObserverLabel textLabel;
    private final JFXSpinner progressSpinner;

    /**
     * Constructor for the ForegroundPane.
     *
     * @param height Height of the application window
     * @param width Width of the application window
     */
    public InfoPane(final double height, final double width) {
        this.setPrefSize(width, height);
        this.getStyleClass().add("info-pane");

        this.textLabel = new ObserverLabel("initRemotes");

        this.progressSpinner = new JFXSpinner();

        this.centerPane = new VBox(Constants.INSETS);
        this.centerPane.setAlignment(Pos.CENTER);
        this.centerPane.getChildren().addAll(progressSpinner, textLabel);

        this.setCenter(this.centerPane);
    }

    /**
     * Sets the new identifier for the TextLabel.
     *
     * @param identifier identifier
     */
    public void setTextLabelIdentifier(final String identifier) {
        assert identifier != null;
        Platform.runLater(() -> {
            textLabel.setIdentifier(identifier);
        });
    }
}
