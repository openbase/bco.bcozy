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
package org.openbase.bco.bcozy.controller;

import javafx.application.Platform;
import org.openbase.bco.bcozy.view.location.LocationMapPane;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.openbase.jul.extension.type.processing.LabelProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openbase.type.domotic.unit.UnitConfigType.UnitConfig;
import org.openbase.type.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;

import java.util.List;

import org.openbase.bco.registry.remote.Registries;

/**
 * @author julian
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class LocationMapPaneController {

    /**
     * Application logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LocationMapPaneController.class);

    private final LocationMapPane locationMapPane;

    /**
     * The constructor.
     *
     * @param locationMapPane the location pane
     */
    public LocationMapPaneController(final LocationMapPane locationMapPane) {
        this.locationMapPane = locationMapPane;
    }

    /**
     * Establishes the connection with the RemoteRegistry.
     */
    public void init() {
        try {
            Registries.waitForData();
            Registries.getUnitRegistry().addDataObserver((source, data) -> Platform.runLater(() -> {
                try {
                    fetchLocations();
                    fetchConnections();
                    locationMapPane.updateLocationPane();
                } catch (CouldNotPerformException | InterruptedException ex) {
                    ExceptionPrinter.printHistory(ex, LOGGER);
                }
            }));
            updateAndZoomFit();
            locationMapPane.setInitialized(true);
        } catch (Exception ex) {
            ExceptionPrinter.printHistory(ex, LOGGER, LogLevel.ERROR);
        }
    }

    private void fetchLocations() throws CouldNotPerformException, InterruptedException {
        final List<UnitConfig> locationUnitConfigList = Registries.getUnitRegistry().getUnitConfigsByUnitType(UnitType.LOCATION);

        locationMapPane.clearLocations();

        for (final UnitConfig locationUnitConfig : locationUnitConfigList) {
            try {
                //skip locations without a shape    
                if (locationUnitConfig.getPlacementConfig().getShape().getFloorCount() == 0) {
                    continue;
                }
                locationMapPane.addLocation(locationUnitConfig);
            } catch (CouldNotPerformException ex) {
                ExceptionPrinter.printHistory("Error while fetching transformation for location \"" + LabelProcessor.getBestMatch(locationUnitConfig.getLabel(),"?") + "\", locationID: " + locationUnitConfig.getId(),
                        ex, LOGGER, LogLevel.ERROR);
            }
          
        }
    }

    private void fetchConnections() throws CouldNotPerformException, InterruptedException {
        final List<UnitConfig> connectionUnitConfigList = Registries.getUnitRegistry().getUnitConfigsByUnitType(UnitType.CONNECTION);

        locationMapPane.clearConnections();

        //check which connection has a shape
        for (final UnitConfig connectionUnitConfig : connectionUnitConfigList) {
            try {
                //skip connections without a shape
                if (connectionUnitConfig.getPlacementConfig().getShape().getFloorCount() == 0) {
                    continue;
                }
                locationMapPane.addConnection(connectionUnitConfig);
            } catch (CouldNotPerformException ex) {
                ExceptionPrinter.printHistory("Error while fetching transformation for connection \"" + LabelProcessor.getBestMatch(connectionUnitConfig.getLabel(),"?") + "\", connectionID: " + connectionUnitConfig.getId(),
                        ex, LOGGER, LogLevel.ERROR);
            }
        }
    }

    /**
     * Method to trigger a complete update of the locationPane. Will furthermore
     * apply a zoomFit after everything is finished.
     */
    public void updateAndZoomFit() {
        Platform.runLater(() -> {
            try {
                fetchLocations();
                fetchConnections();
                locationMapPane.updateLocationPane();
                locationMapPane.zoomFit();
            } catch (CouldNotPerformException | InterruptedException ex) {
                ExceptionPrinter.printHistory(ex, LOGGER);
            }
        });
    }
}
