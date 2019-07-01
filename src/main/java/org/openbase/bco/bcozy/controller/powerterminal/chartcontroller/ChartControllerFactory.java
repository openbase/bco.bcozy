package org.openbase.bco.bcozy.controller.powerterminal.chartcontroller;

import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.VisualizationType;

import java.util.Map;
import java.util.function.Supplier;

public class ChartControllerFactory {

    private final static Map<VisualizationType, Supplier<TilesFxChartController>> constructorMap = Map.of(
            VisualizationType.BAR_CHART, BarChartController::new,
            VisualizationType.LINE_CHART, LineChartController::new,
            VisualizationType.PIE_CHART, PieChartController::new,
            VisualizationType.TREE_CHART, TreeChartController::new);

    public static TilesFxChartController getChartController(VisualizationType visualizationType) {
        return constructorMap.get(visualizationType).get();
    }

}
