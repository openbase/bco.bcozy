package org.openbase.bco.bcozy.controller.powerterminal;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.visual.javafx.control.AbstractFXController;

public class PowerBarChartVisualizationController extends AbstractFXController  {
    @FXML
    BarChart<Number, String> barChart;

    @FXML
    NumberAxis xAxis = new NumberAxis();
    @FXML
    CategoryAxis yAxis = new CategoryAxis();

    public PowerBarChartVisualizationController() {}

    @Override
    public void updateDynamicContent() throws CouldNotPerformException {

    }

    @Override
    public void initContent() throws InitializationException {
        /* try {
            System.out.println("test, test, test");
            System.out.println(InfluxDBHandler.getAveragePowerConsumption("1m", (long)1559632071, (long)1559632871, "consumption"));
        } catch (CouldNotPerformException e) {
            e.printStackTrace();
        } */
        xAxis.setLabel("Percent");
        xAxis.setTickLabelRotation(90);
        yAxis.setLabel("Performance");

        barChart = new BarChart<Number,String>(xAxis,yAxis);

        XYChart.Series series1 = new XYChart.Series();
        series1.getData().add(new XYChart.Data(80, "test"));
        barChart.getData().add(series1);
    }
}
