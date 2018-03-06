package org.openbase.bco.bcozy.view.pane.unit;

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
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.scene.paint.Color;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.unit.dal.LightDataType.LightData;
import java.util.concurrent.Future;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.dal.remote.unit.LightRemote;
import rst.domotic.state.PowerStateType.PowerState;

/**
 * Created by agatting on 03.12.15.
 */
public class LightPane extends AbstractUnitPane<LightRemote, LightData> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LightPane.class);

    /**
     * Constructor for the Light Pane.
     *
     */
    public LightPane() {
        super(LightRemote.class, true);
        this.setIcon(MaterialDesignIcon.LIGHTBULB_OUTLINE, MaterialDesignIcon.LIGHTBULB);
    }

    @Override
    public void updateDynamicContent() {
        super.updateDynamicContent();

        // detect power state
        PowerState.State state = PowerState.State.UNKNOWN;
        try {
            state = getUnitRemote().getData().getPowerState().getValue();
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER, LogLevel.DEBUG);
        }

        switch (state) {
            case OFF:
                getIcon().setBackgroundIconColor(Constants.LIGHTBULB_OFF_COLOR);
                setPrimaryActivationWithoutNotification(Boolean.FALSE);
                break;
            case ON:
                getIcon().setBackgroundIconColor(Color.CORNSILK);
                setPrimaryActivationWithoutNotification(Boolean.TRUE);
                break;
            default:
                break;
        }
    }

    @Override
    protected Future applyPrimaryActivationUpdate(final boolean activation) throws CouldNotPerformException {
        return (activation) ? getUnitRemote().setPowerState(PowerState.State.ON) : getUnitRemote().setPowerState(PowerState.State.OFF);
    }
}
