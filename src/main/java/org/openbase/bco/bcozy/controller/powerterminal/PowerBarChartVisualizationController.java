package org.openbase.bco.bcozy.controller.powerterminal;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.skins.BarChartItem;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import org.influxdata.query.FluxRecord;
import org.influxdata.query.FluxTable;
import org.openbase.bco.bcozy.model.InfluxDBHandler;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.visual.javafx.control.AbstractFXController;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import static eu.hansolo.tilesfx.Tile.BLUE;


public class PowerBarChartVisualizationController extends AbstractFXController {

    @FXML
    Tile barChart;

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

        barChart.setTitle("BarChart");

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

        try {
            List<FluxTable> energy = InfluxDBHandler.getAveragePowerConsumptionTables(
                    interval, new Timestamp(calendar.getTimeInMillis()/1000).getTime(), new Timestamp(System.currentTimeMillis()/1000).getTime(), "consumption");
            for (FluxTable fluxTable : energy) {
                List<FluxRecord> records = fluxTable.getRecords();
                for (FluxRecord fluxRecord : records) {
                    if (fluxRecord.getValueByKey("_value") == null) {
                        barChart.addBarChartItem(new BarChartItem(String.valueOf(change), 0,  Tile.ORANGE));
                    }
                    else
                        barChart.addBarChartItem(new BarChartItem(String.valueOf(change), (double) fluxRecord.getValueByKey("_value"), Tile.ORANGE));
                }
                change++;

            }
        } catch (CouldNotPerformException e) {
            e.printStackTrace();
        }
    }
}
