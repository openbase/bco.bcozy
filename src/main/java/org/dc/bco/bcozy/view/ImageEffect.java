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

import javafx.scene.Group;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

/**
 * Created by agatting on 25.11.15.
 */
public class ImageEffect extends Group {

    /**
     * Constructor for ImageEffect.
     * @param bottom mask image.
     * @param top structured image.
     * @param color color effect.
     */
    public ImageEffect(final Image bottom, final Image top, final Color color) {

        final Group group = effectGroup(bottom, top, color);

        this.getChildren().add(group);
    }

    /**
     * Method creates a colored image.
     * @param bottom is the image mask.
     * @param top is the image with structure.
     * @param color is the color to paint the image.
     * @return imageEffect of type Group.
     */
    private Group effectGroup(final Image bottom, final Image top, final Color color) {

        final ImageView bottomView = new ImageView(bottom);
        final ImageView topView = new ImageView(top);

        bottomView.setClip(new ImageView(bottom));
        bottomView.setScaleX(Constants.SMALL_ICON_SCALE_FACTOR);
        bottomView.setScaleY(Constants.SMALL_ICON_SCALE_FACTOR);
        bottomView.setSmooth(true);

        topView.setClip(new ImageView(top));
        topView.setScaleX(Constants.SMALL_ICON_SCALE_FACTOR);
        topView.setScaleY(Constants.SMALL_ICON_SCALE_FACTOR);
        topView.setSmooth(true);

        //color property
        final ColorAdjust monochrome = new ColorAdjust();
        monochrome.setSaturation(-1.0);

        final Blend blushEffect = new Blend(BlendMode.ADD, monochrome, new ColorInput(0, 0,
                bottomView.getImage().getWidth(), bottomView.getImage().getHeight(), color));

        bottomView.setEffect(blushEffect);
        //bottomView.setCache(true);
        //bottomView.setCacheHint(CacheHint.SPEED);

        final Group imageEffect = new Group(bottomView, topView);

        return imageEffect;
    }
}
