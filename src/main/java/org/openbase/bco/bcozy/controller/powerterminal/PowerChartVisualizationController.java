package org.openbase.bco.bcozy.controller.powerterminal;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.chart.ChartData;
import javafx.fxml.FXML;
import javafx.scene.chart.Chart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import jdk.jshell.Snippet;
import org.influxdata.query.FluxRecord;
import org.influxdata.query.FluxTable;
import org.openbase.bco.bcozy.model.InfluxDBHandler;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.schedule.GlobalScheduledExecutorService;
import org.openbase.jul.visual.javafx.control.AbstractFXController;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class PowerChartVisualizationController extends AbstractFXController {

    @FXML
    Tile chart;

    public static final String name = "Verbrauch";

    String duration;
    String unit;
    Tile.SkinType skinType;

    private int dataStep;

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    private String interval;

    //Time in seconds how often the Chart is updated
    private int period;


    public PowerChartVisualizationController() {
        this.duration = "Hour";
        this.unit = "?";
        this.skinType = Tile.SkinType.MATRIX;
        this.dataStep = 100;
        this.period = 5;
        //TODO add new Buttons in Unit Menu so the duration and unit can be choosen by the user

    }

    @Override
    public void updateDynamicContent() throws CouldNotPerformException {

    }

    @Override
    public void initContent() throws InitializationException {
        chart.setSkinType(skinType);

        List<ChartData> datas = new ArrayList<ChartData>();
        XYChart.Series<String, Number> series = new XYChart.Series();

        Calendar calendar = getCalendar();

        //Initialize the previous Chart entries
        try {
            List<FluxTable> energy = InfluxDBHandler.getAveragePowerConsumptionTables(
                    interval, new Timestamp(calendar.getTimeInMillis()/1000).getTime(), new Timestamp(System.currentTimeMillis()/1000).getTime(), "consumption");
            int change = 0;
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


        switch (skinType) {
            case MATRIX:
                chart.setAnimated(true);
                chart.setChartData(datas);
                chart.setTitle("test");
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

                }
                series.getData().add(new XYChart.Data("2", 2));
                series.getData().add(new XYChart.Data("3", 4));
                chart.setTitle("test");
                chart.addSeries(series);
                break;
        }

        try {GlobalScheduledExecutorService.scheduleAtFixedRate(()->{
            try {
                double tempEnergy = InfluxDBHandler.getAveragePowerConsumption(
                        period+"m", new Timestamp(System.currentTimeMillis()/1000).getTime() - period, new Timestamp(System.currentTimeMillis()/1000).getTime(), "consumption");
                datas.get(datas.size()-1).setValue(datas.get(datas.size()-1).getValue() + tempEnergy/dataStep);
                //series.getData().forEach(data -> data.setYValue(3));
                //series.getData().set(series.getData().size()-1, new XYChart.Data(datas.get(datas.size() -1).getName(), datas.get(datas.size()-1).getValue()));
            } catch (CouldNotPerformException e) {
                e.printStackTrace();
            }
        }, 1, period, TimeUnit.SECONDS);
        } catch (NotAvailableException e) {
            e.printStackTrace();
        }
    }

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
}
