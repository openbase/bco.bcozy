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
import javafx.scene.image.ImageView;

/**
 * Created by hoestreich on 12/14/15.
 */
public final class ImageViewProvider {

    private ImageViewProvider() {

    }

    /**
     * Method to create an Image view with quadratic measures.
     * @param imagePath the path to the image applied to the image view
     * @param size the width and height (same value used for both)
     * @return a new ImageView Instance initialized with the provided parameters
     */
    public static ImageView createImageView(final String imagePath, final double size) {
        return init(imagePath, size, size);
    }

    /**
     * Method to create an Image view with quadratic measures.
     * @param imagePath the path to the image applied to the image view
     * @param width the width for the image
     * @param height the height for the image
     * @return a new ImageView Instance initialized with the provided parameters
     */
    public static ImageView createImageView(final String imagePath, final double width, final double height) {
        return init(imagePath, width, height);
    }

    private static ImageView init(final String imagePath, final double width, final double height) {
        final Image icon = new Image(ImageViewProvider.class.getResourceAsStream(imagePath));
        final ImageView imageView = new ImageView(icon);
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(height);
        imageView.setFitWidth(width);
        imageView.setSmooth(true);
        return imageView;

    }
}
