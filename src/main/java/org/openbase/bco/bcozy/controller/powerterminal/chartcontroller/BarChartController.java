package org.openbase.bco.bcozy.controller.powerterminal.chartcontroller;

import eu.hansolo.tilesfx.Tile;
import org.openbase.bco.bcozy.controller.powerterminal.PowerChartVisualizationController;
import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.DateRange;
import org.openbase.bco.bcozy.model.LanguageSelection;
import org.openbase.bco.bcozy.model.powerterminal.ChartStateModel;

/**
 * Controller for displaying a BarChart.
 */
public class BarChartController extends TilesFxChartController {

    @Override
    public void init(ChartStateModel chartStateModel, PowerChartVisualizationController powerChartVisualizationController) {
        DateRange dateRange = chartStateModel.getDateRange();

        setupView(LanguageSelection.getLocalized(POWERTERMINAL_CHART_HEADER_IDENTIFIER),
                Tile.SkinType.MATRIX, LanguageSelection.getProperty(dateRange.getDefaultIntervalSize().name()));
        getView().setAnimated(true);

//        updateChart(chartStateModel);
    }
}
