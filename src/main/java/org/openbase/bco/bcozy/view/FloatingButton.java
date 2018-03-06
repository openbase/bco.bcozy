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

import de.jensd.fx.glyphs.GlyphIcons;
import javafx.animation.*;
import javafx.scene.control.Button;
import javafx.util.Duration;
import org.openbase.jul.visual.javafx.geometry.svg.SVGGlyphIcon;

/**
 * Created by hoestreich on 11/26/15.
 */
public class FloatingButton extends Button {

    private SVGGlyphIcon icon;

    /**
     * Constructor for a floating button which has only an icon and no text.
     * @param icon the icon to set for the button
     */
    public FloatingButton(final SVGGlyphIcon icon) {
        this.icon = icon;
        this.getStyleClass().clear();
        this.getStyleClass().add("floating-button");

        //this.getChildren().add(this.icon);

        final RotateTransition rotate = new RotateTransition(Duration.seconds(0.5), this.icon);
        rotate.setByAngle(Constants.FULL_ROTATION);
        rotate.setCycleCount(1);
        rotate.setInterpolator(Interpolator.LINEAR);

        this.setOnAction(event -> rotate.play());

        super.setGraphic(this.icon);
    }

    /**
     * Method to change the icon of a floating button.
     * @param icon The new icon to be set
     */
    public void changeIcon(final GlyphIcons icon) {
        this.icon = new SVGGlyphIcon(icon, this.icon.getSize(), true);
        super.setGraphic(this.icon);
    }

}
