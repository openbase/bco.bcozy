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

import javax.sound.sampled.Line;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created by hoestreich on 11/10/15.
 *
 * @author vdasilva
 */
public class InfoFooter extends BorderPane {

    /**
     * Constructor for the InfoFooter.
     *
     * @param height Height
     * @param width  Width
     */
    public InfoFooter(final double height, final double width) {
        this.setPrefHeight(height);
        this.setPrefWidth(width);
    }

    public class TextBuilder {

        final Label label;

        private TextBuilder(Label label) {
            this.label = label;
            this.backgroundColor(Color.TRANSPARENT);
            this.color(new Color(1, 1, 1, 0.9));
        }

        public TextBuilder color(Color color) {
            label.setTextFill(Objects.requireNonNull(color));
            return this;
        }

        public TextBuilder backgroundColor(Color color) {
            InfoFooter.this.setBackground(new Background(new BackgroundFill(Objects.requireNonNull(color),
                    CornerRadii.EMPTY,
                    Insets.EMPTY)));
            return this;
        }

        public void show() {
            label.getStyleClass().remove("label");
            label.setAlignment(Pos.CENTER);

            PlatformImpl.runAndWait(() -> InfoFooter.this.setCenter(label));
        }

        public void showFor(Duration duration) {
            this.show();

            PlatformImpl.runAndWait(() -> {
                Timeline timeline = new Timeline(new KeyFrame(duration, e -> InfoFooter.this.hide()));
                timeline.play();
            });
        }

    }

    public TextBuilder withText(String text) {
        return new TextBuilder(new Label(text));
    }

    public TextBuilder withIdentifier(String text) {
        return new TextBuilder(new ObserverLabel(text));
    }

    public void hide() {
        this.setCenter(null);
        this.setBackground(null);
    }

}
