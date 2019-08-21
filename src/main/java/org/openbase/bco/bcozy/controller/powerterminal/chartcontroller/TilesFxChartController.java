package org.openbase.bco.bcozy.controller.powerterminal.chartcontroller;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.chart.ChartData;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import org.openbase.bco.bcozy.model.LanguageSelection;
import org.openbase.bco.bcozy.model.powerterminal.ChartStateModel;
import org.openbase.bco.bcozy.model.powerterminal.PowerTerminalDBService;
import org.openbase.bco.bcozy.util.powerterminal.UnitConverter;
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
 * Controller basics for charts from the TilesFx Library.
 */
public abstract class TilesFxChartController implements ChartController{

    private static final Logger LOGGER = LoggerFactory.getLogger(TilesFxChartController.class);
    public static final int TILE_WIDTH = (int) Screen.getPrimary().getVisualBounds().getWidth();
    public static final int TILE_HEIGHT = (int) Screen.getPrimary().getVisualBounds().getHeight();
    public static final String POWERTERMINAL_CHART_HEADER_IDENTIFIER = "powerterminal.chartHeader";
    private Tile view;


    @Override
    public ScheduledFuture enableDataRefresh(long interval, ChartStateModel chartStateModel) {
        ScheduledFuture refreshSchedule = null;
        try {
            refreshSchedule = GlobalScheduledExecutorService.scheduleAtFixedRate(() -> {
                List<ChartData> data = PowerTerminalDBService.getAverageConsumption(chartStateModel.getDateRange(), chartStateModel.getSelectedConsumer());
                Platform.runLater(() -> updateChart(UnitConverter.convert(chartStateModel.getUnit(), data)));
                    }, 50, interval, TimeUnit.MILLISECONDS);
        } catch (NotAvailableException ex) {
            ExceptionPrinter.printHistory("Could not refresh power chart data", ex, LOGGER);
        }
        return refreshSchedule;
    }


    @Override
    public void updateChart(ChartStateModel chartStateModel) {
        GlobalCachedExecutorService.submit(() -> {
            List<ChartData> data = PowerTerminalDBService.getAverageConsumption(chartStateModel.getDateRange(), chartStateModel.getSelectedConsumer());
            Platform.runLater(() -> updateChart(UnitConverter.convert(chartStateModel.getUnit(), data)));
        });
    }

    /**
     * Updates the visualization
     * @param data New Data to be displayed
     */
    public void updateChart(List<ChartData> data) {
                this.view.getChartData().clear();
                this.view.getChartData().setAll(data);
    }

    @Override
    public Tile getView() {
        return view;
    }

    /**
     * Initializes the displayed TilesFx Tile.
     * @param header Heading text
     * @param skinType Type of Tile that is displayed
     * @param text Additional text that is displayed below the Tile
     */
    public void setupView(String header, Tile.SkinType skinType, ReadOnlyStringProperty text) {
        view = new Tile();
        view.setPrefSize(TILE_WIDTH, TILE_HEIGHT);
        view.setTitle(LanguageSelection.getLocalized(header));
        view.setSkinType(skinType);
        view.setTextAlignment(TextAlignment.RIGHT);
        view.textProperty().bind(text);
    }

    public void setView(Tile view) {
        this.view = view;
    }
}
