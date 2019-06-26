package org.openbase.bco.bcozy.controller.powerterminal;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.chart.ChartData;
import eu.hansolo.tilesfx.tools.FlowGridPane;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebErrorEvent;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import org.influxdata.query.FluxRecord;
import org.influxdata.query.FluxTable;
import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.DateRange;
import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.VisualizationType;
import org.openbase.bco.bcozy.model.InfluxDBHandler;
import org.openbase.bco.bcozy.model.LanguageSelection;
import org.openbase.bco.bcozy.model.powerterminal.ChartStateModel;
import org.openbase.jul.exception.*;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.schedule.GlobalCachedExecutorService;
import org.openbase.jul.schedule.GlobalScheduledExecutorService;
import org.openbase.jul.visual.javafx.control.AbstractFXController;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


public class PowerChartVisualizationController extends AbstractFXController {

    public static final VisualizationType DEFAULT_VISUALISATION_TYPE = VisualizationType.BARCHART;
    public static final String CHART_HEADER_IDENTIFIER = "chartHeader";

    @FXML
    FlowGridPane pane;

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(PowerChartVisualizationController.class);

    public static final String WEBENGINE_ALERT_MESSAGE = "Webengine alert detected!";
    public static final String WEBENGINE_ERROR_MESSAGE = "Webengine error detected!";
    //    public static String CHRONOGRAPH_URL = "http://192.168.75.100:9999/orgs/03e2c6b79272c000/dashboards/03e529b61ff2c000?lower=now%28%29%20-%2024h";
    public static String CHRONOGRAPH_URL = "http://localhost:9999";
    public static final int TILE_WIDTH = (int) Screen.getPrimary().getVisualBounds().getWidth();
    public static final int TILE_HEIGHT = (int) Screen.getPrimary().getVisualBounds().getHeight();
    private static final int REFRESH_TIMEOUT_MINUTES = 1;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(PowerChartVisualizationController.class);


    String unit;

    private int dataStep;

    private boolean firstRun = true;

    private WebEngine webEngine;
    private Future task;

    Timestamp olddataTime;
    private ChartStateModel chartStateModel;


    public PowerChartVisualizationController() {
        this.unit = "Watt";
        this.dataStep = 10;
    }

    @Override
    public void updateDynamicContent() throws CouldNotPerformException {

    }

    @Override
    public void initContent() throws InitializationException {
        pane.setMinSize(Screen.getPrimary().getVisualBounds().getWidth(), Screen.getPrimary().getVisualBounds().getHeight() - 600);
        setChartType(DEFAULT_VISUALISATION_TYPE);
        enableDataRefresh(REFRESH_TIMEOUT_MINUTES);
    }

    private WebView generateWebView() {
        WebView webView = new WebView();
        webEngine = webView.getEngine();
        webEngine.setOnAlert((WebEvent<String> event) -> {
            ExceptionPrinter.printHistory(new InvalidStateException(WEBENGINE_ALERT_MESSAGE, new CouldNotPerformException(event.toString())), logger);
        });
        webEngine.setOnError((WebErrorEvent event) -> {
            ExceptionPrinter.printHistory(new InvalidStateException(WEBENGINE_ERROR_MESSAGE, new CouldNotPerformException(event.toString())), logger);
        });
        task = GlobalCachedExecutorService.submit(() -> {
            HttpURLConnection connection = null;
            try {
                URL myurl = new URL(CHRONOGRAPH_URL);
                connection = (HttpURLConnection) myurl.openConnection();
                connection.setRequestMethod("HEAD");
                int code = connection.getResponseCode();
            } catch (MalformedURLException e) {
                CHRONOGRAPH_URL = "https://www.google.com/";
            } catch (IOException e) {
                CHRONOGRAPH_URL = "https://www.google.com/";
            }
            Platform.runLater(() -> {
                webEngine.load(CHRONOGRAPH_URL);
            });
        });
        webView.setMaxHeight(TILE_HEIGHT);
        webView.setMaxWidth(TILE_WIDTH / 2);
        webView.setMinHeight(TILE_HEIGHT / 2 + TILE_HEIGHT / 4);
        webView.setMinWidth(TILE_WIDTH / 2);
        return webView;
    }


    /**
     * Depending on the skinType the correct DataType is added to the Chart
     */
    private void addCorrectDataType(Tile.SkinType skinType, Tile chart, List<ChartData> data) {
        XYChart.Series<String, Number> series = new XYChart.Series();

        switch (skinType) {
            case MATRIX:
                chart.setAnimated(true);
                chart.setChartData(data);
                //The Matrix skinType does not show any data if they are not updated (Bug in tilesfx)
                if (skinType.equals(Tile.SkinType.MATRIX)) {
                    GlobalScheduledExecutorService.execute(() -> {
                        for (ChartData datum : data) {
                            datum.setValue(datum.getValue());
                        }
                    });
                }
                break;
            case SMOOTHED_CHART:
                for (ChartData datum : data) {
                    series.getData().add(new XYChart.Data(datum.getName(), datum.getValue()));
                }
                chart.addSeries(series);
                break;
        }
        chart.setSkinType(skinType);
    }


