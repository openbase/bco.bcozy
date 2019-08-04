package org.openbase.bco.bcozy.model.powerterminal;

import eu.hansolo.tilesfx.chart.ChartData;
import org.influxdata.query.FluxRecord;
import org.influxdata.query.FluxTable;
import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.DateRange;
import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.Granularity;
import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.Interval;
import org.openbase.bco.bcozy.model.InfluxDBHandler;
import org.openbase.bco.bcozy.util.powerterminal.TimeLabelFormatter;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.openbase.bco.bcozy.controller.powerterminal.PowerChartVisualizationController.INFLUXDB_FIELD_CONSUMPTION;

/**
 * Service wrapping the InfluxDBHandler, providing a high level interface, keeping MVC model tasks where they belong.
 */
public class PowerTerminalDBService {
    public static final long FIVE_MINUTES_IN_MILLISECONDS = 60000;
    private static final Logger LOGGER = LoggerFactory.getLogger(PowerTerminalDBService.class);

    /**
     * Returns TilesFX Chartdata of the average power consumption during the given DateRange
     * @param dateRange DateRange in about which the database will be queried
     * @param granularity
     * @return Data about the average power consumption
     */
    public static List<ChartData> getAverageConsumptionForDateRangeAndGranularity(DateRange dateRange, Granularity granularity) {
        Timestamp startTime = dateRange.getStartDateAtCurrentTime();
        Timestamp endTime = dateRange.getEndDateAtCurrentTime();
        if (dateRange.isEmpty()) {
            return getChartData(dateRange.getDefaultIntervalSize(), startTime);
        } else {
            return getChartData(dateRange.getDefaultIntervalSize(), startTime, endTime);
        }
    }

    private static List<ChartData> getChartData(Interval intervalSize, Timestamp startAndEndTime) {
        long timeInSeconds = TimeUnit.MILLISECONDS.toSeconds(startAndEndTime.getTime());
        List<ChartData> data = new ArrayList<>();
        try {
            double value = InfluxDBHandler.getAveragePowerConsumption(intervalSize.getInfluxIntervalString(), timeInSeconds - FIVE_MINUTES_IN_MILLISECONDS,
                    timeInSeconds, INFLUXDB_FIELD_CONSUMPTION);
            System.out.println("Value of now: " + value);
            data.add(new ChartData(TimeLabelFormatter.createTimeLabel(startAndEndTime, 0, intervalSize), value));
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory("Could not load datum!", e, LOGGER);
        }
        return data;
    }

    private static List<ChartData> getChartData(Interval intervalSize, Timestamp startTime, Timestamp endTime) {
        List<ChartData> data = new ArrayList<>();
        String interval = intervalSize.getInfluxIntervalString();
        long startTimeInSeconds = TimeUnit.MILLISECONDS.toSeconds(startTime.getTime());
        long endTimeInSeconds = TimeUnit.MILLISECONDS.toSeconds(endTime.getTime());
        try {
            List<FluxTable> fluxTables = InfluxDBHandler.getAveragePowerConsumptionTables(
                    interval, startTimeInSeconds, endTimeInSeconds, INFLUXDB_FIELD_CONSUMPTION);
            if (fluxTables.size() == 0) {
                data.add(new ChartData("No Data!", 0));
            }
            for (int i = 0; i < fluxTables.size(); i++) {
                List<FluxRecord> records = fluxTables.get(i).getRecords();
                for (FluxRecord fluxRecord : records) {
                    Double value = (Double) fluxRecord.getValueByKey("_value");
                    if (value == null) {
                        value = 0.0;
                    }
                    data.add(new ChartData(TimeLabelFormatter.createTimeLabel(startTime, i, intervalSize), value));
                }
            }
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory("Could not load chart data!", e, LOGGER);
        }
        return data;
    }
}

