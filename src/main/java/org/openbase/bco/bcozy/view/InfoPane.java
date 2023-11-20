/*
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

    private Timeline timeline;

    /**
     * Constructor for the InfoFooter.
     *
     * @param height Height
     * @param width  Width
     */
    public InfoPane(final double height, final double width) {
        this.getStyleClass().clear();
        this.getStyleClass().add("info-pane");
        this.textLabel = new ObserverLabel();
        this.textLabel.getStyleClass().clear();
        this.textLabel.getStyleClass().add("info-pane");
        this.textLabel.setAlignment(Pos.CENTER);
        this.setCenter(textLabel);
        this.setPrefHeight(height);
        this.setPrefWidth(width);
        InfoPane.instance = this;

    }

    public static InfoPane getInstance() throws NotAvailableException {
        if (instance == null) {
            throw new NotAvailableException("InfoPane", new InvalidStateException("InfoPane not initialized!"));
        }
        return instance;
    }

    public static InfoPaneConfigurer info(final String identifier) {
//        final String style;
//        if (ThemeManager.isDarkThemeSelected()) {
//            style = ("-fx-text-fill: white;");
//        } else {
//            style = ("-fx-text-fill: black;");
//        }

        return show(identifier, "");
    }

    public static InfoPaneConfigurer confirmation(final String identifier) {
        return show(identifier, "-fx-text-fill: green;");
    }

    public static InfoPaneConfigurer warn(final String identifier) {
        return show(identifier, "-fx-text-fill: orange;");
    }

    public static InfoPaneConfigurer error(final String identifier) {
        return show(identifier, "-fx-text-fill: red;");
    }


    public static InfoPaneConfigurer show(final String identifier, String style) {
        return show(identifier, style, "");
    }

    public static InfoPaneConfigurer show(final String identifier, String style, String infopaneStyle) {
        Objects.requireNonNull(identifier);
        Objects.requireNonNull(style);

        try {
            final InfoPane infoPane = getInstance();
            infoPane.resetTimeline();

            Platform.runLater(() -> {
                infoPane.clearBackground();
                infoPane.setStyle(infopaneStyle);
                infoPane.textLabel.setStyle(style);
                infoPane.textLabel.setIdentifier(identifier);
            });
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory("Could not print user feedback!", ex, LOGGER);
        }
        return new InfoPaneConfigurer();
    }

    /**
     * Hides the InfoPane.
     */
    public static void hide() {
        try {
            show("", "");
            getInstance().clearBackground();
            getInstance().resetTimeline();
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory("Could not print user feedback!", ex, LOGGER);
        }
    }

    private void clearBackground() {
        this.setBackground(null);
    }

    private void resetTimeline() throws NotAvailableException {
        if (this.timeline != null) {
            this.timeline.stop();
        }
        this.timeline = null;
    }

    /**
     * Used to hide InfoPane after certain time with Builder-like style.
     */
    public static class InfoPaneConfigurer {

        //Prevent instantiation from other classes
        private InfoPaneConfigurer() {
        }

        /**
         * Hides the InfoPane after the given duration.
         *
         * @param duration the duration
         */
        public InfoPaneConfigurer hideAfter(Duration duration) {
            Platform.runLater(() -> {
                try {
                    InfoPane.getInstance().resetTimeline();
                    InfoPane.getInstance().timeline = new Timeline(new KeyFrame(duration, e -> InfoPane.hide()));
                    InfoPane.getInstance().timeline.play();
                } catch (NotAvailableException ex) {
                    ExceptionPrinter.printHistory("Could not print user feedback!", ex, LOGGER);
                }
            });
            return this;
        }

        public InfoPaneConfigurer backgroundColor(Color color) {
            Platform.runLater(() -> {
                try {
                    InfoPane.getInstance().setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
                } catch (NotAvailableException ex) {
                    ExceptionPrinter.printHistory("Could not print user feedback!", ex, LOGGER);
                }
            });

            return this;
        }
    }

}
