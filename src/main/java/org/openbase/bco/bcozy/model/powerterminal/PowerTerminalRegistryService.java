package org.openbase.bco.bcozy.model.powerterminal;

import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.type.domotic.unit.UnitConfigType.UnitConfig;
import org.openbase.type.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;
import org.openbase.type.domotic.unit.location.LocationConfigType.LocationConfig.LocationType;

import java.util.ArrayList;
import java.util.List;

public class PowerTerminalRegistryService {

    public static List<UnitConfig> getConsumers(String LocationId) {
        List<UnitConfig> consumers = null;
        try {
            consumers = Registries.getUnitRegistry().getUnitConfigsByLocation(UnitType.POWER_CONSUMPTION_SENSOR, LocationId);
        } catch (CouldNotPerformException e) {
            e.printStackTrace();
        }
        return consumers;
    }

    public static List<UnitConfig> getLocations(LocationType locationType) {
        List<UnitConfig> locations = new ArrayList<>();
        try {
            locations = Registries.getUnitRegistry().getUnitConfigs(UnitType.LOCATION);
        } catch (CouldNotPerformException e) {
            e.printStackTrace();
        }
        locations.removeIf(unit -> unit.getLocationConfig().getLocationType() != locationType);
        return locations;
    }

    public static List<UnitConfig> getTileLocations() {
        return getLocations(LocationType.TILE);
    }

}
