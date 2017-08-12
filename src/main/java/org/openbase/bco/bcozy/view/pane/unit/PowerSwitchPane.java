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
import java.util.concurrent.Future;
import javafx.scene.paint.Color;
import org.openbase.bco.dal.remote.unit.PowerSwitchRemote;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import rst.domotic.state.PowerStateType.PowerState;
import rst.domotic.unit.dal.PowerSwitchDataType.PowerSwitchData;

/**
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class PowerSwitchPane extends AbstractUnitPane<PowerSwitchRemote, PowerSwitchData> {

    /**
     * Constructor for the PowerSwitchPane.
     *
     */
    public PowerSwitchPane() {
        super(PowerSwitchRemote.class, true);
        getIcon().setForegroundIcon(MaterialDesignIcon.POWER);
    }

    @Override
    public void updateDynamicContent() {
        super.updateDynamicContent();

        PowerState.State state = PowerState.State.UNKNOWN;

        try {
            state = getUnitRemote().getData().getPowerState().getValue();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.DEBUG);
        }

        switch (state) {
            case ON:
                getIcon().setForegroundIconColor(Color.GREEN);
                setPrimaryActivationWithoutNotification(Boolean.TRUE);
                break;
            case OFF:
                getIcon().setForegroundIconColor(Color.BLACK);
                setPrimaryActivationWithoutNotification(Boolean.FALSE);
            default:
                break;
        }
    }

    @Override
    protected Future applyPrimaryActivationUpdate(final boolean activation) throws CouldNotPerformException {
        return (activation) ? getUnitRemote().setPowerState(PowerState.State.ON) : getUnitRemote().setPowerState(PowerState.State.OFF);
    }
}
