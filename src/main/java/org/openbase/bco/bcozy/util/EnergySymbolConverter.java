package org.openbase.bco.bcozy.util;

import eu.hansolo.tilesfx.chart.ChartData;
import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.SymbolUnit;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EnergySymbolConverter {

    private final static Map<SymbolUnit, Function<List<ChartData>, List<ChartData>>> converterMap = Map.of(
            SymbolUnit.LIGHTBULB, EnergySymbolConverter::convertToLightBulbs,
            SymbolUnit.ENERGY, EnergySymbolConverter::convertToEnergy,
            SymbolUnit.MONEY, EnergySymbolConverter::convertToCosts);
    public static final double EURO_PER_KILOWATTHOUR = 0.3;
    public static final double LIGHTBULB_POWER_DRAW_IN_WATT = 9.5;
    public static final double WATT_HOURS_PER_KILOWATTHOUR = 1000.0;

    public static List<ChartData> convert(final SymbolUnit targetSymbolUnit, List<ChartData> data) {
        return converterMap.get(targetSymbolUnit).apply(data);
    }

    private static List<ChartData> convertToEnergy (List<ChartData> data) {
        return data.stream().map((datum) -> new ChartData(datum.getName(), datum.getValue() / WATT_HOURS_PER_KILOWATTHOUR))
                .collect(Collectors.toList());
    }

    private static List<ChartData> convertToCosts (List<ChartData> data) {
        return data.stream().map((datum) -> new ChartData(datum.getName(), (datum.getValue() / WATT_HOURS_PER_KILOWATTHOUR) * EURO_PER_KILOWATTHOUR))
                .collect(Collectors.toList());
    }

    private static List<ChartData> convertToLightBulbs (List<ChartData> data) {
        return data.stream().map((datum) -> new ChartData(datum.getName(), datum.getValue() / LIGHTBULB_POWER_DRAW_IN_WATT))
                .collect(Collectors.toList());
    }


}
