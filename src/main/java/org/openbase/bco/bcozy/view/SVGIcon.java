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
import de.jensd.fx.glyphs.GlyphsDude;
import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.animation.FadeTransition;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hoestreich
 * @author <a href="mailto:divine@openbase.org">Hendrik Threepwood</a>
 *
 */
public class SVGIcon extends StackPane {

    private static final Logger LOGGER = LoggerFactory.getLogger(SVGIcon.class);

    public enum Layer {
        FOREGROUND,
        BACKGROUND;
    }

    private Text backgroundIcon;
    private Text backgroundFadeIcon;
    private Text foregroundIcon;
    private Text foregroundFadeIcon;

    private final double size;
    private final boolean styled;
    private FadeTransition foregroundColorFadeAnimation, backgroundColorFadeAnimation;
    private IconState iconState;

    /**
     * Constructor for a SVGIcon.
     *
     * @param icon the Icon to be set in the backgroundIcon
     * (can be chosen from one of the supported fonts from fontawesomefx)
     * @param size the size in px for the icon
     * @param styled true if color should be changed by theme, otherwise false
     */
    public SVGIcon(final GlyphIcons icon, final double size, final boolean styled) {
        this(size, styled);
        this.foregroundIcon = createIcon(icon, Layer.FOREGROUND);
        this.foregroundFadeIcon = createFadeIcon(icon, Layer.FOREGROUND);
        this.backgroundIcon = null;
        this.backgroundFadeIcon = null;
        this.getChildren().addAll(foregroundIcon, foregroundFadeIcon);
    }

    /**
     * Constructor for a SVGIcon.
     *
     * @param backgroundIcon the Icon to be set in the backgroundIcon
     * (can be chosen from one of the supported fonts from fontawesomefx)
     * @param foregroundIcon the Icon to be set in the foregroundIcon
     * @param size the size in px for the icon
     */
    public SVGIcon(final GlyphIcons backgroundIcon, final GlyphIcons foregroundIcon, final double size) {
        this(size, true);
        this.foregroundIcon = createIcon(foregroundIcon, Layer.FOREGROUND);
        this.foregroundFadeIcon = createFadeIcon(foregroundIcon, Layer.FOREGROUND);
        this.backgroundIcon = createIcon(backgroundIcon, Layer.BACKGROUND);
        this.backgroundFadeIcon = createFadeIcon(backgroundIcon, Layer.BACKGROUND);
        this.getChildren().addAll(this.backgroundIcon, this.backgroundFadeIcon, this.foregroundIcon, this.foregroundFadeIcon);
    }

