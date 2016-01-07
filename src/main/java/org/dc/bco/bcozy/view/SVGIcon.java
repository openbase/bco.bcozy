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

import de.jensd.fx.glyphs.GlyphIcons;
import de.jensd.fx.glyphs.GlyphsDude;
import javafx.animation.FadeTransition;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * Created by hoestreich on 12/21/15.
 */
public class SVGIcon extends StackPane {

    private static final String ICON_CSS_STYLE = "icons";
    private Text backgroundIcon; //NOPMD
    private Text foregroundIcon; //NOPMD
    private final double size;

    /**
     * Constructor for a SVGIcon.
     * @param icon the Icon to be set in the backgroundIcon
     *             (can be chosen from one of the supported fonts from fontawesomefx)
     * @param size the size in px for the icon
     */
    public SVGIcon(final GlyphIcons icon, final double size) {
        this.size = size;
        backgroundIcon = createIcon(icon, String.valueOf(size));
        backgroundIcon.getStyleClass().add(ICON_CSS_STYLE);
        backgroundIcon.setSmooth(true);
        foregroundIcon = null;
        this.getChildren().addAll(backgroundIcon);
    }

    /**
     * Constructor for a SVGIcon.
     * @param backgroundIcon the Icon to be set in the backgroundIcon
     *                       (can be chosen from one of the supported fonts from fontawesomefx)
     * @param foregroundIcon the Icon to be set in the foregroundIcon
     * @param size the size in px for the icon
     */
    public SVGIcon(final GlyphIcons backgroundIcon, final GlyphIcons foregroundIcon, final double size) {
        this.size = size;
        this.backgroundIcon = createIcon(backgroundIcon, String.valueOf(size));
        this.backgroundIcon.getStyleClass().add(ICON_CSS_STYLE);
        this.backgroundIcon.setSmooth(true);
        this.foregroundIcon = createIcon(foregroundIcon, String.valueOf(size));
        this.foregroundIcon.getStyleClass().add(ICON_CSS_STYLE);
        this.foregroundIcon.setSmooth(true);

        this.getChildren().addAll(this.backgroundIcon, this.foregroundIcon);
    }

    private Text createIcon(final GlyphIcons icon, final String iconSize) {
        return GlyphsDude.createIcon(icon, iconSize);
    }

    /**
     * Apply and play a FadeTransition on the icon in the foregroundIcon.
     * This Transition modifies the opacity of the foregroundIcon from fully transparent to opaque.
     */
    public void fadeForegroundFromTransparentToOpaque() {
        final FadeTransition colorFade = AnimationProvider.createFadeTransition(
                foregroundIcon, Constants.FULLY_TRANSPARENT, Constants.NO_TRANSPARENCY,
                1, Constants.SLOW_FADE_DURATION);
        colorFade.setOnFinished(event -> foregroundIcon.setOpacity(Constants.NO_TRANSPARENCY));
        colorFade.play();
    }

    /**
     * Apply and play a FadeTransition on the icon in the foregroundIcon.
     * This Transition modifies the opacity of the foregroundIcon from opaque to fully transparent.
     */
    public void fadeForegroundFromOpaqueToTransparent() {
        final FadeTransition colorFade = AnimationProvider.createFadeTransition(
                foregroundIcon, Constants.NO_TRANSPARENCY, Constants.FULLY_TRANSPARENT,
                1, Constants.SLOW_FADE_DURATION);
        colorFade.setOnFinished(event -> foregroundIcon.setOpacity(Constants.FULLY_TRANSPARENT));
        colorFade.play();
    }

    /**
     * Allows to set a new color to the backgroundIcon icon and animate its change (by a FadeTransition).
     * @param color the color for the backgroundIcon icon to be set
     */
    public void setBackgroundIconColorAnimated(final Color color) {
        setAnimatedColor(backgroundIcon, color);
    }

    /**
     * Allows to set a new color to the foregroundIcon icon and animate its change (by a FadeTransition).
     * @param color the color for the foregroundIcon icon to be set
     */
    public void setForegroundIconColorAnimated(final Color color) {
        setAnimatedColor(foregroundIcon, color);
    }

    private void setAnimatedColor(final Text node, final Color color) {
        node.setOpacity(Constants.FULLY_TRANSPARENT);
        node.setFill(color);
        final FadeTransition colorFade = AnimationProvider.createFadeTransition(
                node, Constants.FULLY_TRANSPARENT, Constants.NO_TRANSPARENCY,
                1, Constants.FASTFADEDURATION);
        colorFade.setOnFinished(event -> node.setOpacity(Constants.NO_TRANSPARENCY));
        colorFade.play();
    }

    /**
     * Changes the backgroundIcon icon.
     * @param icon the icon which should be set as the new icon
     */
    public void changeBackgroundIcon(final GlyphIcons icon) {
        this.backgroundIcon = createIcon(icon, String.valueOf(this.size));
        this.backgroundIcon.getStyleClass().add(ICON_CSS_STYLE);
        this.backgroundIcon.setSmooth(true);
    }

    /**
     * Changes the foregroundIcon icon.
     * @param icon the icon which should be set as the new icon
     */
    public void changeForegroundIcon(final GlyphIcons icon) {
        this.foregroundIcon = createIcon(icon, String.valueOf(this.size));
    }

    /**
     * Getter for the size of the icons.
     * @return size as a double value
     */
    public double getSize() {
        return size;
    }

}