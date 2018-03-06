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
import org.openbase.bco.bcozy.BCozy;
import org.openbase.bco.bcozy.util.ThemeManager;
import org.openbase.jul.exception.NotAvailableException;

/**
 * Created by tmichalski on 14.01.16.
 */
public class LoadingPane extends BorderPane {

    private static LoadingPane instance;
    private final VBox centerPane;
    private final ObserverLabel textLabel;
    private final JFXSpinner progressSpinner;

    /**
     * Constructor for the ForegroundPane.
     *
     * @param height Height of the application window
     * @param width Width of the application window
     */
    public LoadingPane(final double height, final double width) {
        this.setPrefSize(width, height);
        this.getStyleClass().add("loading-pane");

        this.textLabel = new ObserverLabel("initRemotes");
        this.textLabel.getStyleClass().clear();
        this.textLabel.getStyleClass().add("loading-pane");

        this.progressSpinner = new JFXSpinner();

        this.centerPane = new VBox(Constants.INSETS);
        this.centerPane.setAlignment(Pos.CENTER);
        this.centerPane.getChildren().addAll(progressSpinner, textLabel);

        this.setCenter(this.centerPane);
        instance = this;
    }

    public static LoadingPane getInstance() throws NotAvailableException {
        if(instance == null) {
            throw new NotAvailableException(LoadingPane.class);
        }
        return instance;
    }

    /**
     * Shows an info text in the loading pane referred by the identifier.
     *
     * @param identifier identifier which is used to resolved the text via the language bundle.
     */
    public void info(final String identifier) {
        assert identifier != null;
        Platform.runLater(() -> {
            textLabel.setStyle("");
            textLabel.setIdentifier(identifier);
        });
    }

    /**
     * Shows an confirmation text in the loading pane referred by the identifier.
     *
     * @param identifier identifier which is used to resolved the text via the language bundle.
     */
    public void confirmation(final String identifier) {
        assert identifier != null;
        Platform.runLater(() -> {
            textLabel.setStyle("-fx-text-fill: green;");
            textLabel.setIdentifier(identifier);
        });
    }

    /**
     * Shows an warning text in the loading pane referred by the identifier.
     *
     * @param identifier identifier which is used to resolved the text via the language bundle.
     */
    public void warn(final String identifier) {
        assert identifier != null;
        Platform.runLater(() -> {
            textLabel.setStyle("-fx-text-fill: orange;");
            textLabel.setIdentifier(identifier);
        });
    }

    /**
     * Shows an error text in the loading pane referred by the identifier.
     *
     * @param identifier identifier which is used to resolved the text via the language bundle.
     */
    public void error(final String identifier) {
        assert identifier != null;
        Platform.runLater(() -> {
            textLabel.setStyle("-fx-text-fill: red;");
            textLabel.setIdentifier(identifier);
        });
    }
}
