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
package org.openbase.bco.bcozy.view.pane.unit.scene;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import java.util.concurrent.Future;
import javafx.scene.paint.Color;
import org.openbase.bco.bcozy.view.pane.unit.AbstractUnitPane;
import org.openbase.bco.dal.remote.layer.unit.scene.SceneRemote;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import rst.domotic.state.ActivationStateType.ActivationState.State;
import rst.domotic.unit.scene.SceneDataType.SceneData;

/**
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class ScenePane extends AbstractUnitPane<SceneRemote, SceneData> {

    /**
     * Constructor for the ScenePane.
     */
    public ScenePane() {
        super(SceneRemote.class, true);
        getIcon().setForegroundIcon(MaterialDesignIcon.VECTOR_COMBINE);
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
    }

    @Override
    protected Future applyPrimaryActivationUpdate(final boolean activation) throws CouldNotPerformException {
        return (activation) ? getUnitRemote().setActivationState(State.ACTIVE) : getUnitRemote().setActivationState(State.DEACTIVE);
    }
}
