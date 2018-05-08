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
package org.openbase.bco.bcozy.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.openbase.bco.bcozy.view.ForegroundPane;
import org.openbase.bco.bcozy.view.pane.unit.TitledUnitPaneContainer;
import org.openbase.bco.bcozy.view.location.LocationPane;
import org.openbase.bco.dal.lib.layer.unit.UnitRemote;
import org.openbase.bco.dal.remote.unit.Units;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.unit.UnitConfigType.UnitConfig;
import rst.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;

/**
 * @author tmichalksi
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class ContextMenuController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContextMenuController.class);

    private final ForegroundPane foregroundPane;
    private final Map<String, TitledUnitPaneContainer> titledPaneMap;

    /**
     * Constructor for the ContextMenuController.
     *
     * @param foregroundPane foregroundPane
     * @param backgroundPane backgroundPane
     */
    public ContextMenuController(final ForegroundPane foregroundPane, final LocationPane backgroundPane) {
        this.foregroundPane = foregroundPane;
        this.titledPaneMap = new HashMap<>();


        backgroundPane.addSelectedLocationIdListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String location) {
                if (Registries.isDataAvailable()) {
                    try {
                        setContextMenuUnitPanes(location);
                    } catch (CouldNotPerformException | InterruptedException ex) {
                        ExceptionPrinter.printHistory("Units for selected location[" + location + "] could not be loaded.", ex, LOGGER, LogLevel.ERROR);
                    }
                } else {
                    LOGGER.warn("Registries not ready yet! Thus no Context Menu will be loaded!");
                }
            }
        });
    }
    
    //idea: addselected unit id listener

    /**
     * Takes a locationId and creates new TitledPanes for all UnitTypes.
     *
     * @param locationID locationID
     * @throws CouldNotPerformException CouldNotPerformException
     * @throws java.lang.InterruptedException
     */
    public void setContextMenuUnitPanes(final String locationID) throws CouldNotPerformException, InterruptedException {
        try {
            if ("none".equals(locationID)) {
                throw new CouldNotPerformException("No location is selected.");
            }

            TitledUnitPaneContainer titledPaneContainer;
            if (titledPaneMap.containsKey(locationID)) {
                titledPaneContainer = titledPaneMap.get(locationID);
            } else {
                titledPaneContainer = new TitledUnitPaneContainer();
                fillTitledPaneContainer(titledPaneContainer, locationID);
            }

            foregroundPane.getUnitMenu().setTitledPaneContainer(titledPaneContainer);
        } catch (CouldNotPerformException ex) {
            throw new CouldNotPerformException("Could not set context menu.", ex);
        }
    }

    private void fillTitledPaneContainer(final TitledUnitPaneContainer titledPaneContainer, final String locationID) throws CouldNotPerformException, InterruptedException {
        try {
            titledPaneMap.put(locationID, titledPaneContainer);

            for (final Map.Entry<UnitType, List<UnitRemote>> nextEntry : Units.getUnit(locationID, false, Units.LOCATION).getUnitMap().entrySet()) {
                if (nextEntry.getValue().isEmpty()) {
                    continue;
                }
                
                // filter shadowned units
                switch(nextEntry.getKey()) {
                    case BUTTON:
                    case DEVICE:
                    case UNKNOWN:
                        continue;
                }
                titledPaneContainer.createAndAddNewTitledPane(nextEntry.getKey(), nextEntry.getValue());
            }

            titledPaneContainer.addDummyPane(); //TODO: Find a way to solve this problem properly...
        } catch (CouldNotPerformException ex) {
            throw new CouldNotPerformException("Could not fill titled pane container.", ex);
        }
    }

    /**
     * Clears all stored titledPanes and clears the map afterwards.
     */
    public void clearTitledPaneMap() {
        for (final Map.Entry<String, TitledUnitPaneContainer> nextEntry : this.titledPaneMap.entrySet()) {
            nextEntry.getValue().clearTitledPane();
        }

        this.titledPaneMap.clear();
        this.foregroundPane.getUnitMenu().clearVerticalScrollPane();
    }

    /**
     * Initializes and saves all TitledPanes of all Locations.
     *
     * @throws CouldNotPerformException CouldNotPerformException
     * @throws java.lang.InterruptedException
     */
    public void initTitledPaneMap() throws CouldNotPerformException, InterruptedException {
        try {
            for (final UnitConfig locationUnitConfig : Registries.getLocationRegistry().getLocationConfigs()) {
                final String locationID = locationUnitConfig.getId();

                final TitledUnitPaneContainer titledPaneContainer = new TitledUnitPaneContainer();
                fillTitledPaneContainer(titledPaneContainer, locationID);
            }
        } catch (CouldNotPerformException | NullPointerException ex) {
            ExceptionPrinter.printHistory(new CouldNotPerformException("Could not init initTitledPaneMap!", ex), LOGGER);
        }
    }
}
