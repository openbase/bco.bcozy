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
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class UnitButton extends Pane {
    
    protected final Logger LOGGER = LoggerFactory.getLogger(UnitButton.class);

    UnitRemote<? extends GeneratedMessage> unitRemote;

    public UnitButton(UnitRemote<? extends GeneratedMessage> u) {
        try {                                 
            AbstractUnitPane content;
            content = UnitPaneFactoryImpl.getInstance().newInitializedInstance(u.getConfig());
            content.setDisplayMode(DisplayMode.ICON_ONLY); 
            unitRemote = content.getUnitRemote();
            this.getChildren().add(content);
        } catch (CouldNotPerformException | InterruptedException ex) {
            ExceptionPrinter.printHistory("Could not create UnitButton for unit " + this, ex, LOGGER);
        }
    }
    
    public UnitRemote<? extends GeneratedMessage> getUnitRemote() {
        return this.unitRemote;
    }
    
    public String getLocationId() {
        try {
            return this.unitRemote.getConfig().getPlacementConfig().getLocationId();
        } catch (NotAvailableException ex) {
            ExceptionPrinter.printHistory("Could not retrieve locationId for unit " + this, ex, LOGGER);
        }
        return null;
    }
}
