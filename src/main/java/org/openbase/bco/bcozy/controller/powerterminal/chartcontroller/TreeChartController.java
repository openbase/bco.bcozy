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
import org.openbase.jul.schedule.GlobalScheduledExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Controller for displaying a value by the health of a tree.
 */
public class TreeChartController implements ChartController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TilesFxChartController.class);

    private ImageView view;

    public TreeChartController() {

    }

    @Override
    public void init(ChartStateModel chartStateModel, PowerChartVisualizationController powerChartVisualizationController) {
        view = new ImageView();
        view.setPreserveRatio(true);
        view.setFitHeight(Screen.getPrimary().getVisualBounds().getHeight()/1.2);
    }

    @Override
    public ScheduledFuture enableDataRefresh(long interval, ChartStateModel chartStateModel) {
        ScheduledFuture refreshSchedule = null;
        try {
            refreshSchedule = GlobalScheduledExecutorService.scheduleAtFixedRate(() -> Platform.runLater(() -> updateChart(chartStateModel)),
                    10, interval, TimeUnit.MILLISECONDS);
        } catch (NotAvailableException ex) {
            ExceptionPrinter.printHistory("Could not refresh power chart data", ex, LOGGER);
        }
        return refreshSchedule;
    }

    @Override
    public void updateChart(ChartStateModel chartStateModel) {
        view.setImage(getImageByPowerDraw(PowerTerminalDBService.getAverageConsumptionForDateRange(chartStateModel.getDateRange()).get(0)));

    }

    @Override
    public Node getView() {
        return view;
    }

    private Image getImageByPowerDraw(ChartData datum) {
        if (datum.getValue() > 2000) {
            return new Image("/images/tree0.png");
        } else if (datum.getValue() > 1500) {
            return new Image("/images/tree1.png");
        } else if (datum.getValue() > 900) {
            return new Image("/images/tree2.png");
        } else if (datum.getValue() > 600) {
            return new Image("/images/tree3.png");
        } else if (datum.getValue() > 400) {
            return new Image("/images/tree4.png");
        } else if (datum.getValue() > 200) {
            return new Image("/images/tree5.png");
        } else if (datum.getValue() > 0) {
            return new Image("/images/tree6.png");
        } else {
            return new Image("/images/tree7.png");
        }
    }
}