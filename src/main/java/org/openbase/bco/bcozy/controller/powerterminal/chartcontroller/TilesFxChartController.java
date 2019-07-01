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
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.schedule.GlobalScheduledExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class TilesFxChartController implements ChartController{

    private static final Logger LOGGER = LoggerFactory.getLogger(TilesFxChartController.class);
    public static final int TILE_WIDTH = (int) Screen.getPrimary().getVisualBounds().getWidth();
    public static final int TILE_HEIGHT = (int) Screen.getPrimary().getVisualBounds().getHeight();
    private Tile view;

    @Override
    public void enableDataRefresh(long interval, ChartStateModel chartStateModel) {
        try {
            GlobalScheduledExecutorService.scheduleAtFixedRate(() -> Platform.runLater(() -> updateChart(chartStateModel)),
                    50, interval, TimeUnit.MILLISECONDS);
        } catch (NotAvailableException ex) {
            ExceptionPrinter.printHistory("Could not refresh power chart data", ex, LOGGER);
        }
    }

    @Override
    public void updateChart(ChartStateModel chartStateModel) {
        view.getChartData().clear();
        view.getChartData().setAll(PowerTerminalDBService.getAverageConsumptionForDateRange(chartStateModel.getDateRange()));
    }

    public void setChartData(List<ChartData> data) {
        view.addChartData(data);
    }

    @Override
    public Tile getView() {
        return view;
    }

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
