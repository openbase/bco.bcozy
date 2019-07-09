package org.openbase.bco.bcozy.controller.powerterminal.chartcontroller;

import javafx.scene.Node;
import org.openbase.bco.bcozy.controller.powerterminal.PowerChartVisualizationController;
import org.openbase.bco.bcozy.model.powerterminal.ChartStateModel;

/**
 * Controller for charts
 */
public interface ChartController {

    /**
     * Initializes the controlled visualization with data
     * @param chartStateModel ChartStateModel containing the data
     * @param powerChartVisualizationController
     */
    void init(ChartStateModel chartStateModel, PowerChartVisualizationController powerChartVisualizationController);

    /**
     * Enables automatic refreshment of the displayed data
     * @param interval Interval of the refreshes
     * @param chartStateModel ChartStateModel from which to reload the data
     */
    void enableDataRefresh(long interval, ChartStateModel chartStateModel);

    /**
     * Manual update of the displayed data
     * @param chartStateModel ChartStateModel from which to reload the data
     */
    void updateChart(ChartStateModel chartStateModel);

    Node getView();
}
