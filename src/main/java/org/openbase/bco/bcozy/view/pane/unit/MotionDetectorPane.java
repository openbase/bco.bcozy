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
package org.openbase.bco.bcozy.view.pane.unit;

import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.scene.paint.Color;
import org.openbase.bco.dal.remote.layer.unit.MotionDetectorRemote;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.visual.javafx.JFXConstants;
import rst.domotic.unit.dal.MotionDetectorDataType.MotionDetectorData;

/**
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class MotionDetectorPane extends AbstractUnitPane<MotionDetectorRemote, MotionDetectorData> {

    public MotionDetectorPane() {
        super(MotionDetectorRemote.class, false);
        this.setIcon(MaterialIcon.BLUR_ON, MaterialIcon.LENS);
    }

    @Override
    public void updateDynamicContent() {
        super.updateDynamicContent();
        try {
            switch (getUnitRemote().getData().getMotionState().getValue()) {
                case MOTION:
                    setIcon(MaterialIcon.BLUR_ON, MaterialIcon.LENS);
                    getIcon().setForegroundIconColor(Color.BLACK);
                    getIcon().setBackgroundIconColor(Color.WHITE);
                    getIcon().startForegroundIconRotateAnimation(0, 360, Animation.INDEFINITE, JFXConstants.DURATION_ROTATE_FAST, Interpolator.LINEAR, false);
                    break;
                case NO_MOTION:
                    setIcon(MaterialIcon.BLUR_ON, MaterialIcon.LENS);
                    getIcon().setForegroundIconColor(Color.BLACK);
                    getIcon().setBackgroundIconColor(Color.WHITE);
                    getIcon().stopIconRotateAnimation();
                    break;
                case UNKNOWN:
                default:
                    setIcon(MaterialIcon.BLUR_ON, MaterialIcon.LENS);
                    getIcon().setForegroundIconColor(Color.ORANGE);
                    getIcon().setBackgroundIconColor(Color.BLACK);
                    getIcon().stopIconRotateAnimation();
                    break;
            }
        } catch (CouldNotPerformException ex) {
            setIcon(MaterialIcon.BLUR_ON, MaterialIcon.LENS);
            getIcon().setForegroundIconColor(Color.RED);
            getIcon().setBackgroundIconColor(Color.BLACK);
            getIcon().stopIconRotateAnimation();
        }
    }
}
