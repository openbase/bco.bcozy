package org.openbase.bco.bcozy.controller.powerterminal;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.chart.ChartData;
import eu.hansolo.tilesfx.tools.FlowGridPane;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebErrorEvent;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import org.influxdata.query.FluxRecord;
import org.influxdata.query.FluxTable;
import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.Interval;
import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.VisualizationType;
import org.openbase.bco.bcozy.model.InfluxDBHandler;
import org.openbase.jul.exception.*;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.schedule.GlobalCachedExecutorService;
import org.openbase.jul.schedule.GlobalScheduledExecutorService;
import org.openbase.jul.visual.javafx.control.AbstractFXController;
import org.saxpath.Axis;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLOutput;
import java.sql.Timestamp;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;


public class PowerChartVisualizationController extends AbstractFXController {

    public static final VisualizationType DEFAULT_VISUALISATION_TYPE = VisualizationType.BAR;
    public static final String INFLUX_HOURLY_INTERVAL = "1h";
    public static final String INFLUX_DAILY_INTERVAL = "1d";
    public static final String INFLUX_WEEKLY_INTERVAL = "1w";
    public static final String INFLUX_MONTHLY_INTERVAL = "30d";
    public static final String INFLUX_YEARLY_INTERVAL = "365d";
    @FXML
    FlowGridPane pane;

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(PowerChartVisualizationController.class);

    public static final String WEBENGINE_ALERT_MESSAGE = "Webengine alert detected!";
    public static final String WEBENGINE_ERROR_MESSAGE = "Webengine error detected!";
    public static String CHRONOGRAPH_URL = "http://192.168.75.100:9999/orgs/03e2c6b79272c000/dashboards/03e529b61ff2c000?lower=now%28%29%20-%2024h";
//    public static String CHRONOGRAPH_URL = "http://localhost:9999";
    public static final int TILE_WIDTH = 1000;
    public static final int TILE_HEIGHT = 1000;
    private static final int REFRESH_TIMEOUT_MINUTES = 1;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(PowerChartVisualizationController.class);


    String duration;
    String unit;

    private String title;

    private int dataStep;

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    private String interval;

    private WebEngine webEngine;
    private Future task;

    Timestamp olddataTime;
    private int timeStampDuration;

    //Time in seconds how often the Chart is updated
    private ObjectProperty<VisualizationType> visualizationTypeProperty;
    private ObjectProperty<LocalDate> startDateObjectProperty;
    private ObjectProperty<LocalDate> endDateObjectProperty;


    public PowerChartVisualizationController() {
        this.duration = "Hour";
        this.unit = "Watt";
        this.dataStep = 10;
        title = "Average Consumption in " + unit + "/" + dataStep + " per " + duration;

    }

    @Override
    public void updateDynamicContent() throws CouldNotPerformException {

    }

    @Override
    public void initContent() throws InitializationException {
        setChartType(DEFAULT_VISUALISATION_TYPE);
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
        chart.setTitle(title);
        chart.setSkinType(skinType);
    }


    /**
     * Sets a Calender to start Date of energy overview
     *
     * @return Calender set so start Date
     */
    //TODO: Has to be GMT Time, but in future release a date picker is used to get the date
    private Calendar getCalendar() {
        Calendar calendar = Calendar.getInstance();
        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH);
        this.day = calendar.get(Calendar.DATE);
        this.hour = calendar.get(Calendar.HOUR_OF_DAY);
        this.minute = 0;

        switch (duration) {
            case "Month":
                day = 1;
                hour = 0;
                interval = "30d";
                timeStampDuration = 0;
                break;
            case "Week":
                day = 0;
                interval = "1w";
                timeStampDuration = 604800;
                break;
            case "Day":
                day = 0;
                interval = "1d";
                timeStampDuration = 86400;
                break;
            case "Hour":
                interval = "1h";
                hour = 10;
                timeStampDuration = 3600;
                break;
            case "Minute":
                interval = "1m";
                timeStampDuration = 60;
                break;
        }

