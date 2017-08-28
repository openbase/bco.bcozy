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
 * along with org.openbase.bco.bcozy. If not, see
 * <http://www.gnu.org/licenses/>.
 * ==================================================================
 */
package org.openbase.bco.bcozy.view;

import com.google.protobuf.GeneratedMessage;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import org.openbase.bco.bcozy.view.location.UnitButton;
import org.openbase.bco.dal.lib.layer.unit.UnitRemote;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;

/**
 * Pane for the maintenance layer of the room plan that includes buttons for the following units:
 * batteries, tamper detectors, temperature sensors, smoke detectors.
 *
 * @author lili
 */
public class SimpleUnitSymbolsPane extends Pane {

    /**
     * Application logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleUnitSymbolsPane.class);
    // unitId, unit button
    private final Map<String, UnitButton> unitsMap;

    /**
     * Constructor for the UnitSymbolsPane.
     */
    public SimpleUnitSymbolsPane() {
        super();
        unitsMap = new HashMap<>();
    }

    /**
     * Adds a new button for the control of a unit 
     *
     * @param unitRemoteObject Unit to be controlled by the button.
     * @param position Position of the button on the map, should be the center of the location unit pane.
     * @param locationId Id of the location the unit belongs to.
     * @throws CouldNotPerformException
     * @throws InterruptedException
     */
    public void addUnit(final UnitRemote<? extends GeneratedMessage> unitRemoteObject, final Point2D position, final String locationId) throws CouldNotPerformException, InterruptedException {
        UnitButton newButton;
        try {
            newButton = new UnitButton(unitRemoteObject);

            newButton.setTranslateX(position.getY());
            newButton.setTranslateY(position.getX());
            
            unitsMap.put(locationId, newButton);

        } catch (NotAvailableException ex) {
           //throw new CouldNotPerformException("Could not create unit button for unit " + this, ex);
            ExceptionPrinter.printHistory("Type not available as pane: " + unitRemoteObject.getConfigClass().getName(), ex, LOGGER);
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory("Type not available as pane: " + unitRemoteObject.getConfigClass().getName(), ex, LOGGER);
            System.out.println("Type not available: " + unitRemoteObject.getConfigClass().getName());
        }
    }

    
    /**
     * Clears the UnitSymbolsPane to prepare the update.
     */
    public void clearUnits() {
        unitsMap.forEach((unitId, button)
            -> {
            this.getChildren().remove(button);
        });
        unitsMap.clear();
    }

    /**
     * Draws all location buttons except for the selected location, draws all unit buttons
     * and grouped buttons for the selected location.
     */
    public void updateUnitsPane() {
        this.getChildren().clear();

        unitsMap.forEach((unitId, button)
            -> {
            
                this.getChildren().add(button);            
        });
    }

}
