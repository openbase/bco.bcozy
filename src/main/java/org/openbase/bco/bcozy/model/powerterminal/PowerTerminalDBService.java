package org.openbase.bco.bcozy.model.powerterminal;

import eu.hansolo.tilesfx.chart.ChartData;
import org.influxdata.query.FluxRecord;
import org.influxdata.query.FluxTable;
import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.DateRange;
import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.Interval;
import org.openbase.bco.bcozy.model.InfluxDBHandler;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.storage.registry.Registry;
import org.openbase.type.domotic.unit.UnitTemplateType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.openbase.bco.bcozy.controller.powerterminal.PowerChartVisualizationController.INFLUXDB_FIELD_CONSUMPTION;

/**
 * Service wrapping the InfluxDBHandler, providing a high level interface, keeping MVC model tasks where they belong.
 */
public class PowerTerminalDBService {
//        Registries.getUnitRegistry().getUnitConfigsByUnitType(UnitTemplateType.UnitTemplate.UnitType.POWER_CONSUMPTION_SENSOR); mit .getID bekommt man Unit ID die man in DB werfen kann
//        Registries.getUnitRegistry().getUnitConfigsByUnitType(UnitTemplateType.UnitTemplate.UnitType.LOCATION);
    public static final long FIVE_MINUTES_IN_MILLISECONDS = 60000;
    private static final Logger LOGGER = LoggerFactory.getLogger(PowerTerminalDBService.class);

    /**
     * Returns TilesFX Chartdata of the average power consumption during the given DateRange
     * @param dateRange DateRange in about which the database will be queried
     * @return Data about the average power consumption
     */
    public static List<ChartData> getAverageConsumptionForDateRange(DateRange dateRange) {
        Timestamp startTime = dateRange.getStartDateAtCurrentTime();
        Timestamp endTime = dateRange.getEndDateAtCurrentTime();
        if (dateRange.isEmpty()) {
            return getChartData(dateRange.getDefaultIntervalSize(), startTime);
        } else {
            return getChartData(dateRange.getDefaultIntervalSize(), startTime, endTime);
        }
    }

    private static List<ChartData> getChartData(Interval defaultIntervalSize, Timestamp startAndEndTime) {
        long timeInSeconds = TimeUnit.MILLISECONDS.toSeconds(startAndEndTime.getTime());
        List<ChartData> data = new ArrayList<>();
        try {
            double value = InfluxDBHandler.getAveragePowerConsumption(defaultIntervalSize.getInfluxIntervalString(), timeInSeconds - FIVE_MINUTES_IN_MILLISECONDS,
                    timeInSeconds, INFLUXDB_FIELD_CONSUMPTION);
            System.out.println("Value of now: " + value);
            data.add(new ChartData("Now", value));
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory("Could not load datum!", e, LOGGER);
        }
        return data;
    }

    private static List<ChartData> getChartData(Interval defaultIntervalSize, Timestamp startTime, Timestamp endTime) {
        List<ChartData> data = new ArrayList<>();
        String interval = defaultIntervalSize.getInfluxIntervalString();
        long startTimeInSeconds = TimeUnit.MILLISECONDS.toSeconds(startTime.getTime());
        long endTimeInSeconds = TimeUnit.MILLISECONDS.toSeconds(endTime.getTime());
        try {
            List<FluxTable> fluxTables = InfluxDBHandler.getAveragePowerConsumptionTables(
                    interval, startTimeInSeconds, endTimeInSeconds, INFLUXDB_FIELD_CONSUMPTION);
            for (int i = 0; i < fluxTables.size(); i++) {
                List<FluxRecord> records = fluxTables.get(i).getRecords();
                for (FluxRecord fluxRecord : records) {
                    Double value = (Double) fluxRecord.getValueByKey("_value");
                    if (value == null) {
                        value = 0.0;
                    }
                    data.add(new ChartData(createTimeLabel(startTime, i), value));
                }
            }
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory("Could not load chart data!", e, LOGGER);
        }
        return data;
    }

    private static String createTimeLabel(Timestamp time, int shift) {
        LocalTime timeLabel = time.toLocalDateTime().toLocalTime().plusHours(shift + 1);
        timeLabel = timeLabel.plusHours(timeLabel.getMinute() > 29 ? 1 : 0).truncatedTo(ChronoUnit.HOURS);
        return timeLabel.format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}

