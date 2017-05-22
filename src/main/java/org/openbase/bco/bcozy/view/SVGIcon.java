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
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 *
 */
public class SVGIcon extends StackPane {

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
    private FadeTransition colorFadeAnimation;
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

        final BooleanProperty animationProperty = new SimpleBooleanProperty();
        Color forgroundColor;

        disableProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean disabled) -> {
            if (disabled) {

                // save values
                iconState.save(this);

                setAnimation(false);
                setForegroundIconColor(Color.GRAY);
                setBackgroundIconColor(Color.GRAY);
            } else {
                // restore values if exists
                iconState.restore(this);
            }
        });
    }

    private class IconState {

        private Boolean animated;
        private Color foregroundColor, backgroundColor;

        public boolean isAnimated() {
            return animated;
        }

        public void setAnimated(boolean animated) {
            this.animated = animated;
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
            animated = icon.colorFadeAnimation != null && icon.colorFadeAnimation.getStatus().equals(Status.RUNNING);
            foregroundColor = icon.getForegroundIconColor();
            backgroundColor = icon.getBackgroundIconColor();
        }

        public void restore(final SVGIcon icon) {
            if (animated != null) {
                setAnimated(animated);
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
     */
    public void fadeForegroundFromTransparentToOpaque() {
        setAnimation(false);
        colorFadeAnimation = AnimationProvider.createFadeTransition(foregroundIcon, Constants.FULLY_TRANSPARENT, Constants.NO_TRANSPARENCY, 1, Constants.SLOW_FADE_DURATION);
        colorFadeAnimation.setOnFinished(event -> foregroundIcon.setOpacity(Constants.NO_TRANSPARENCY));
        colorFadeAnimation.play();
    }

    /**
     * Apply and play a FadeTransition on the icon in the foregroundIcon.
     * This Transition modifies the opacity of the foregroundIcon from opaque to fully transparent.
     */
    public void fadeForegroundFromOpaqueToTransparent() {
        setAnimation(false);
        colorFadeAnimation = AnimationProvider.createFadeTransition(foregroundIcon, Constants.NO_TRANSPARENCY, Constants.FULLY_TRANSPARENT, 1, Constants.SLOW_FADE_DURATION);
        colorFadeAnimation.setOnFinished(event -> foregroundIcon.setOpacity(Constants.FULLY_TRANSPARENT));
        colorFadeAnimation.play();
    }

    /**
     * Method starts or stops the fade animation of the foreground icon color.
     *
     * @param enabled if true the animation will be started and if false the animation is stopped if currently running.
     */
    public void setAnimation(final boolean enabled) {
        if (enabled) {
            colorFadeAnimation = AnimationProvider.createFadeTransition(foregroundIcon, Constants.FULLY_TRANSPARENT, Constants.NO_TRANSPARENCY, Animation.INDEFINITE, Constants.SLOW_FADE_DURATION);
            colorFadeAnimation.setOnFinished(event -> foregroundIcon.setOpacity(Constants.FULLY_TRANSPARENT));
            colorFadeAnimation.play();
        } else {
            if (colorFadeAnimation != null) {
                colorFadeAnimation.stop();
            }
        }
    }

    /**
     * Allows to set a new color to the backgroundIcon icon and setAnimation its change (by a FadeTransition).
     *
     * @param color the color for the backgroundIcon icon to be set
     */
    public void setBackgroundIconColorAnimated(final Color color) {
        setAnimatedColor(backgroundIcon, color);
    }

    /**
     * Allows to set a new color to the foregroundIcon icon and setAnimation its change (by a FadeTransition).
     *
     * @param color the color for the foregroundIcon icon to be set
     */
    public void setForegroundIconColorAnimated(final Color color) {
        setAnimatedColor(foregroundIcon, color);
    }

    /**
     * Method sets the icon color only.
     *
     * @param color the color for the foregroundIcon icon to be set
     */
    public void setForegroundIconColor(final Color color) {
        foregroundIcon.setFill(color);
        foregroundIcon.setStroke(Color.TRANSPARENT);
        foregroundIcon.setStrokeWidth(0);
    }

    /**
     * Reset the current foreground icon color to default.
     */
    public void setForegroundIconColorDefault() {
        setAnimation(false);
        foregroundIcon.getStyleClass().clear();
        foregroundIcon.getStyleClass().add(Constants.ICONS_CSS_STRING);
    }

    /**
     * Reset the current background icon color to default.
     */
    public void setBackgroundIconColorDefaultInverted() {
        setAnimation(false);
        backgroundIcon.getStyleClass().clear();
        backgroundIcon.getStyleClass().add(Constants.ICONS_CSS_STRING);
        setBackgroundIconColor(getBackgroundIconColor().invert());
    }

    /**
     * Reset the current foreground icon color to default.
     */
    public void setForegroundIconColorDefaultInverted() {
        setAnimation(false);
        foregroundIcon.getStyleClass().clear();
        foregroundIcon.getStyleClass().add(Constants.ICONS_CSS_STRING);
        setForegroundIconColor(getForegroundIconColor().invert());
    }

    /**
     * Reset the current background icon color to default.
     */
    public void setBackgroundIconColorDefault() {
        setAnimation(false);
        backgroundIcon.getStyleClass().clear();
        backgroundIcon.getStyleClass().add(Constants.ICONS_CSS_STRING);
    }

    /**
     * Method sets the icon color only.
     *
     * @param color the color for the backgroundIcon icon to be set
     */
    public void setBackgroundIconColor(final Color color) {
        backgroundIcon.setFill(color);
        backgroundIcon.setStroke(Color.TRANSPARENT);
        backgroundIcon.setStrokeWidth(0);
    }

    /**
     * Method sets the icon color and a stroke with a given color and width.
     *
     * @param color color for the foregroundIcon icon to be set
     * @param outline color for the stroke
     * @param width width of the stroke
     */
    public void setForegroundIconColor(final Color color, final Color outline, final double width) {
        foregroundIcon.setFill(color);
        foregroundIcon.setStroke(outline);
        foregroundIcon.setStrokeWidth(width);
    }

    private void setAnimatedColor(final Text node, final Color color) {
        setAnimation(false);
        if (node.equals(backgroundIcon)) {
            backgroundFadeIcon.setFill(color);
            colorFadeAnimation = AnimationProvider.createFadeTransition(backgroundFadeIcon, Constants.FULLY_TRANSPARENT, Constants.NO_TRANSPARENCY, 1, Constants.LIGHT_CHANGE_FADE_DURATION);
            colorFadeAnimation.setOnFinished(event -> {
                backgroundIcon.setFill(color);
                backgroundFadeIcon.setOpacity(Constants.FULLY_TRANSPARENT);
            });
            colorFadeAnimation.play();
        } else if (node.equals(foregroundIcon)) {
            foregroundFadeIcon.setFill(color);
            colorFadeAnimation = AnimationProvider.createFadeTransition(foregroundFadeIcon, Constants.FULLY_TRANSPARENT, Constants.NO_TRANSPARENCY, 1, Constants.LIGHT_CHANGE_FADE_DURATION);
            colorFadeAnimation.setOnFinished(event -> {
                foregroundIcon.setFill(color);
                foregroundFadeIcon.setOpacity(Constants.FULLY_TRANSPARENT);
            });
            colorFadeAnimation.play();
        }

    }

    /**
     * Changes the backgroundIcon icon.
     *
     * @param icon the icon which should be set as the new icon.
     */
    public void setBackgroundIcon(final GlyphIcons icon) {
        setBackgroundIcon(icon, null);
    }

    /**
     * Changes the backgroundIcon icon.
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

        // replace old icons with new ones.
        getChildren().replaceAll((final Node node) -> {
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
     * Changes the foregroundIcon icon.
     *
     * @param icon the icon which should be set as the new icon
     */
    public void setForegroundIcon(final GlyphIcons icon) {
        setForegroundIcon(icon, null);
    }

    /**
     * Changes the foregroundIcon icon.
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
        getChildren().replaceAll((final Node node) -> {
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
     * Getter for the size of the icons.
     *
     * @return size as a double value
     */
    public double getSize() {
        return size;
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
        return (Color) backgroundIcon.getFill();
    }
}
