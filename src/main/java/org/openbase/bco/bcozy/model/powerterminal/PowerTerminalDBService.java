package org.openbase.bco.bcozy.model.powerterminal;

import eu.hansolo.tilesfx.chart.ChartData;
import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.DateRange;
import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.Interval;
import org.openbase.bco.bcozy.util.powerterminal.TimeLabelFormatter;
import org.openbase.bco.dal.remote.layer.unit.location.LocationRemote;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.type.domotic.database.RecordCollectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.openbase.bco.bcozy.controller.powerterminal.PowerChartVisualizationController.INFLUXDB_FIELD_CONSUMPTION;

/**
 * Service wrapping the InfluxDBHandler, providing a high level interface, keeping MVC model tasks where they belong.
 */
public class PowerTerminalDBService {
    public static final long FIVE_MINUTES_IN_MILLISECONDS = 60000;
    private static final Logger LOGGER = LoggerFactory.getLogger(PowerTerminalDBService.class);
    private static final LocationRemote locationRemote = new LocationRemote();
    public static final String UNIT_ID_GLOBAL_CONSUMPTION = "Global Consumption";
    public static final String UNIT_ID_LOCATION_CONSUMPTION = "Location Consumption";


    /**
     * Returns TilesFX Chartdata of the average power consumption during the given DateRange
     *
     * @param dateRange DateRange in about which the database will be queried
     * @param unitId
     * @return Data about the average power consumption
     */
    public static List<ChartData> getAverageConsumption(DateRange dateRange, String unitId) {
        Timestamp startTime = dateRange.getStartDateAtCurrentTime();
        Timestamp endTime = dateRange.getEndDateAtCurrentTime();
        if (dateRange.isEmpty()) {
            return getChartData(dateRange.getDefaultIntervalSize(), startTime, unitId);
        } else {
            return getChartData(dateRange.getDefaultIntervalSize(), startTime, endTime, unitId);
        }
    }

    private static List<ChartData> getChartData(Interval intervalSize, Timestamp startAndEndTime, String unitId) {
        long timeInSeconds = TimeUnit.MILLISECONDS.toSeconds(startAndEndTime.getTime());
        List<ChartData> data = new ArrayList<>();

//        try {
//            double value = 0;
//            if (unitId.equals(UNIT_ID_GLOBAL_CONSUMPTION)) {
//                unitId = Units.getUnitRegistry().getRootLocationConfig().getId();
//            }
//            PowerConsumptionSensorRemote consumer = Units.getUnit(unitId, false, Units.POWER_CONSUMPTION_SENSOR);
//            value = consumer.getPowerConsumptionState().getConsumption();
//            data.add(new ChartData(TimeLabelFormatter.createTimeLabel(startAndEndTime, 0, intervalSize), value));
//        } catch (CouldNotPerformException | InterruptedException e) {
//            ExceptionPrinter.printHistory("Could not load datum!", e, LOGGER);
//        }

        try {
            double value = 0;
            if (unitId.equals(UNIT_ID_GLOBAL_CONSUMPTION)) {
                value = locationRemote.getAveragePowerConsumption(intervalSize.getInfluxIntervalString(),
                        timeInSeconds - FIVE_MINUTES_IN_MILLISECONDS, timeInSeconds, INFLUXDB_FIELD_CONSUMPTION).get().getRecord(0).getValue();
            } else {
                value = locationRemote.getAveragePowerConsumption(intervalSize.getInfluxIntervalString(), unitId,
                        timeInSeconds - FIVE_MINUTES_IN_MILLISECONDS, timeInSeconds, INFLUXDB_FIELD_CONSUMPTION).get().getRecord(0).getValue();
            }
            data.add(new ChartData(TimeLabelFormatter.createTimeLabel(startAndEndTime, 0, intervalSize), value));
        } catch (InterruptedException | ExecutionException) {
            ExceptionPrinter.printHistory("Could not load datum!", e, LOGGER);
        }
        return data;
    }

    private static List<ChartData> getChartData(Interval intervalSize, Timestamp startTime, Timestamp endTime, String unitId) {
        List<ChartData> data = new ArrayList<>();
        String interval = intervalSize.getInfluxIntervalString();
        long startTimeInSeconds = TimeUnit.MILLISECONDS.toSeconds(startTime.getTime());
        long endTimeInSeconds = TimeUnit.MILLISECONDS.toSeconds(endTime.getTime());
        try {
            RecordCollectionType.RecordCollection fluxTablesCollection;
            if (unitId.equals(UNIT_ID_GLOBAL_CONSUMPTION)) {
                fluxTablesCollection = locationRemote.getAveragePowerConsumptionTables(
                        interval, startTimeInSeconds, endTimeInSeconds, INFLUXDB_FIELD_CONSUMPTION).get();
            } else {
                fluxTablesCollection = locationRemote.getAveragePowerConsumptionTables(
                        interval, unitId, startTimeInSeconds, endTimeInSeconds, INFLUXDB_FIELD_CONSUMPTION).get();
                ;
            }
            data = convertToChartDataList(intervalSize, startTime, fluxTablesCollection);
        } catch (InterruptedException | ExecutionException e) {
            ExceptionPrinter.printHistory("Could not load chart data!", e, LOGGER);
        }
        return data;
    }

    private static List<ChartData> convertToChartDataList(Interval intervalSize, Timestamp startTime, RecordCollectionType.RecordCollection fluxTablesCollection) {
        List<ChartData> data = new ArrayList<>();
        if (fluxTablesCollection.getRecordList().size() == 0) {
            data.add(new ChartData("No Data!", 0));
        }

        for (int i = 0; i < fluxTablesCollection.getRecordList().size(); i++) {
            Double value = fluxTablesCollection.getRecord(i).getValue();
            if (value == null) {
                value = 0.0;
            }

            data.add(i, new ChartData(TimeLabelFormatter.createTimeLabel(startTime, i, intervalSize), value));
        }

        return data;
    }


}






