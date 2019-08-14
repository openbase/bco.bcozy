package org.openbase.bco.bcozy.model.powerterminal;

import eu.hansolo.tilesfx.chart.ChartData;
import eu.hansolo.tilesfx.events.ChartDataEventListener;
import org.influxdata.query.FluxRecord;
import org.influxdata.query.FluxTable;
import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.DateRange;
import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.Interval;
import org.openbase.bco.bcozy.model.InfluxDBHandler;
import org.openbase.bco.bcozy.util.powerterminal.TimeLabelFormatter;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.type.domotic.unit.UnitConfigType;
import org.openbase.type.domotic.unit.UnitConfigType.UnitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.openbase.bco.bcozy.controller.powerterminal.PowerChartVisualizationController.INFLUXDB_FIELD_CONSUMPTION;

/**
 * Service wrapping the InfluxDBHandler, providing a high level interface, keeping MVC model tasks where they belong.
 */
public class PowerTerminalDBService {
    public static final long FIVE_MINUTES_IN_MILLISECONDS = 60000;
    private static final Logger LOGGER = LoggerFactory.getLogger(PowerTerminalDBService.class);
    public static final String UNIT_ID_GLOBAL_CONSUMPTION = "Global Consumption";
    public static final String UNIT_ID_LOCATION_CONSUMPTION = "Location Consumption";
    public static final String SUM_CHILDREN_CONSUMPTION_PREFIX = "SUM";
    public static final String PREFIX_DELIM = " ";

    /**
     * Returns TilesFX Chartdata of the average power consumption during the given DateRange
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
        try {
            double value = 0;
            if (unitId.equals(UNIT_ID_GLOBAL_CONSUMPTION)) {
                value = InfluxDBHandler.getAveragePowerConsumption(intervalSize.getInfluxIntervalString(),
                        timeInSeconds - FIVE_MINUTES_IN_MILLISECONDS, timeInSeconds, INFLUXDB_FIELD_CONSUMPTION);
            } else if (unitId.split(PREFIX_DELIM)[0].equals(SUM_CHILDREN_CONSUMPTION_PREFIX)) {
                List<String> subUnitIds = PowerTerminalRegistryService.getConsumers(unitId.split(PREFIX_DELIM)[1]).stream().map(UnitConfig::getId).collect(Collectors.toList());
                for (String subUnitId : subUnitIds) {
                    value += InfluxDBHandler.getAveragePowerConsumption(intervalSize.getInfluxIntervalString(), subUnitId,
                            timeInSeconds - FIVE_MINUTES_IN_MILLISECONDS, timeInSeconds, INFLUXDB_FIELD_CONSUMPTION);
                }
            } else {
                value = InfluxDBHandler.getAveragePowerConsumption(intervalSize.getInfluxIntervalString(), unitId,
                        timeInSeconds - FIVE_MINUTES_IN_MILLISECONDS, timeInSeconds, INFLUXDB_FIELD_CONSUMPTION);
            }
            data.add(new ChartData(TimeLabelFormatter.createTimeLabel(startAndEndTime, 0, intervalSize), value));
        } catch (CouldNotPerformException e) {
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
            List<FluxTable> fluxTables;
            if (unitId.equals(UNIT_ID_GLOBAL_CONSUMPTION)) {
                fluxTables = InfluxDBHandler.getAveragePowerConsumptionTables(
                        interval, startTimeInSeconds, endTimeInSeconds, INFLUXDB_FIELD_CONSUMPTION);
            }  else if (unitId.split(PREFIX_DELIM)[0].equals(SUM_CHILDREN_CONSUMPTION_PREFIX)) {
                List<String> subUnitIds = PowerTerminalRegistryService.getConsumers(unitId.split(PREFIX_DELIM)[1]).stream().map(UnitConfig::getId).collect(Collectors.toList());
                System.out.println("\n\n=======================================> Subunits found: " + subUnitIds.size());
                List<List<ChartData>> datas = new ArrayList<>();
                for (String subUnitId : subUnitIds) {
                    fluxTables = InfluxDBHandler.getAveragePowerConsumptionTables(
                            interval, subUnitId, startTimeInSeconds, endTimeInSeconds, INFLUXDB_FIELD_CONSUMPTION);
                    List<ChartData> subUnitData = convertToChartDataList(intervalSize, startTime, fluxTables);
                    datas.add(subUnitData);
                }
                return dimensionalCollapse(datas);
            } else {
                fluxTables = InfluxDBHandler.getAveragePowerConsumptionTables(
                        interval, unitId, startTimeInSeconds, endTimeInSeconds, INFLUXDB_FIELD_CONSUMPTION);
            }
            data = convertToChartDataList(intervalSize, startTime, fluxTables);
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory("Could not load chart data!", e, LOGGER);
        }
        return data;
    }

    private static List<ChartData> convertToChartDataList(Interval intervalSize, Timestamp startTime, List<FluxTable> fluxTables) {
        List<ChartData> data = new ArrayList<>();
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
                data.add(i, new ChartData(TimeLabelFormatter.createTimeLabel(startTime, i, intervalSize), value));
            }
        }
        return data;
    }

    private static List<ChartData> dimensionalCollapse(List<List<ChartData>> inputData) {
        List<ChartData> outputData = inputData.get(0);
        for (int i = 1; i < inputData.size(); i++){
            for (int j = 0; j < inputData.get(i).size(); j++) {
                outputData.get(j).setValue(outputData.get(j).getValue() + inputData.get(i).get(j).getValue());
            }
        }
        return outputData;
    }


}