    public SVGIcon(double size, boolean styled) {
        this.size = size;
        this.styled = styled;

        disableProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean disabled) -> {
            if (disabled) {
                // save values
                iconState.save(this);
                stopAnimation();
                setForegroundIconColor(Color.GRAY);
                setBackgroundIconColor(Color.GRAY);
            } else {
                // restore values if exists
                iconState.restore(this);
            }
        });
    }

    private class IconState {

        private Boolean foregroundAnimated;
        private Boolean backgroundAnimated;
        private Color foregroundColor, backgroundColor;

        public boolean forgroundAnimated() {
            return foregroundAnimated;
        }

        public boolean backgroundAnimated() {
            return backgroundAnimated;
        }

        public void setForegroundAnimated(boolean animated) {
            this.foregroundAnimated = animated;
        }

        public void setBackgroundAnimated(boolean animated) {
            this.backgroundAnimated = animated;
        }

        public Color getForegroundColor() {
            return foregroundColor;
        }

        public void setForegroundColor(Color foregroundColor) {
            this.foregroundColor = foregroundColor;
        }

        public Color getBackgroundColor() {
            return backgroundColor;
        }

        public void setBackgroundColor(Color backgroundColor) {
            this.backgroundColor = backgroundColor;
        }

        public void save(final SVGIcon icon) {
            foregroundAnimated = icon.foregroundColorFadeAnimation != null && icon.foregroundColorFadeAnimation.getStatus().equals(Status.RUNNING);
            backgroundAnimated = icon.backgroundColorFadeAnimation != null && icon.backgroundColorFadeAnimation.getStatus().equals(Status.RUNNING);
            foregroundColor = icon.getForegroundIconColor();
            backgroundColor = icon.getBackgroundIconColor();
        }

        public void restore(final SVGIcon icon) {
            if (icon == null) {
                return;
            }
            if (foregroundAnimated != null) {
                setForegroundAnimated(foregroundAnimated);
            }
            if (backgroundAnimated != null) {
                setBackgroundAnimated(backgroundAnimated);
            }
            if (foregroundColor != null) {
                icon.setForegroundIconColor(foregroundColor);
            }
            if (backgroundColor != null) {
                icon.setBackgroundIconColor(backgroundColor);
            }
        }
    }

    private Text createIcon(final GlyphIcons glyphIcon, final Layer layer) {
        return createIcon(glyphIcon, size, layer == Layer.FOREGROUND && styled);
    }

    private Text createFadeIcon(final GlyphIcons glyphIcon, final Layer layer) {
        return createFadeIcon(glyphIcon, size, layer);
    }

    private static Text createIcon(final GlyphIcons glyphIcon, final double size, final boolean styled) {
        final Text icon = GlyphsDude.createIcon(glyphIcon, String.valueOf(size));
        icon.setSmooth(true);
        if (styled) {
            icon.getStyleClass().clear();
            icon.getStyleClass().add(Constants.ICONS_CSS_STRING);
        }
        return icon;
    }

    private static Text createFadeIcon(final GlyphIcons glyphIcon, final double size, final Layer layer) {
        final Text icon = createIcon(glyphIcon, size, layer == Layer.FOREGROUND);

        // should be only visible on fade.
        icon.setOpacity(Constants.FULLY_TRANSPARENT);
        return icon;
    }

    /**
     * Apply and play a FadeTransition on the icon in the foregroundIcon.
     * This Transition modifies the opacity of the foregroundIcon from fully transparent to opaque.
     *
     * @param cycleCount the number of times the animation should be played (use Animation.INDEFINITE for endless)
     */
    public void fadeForegroundFromTransparentToOpaque(final int cycleCount) {
        stopForegroundAnimation();
        foregroundColorFadeAnimation = AnimationProvider.createFadeTransition(foregroundIcon, Constants.FULLY_TRANSPARENT, Constants.NO_TRANSPARENCY, cycleCount, Constants.SLOW_FADE_DURATION);
        foregroundColorFadeAnimation.setOnFinished(event -> foregroundIcon.setOpacity(Constants.NO_TRANSPARENCY));
        foregroundColorFadeAnimation.play();
    }

    /**
     * Apply and play a FadeTransition on the icon in the foregroundIcon.
     * This Transition modifies the opacity of the foregroundIcon from opaque to fully transparent.
     *
     * @param cycleCount the number of times the animation should be played (use Animation.INDEFINITE for endless)
     */
    public void fadeForegroundFromOpaqueToTransparent(final int cycleCount) {
        stopForegroundAnimation();
        foregroundColorFadeAnimation = AnimationProvider.createFadeTransition(foregroundIcon, Constants.NO_TRANSPARENCY, Constants.FULLY_TRANSPARENT, cycleCount, Constants.SLOW_FADE_DURATION);
        foregroundColorFadeAnimation.setOnFinished(event -> foregroundIcon.setOpacity(Constants.FULLY_TRANSPARENT));
        foregroundColorFadeAnimation.play();
    }

    /**
     * Apply and play a FadeTransition on the icon in the foregroundIcon.
     * This Transition modifies the opacity of the foregroundIcon from fully transparent to opaque.
     *
     * @param cycleCount the number of times the animation should be played (use Animation.INDEFINITE for endless)
     */
    public void fadeBackgroundFromTransparentToOpaque(final int cycleCount) {
        stopBackgroundAnimation();
        backgroundColorFadeAnimation = AnimationProvider.createFadeTransition(backgroundIcon, Constants.FULLY_TRANSPARENT, Constants.NO_TRANSPARENCY, 1, Constants.SLOW_FADE_DURATION);
        backgroundColorFadeAnimation.setOnFinished(event -> backgroundIcon.setOpacity(Constants.NO_TRANSPARENCY));
        backgroundColorFadeAnimation.play();
    }

    /**
     * Apply and play a FadeTransition on the icon in the foregroundIcon.
     * This Transition modifies the opacity of the foregroundIcon from opaque to fully transparent.
     *
     * @param cycleCount the number of times the animation should be played (use Animation.INDEFINITE for endless)
     */
    public void fadeBackgroundFromOpaqueToTransparent(final int cycleCount) {
        stopBackgroundAnimation();
        backgroundColorFadeAnimation = AnimationProvider.createFadeTransition(backgroundIcon, Constants.NO_TRANSPARENCY, Constants.FULLY_TRANSPARENT, cycleCount, Constants.SLOW_FADE_DURATION);
        backgroundColorFadeAnimation.setOnFinished(event -> backgroundIcon.setOpacity(Constants.FULLY_TRANSPARENT));
        backgroundColorFadeAnimation.play();
    }

    /**
     * Method starts the fade animation of the background icon color.
     *
     * @param cycleCount the number of times the animation should be played (use Animation.INDEFINITE for endless)
     */
    public void startForgroundAnimation(final int cycleCount) {
        foregroundColorFadeAnimation = AnimationProvider.createFadeTransition(foregroundIcon, Constants.FULLY_TRANSPARENT, Constants.NO_TRANSPARENCY, cycleCount, Constants.SLOW_FADE_DURATION);
        foregroundColorFadeAnimation.setOnFinished(event -> foregroundIcon.setOpacity(Constants.FULLY_TRANSPARENT));
        foregroundColorFadeAnimation.play();
    }

    /**
     * Method starts the fade animation of the background icon color.
     *
     * @param cycleCount the number of times the animation should be played (use Animation.INDEFINITE for endless)
     */
    public void startBackgroundAnimation(final int cycleCount) {
        if (backgroundIcon == null) {
            LOGGER.warn("Background animation skipped because background icon not set!");
            return;
        }
        backgroundColorFadeAnimation = AnimationProvider.createFadeTransition(backgroundIcon, Constants.FULLY_TRANSPARENT, Constants.NO_TRANSPARENCY, cycleCount, Constants.SLOW_FADE_DURATION);
        backgroundColorFadeAnimation.setOnFinished(event -> backgroundIcon.setOpacity(Constants.FULLY_TRANSPARENT));
        backgroundColorFadeAnimation.play();
    }

    /**
     * Method stops the fade animation of the background icon color.
     */
    public void stopForegroundAnimation() {
        if (foregroundColorFadeAnimation != null) {
            foregroundColorFadeAnimation.stop();
        }
    }

    /**
     * Method stops the fade animation of the background icon color.
     */
    public void stopBackgroundAnimation() {
        if (backgroundColorFadeAnimation != null) {
            backgroundColorFadeAnimation.stop();
        }
    }

    /**
     * Method stops the foreground and background fade animation .
     */
    public void stopAnimation() {
        stopForegroundAnimation();
        stopBackgroundAnimation();
    }

    /**
     * Allows to set a new color to the foregroundIcon icon and setAnimation its change (by a FadeTransition).
     *
     * @param color the color for the foregroundIcon icon to be set
     * @param cycleCount the number of times the animation should be played (use Animation.INDEFINITE for endless)
     */
    public void setForegroundIconColorAnimated(final Color color, final int cycleCount) {
        stopForegroundAnimation();
        foregroundFadeIcon.setFill(color);
        foregroundColorFadeAnimation = AnimationProvider.createFadeTransition(foregroundFadeIcon, Constants.FULLY_TRANSPARENT, Constants.NO_TRANSPARENCY, Animation.INDEFINITE, Constants.LIGHT_CHANGE_FADE_DURATION);
        foregroundColorFadeAnimation.setOnFinished(event -> {
            foregroundFadeIcon.setFill(color);
            foregroundFadeIcon.setOpacity(Constants.FULLY_TRANSPARENT);
        });
        foregroundColorFadeAnimation.play();
    }

    /**
     * Allows to set a new color to the backgroundIcon icon and setAnimation its change (by a FadeTransition).
     *
     * @param color the color for the backgroundIcon icon to be set
     * @param cycleCount the number of times the animation should be played (use Animation.INDEFINITE for endless)
     */
    public void setBackgroundIconColorAnimated(final Color color, final int cycleCount) {
        if (backgroundIcon == null) {
            LOGGER.warn("Background modification skipped because background icon not set!");
            return;
        }
        stopBackgroundAnimation();
        backgroundFadeIcon.setFill(color);
        backgroundColorFadeAnimation = AnimationProvider.createFadeTransition(backgroundFadeIcon, Constants.FULLY_TRANSPARENT, Constants.NO_TRANSPARENCY, cycleCount, Constants.LIGHT_CHANGE_FADE_DURATION);
        backgroundColorFadeAnimation.setOnFinished(event -> {
            backgroundFadeIcon.setFill(color);
            backgroundFadeIcon.setOpacity(Constants.FULLY_TRANSPARENT);
        });
        backgroundColorFadeAnimation.play();
    }

    /**
     * Method sets the icon color only.
     *
     * @param color the color for the foregroundIcon icon to be set
     */
    public void setForegroundIconColor(final Color color) {
        stopForegroundAnimation();
        foregroundIcon.setFill(color);
        foregroundIcon.setStroke(Color.TRANSPARENT);
        foregroundIcon.setStrokeWidth(0);
        foregroundFadeIcon.setFill(Color.TRANSPARENT);
    }

    /**
     * Method sets the icon color only.
     *
     * @param color the color for the backgroundIcon icon to be set
     */
    public void setBackgroundIconColor(final Color color) {
        if (backgroundIcon == null) {
            LOGGER.warn("Background modification skipped because background icon not set!");
            return;
        }
        stopBackgroundAnimation();
        backgroundIcon.setFill(color);
//        backgroundIcon.setStroke(Color.TRANSPARENT);
//        backgroundIcon.setStrokeWidth(0);
        backgroundFadeIcon.setFill(Color.TRANSPARENT);
    }

    /**
     * Method sets the icon color and a stroke with a given color and width.
     *
     * @param color color for the foreground icon to be set
     * @param outline color for the stroke
     * @param width width of the stroke
     */
    public void setForegroundIconColor(final Color color, final Color outline, final double width) {
        setForegroundIconColor(color);
        foregroundIcon.setStroke(outline);
        foregroundIcon.setStrokeWidth(width);
    }

    /**
     * Method sets the icon color and a stroke with a given color and width.
     *
     * @param color color for the background icon to be set
     * @param outline color for the stroke
     * @param width width of the stroke
     */
    public void seBackgroundIconColor(final Color color, final Color outline, final double width) {
        setBackgroundIconColor(color);
        backgroundIcon.setStroke(outline);
        backgroundIcon.setStrokeWidth(width);
    }

    /**
     * Reset the current foreground icon color to default.
     */
    public void setForegroundIconColorDefault() {
        stopForegroundAnimation();
        foregroundIcon.getStyleClass().clear();
        foregroundIcon.getStyleClass().add(Constants.ICONS_CSS_STRING);
        foregroundFadeIcon.setFill(Color.TRANSPARENT);
    }

    /**
     * Reset the current background icon color to default.
     */
    public void setBackgroundIconColorDefault() {
        if (backgroundIcon == null) {
            LOGGER.warn("Background modification skipped because background icon not set!");
            return;
        }
        stopBackgroundAnimation();
        backgroundIcon.getStyleClass().clear();
        backgroundIcon.getStyleClass().add(Constants.ICONS_CSS_STRING);
        backgroundFadeIcon.setFill(Color.TRANSPARENT);
    }

    /**
     * Reset the current foreground icon color to default.
     */
    public void setForegroundIconColorDefaultInverted() {
        setForegroundIconColorDefault();
        setForegroundIconColor(getForegroundIconColor().invert());
    }

    /**
     * Reset the current background icon color to default.
     */
    public void setBackgroundIconColorDefaultInverted() {
        if (backgroundIcon == null) {
            LOGGER.warn("Background modification skipped because background icon not set!");
            return;
        }
        setBackgroundIconColorDefault();
        setBackgroundIconColor(getBackgroundIconColor().invert());
    }

    /**
     * Changes the foregroundIcon icon.
     * 
     * Note: previous color setup and animations are reset as well.
     *
     * @param icon the icon which should be set as the new icon
     */
    public void setForegroundIcon(final GlyphIcons icon) {
        setForegroundIcon(icon, null);
    }

    /**
     * Changes the backgroundIcon icon.
     * 
     * Note: previous color setup and animations are reset as well.
     *
     * @param icon the icon which should be set as the new icon.
     */
    public void setBackgroundIcon(final GlyphIcons icon) {
        setBackgroundIcon(icon, null);
    }

    /**
     * Changes the foregroundIcon icon.
     *
     * Note: previous color setup and animations are reset as well.
     * 
     * @param icon the icon which should be set as the new icon.
     * @param color the color of the new icon.
     */
    public void setForegroundIcon(final GlyphIcons icon, final Color color) {
        // copy old images to replace later.
        final Text oldForegroundIcon = foregroundIcon;
        final Text oldForegroundFadeIcon = foregroundFadeIcon;

        // create new images.
        this.foregroundIcon = createIcon(icon, Layer.FOREGROUND);
        this.foregroundFadeIcon = createFadeIcon(icon, Layer.FOREGROUND);

        // setup icon color
        if (color != null) {
            setForegroundIconColor(color);
        }

        // replace old icons with new ones.
        getChildren().replaceAll((node) -> {
            if (node.equals(oldForegroundIcon)) {
                return foregroundIcon;
            } else if (node.equals(oldForegroundFadeIcon)) {
                return foregroundFadeIcon;
            } else {
                return node;
            }
        });
    }

    /**
     * Changes the backgroundIcon icon.
     * 
     * Note: previous color setup and animations are reset as well.
     *
     * @param icon the icon which should be set as the new icon.
     * @param color the color of the new icon.
     */
    public void setBackgroundIcon(final GlyphIcons icon, final Color color) {
        // copy old images to replace later.
        final Text oldBackgroundIcon = backgroundIcon;
        final Text oldBackgroundFadeIcon = backgroundFadeIcon;

        // create new images.
        this.backgroundIcon = createIcon(icon, Layer.BACKGROUND);
        this.backgroundFadeIcon = createFadeIcon(icon, Layer.BACKGROUND);

        // setup icon color
        if (color != null) {
            setBackgroundIconColor(color);
        }

        // add background icon if not exists
        if (oldBackgroundIcon == null || oldBackgroundFadeIcon == null) {
            getChildren().clear();
            getChildren().addAll(this.backgroundIcon, this.backgroundFadeIcon, this.foregroundIcon, this.foregroundFadeIcon);
            return;
        }

        // replace old icons with new ones.
        getChildren().replaceAll((node) -> {
            if (node.equals(oldBackgroundIcon)) {
                return backgroundIcon;
            } else if (node.equals(oldBackgroundFadeIcon)) {
                return backgroundFadeIcon;
            } else {
                return node;
            }
        });
    }

    /**
     * Getter for the color of the foreground icons.
     *
     * @return color value
     */
    public Color getForegroundIconColor() {
        return (Color) foregroundIcon.getFill();
    }

    /**
     * Getter for the color of the background icons.
     *
     * @return color value
     */
    public Color getBackgroundIconColor() {
        if (backgroundIcon == null) {
            LOGGER.warn("Background color unknown because background icon not set!");
            return Color.TRANSPARENT;
        }
        return (Color) backgroundIcon.getFill();
    }

    /**
     * Getter for the size of the icons.
     *
     * @return size as a double value
     */
    public double getSize() {
        return size;
    }
}
