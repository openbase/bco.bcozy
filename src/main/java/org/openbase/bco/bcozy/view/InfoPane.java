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

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import org.openbase.bco.bcozy.BCozy;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InvalidStateException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by hoestreich on 11/10/15.
 */
public class InfoPane extends BorderPane {

    private static final Logger LOGGER = LoggerFactory.getLogger(InfoPane.class);

    private static InfoPane instance;

    private final ObserverLabel textLabel;

    /**
     * Constructor for the InfoFooter.
     *
     * @param height Height
     * @param width Width
     */
    public InfoPane(final double height, final double width) {
        this.textLabel = new ObserverLabel();
        this.textLabel.getStyleClass().add("floating-label");
        //this.mouseOverText.getStyleClass().add("small-label");
        this.textLabel.setAlignment(Pos.CENTER);
        this.setCenter(textLabel);
        this.setPrefHeight(height);
        this.setPrefWidth(width);
        InfoPane.instance = this;
        //this.getStyleClass().add("info-footer");
    }

    public static InfoPane getInstance() throws NotAvailableException {
        if (instance == null) {
            throw new NotAvailableException("InfoPane", new InvalidStateException("InfoPane not initialized!"));
        }
        return instance;
    }

    /**
     * Getter for the actual mouseOverLabel.
     *
     * @return the actual text which is set as a string
     * @deprecated please use method info() instead.
     */
    @Deprecated
    public Label getMouseOverText() {
        return textLabel;
    }

    public static void info(final String identifier) {
        assert identifier != null;
        try {
            final ObserverLabel textLabel = getInstance().textLabel;
            Platform.runLater(() -> {
                if (BCozy.baseColorIsWhite) {
                    textLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16;");
                } else {
                    textLabel.setStyle("-fx-text-fill: black; -fx-font-size: 16;");
                }
                textLabel.setIdentifier(identifier);
            });
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory("Could not print user feedback!", ex, LOGGER);
        }
    }

    public static void confirmation(final String identifier) {
        assert identifier != null;
        try {
            final ObserverLabel textLabel = getInstance().textLabel;
            Platform.runLater(() -> {
                textLabel.setStyle("-fx-text-fill: green; -fx-font-size: 16;");
                textLabel.setIdentifier(identifier);
            });
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory("Could not print user feedback!", ex, LOGGER);
        }
    }

    public static void warn(final String identifier) {
        assert identifier != null;
        try {

            final ObserverLabel textLabel = getInstance().textLabel;
            Platform.runLater(() -> {
                textLabel.setStyle("-fx-text-fill: orange; -fx-font-size: 16;");
                textLabel.setIdentifier(identifier);
            });

        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory("Could not print user feedback!", ex, LOGGER);
        }
    }

    public static void error(final String identifier) {
        assert identifier != null;
        try {
            final ObserverLabel textLabel = getInstance().textLabel;

            Platform.runLater(() -> {
                textLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16;");
                textLabel.setIdentifier(identifier);
            });
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory("Could not print user feedback!", ex, LOGGER);
        }
    }
}
