package org.openbase.bco.bcozy.controller.powerterminal.chartcontroller;

import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.VisualizationType;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Factory providing chart controllers.
 */
public class ChartControllerFactory {

    /**
     * Map that connects constructors with VisualizationType Enum to avoid ugly switch cases.
     */
    private final static Map<VisualizationType, Supplier<TilesFxChartController>> constructorMap = Map.of(
            VisualizationType.BAR_CHART, BarChartController::new,
            VisualizationType.LINE_CHART, LineChartController::new,
            VisualizationType.PIE_CHART, PieChartController::new,
            VisualizationType.TREE_CHART, TreeChartController::new);

    /**
     * Provides a new ChartController.
     * @param visualizationType Defines which type of controller is required.
     * @return Freshly created ChartController of requested type.
     */
    public static TilesFxChartController getChartController(VisualizationType visualizationType) {
        return constructorMap.get(visualizationType).get();
    }

}
