package org.openbase.bco.bcozy.controller.powerterminal.chartcontroller;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.chart.ChartData;
import javafx.scene.chart.XYChart;
import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.DateRange;
import org.openbase.bco.bcozy.model.LanguageSelection;
import org.openbase.bco.bcozy.model.powerterminal.ChartStateModel;
import org.openbase.bco.bcozy.model.powerterminal.PowerTerminalDBService;

import java.util.List;

public class LineChartController extends TilesFxChartController {


    @Override
    public void init(ChartStateModel chartStateModel) {
        DateRange dateRange = chartStateModel.getDateRange();
        setupView(LanguageSelection.getLocalized(POWERTERMINAL_CHART_HEADER_IDENTIFIER),
                Tile.SkinType.SMOOTHED_CHART, LanguageSelection.getProperty(dateRange.getDefaultIntervalSize().name()));

        List<ChartData> data = PowerTerminalDBService.getAverageConsumptionForDateRange(dateRange);
        setChartData(data);
    }

    @Override
    public void setChartData(List<ChartData> data) {
        XYChart.Series<String, Number> series = new XYChart.Series();
        for (ChartData datum : data) {
            series.getData().add(new XYChart.Data(datum.getName(), datum.getValue()));
        }

        Tile view = TileBuilder.create()
                .skinType(Tile.SkinType.SMOOTHED_CHART)
                .prefSize(TILE_WIDTH, TILE_HEIGHT)
                .title(LanguageSelection.getLocalized(POWERTERMINAL_CHART_HEADER_IDENTIFIER))
                .smoothing(false)
                .series(series)
                .build();

        setView(view);
//        view.addChartData(data); This does not work because TilesFx ain't that great, huh?
    }

    @Override
    public void updateChart(ChartStateModel chartStateModel) {
        setChartData(PowerTerminalDBService.getAverageConsumptionForDateRange(chartStateModel.getDateRange()));
    }
}
