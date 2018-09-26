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

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.animation.Animation;
import javafx.scene.paint.Color;
import org.openbase.bco.dal.remote.layer.unit.TamperDetectorRemote;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import rst.domotic.state.TamperStateType.TamperState.State;
import rst.domotic.unit.dal.TamperDetectorDataType.TamperDetectorData;

/**
 * Created by agatting on 12.04.16.
 */
public class TamperDetectorPane extends AbstractUnitPane<TamperDetectorRemote, TamperDetectorData> {

    /**
     * Constructor for the TamperPane.
     *
     */
    public TamperDetectorPane() {
        super(TamperDetectorRemote.class, false);
        getIcon().setForegroundIcon(MaterialDesignIcon.CHECKBOX_MARKED_CIRCLE_OUTLINE);
    }

    @Override
    public void updateDynamicContent() {
        super.updateDynamicContent();

        State state = State.UNKNOWN;

        try {
            state = getUnitRemote().getData().getTamperState().getValue();
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER, LogLevel.DEBUG);
        }

        switch (state) {
            case NO_TAMPER:
                getIcon().setForegroundIcon(MaterialDesignIcon.CHECKBOX_MARKED_CIRCLE_OUTLINE);
                setInfoText("noTamper");
                break;
            case TAMPER:
                getIcon().setForegroundIcon(MaterialDesignIcon.ALERT_CIRCLE, Color.RED);
                getIcon().startForegroundIconColorFadeAnimation(Animation.INDEFINITE);
                setInfoText("tamper");
                break;
            default:
                setInfoText("unknown");
                break;
        }
    }
}
