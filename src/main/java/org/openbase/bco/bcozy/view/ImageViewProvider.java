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

import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.NotAvailableException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hoestreich on 12/14/15.
 *
 * @author hoestreich
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 *
 */
public final class ImageViewProvider {

    private static List<ImageView> imageViews = new ArrayList<>();

    private static ColorAdjust colorEffect = new ColorAdjust(1.0, 1, -1, 0.0);

    private ImageViewProvider() {
    }

    /**
     * Method to create an Image view with quadratic measures.
     *
     * @param size the width and height (same value used for both)
     * @param imageURI the path to the image applied to the image view
     * @return a new ImageView Instance initialized with the provided parameters
     */
    public static ImageView createImageView(final double size, final String imageURI) throws CouldNotPerformException {
        return createImageView(size, size, imageURI);
    }

    /**
     * Method to create an Image view with quadratic measures.
     *
     * @param width the width for the image
     * @param height the height for the image
     * @param imageURI the path to the image applied to the image view
     * @return a new ImageView Instance initialized with the provided parameters
     */
    public static ImageView createImageView(final double width, final double height, final String imageURI) throws CouldNotPerformException {
        final Image icon = new Image(ImageViewProvider.class.getResourceAsStream(imageURI));
        final ImageView imageView = new ImageView(icon);
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(height);
        imageView.setFitWidth(width);
        return registerImageView(imageView);
    }

    /**
     * Method to create an Image view with quadratic measures.
     *
     * @param width the width for the image
     * @param height the height for the image
     * @param imageURI the path to the image applied to the image view
     * @return a new ImageView Instance initialized with the provided parameters
     */
    public static List<ImageView> createImageView(final double width, final double height, final String... imageURI) throws CouldNotPerformException {
        final List<ImageView> imageViewList = new ArrayList<>(imageURI.length);
        for (String uri : imageURI) {
            imageViewList.add(createImageView(width, height, uri));
        }
        return imageViewList;
    }

    public static ImageView registerImageView(final ImageView imageView) throws CouldNotPerformException {

        if(imageView == null) {
            throw new NotAvailableException("imageView");
        }

        imageView.setSmooth(true);
        imageView.setEffect(colorEffect);
        imageViews.add(imageView);
        return imageView;
    }

    /**
     * Colorizes all icons created by this provider to white.
     */
    public static void colorizeIconsToWhite() {
        colorizeIcons(1, 1, 1, 0.0);
    }

    /**
     * Colorizes all icons created by this provider to black.
     */
    public static void colorizeIconsToBlack() {
        colorizeIcons(1, 1, -1, 0);
    }

    /**
     * Colorizes all icons created by this provider to a choosen color (defined by the parameters).
     *
     * @param hue the hue of the color to be set between -1 and 1
     * @param saturation the saturation of the color to be set between -1 and 1
     * @param brightness the brightness of the color to be set between -1 and 1
     * @param contrast the contrast of the color to be set between -1 and 1
     */
    public static void colorizeIcons(final double hue, final double saturation, final double brightness, final double contrast) {

        colorEffect = new ColorAdjust(hue, saturation, brightness, contrast);

        for (final ImageView imageView : imageViews) {
            imageView.setEffect(colorEffect);
            imageView.setSmooth(true);
        }
    }

    public static ColorAdjust getColorEffect() {
        return colorEffect;
    }
}
