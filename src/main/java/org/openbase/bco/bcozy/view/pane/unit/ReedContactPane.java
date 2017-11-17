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
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import javafx.scene.paint.Color;
import org.openbase.bco.dal.remote.unit.ReedContactRemote;
import org.openbase.jul.exception.CouldNotPerformException;
import rst.domotic.unit.dal.ReedContactDataType.ReedContactData;

/**
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class ReedContactPane extends AbstractUnitPane<ReedContactRemote, ReedContactData> {

    public ReedContactPane() {
        super(ReedContactRemote.class, false);
        this.setIcon(MaterialDesignIcon.HELP_CIRCLE, MaterialDesignIcon.CHECKBOX_BLANK_CIRCLE);
    }

    @Override
    public void updateDynamicContent() {
        super.updateDynamicContent();
        try {
            switch (getUnitRemote().getData().getContactState().getValue()) {
                case CLOSED:
                    setIcon(MaterialIcon.RADIO_BUTTON_CHECKED, MaterialIcon.LENS);
                    getIcon().setForegroundIconColor(Color.BLACK);
                    getIcon().setBackgroundIconColor(Color.WHITE);
                    break;
                case OPEN:
                    setIcon(MaterialIcon.RADIO_BUTTON_UNCHECKED, MaterialIcon.LENS);
                    getIcon().setForegroundIconColor(Color.BLACK);
                    getIcon().setBackgroundIconColor(Color.WHITE);
                    break;
                case UNKNOWN:
                default:
                    setIcon(MaterialDesignIcon.HELP_CIRCLE, MaterialDesignIcon.CHECKBOX_BLANK_CIRCLE);
                    getIcon().setForegroundIconColor(Color.ORANGE);
                    getIcon().setBackgroundIconColor(Color.BLACK);
                    break;
            }
        } catch (CouldNotPerformException ex) {
            setIcon(MaterialDesignIcon.HELP_CIRCLE, MaterialDesignIcon.CHECKBOX_BLANK_CIRCLE);
            getIcon().setForegroundIconColor(Color.RED);
            getIcon().setBackgroundIconColor(Color.BLACK);
        }
    }
}
