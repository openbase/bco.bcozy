package org.openbase.bco.bcozy.controller.powerterminal;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import org.openbase.bco.bcozy.model.InfluxDBHandler;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.visual.javafx.control.AbstractFXController;

import java.util.Calendar;
import java.sql.Timestamp;

public class PowerBarChartVisualizationController extends AbstractFXController  {

    @FXML
    NumberAxis xAxis;
    @FXML
    CategoryAxis yAxis;

    @FXML
    BarChart<String, Number> bc;

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
        xAxis.setLabel(duration);
        yAxis.setLabel(unit);


        XYChart.Series<String, Number> series1 = new XYChart.Series();
        series1.setName("Verbrauch");

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = 0;

        int month_next = 0;
        int day_next = 0;
        int hour_next = 0;
        int minute_next = 0;

        int change = 1;

        if (duration.equals("Year")) {
            month_next = 1;
            month = 0;
            hour = 0;
        }
        else if (duration.equals("Month")) {
            day_next = 7;
            day = 0;
            hour = 0;
        }
        else if (duration.equals("Week")) {
            day_next = 1;
            day = 0;
            hour = 0;
        }
        else if (duration.equals("Day")) {
            hour_next = 1;
            hour = 0;
        }

        else if (duration.equals("Hour")) {
            minute_next = 1;
        }


        calendar.set(year, month, day, hour, minute, 0);
        Timestamp time_first = new Timestamp(calendar.getTimeInMillis());

        calendar.set(year, month+month_next, day+day_next, hour+hour_next, minute+minute_next, 0);
        Timestamp time_second = new Timestamp(calendar.getTimeInMillis());

        double energy = 0;

        while (time_first.getTime() < new Timestamp(System.currentTimeMillis()).getTime()) {
            try {
                energy = InfluxDBHandler.getAveragePowerConsumption
                        ("1m", time_first.getTime(), time_second.getTime(), "consumption");
            } catch (CouldNotPerformException e) {
                e.printStackTrace();
            }

            System.out.println(time_first);
            System.out.println(time_second);

            energy = minute;

            series1.getData().add(new XYChart.Data(String.valueOf(change), energy));

            change++;
            month += month_next;
            day += day_next;
            hour += hour_next;
            minute += minute_next;

            calendar.set(year, month, day, hour, minute, 0);
            time_first = new Timestamp(calendar.getTimeInMillis());

            calendar.set(year, month+month_next, day+day_next, hour+hour_next, minute+minute_next, 0);
            time_second = new Timestamp(calendar.getTimeInMillis());
        }
        bc.getData().addAll(series1);
    }
}
