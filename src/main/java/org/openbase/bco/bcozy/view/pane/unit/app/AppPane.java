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
package org.openbase.bco.bcozy.view.pane.unit.app;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import java.util.concurrent.Future;
import javafx.scene.paint.Color;
import org.openbase.bco.bcozy.view.pane.unit.AbstractUnitPane;
import org.openbase.bco.dal.remote.unit.app.AppRemote;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import rst.domotic.state.ActivationStateType.ActivationState.State;
import rst.domotic.unit.app.AppDataType.AppData;

/**
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class AppPane extends AbstractUnitPane<AppRemote, AppData> {

    /**
     * Constructor for the AgentPane.
     *
     */
    public AppPane() {
        super(AppRemote.class, true);
        getIcon().setForegroundIcon(MaterialDesignIcon.POWER);
    }

    @Override
    public void updateDynamicContent() {
        super.updateDynamicContent();

        State state = State.UNKNOWN;

        try {
            state = getUnitRemote().getData().getActivationState().getValue();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.DEBUG);
        }

        switch (state) {
            case ACTIVE:
                getIcon().setForegroundIconColor(Color.GREEN);
                primaryActivationProperty().setValue(Boolean.TRUE);
                break;
            case DEACTIVE:
                getIcon().setForegroundIconColor(Color.BLACK);
                primaryActivationProperty().setValue(Boolean.FALSE);
            default:
                break;
        }
    }

    @Override
    protected Future applyPrimaryActivationUpdate(final boolean activation) throws CouldNotPerformException {
        return (activation) ? getUnitRemote().setActivationState(State.ACTIVE) : getUnitRemote().setActivationState(State.DEACTIVE);
    }
}
