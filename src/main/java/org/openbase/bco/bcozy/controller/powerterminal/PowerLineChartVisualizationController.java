package org.openbase.bco.bcozy.controller.powerterminal;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.skins.BarChartItem;
import javafx.fxml.FXML;
import javafx.scene.chart.XYChart;
import org.influxdata.query.FluxRecord;
import org.influxdata.query.FluxTable;
import org.openbase.bco.bcozy.model.InfluxDBHandler;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.visual.javafx.control.AbstractFXController;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

public class PowerLineChartVisualizationController extends AbstractFXController {

    @FXML
    Tile lineChart;

    String duration;
    String unit;

    public PowerLineChartVisualizationController() {
        this.duration = "Hour";
        this.unit = "?";
    }

    @Override
    public void updateDynamicContent() throws CouldNotPerformException {

    }

    @Override
    public void initContent() throws InitializationException {

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = 0;

        int change = 0;
        String interval = "1h";

        switch (duration) {
            case "Month":
                day = 1;
                hour = 0;
                interval = "1w";
                break;
            case "Week":
                interval = "1d";
                day = 0;
                break;
            case "Day":
                interval = "1h";
                hour = 0;
                break;
            case "Hour":
                interval = "1m";
                break;
        }

        calendar.set(year, month, day, hour, minute, 0);

        XYChart.Series<String, Number> series = new XYChart.Series();

        try {
            List<FluxTable> energy = InfluxDBHandler.getAveragePowerConsumptionTables(
                    interval, new Timestamp(calendar.getTimeInMillis()/1000).getTime(), new Timestamp(System.currentTimeMillis()/1000).getTime(), "consumption");
            for (FluxTable fluxTable : energy) {
                List<FluxRecord> records = fluxTable.getRecords();
                for (FluxRecord fluxRecord : records) {
                    if (fluxRecord.getValueByKey("_value") == null) {
                        series.getData().add(new XYChart.Data(String.valueOf(change), 0));
                    }
                    else
                        series.getData().add(new XYChart.Data(String.valueOf(change), (double) fluxRecord.getValueByKey("_value")));
                 }
                change++;
            }
        } catch (CouldNotPerformException e) {
            e.printStackTrace();
        }

        lineChart.setTitle("Line Chart");
        lineChart.addSeries(series);
    }
}
