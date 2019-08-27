package org.openbase.bco.bcozy.controller.powerterminal.chartcontroller;

import eu.hansolo.tilesfx.chart.ChartData;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import org.openbase.bco.bcozy.controller.powerterminal.PowerChartVisualizationController;
import org.openbase.bco.bcozy.model.powerterminal.ChartStateModel;
import org.openbase.bco.bcozy.model.powerterminal.PowerTerminalDBService;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.schedule.GlobalCachedExecutorService;
import org.openbase.jul.schedule.GlobalScheduledExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Controller for displaying a value by the health of a tree.
 */
public class TreeChartController implements ChartController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TilesFxChartController.class);
    private static final int DISPLAYED_CONSUMPTION_RANGE = 2000;

    private ImageView view;

    public TreeChartController() {

    }

    @Override
    public void init(ChartStateModel chartStateModel, PowerChartVisualizationController powerChartVisualizationController) {
        view = new ImageView();
        view.setPreserveRatio(true);
        view.setFitHeight(Screen.getPrimary().getVisualBounds().getHeight() / 1.2);
    }

    @Override
    public ScheduledFuture enableDataRefresh(long interval, ChartStateModel chartStateModel) {
        ScheduledFuture refreshSchedule = null;
        try {
            refreshSchedule = GlobalScheduledExecutorService.scheduleAtFixedRate(() -> {
                        List<ChartData> data = PowerTerminalDBService.getAverageConsumption(chartStateModel.getDateRange(), chartStateModel.getSelectedConsumer());
                        Platform.runLater(() -> updateChart(data));
                    },
                    10, interval, TimeUnit.MILLISECONDS);
        } catch (NotAvailableException ex) {
            ExceptionPrinter.printHistory("Could not refresh power chart data", ex, LOGGER);
        }
        return refreshSchedule;
    }

    @Override
    public void updateChart(ChartStateModel chartStateModel) {
        GlobalCachedExecutorService.submit(() -> {
                List<ChartData> data = PowerTerminalDBService.getAverageConsumption(chartStateModel.getDateRange(), chartStateModel.getSelectedConsumer());
                Platform.runLater(() -> updateChart(data));
        });
    }

    private void updateChart(List<ChartData> data) {
        view.setImage(getImageByPowerDraw(data.get(0)));
    }

    @Override
    public Node getView() {
        return view;
    }

    private Image getImageByPowerDraw(ChartData datum) {
        if (datum.getValue() > DISPLAYED_CONSUMPTION_RANGE) {
            return new Image("/images/tree0.png");
        } else if (datum.getValue() > DISPLAYED_CONSUMPTION_RANGE * .8) {
            return new Image("/images/tree1.png");
        } else if (datum.getValue() > DISPLAYED_CONSUMPTION_RANGE * .6) {
            return new Image("/images/tree2.png");
        } else if (datum.getValue() > DISPLAYED_CONSUMPTION_RANGE * .4) {
            return new Image("/images/tree3.png");
        } else if (datum.getValue() > DISPLAYED_CONSUMPTION_RANGE * .2) {
            return new Image("/images/tree4.png");
        } else if (datum.getValue() > DISPLAYED_CONSUMPTION_RANGE * .1) {
            return new Image("/images/tree5.png");
        } else if (datum.getValue() > 0) {
            return new Image("/images/tree6.png");
        } else {
            return new Image("/images/tree7.png");
        }
    }
}