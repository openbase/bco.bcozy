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
package org.openbase.bco.bcozy.view.pane.unit.agent;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import java.util.concurrent.Future;
import javafx.beans.value.ChangeListener;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.generic.EmphasisAdjustment;
import org.openbase.bco.bcozy.view.pane.unit.AbstractUnitPane;
import org.openbase.bco.dal.remote.unit.agent.AgentRemote;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.openbase.jul.schedule.RecurrenceEventFilter;
import rst.domotic.state.ActivationStateType.ActivationState.State;
import rst.domotic.state.EmphasisStateType.EmphasisState;
import rst.domotic.unit.agent.AgentDataType.AgentData;

/**
 * Created by agatting on 12.04.16.
 */
public class AgentPane extends AbstractUnitPane<AgentRemote, AgentData> {

    private EmphasisAdjustment emphasisAdjustment;

    /**
     * Constructor for the AgentPane.
     *
     */
    public AgentPane() {
        super(AgentRemote.class, true);
        getIcon().setForegroundIcon(MaterialDesignIcon.POWER);
    }

    private final RecurrenceEventFilter<EmphasisState> recurrenceEventFilterComfort = new RecurrenceEventFilter<EmphasisState>(Constants.RECURRENCE_EVENT_FILTER_MILLI_TIMEOUT) {
        @Override
        public void relay() {
            try {
                getUnitRemote().setEmphasisState(getLatestValue());
            } catch (CouldNotPerformException ex) {
                ExceptionPrinter.printHistory("Could not send color update!", ex, LOGGER);
            }
        }
    };

    @Override
    protected void initBodyContent(Pane bodyPane) throws CouldNotPerformException {
        emphasisAdjustment = new EmphasisAdjustment();
        emphasisAdjustment.initContent();

        ChangeListener changeListener = (ChangeListener) (observable, oldValue, newValue) -> {
            if (isHover()) {
                try {
                    recurrenceEventFilterComfort.trigger(emphasisAdjustment.getCurrentEmphasisState());
                } catch (CouldNotPerformException ex) {
                    ExceptionPrinter.printHistory("Could not trigger emphasis change!", ex, LOGGER);
                }
            }
        };
        emphasisAdjustment.getComfortProperty().addListener(changeListener);
        emphasisAdjustment.getEnergyProperty().addListener(changeListener);
        emphasisAdjustment.getSecurityProperty().addListener(changeListener);

        bodyPane.getChildren().add(emphasisAdjustment);
    }

    @Override
    public void updateDynamicContent() {
        super.updateDynamicContent();

        State state = State.UNKNOWN;

        try {
            state = getUnitRemote().getData().getActivationState().getValue();
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER, LogLevel.DEBUG);
        }

        switch (state) {
            case ACTIVE:
                getIcon().setForegroundIconColor(Color.GREEN);
                setPrimaryActivationWithoutNotification(Boolean.TRUE);
                break;
            case DEACTIVE:
                getIcon().setForegroundIconColor(Color.BLACK);
                setPrimaryActivationWithoutNotification(Boolean.FALSE);
            default:
                break;
        }

        if (emphasisAdjustment != null && expansionProperty().get()) {
            try {
                emphasisAdjustment.setSelectedEmphasis(getData().getEmphasisState());
            } catch (NotAvailableException ex) {
                ExceptionPrinter.printHistory(ex, LOGGER, LogLevel.DEBUG);
            }
        }
    }

    @Override
    protected Future applyPrimaryActivationUpdate(final boolean activation) throws CouldNotPerformException {
        return (activation) ? getUnitRemote().setActivationState(State.ACTIVE) : getUnitRemote().setActivationState(State.DEACTIVE);
    }
}
