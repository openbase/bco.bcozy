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
public class ImageEffect {

    private static final double SCALE = 0.1;
    private ImageView bottomView;
    private ImageView topView;

    /**
     * Method to create an image group with blend effect.
     *
     * @param bottom Bottom image of the icon
     * @param top Top image of the icon
     * @param color Current adjusted color
     *
     * @return imageEffect Image with blend effect of type Group
     */
    public Group imageBlendEffect(final Image bottom, final Image top, final Color color) {

        //SvgImageLoaderFactory.install();

        bottomView = new ImageView(bottom);
        bottomView.setScaleX(SCALE);
        bottomView.setScaleY(SCALE);
        bottomView.setClip(new ImageView(bottom));

        topView = new ImageView(top);
        topView.setScaleX(SCALE);
        topView.setScaleY(SCALE);
        topView.setClip(new ImageView(top));

        //color property
        final ColorAdjust monochrome = new ColorAdjust();
        monochrome.setSaturation(-1.0);

        final Blend blushEffect = new Blend(BlendMode.ADD, monochrome, new ColorInput(0, 0,
                bottomView.getImage().getWidth(), bottomView.getImage().getHeight(), color));
        bottomView.setEffect(blushEffect);

        final Group imageEffect = new Group(bottomView, topView);

        return imageEffect;
    }
}
