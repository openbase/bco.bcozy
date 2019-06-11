package org.openbase.bco.bcozy.controller.powerterminal;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.chart.ChartData;
import eu.hansolo.tilesfx.tools.FlowGridPane;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.Chart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebErrorEvent;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import jdk.jshell.Snippet;
import org.influxdata.query.FluxRecord;
import org.influxdata.query.FluxTable;
import org.openbase.bco.bcozy.model.InfluxDBHandler;
import org.openbase.bco.dal.lib.layer.unit.Switch;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.exception.InvalidStateException;
import org.openbase.jul.exception.NotAvailableException;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


public class PowerChartVisualizationController extends AbstractFXController {

    @FXML
    FlowGridPane pane;

    public static final String WEBENGINE_ALERT_MESSAGE = "Webengine alert detected!";
    public static final String WEBENGINE_ERROR_MESSAGE = "Webengine error detected!";
    public static String CHRONOGRAPH_URL = "http://192.168.75.100:9999/orgs/03e2c6b79272c000/dashboards/03e529b61ff2c000?lower=now%28%29%20-%2024h";

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(PowerChartVisualizationController.class);

    String duration;
    String unit;
    String chartType;
    Tile.SkinType skinType;


    private String title;

    private int dataStep;

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    private String interval;

    private Tile chart;
    private WebView webView;
    private WebEngine webEngine;
    private Future task;

    //Time in seconds how often the Chart is updated
    private int period;


    public PowerChartVisualizationController() {
        this.duration = "Hour";
        this.unit = "?";
        //SkinTypes can be MATRIX and SMOOTHED_CHART
        this.chartType = "LineChart";
        this.dataStep = 100;
        this.period = 5;
        title = "Average Consumption in " + unit + " per " + duration;
        //TODO Set chartType, duration, unit from Unit Menu

    }

    @Override
    public void updateDynamicContent() throws CouldNotPerformException {

    }

    @Override
    public void initContent() throws InitializationException {
        switch (chartType) {
            case "Bar":
                this.chart = new Tile();
                this.chart.setPrefSize(400, 400);
                this.skinType = Tile.SkinType.MATRIX;
                loadBarLineChart();
                pane.getChildren().add(chart);
                break;
            case "LineChart":
                this.chart = new Tile();
                this.chart.setPrefSize(400, 400);
                this.skinType = Tile.SkinType.SMOOTHED_CHART;
                loadBarLineChart();
                pane.getChildren().add(chart);
                break;
            case "WebView":
                webView = new WebView();
                pane.getChildren().add(webView);
                loadWebView();
                break;
        }

    }

    private void loadWebView() {
        webEngine = webView.getEngine();
        webEngine.setOnAlert((WebEvent<String> event) -> {
            ExceptionPrinter.printHistory(new InvalidStateException(WEBENGINE_ALERT_MESSAGE, new CouldNotPerformException(event.toString())), logger);
        });
        webEngine.setOnError((WebErrorEvent event) -> {
            ExceptionPrinter.printHistory(new InvalidStateException(WEBENGINE_ERROR_MESSAGE, new CouldNotPerformException(event.toString())), logger);
        });
        //TODO: loading Screen till this Thread is ready
        task = GlobalCachedExecutorService.submit(() -> {
            HttpURLConnection connection = null;
            try{
                URL myurl = new URL(CHRONOGRAPH_URL);
                connection = (HttpURLConnection) myurl.openConnection();
                connection.setRequestMethod("HEAD");
                int code = connection.getResponseCode();
            } catch (MalformedURLException e) {
                CHRONOGRAPH_URL = "https://www.google.com/";
            }
            catch (IOException e) {
                CHRONOGRAPH_URL = "https://www.google.com/";
            }
            System.out.println("fertig1");
            Platform.runLater(()-> {
                webEngine.load(CHRONOGRAPH_URL);
            });
        });
    }

    /**
     * Loads the BarChart or the LineChart
     */
    private void loadBarLineChart() {
        XYChart.Series<String, Number> series = new XYChart.Series();
        List<ChartData> datas = initializePreviousEntries();

        addCorrectDataType(datas, series);

        try {GlobalScheduledExecutorService.scheduleAtFixedRate(()->{
            refreshData(datas);
        }, 1, period, TimeUnit.SECONDS);
        } catch (NotAvailableException e) {
            e.printStackTrace();
        }
    }


    /**
     * Depending on the skinType the correct DataType is added to the Chart
     *
     * @param datas List of ChartData
     * @param series empty XYChart
     */
    private void addCorrectDataType(List<ChartData> datas, XYChart.Series<String, Number> series) {
        switch (skinType) {
            case MATRIX:
                chart.setAnimated(true);
                chart.setChartData(datas);
                //The Matrix skinType does not show any data if they are not updated (Error in tilesfx)
                if (skinType.equals(Tile.SkinType.MATRIX)) {
                    GlobalScheduledExecutorService.execute(() -> {
                        for (ChartData data: datas) {
                            data.setValue(data.getValue());
                        }
                    });
                }
                break;
            case SMOOTHED_CHART:
                for (ChartData data: datas) {
                    series.getData().add(new XYChart.Data(data.getName(), data.getValue()));
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
     * @return  Calender set so start Date
     */
    private Calendar getCalendar () {
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
                interval = "1w";
                break;
            case "Week":
                day = 0;
                interval = "1d";
                break;
            case "Day":
                hour = 0;
                interval = "1h";
                break;
            case "Hour":
                interval = "1m";
                break;
        }

        calendar.set(year, month, day, hour, minute, 0);
        return calendar;
    }


    /**
     * Refreshes the last datapoint
     *
     * @param datas List of ChartData
     */
    //TODO: A new ChartData has to be added if a new duration (Example: new Minute) has started
    //TODO: Are the DataPoints right?
    private void refreshData(List<ChartData> datas) {
        try {
            double tempEnergy = InfluxDBHandler.getAveragePowerConsumption(
                    "1m", new Timestamp(System.currentTimeMillis()/1000).getTime() - period, new Timestamp(System.currentTimeMillis()/1000).getTime(), "consumption");
            System.out.println("Neue Energie der letzten 5 sekunden" + tempEnergy);
            tempEnergy = datas.get(datas.size()-1).getValue() + tempEnergy/dataStep;
            datas.get(datas.size()-1).setValue(tempEnergy);
        } catch (CouldNotPerformException e) {
            e.printStackTrace();
        }
    }


    /**
     * Initializes the previous energy consumption
     *
     * @return List of ChartData with previous Energy Consumption
     */
    private List<ChartData> initializePreviousEntries() {
        Calendar calendar = getCalendar();
        List<ChartData> datas = new ArrayList<ChartData>();
        int change = 0;
        try {
            List<FluxTable> energy = InfluxDBHandler.getAveragePowerConsumptionTables(
                    interval, new Timestamp(calendar.getTimeInMillis()/1000).getTime(), new Timestamp(System.currentTimeMillis()/1000).getTime(), "consumption");
            for (FluxTable fluxTable : energy) {
                List<FluxRecord> records = fluxTable.getRecords();
                for (FluxRecord fluxRecord : records) {
                    if (fluxRecord.getValueByKey("_value") == null) {
                        ChartData temp = new ChartData(String.valueOf(change), 0,  Tile.ORANGE);
                        temp.setName(String.valueOf(change));
                        datas.add(temp);
                    }
                    else {
                        ChartData temp = new ChartData(String.valueOf(change), (double) fluxRecord.getValueByKey("_value")/dataStep, Tile.ORANGE);
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
}
