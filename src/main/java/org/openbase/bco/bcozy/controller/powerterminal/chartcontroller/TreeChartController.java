package org.openbase.bco.bcozy.controller.powerterminal.chartcontroller;

import eu.hansolo.tilesfx.chart.ChartData;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
            return new Image(getClass().getResource("/images/tree0.png").getPath());
        } else if (datum.getValue() > 1500) {
            return new Image(getClass().getResource("/images/tree1.png").getPath());
        } else if (datum.getValue() > 900) {
            return new Image(getClass().getResource("/images/tree2.png").getPath());
        } else if (datum.getValue() > 600) {
            System.out.println(getClass().getResource("/images/tree3.png"));
            return new Image(getClass().getResource("/images/tree3.png").getPath());
        } else if (datum.getValue() > 400) {
            return new Image(getClass().getResource("/images/tree4.png").getPath());
        } else if (datum.getValue() > 200) {
            return new Image(getClass().getResource("/images/tree5.png").getPath());
        } else if (datum.getValue() > 0) {
            return new Image(getClass().getResource("/images/tree6.png").getPath());
        } else {
            return new Image(getClass().getResource("/images/tree7.png").getPath());
        }
    }
}