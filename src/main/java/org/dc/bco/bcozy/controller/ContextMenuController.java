/**
 * ==================================================================
 *
 * This file is part of org.dc.bco.bcozy.
 *
 * org.dc.bco.bcozy is free software: you can redistribute it and modify
 * it under the terms of the GNU General Public License (Version 3)
 * as published by the Free Software Foundation.
 *
 * org.dc.bco.bcozy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with org.dc.bco.bcozy. If not, see <http://www.gnu.org/licenses/>.
 * ==================================================================
 */

package org.dc.bco.bcozy.controller;

import de.citec.dal.remote.unit.DALRemoteService;
import org.dc.jul.exception.CouldNotPerformException;
import org.dc.jul.exception.printer.ExceptionPrinter;
import org.dc.jul.exception.printer.LogLevel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import org.dc.bco.bcozy.view.ForegroundPane;
import org.dc.bco.bcozy.view.location.LocationPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.homeautomation.unit.UnitTemplateType.UnitTemplate.UnitType;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by timo on 03.12.15.
 */
public class ContextMenuController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContextMenuController.class);

    private final ForegroundPane foregroundPane;
    private final LocationPane backgroundPane;
    private final RemotePool remotePool;

    /**
     * Constructor for the ContextMenuController.
     * @param foregroundPane foregroundPane
     * @param backgroundPane backgroundPane
     * @param remotePool remotePool
     */
    public ContextMenuController(final ForegroundPane foregroundPane, final LocationPane backgroundPane,
                                 final RemotePool remotePool) {
        this.foregroundPane = foregroundPane;
        this.backgroundPane = backgroundPane;
        this.remotePool = remotePool;

        this.foregroundPane.getMainMenu().addFillContextMenuButtonEventHandler(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent event) {
                try {
                    setContextMenuDevicePanes("81b9efa4-2dc9-432e-b47c-1d73021ff0f3");
                } catch (CouldNotPerformException e) {
                    ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                }
            }
        });

        this.backgroundPane.addSelectedRoomIdListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> observable, final String oldValue,
                                final String newValue) {
                try {
                    setContextMenuDevicePanes(newValue);
                } catch (CouldNotPerformException e) {
                    ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                }
            }
        });
    }

    /**
     * Takes a locationId and creates new TitledPanes for all UnitTypes.
     * @param locationID locationID
     * @throws CouldNotPerformException CouldNotPerformException
     */
    public void setContextMenuDevicePanes(final String locationID) throws CouldNotPerformException {
        foregroundPane.getContextMenu().getTitledPaneContainer().clearTitledPane();

        if ("none".equals(locationID)) {
            throw new CouldNotPerformException("No location is selected.");
        }

        final Map<UnitType, List<DALRemoteService>> unitRemoteMap =
                remotePool.getUnitRemoteMapOfLocation(locationID);

        final Iterator<Map.Entry<UnitType, List<DALRemoteService>>> entryIterator =
                unitRemoteMap.entrySet().iterator();

        while (entryIterator.hasNext()) {
            final Map.Entry<UnitType, List<DALRemoteService>> nextEntry =
                    entryIterator.next();

            foregroundPane.getContextMenu().getTitledPaneContainer().createAndAddNewTitledPane(
                    nextEntry.getKey(), nextEntry.getValue());
        }
    }
}
