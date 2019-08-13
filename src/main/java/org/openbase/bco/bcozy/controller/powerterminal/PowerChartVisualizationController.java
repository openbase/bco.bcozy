package org.openbase.bco.bcozy.controller.powerterminal;

import eu.hansolo.tilesfx.tools.FlowGridPane;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.stage.Screen;
import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.DateRange;
import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.Unit;
import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.VisualizationType;
import org.openbase.bco.bcozy.controller.powerterminal.chartcontroller.ChartController;
import org.openbase.bco.bcozy.controller.powerterminal.chartcontroller.ChartControllerFactory;
import org.openbase.bco.bcozy.model.powerterminal.ChartStateModel;
import org.openbase.jul.exception.*;
import org.openbase.jul.visual.javafx.control.AbstractFXController;

import java.util.concurrent.ScheduledFuture;

/**
 * Controller that manages the interaction between the PowerTerminalSidebarPane and the different charttypes by listening
 * to changes of the sidebars properties
 */
public class PowerChartVisualizationController extends AbstractFXController {

    public static final VisualizationType DEFAULT_VISUALISATION_TYPE = VisualizationType.LINE_CHART;
    public static final String INFLUXDB_FIELD_CONSUMPTION = "consumption";

    private ScheduledFuture refreshScheduler;

    @FXML
    FlowGridPane pane;

    private ChartStateModel chartStateModel;
    private ChartController chartController;

    @Override
    public void updateDynamicContent() throws CouldNotPerformException {

    }

    @Override
    public void initContent() throws InitializationException {
        pane.setMinSize(Screen.getPrimary().getVisualBounds().getWidth(), Screen.getPrimary().getVisualBounds().getHeight() - 600);
    }


    /**
     * Connects the given chart attribute properties to the chart by creating listeners incorporating the changes
     * into the chart
     *
     * @param chartStateModel StateModel that describes the state of the chart as configured by other panes
     */
    public void initChartState(ChartStateModel chartStateModel) {

        this.chartStateModel = chartStateModel;

        chartStateModel.visualizationTypeProperty().addListener((source, old, newVisualizationType) ->
                setUpChart(newVisualizationType)
        );
        chartStateModel.dateRangeProperty().addListener((source, old, newDateRange) ->
                chartController.updateChart(chartStateModel)
        );
        chartStateModel.selectedConsumerProperty().addListener((source, old, newConsumer) ->
                chartController.updateChart(chartStateModel)
        );
        chartStateModel.unitProperty().addListener((source, oldValue, newValue) ->
                chartController.updateChart(chartStateModel)
        );
        setUpChart(DEFAULT_VISUALISATION_TYPE);
    }

    public FlowGridPane getPane() {
        return pane;
    }

    public ChartStateModel getChartStateModel() {
        return chartStateModel;
    }

    private void setUpChart(VisualizationType visualizationType) {
        if (refreshScheduler != null) {
            refreshScheduler.cancel(true);
        }
        pane.getChildren().clear();
        chartController = ChartControllerFactory.getChartController(visualizationType);
        chartController.init(chartStateModel, this);
        pane.getChildren().add(chartController.getView());
        refreshScheduler = chartController.enableDataRefresh(30000, chartStateModel);
    }
}