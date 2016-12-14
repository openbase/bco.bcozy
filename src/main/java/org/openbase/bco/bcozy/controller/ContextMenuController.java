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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import org.openbase.bco.bcozy.view.ForegroundPane;
import org.openbase.bco.bcozy.view.unitpanes.TitledPaneContainer;
import org.openbase.bco.bcozy.view.location.LocationPane;
import org.openbase.bco.dal.remote.unit.UnitRemote;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.unit.UnitConfigType.UnitConfig;
import rst.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;

/**
 * Created by timo on 03.12.15.
 */
public class ContextMenuController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContextMenuController.class);

    private final ForegroundPane foregroundPane;
    private final RemotePool remotePool;
    private final Map<String, TitledPaneContainer> titledPaneMap;

    /**
     * Constructor for the ContextMenuController.
     *
     * @param foregroundPane foregroundPane
     * @param backgroundPane backgroundPane
     * @param remotePool remotePool
     */
    public ContextMenuController(final ForegroundPane foregroundPane, final LocationPane backgroundPane,
            final RemotePool remotePool) {
        this.foregroundPane = foregroundPane;
        this.remotePool = remotePool;
        this.titledPaneMap = new HashMap<>();

        this.foregroundPane.getMainMenu().addFillContextMenuButtonEventHandler(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent event) {
                try {
                    setContextMenuUnitPanes("511adfec-43ed-47f5-bd4d-28f46dc1b5a4");
                } catch (CouldNotPerformException e) {
                    ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                }
            }
        });

        backgroundPane.addSelectedLocationIdListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> observable, final String oldValue,
                    final String newValue) {
                if (remotePool.isMapsFilled()) {
                    try {
                        setContextMenuUnitPanes(newValue);
                    } catch (CouldNotPerformException e) {
                        ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                    }
                } else {
                    LOGGER.error("DALRemoteService Maps are not filled. Thus no Context Menu will be loaded!");
                }
            }
        });
    }

    /**
     * Takes a locationId and creates new TitledPanes for all UnitTypes.
     *
     * @param locationID locationID
     * @throws CouldNotPerformException CouldNotPerformException
     */
    public void setContextMenuUnitPanes(final String locationID) throws CouldNotPerformException {
        if ("none".equals(locationID)) {
            throw new CouldNotPerformException("No location is selected.");
        }

        TitledPaneContainer titledPaneContainer;
        if (this.titledPaneMap.containsKey(locationID)) {
            titledPaneContainer = this.titledPaneMap.get(locationID);
        } else {
            titledPaneContainer = new TitledPaneContainer();
            fillTitledPaneContainer(titledPaneContainer, locationID);
        }

        this.foregroundPane.getContextMenu().setTitledPaneContainer(titledPaneContainer);
    }

    private void fillTitledPaneContainer(final TitledPaneContainer titledPaneContainer, final String locationID) {
        this.titledPaneMap.put(locationID, titledPaneContainer);

        final Map<UnitType, List<UnitRemote>> unitRemoteMap = remotePool.getUnitRemoteMapOfLocation(locationID);

        for (final Map.Entry<UnitType, List<UnitRemote>> nextEntry : unitRemoteMap.entrySet()) {
            try {
                titledPaneContainer.createAndAddNewTitledPane(nextEntry.getKey(), nextEntry.getValue());
            } catch (Exception ex) {
                ExceptionPrinter.printHistory(new CouldNotPerformException("Could not create titledPane!", ex), LOGGER);
            }
        }

        titledPaneContainer.addDummyPane(); //TODO: Find a way to solve this problem properly...
    }

    /**
     * Clears all stored titledPanes and clears the map afterwards.
     */
    public void clearTitledPaneMap() {
        for (final Map.Entry<String, TitledPaneContainer> nextEntry : this.titledPaneMap.entrySet()) {
            nextEntry.getValue().clearTitledPane();
        }

        this.titledPaneMap.clear();
        this.foregroundPane.getContextMenu().clearVerticalScrollPane();
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

                final TitledPaneContainer titledPaneContainer = new TitledPaneContainer();
                fillTitledPaneContainer(titledPaneContainer, locationID);
            }
        } catch (CouldNotPerformException | NullPointerException ex) {
            ExceptionPrinter.printHistory(new CouldNotPerformException("Could not init initTitledPaneMap!", ex), LOGGER);
        }
    }
}
