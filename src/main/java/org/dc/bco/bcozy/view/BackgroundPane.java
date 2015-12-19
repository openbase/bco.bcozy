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

import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.StackPane;
import org.dc.bco.bcozy.view.location.LocationPane;

/**
 *
 */
public class BackgroundPane extends StackPane {

    private final LocationPane locationPane;
    private double prevMouseCordX;
    private double prevMouseCordY;
    private final double scaleDelta = 1.05;

    /**
     *
     * @param foregroundPane
     */
    public BackgroundPane(final ForegroundPane foregroundPane) {
        locationPane = new LocationPane(foregroundPane);
        this.getChildren().add(locationPane);

        final BackgroundImage backgroundImage = new BackgroundImage(
                new Image("backgrounds/blueprint.jpg"),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        this.setBackground(new Background(backgroundImage));


        this.setOnMousePressed(event -> {
            this.prevMouseCordX = event.getX();
            this.prevMouseCordY = event.getY();
        });

        this.setOnMouseDragged(event -> {
            locationPane.setTranslateX(locationPane.getTranslateX() + (event.getX() - prevMouseCordX));
            locationPane.setTranslateY(locationPane.getTranslateY() + (event.getY() - prevMouseCordY));
            this.prevMouseCordX = event.getX();
            this.prevMouseCordY = event.getY();
        });

        this.setOnScroll(event -> {
            event.consume();

            if (event.getDeltaY() == 0) {
                return;
            }

            final double scaleFactor = (event.getDeltaY() > 0) ? scaleDelta : 1 / scaleDelta;

            locationPane.setScaleX(locationPane.getScaleX() * scaleFactor);
            locationPane.setScaleY(locationPane.getScaleY() * scaleFactor);
            locationPane.setTranslateX(locationPane.getTranslateX() * scaleFactor);
            locationPane.setTranslateY(locationPane.getTranslateY() * scaleFactor);
        });
    }

    /**
     *
     * @return
     */
    public LocationPane getLocationPane() {
        return locationPane;
    }
}
