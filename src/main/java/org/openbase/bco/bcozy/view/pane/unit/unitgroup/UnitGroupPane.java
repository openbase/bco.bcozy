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
package org.openbase.bco.bcozy.view.pane.unit.unitgroup;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import java.util.concurrent.Future;
import javafx.scene.paint.Color;
import org.openbase.bco.bcozy.view.pane.unit.AbstractUnitPane;
import org.openbase.bco.dal.lib.state.States.Activation;
import org.openbase.bco.dal.remote.layer.unit.unitgroup.UnitGroupRemote;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.openbase.jul.schedule.FutureProcessor;
import org.openbase.type.domotic.action.ActionDescriptionType.ActionDescription;
import org.openbase.type.domotic.state.PowerStateType.PowerState;
import org.openbase.type.domotic.unit.unitgroup.UnitGroupDataType.UnitGroupData;

/**
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class UnitGroupPane extends AbstractUnitPane<UnitGroupRemote, UnitGroupData> {

    /**
     * Constructor for the PowerSwitchPane.
     *
     */
    public UnitGroupPane() {
        super(UnitGroupRemote.class, true);
        getIcon().setForegroundIcon(MaterialDesignIcon.POWER);
    }

    @Override
    public void updateDynamicContent() {
        super.updateDynamicContent();

        PowerState.State state = PowerState.State.UNKNOWN;

        try {
            state = getUnitRemote().getData().getPowerState().getValue();
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER, LogLevel.DEBUG);
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
    protected Future<ActionDescription> applyPrimaryActivationUpdate(final boolean activation) {
        try {
            return (activation) ? getUnitRemote().setPowerState(PowerState.State.ON) : getUnitRemote().setPowerState(PowerState.State.OFF);
        } catch (NotAvailableException ex) {
            return FutureProcessor.canceledFuture(ActionDescription.class, ex);
        }
    }
}