    /**
     * Refreshes all data every Minute
     *
     * @param refreshTimeout
     */
    private void enableDataRefresh(int refreshTimeout) {
        try {
            GlobalScheduledExecutorService.scheduleAtFixedRate(() -> {
                Platform.runLater(() -> {
                    setChartType(this.chartStateModel.getVisualizationType());
                });
            }, 1, refreshTimeout, TimeUnit.MINUTES);
        } catch (NotAvailableException ex) {
            ExceptionPrinter.printHistory("Could not refresh power chart data", ex, LOGGER);
        }
    }


    /**
     * Initializes the previous energy consumption
     *
     * @return List of ChartData with previous Energy Consumption
     */
    private List<ChartData> loadChartData(String interval, long startTime, long endTime) {
        List<ChartData> datas = new ArrayList<ChartData>();
        int change = 0;
        olddataTime = new Timestamp(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        try {
            List<FluxTable> energy = InfluxDBHandler.getAveragePowerConsumptionTables(
                    interval, TimeUnit.MILLISECONDS.toSeconds(startTime), TimeUnit.MILLISECONDS.toSeconds(endTime), "consumption");
            for (FluxTable fluxTable : energy) {
                List<FluxRecord> records = fluxTable.getRecords();
                for (FluxRecord fluxRecord : records) {
                    if (fluxRecord.getValueByKey("_value") == null) {
                        ChartData temp = new ChartData(String.valueOf(change), 0, Tile.ORANGE);
                        temp.setName(String.valueOf(change));
                        datas.add(temp);
                    } else {
                        ChartData temp = new ChartData(String.valueOf(change), (double) fluxRecord.getValueByKey("_value") / dataStep, Tile.ORANGE);
                        temp.setName(String.valueOf(change));
                        datas.add(temp);
                    }

                }
                change++;
            }
        } catch (CouldNotPerformException e) {
            e.printStackTrace();
        }
        return datas;
    }

    /**
     * Connects the given chart attribute properties to the chart by creating listeners incorporating the changes
     * into the chart
     *
     * @param chartStateModel StateModel that describes the state of the chart as configured by other panes
     */
    public void initChartState(ChartStateModel chartStateModel) {

        this.chartStateModel = chartStateModel;

        chartStateModel.visualizationTypeProperty().addListener(
                (ChangeListener<? super VisualizationType>) (dont, care, newVisualizationType) -> {
                    setChartType(newVisualizationType);
                });

        chartStateModel.dateRangeProperty().addListener((ChangeListener<? super DateRange>) (dont, care, newDateRange) -> {
            System.out.println("===========> Date Picked!");
            setChartType(this.chartStateModel.getVisualizationType());
        });

    }

    private void setChartType(VisualizationType newVisualizationType) {
        if (newVisualizationType==null)
            newVisualizationType = DEFAULT_VISUALISATION_TYPE;

        Node node;
        switch (newVisualizationType) {
            case WEBVIEW:
                node = generateWebView();
                break;
            default:
                if (chartStateModel == null) {
                    node = generateTilesFxChart(newVisualizationType);
                } else {
                    node = generateTilesFxChart(newVisualizationType, chartStateModel.getDateRange());
                }
                break;
        }
        pane.getChildren().clear();
        pane.getChildren().add(node);
    }

    private Tile generateTilesFxChart(VisualizationType newVisualizationType) {
        LocalDate endTime = LocalDate.now();
        LocalDate startTime = LocalDate.now().minus(Period.of(0, 0, 1));
        DateRange defaultDateRange = new DateRange(startTime, endTime);
        return generateTilesFxChart(newVisualizationType, defaultDateRange);
    }

    //TODO bind chartstatemodel properties to listeners so this functions are not needed, see #
    private Tile generateTilesFxChart(VisualizationType visualizationType, DateRange dateRange) {
        if (visualizationType == VisualizationType.WEBVIEW) return null;
        Tile chart = new Tile();
        chart.setPrefSize(TILE_WIDTH, TILE_HEIGHT);
        Tile.SkinType skinType = visualizationType == VisualizationType.BARCHART ?
                Tile.SkinType.MATRIX : Tile.SkinType.SMOOTHED_CHART;
        chart.setTextAlignment(TextAlignment.RIGHT);

        String interval = dateRange.getDefaultIntervalSize().getInfluxIntervalString();
        chart.setTitle(LanguageSelection.getLocalized(CHART_HEADER_IDENTIFIER));
        chart.setText(LanguageSelection.getLocalized(dateRange.getDefaultIntervalSize().name()));

        System.out.println("Interval String is: " + interval);
        System.out.println("Start time is " + dateRange.getStartDate().toString() + ", as Timestamp it is " + dateRange.getStartDateAtCurrentTime().getTime());
        System.out.println("End time is " + dateRange.getEndDate().toString() + ", as Timestamp it is " + dateRange.getEndDateAtCurrentTime().getTime());
        List<ChartData> data = loadChartData(interval, dateRange.getStartDateAtCurrentTime().getTime(), dateRange.getEndDateAtCurrentTime().getTime());
        addCorrectDataType(skinType, chart, data);
        return chart;
    }

}