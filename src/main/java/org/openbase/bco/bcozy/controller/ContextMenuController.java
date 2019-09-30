/**
 * ==================================================================
 * <p>
 * This file is part of org.openbase.bco.bcozy.
 * <p>
 * org.openbase.bco.bcozy is free software: you can redistribute it and modify
 * it under the terms of the GNU General Public License (Version 3)
 * as published by the Free Software Foundation.
 * <p>
 * org.openbase.bco.bcozy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with org.openbase.bco.bcozy. If not, see <http://www.gnu.org/licenses/>.
 * ==================================================================
 */
package org.openbase.bco.bcozy.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.layout.Pane;
import javafx.util.Pair;
import org.openbase.bco.bcozy.BCozy;
import org.openbase.bco.bcozy.controller.powerterminal.PowerTerminalSidebarPaneController;
import org.openbase.bco.bcozy.view.ForegroundPane;
import org.openbase.bco.bcozy.view.location.DynamicUnitPolygon;
import org.openbase.bco.bcozy.view.UnitMenu;
import org.openbase.bco.bcozy.view.pane.unit.TitledUnitPaneContainer;
import org.openbase.bco.bcozy.view.location.LocationMapPane;
import org.openbase.bco.dal.lib.layer.unit.UnitRemote;
import org.openbase.bco.dal.remote.layer.unit.Units;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.openbase.jul.visual.javafx.control.AbstractFXController;
import org.openbase.jul.visual.javafx.fxml.FXMLProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openbase.type.domotic.unit.UnitConfigType.UnitConfig;
import org.openbase.type.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;

/**
 * @author tmichalksi
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class ContextMenuController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContextMenuController.class);

    private final ForegroundPane foregroundPane;
    private final Map<String, TitledUnitPaneContainer> titledPaneMap;
    private Pair<Pane, AbstractFXController> powerTerminalSidebarPaneAndController;

    /**
     * Constructor for the ContextMenuController.
     *
     * @param foregroundPane foregroundPane
     * @param backgroundPane backgroundPane
     */
    public ContextMenuController(final ForegroundPane foregroundPane, final LocationMapPane backgroundPane) {
        this.foregroundPane = foregroundPane;
        this.titledPaneMap = new HashMap<>();

        backgroundPane.addSelectedUnitListener((observable, oldValue, unit) -> {
            if (Registries.isDataAvailable()) {
                try {
                    setContextMenuUnitPanes(unit);
                } catch (CouldNotPerformException | InterruptedException ex) {
                    ExceptionPrinter.printHistory("Units for selected location[" + unit + "] could not be loaded.", ex, LOGGER, LogLevel.ERROR);
                }
            } else {
                LOGGER.warn("Registries not ready yet! Thus no Context Menu will be loaded!");
            }
        });

        final UnitMenu unitMenu = foregroundPane.getUnitMenu();

        try {
            powerTerminalSidebarPaneAndController = FXMLProcessor.loadFxmlPaneAndControllerPair(PowerTerminalSidebarPaneController.class);
//            ((PowerTerminalSidebarPaneController) powerTerminalSidebarPaneAndController.getValue()).init(unitMenu);
            unitMenu.setPowerTerminalSidebarPane(powerTerminalSidebarPaneAndController.getKey());
        } catch (final CouldNotPerformException ex) {
            ExceptionPrinter.printHistory("Content could not be loaded", ex, LOGGER);
        }

        BCozy.appModeProperty.addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case ENERGY:
                    unitMenu.getCollapseIcon().setOnMouseClicked(event -> showHideContextMenu(unitMenu));
                    unitMenu.getCollapseBtn().setOnAction(event -> showHideContextMenu(unitMenu));
                    unitMenu.setInEnergyMode();
                    break;
                default:
                    unitMenu.removeEnergyMode();
                    break;
            }
        });

    }

    /**
     * Maximizes oder minimizes the Unit Menu
     */
    private void showHideContextMenu(final UnitMenu unitMenu) {
        if (unitMenu.getMaximizeProperty().get()) {
            unitMenu.minimizeUnitMenu();
        } else {
            unitMenu.setInEnergyMode();
        }
    }

    /**
     * Takes a locationId and creates new TitledPanes for all UnitTypes.
     *
     * @param unit the unit to load the context for.
     * @throws CouldNotPerformException CouldNotPerformException
     * @throws java.lang.InterruptedException
     */
    public void setContextMenuUnitPanes(final DynamicUnitPolygon unit) throws CouldNotPerformException, InterruptedException {
        try {
            if (unit == null) {
                throw new CouldNotPerformException("No location is selected.");
            }

            final String unitId = unit.getUnitId();

            TitledUnitPaneContainer titledPaneContainer;
            if (titledPaneMap.containsKey(unitId)) {
                titledPaneContainer = titledPaneMap.get(unitId);
            } else {
                titledPaneContainer = new TitledUnitPaneContainer();
                fillTitledPaneContainer(titledPaneContainer, unitId);
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
                switch (nextEntry.getKey()) {
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
            for (final UnitConfig locationUnitConfig : Registries.getUnitRegistry().getUnitConfigsByUnitType(UnitType.LOCATION)) {
                fillTitledPaneContainer(new TitledUnitPaneContainer(), locationUnitConfig.getId());
            }
        } catch (CouldNotPerformException | NullPointerException ex) {
            ExceptionPrinter.printHistory(new CouldNotPerformException("Could not init initTitledPaneMap!", ex), LOGGER);
        }
    }

    public PowerTerminalSidebarPaneController getPowerTerminalSidebarPaneController() {
        return (PowerTerminalSidebarPaneController) powerTerminalSidebarPaneAndController.getValue();
    }

}
