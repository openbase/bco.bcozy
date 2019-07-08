package org.openbase.bco.bcozy.controller.powerterminal.chartcontroller;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.chart.ChartData;
import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.DateRange;
import org.openbase.bco.bcozy.model.LanguageSelection;
import org.openbase.bco.bcozy.model.powerterminal.ChartStateModel;
import org.openbase.bco.bcozy.model.powerterminal.PowerTerminalDBService;

import java.util.List;

/**
 * Controller for displaying PieCharts with a hole in the middle.
 */
public class PieChartController extends TilesFxChartController {

    @Override
    public void init(ChartStateModel chartStateModel) {
        DateRange dateRange = chartStateModel.getDateRange();
        setupView(LanguageSelection.getLocalized(POWERTERMINAL_CHART_HEADER_IDENTIFIER),
                Tile.SkinType.DONUT_CHART, LanguageSelection.getProperty(dateRange.getDefaultIntervalSize().name()));

        List<ChartData> data = PowerTerminalDBService.getAverageConsumptionForDateRange(dateRange);
        setChartData(data);
    }
}