        calendar.set(year, month, day, hour, minute, 0);
        return calendar;
    }

    /**
     * Refreshes all data every Minute
     * @param refreshTimeout
     * @param data
     */
    private void enableDataRefresh(int refreshTimeout, List<ChartData> data, VisualizationType visualizationType) {
        try {
            GlobalScheduledExecutorService.scheduleAtFixedRate(() -> {
                Platform.runLater(() -> {
                    setChartType(visualizationType);
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
    private List<ChartData> initializePreviousEntries(String interval, long startTime, long endTime) {
        Calendar calendar = getCalendar();
        List<ChartData> datas = new ArrayList<ChartData>();
        int change = 0;
        olddataTime = new Timestamp(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        try {
            List<FluxTable> energy = InfluxDBHandler.getAveragePowerConsumptionTables(
                    interval, startTime, endTime, "consumption");
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
     * @param visualizationTypeProperty Property describing the type of chart that is shown
     * @param startDateObjectProperty
     * @param endDateObjectProperty
     */
    public void initChartPropertyListeners(ObjectProperty<VisualizationType> visualizationTypeProperty,
                                           ObjectProperty<LocalDate> startDateObjectProperty,
                                           ObjectProperty<LocalDate> endDateObjectProperty) {

        this.visualizationTypeProperty = visualizationTypeProperty;
        this.startDateObjectProperty = startDateObjectProperty;
        this.endDateObjectProperty = endDateObjectProperty;

        visualizationTypeProperty.addListener(
                (ChangeListener<? super VisualizationType>) (dont, care, newVisualizationType) -> {
                    setChartType(newVisualizationType);
                });
    }

    private void setChartType(VisualizationType newVisualizationType) {
        pane.getChildren().clear();
        Node node;
        if (newVisualizationType == VisualizationType.WEBVIEW) {
            node = generateWebView();
        } else {
            if (startDateObjectProperty == null || endDateObjectProperty == null) {
                node = generateTilesFxChart(newVisualizationType);
            } else {
                node = generateTilesFxChart(newVisualizationType, startDateObjectProperty.getValue(), endDateObjectProperty.getValue());
            }
        }
        pane.getChildren().add(node);
    }

    private Tile generateTilesFxChart(VisualizationType newVisualizationType) {
        LocalDate endTime = LocalDate.now();
        LocalDate startTime = LocalDate.now().minus(Period.of(0,0,1));
        return generateTilesFxChart(newVisualizationType, startTime, endTime);
    }

    private Tile generateTilesFxChart(VisualizationType visualizationType, LocalDate startTime, LocalDate endTime) {
        if (visualizationType == VisualizationType.WEBVIEW) return null;
        Tile chart = new Tile();
        chart.setPrefSize(TILE_WIDTH, TILE_HEIGHT);
        Tile.SkinType skinType = visualizationType == VisualizationType.BAR ?
                Tile.SkinType.MATRIX : Tile.SkinType.SMOOTHED_CHART;//TODO there are also other chart types!
        chart.setTextAlignment(TextAlignment.RIGHT);
        chart.setText(duration);

        System.out.println("Interval String is: " + getDefaultIntervalSize(startTime, endTime));
        List<ChartData> data = initializePreviousEntries(getDefaultIntervalSize(startTime, endTime), toTime(startTime), toTime(endTime));
        addCorrectDataType(skinType, chart, data);
        enableDataRefresh(REFRESH_TIMEOUT_MINUTES, data, visualizationType);

        return chart;
    }

    private long toTime(LocalDate localDate) {
        return Timestamp.valueOf(localDate.atTime(LocalTime.MIDNIGHT)).getTime();
    }

    private String getDefaultIntervalSize(LocalDate earlierDate, LocalDate laterDate) {
        int timeSpanDays = (int) DAYS.between(earlierDate, laterDate);
        return Interval.getDefaultIntervalForTimeSpan(timeSpanDays).getInfluxIntervalString();
    }
}