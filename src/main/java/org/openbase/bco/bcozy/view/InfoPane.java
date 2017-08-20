/**
 * ==================================================================
 * <p>
 * This file is part of org.openbase.bco.bcozy.
 * <p>
 * org.openbase.bco.bcozy is free software: you can redistribute it and modify
 * it under the terms of the GNU General Public License (Version 3)
 * as published by the Free Software Foundation.
 * <p>
 * org.openbase.bco.bcozy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with org.openbase.bco.bcozy. If not, see <http://www.gnu.org/licenses/>.
 * ==================================================================
 */
package org.openbase.bco.bcozy.view;

import com.sun.javafx.application.PlatformImpl;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.openbase.bco.bcozy.BCozy;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InvalidStateException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

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
     * @param width  Width
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

    public static InfoPaneTimer info(final String identifier) {
        final String style;
        if (BCozy.baseColorIsWhite) {
            style = ("-fx-text-fill: white;");
        } else {
            style = ("-fx-text-fill: black;");
        }

        return show(identifier, style);
    }

    public static InfoPaneTimer confirmation(final String identifier) {
        return show(identifier, "-fx-text-fill: green;");
    }

    public static InfoPaneTimer warn(final String identifier) {
        return show(identifier, "-fx-text-fill: orange;");
    }

    public static InfoPaneTimer error(final String identifier) {
        return show(identifier, "-fx-text-fill: red;");
    }

    public static InfoPaneTimer show(final String identifier, String style) {
        Objects.requireNonNull(identifier);
        Objects.requireNonNull(style);
        try {
            final ObserverLabel textLabel = getInstance().textLabel;
            Platform.runLater(() -> {
                textLabel.setStyle(style + "-fx-font-size: 16;");
                textLabel.setIdentifier(identifier);
            });
            return new InfoPaneTimer();
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory("Could not print user feedback!", ex, LOGGER);
        }
        return new InfoPaneTimer() {
            public void hideAfter(Duration duration) {
                //Shouldn't hide after CouldNotPerformException
            }
        };
    }

    /**
     * Hides the InfoPane.
     */
    public static void hide() {
        try {
            show("","");
            getInstance().setBackground(null);
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory("Could not print user feedback!", ex, LOGGER);
        }
    }

    /**
     * Used to hide InfoPane after certain time with Builder-like style.
     */
    public static class InfoPaneTimer {

        //Prevent instantiation from other classes
        private InfoPaneTimer() {
        }

        /**
         * Hides the InfoPane after the given duration.
         *
         * @param duration the duration
         */
        public void hideAfter(Duration duration) {
            PlatformImpl.runLater(() -> {
                Timeline timeline = new Timeline(new KeyFrame(duration, e -> InfoPane.hide()));
                timeline.play();
            });
        }
    }

}
