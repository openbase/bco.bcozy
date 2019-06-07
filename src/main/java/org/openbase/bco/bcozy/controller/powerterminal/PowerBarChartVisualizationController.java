package org.openbase.bco.bcozy.controller.powerterminal;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import org.influxdata.query.FluxRecord;
import org.influxdata.query.FluxTable;
import org.openbase.bco.bcozy.model.InfluxDBHandler;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.visual.javafx.control.AbstractFXController;
import org.slf4j.LoggerFactory;

import javax.swing.text.html.CSS;
import java.sql.Date;
import java.util.Calendar;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.TimerTask;

public class PowerBarChartVisualizationController extends AbstractFXController {

    @FXML
    NumberAxis xAxis;
    @FXML
    CategoryAxis yAxis;

    @FXML
    BarChart<String, Number> bc;

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(PowerBarChartVisualizationController.class);


    public static final String CSS_FOLDER = "css";
    public static final String name = "Verbrauch";

    String duration;
    String unit;

    public PowerBarChartVisualizationController() {
        this.duration = "Hour";
        this.unit = "?";
        //TODO add new Buttons in Unit Menu so the duration and unit can be choosen by the user

    }

    @Override
    public void updateDynamicContent() throws CouldNotPerformException {

    }

    @Override
    public void initContent() throws InitializationException {
        bc.getStylesheets().add(CSS_FOLDER + "/powerTerminal/barChartStyle.css");

        xAxis.setLabel(unit);


        XYChart.Series<String, Number> series1 = new XYChart.Series();
        series1.setName(name + " pro " + duration);

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
                yAxis.setLabel("Woche");
                day = 1;
                hour = 0;
                interval = "1w";
                break;
            case "Week":
                yAxis.setLabel("Tag");
                interval = "1d";
                day = 0;
                break;
            case "Day":
                yAxis.setLabel("Stunde");
                interval = "1h";
                hour = 0;
                break;
            case "Hour":
                yAxis.setLabel("Minute");
                interval = "1m";
                break;
        }

        calendar.set(year, month, day, hour, minute, 0);
        System.out.println(calendar.getTime());

        try {
            List<FluxTable> energy = InfluxDBHandler.getAveragePowerConsumptionTables(
                    interval, new Timestamp(calendar.getTimeInMillis()/1000).getTime(), new Timestamp(System.currentTimeMillis()/1000).getTime(), "consumption");

            for (FluxTable fluxTable : energy) {
                List<FluxRecord> records = fluxTable.getRecords();
                for (FluxRecord fluxRecord : records) {
                    if (fluxRecord.getValueByKey("_value") == null) {
                        series1.getData().add(new XYChart.Data(String.valueOf(change), 0));
                    }
                    else
                        series1.getData().add(new XYChart.Data(String.valueOf(change), fluxRecord.getValueByKey("_value")));
                }
                change++;
            }
        } catch (CouldNotPerformException e) {
            e.printStackTrace();
        }

        bc.getData().add(series1);
    }
}
