package org.openbase.bco.bcozy.model.powerterminal;

import org.openbase.bco.bcozy.controller.powerterminal.PowerTerminalSidebarPaneController;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.type.domotic.unit.UnitConfigType.UnitConfig;
import org.openbase.type.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;
import org.openbase.type.domotic.unit.location.LocationConfigType.LocationConfig.LocationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * Service wrapping the Registries, providing a high level interface, keeping MVC model tasks where they belong.
 */
public class PowerTerminalRegistryService {

    private static Logger LOGGER = LoggerFactory.getLogger(PowerTerminalSidebarPaneController.class);

    public static List<UnitConfig> getConsumers(String LocationId) {
        List<UnitConfig> consumers = null;
        try {
            consumers = Registries.getUnitRegistry().getUnitConfigsByLocationIdAndUnitType(LocationId, UnitType.POWER_CONSUMPTION_SENSOR);
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER);
        }
        return consumers;
    }

    public static List<UnitConfig> getLocations(LocationType locationType) {
        List<UnitConfig> locations = new ArrayList<>();
        try {
            locations = Registries.getUnitRegistry().getUnitConfigsByUnitType(UnitType.LOCATION);
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER);
        }
        locations.removeIf(unit -> unit.getLocationConfig().getLocationType() != locationType);
        return locations;
    }

    public static List<UnitConfig> getTileLocations() {
        return getLocations(LocationType.TILE);
    }

}
