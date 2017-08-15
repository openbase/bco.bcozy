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
package org.openbase.bco.bcozy.view.location;

import com.google.protobuf.GeneratedMessage;
import javafx.scene.layout.Pane;
import org.openbase.bco.bcozy.view.generic.WidgetPane.DisplayMode;
import org.openbase.bco.bcozy.view.pane.unit.AbstractUnitPane;
import org.openbase.bco.bcozy.view.pane.unit.UnitPaneFactoryImpl;
import org.openbase.bco.dal.lib.layer.unit.UnitRemote;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.NotAvailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Button that contains an AbstractUnitPane in a clipped form so the unit can be controlled from within the location plan.
 */
public class UnitButton extends Pane {

    /**
     * Application logger.
     */
    protected final Logger LOGGER = LoggerFactory.getLogger(UnitButton.class);

    private UnitRemote<? extends GeneratedMessage> unitRemote;

    /**
     * Constructor for UnitButton. Creates the content with the help of the UnitPaneFactory dynamically,
     * according to the given unitRemote.
     * @param unitRemote 
     * @throws java.lang.InterruptedException
     * @throws org.openbase.jul.exception.CouldNotPerformException
     */
    public UnitButton(final UnitRemote<? extends GeneratedMessage> unitRemote) throws InterruptedException, CouldNotPerformException {
        try {
            AbstractUnitPane content;
            content = UnitPaneFactoryImpl.getInstance().newInitializedInstance(unitRemote.getConfig());
            content.setDisplayMode(DisplayMode.ICON_ONLY);
            this.unitRemote = content.getUnitRemote();
            this.getChildren().add(content);
        } catch (CouldNotPerformException ex) {
            throw new CouldNotPerformException("Could not create UnitButton for unit", ex);
        }
    }

    /**
     * Returns the UnitRemote for the unit controlled with this button.
     * @return Underlying UnitRemote object
     */
    public UnitRemote<? extends GeneratedMessage> getUnitRemote() {
        return this.unitRemote;
    }

    /**
     * Convenience method to get the location the underlying unit belongs to.
     * @return LocationId of the unit's location
     * @throws CouldNotPerformException 
     */
    public String getLocationId() throws CouldNotPerformException {
        try {
            return this.unitRemote.getConfig().getPlacementConfig().getLocationId();
        } catch (NotAvailableException ex) {
            throw new CouldNotPerformException("Could not retrieve locationId for unit", ex);
        }
    }
}
