package org.openbase.bco.bcozy.model;

import org.openbase.bco.authentication.lib.jp.JPBCOHomeDirectory;
import org.openbase.jps.core.JPService;
import org.openbase.jul.exception.CouldNotPerformException;
import org.influxdata.client.*;
import org.influxdata.query.FluxRecord;
import org.influxdata.query.FluxTable;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

public class InfluxDBHandler {

    //todo: move to module dal, see openbase/bco.dal#151
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(InfluxDBHandler.class);
    private static final String INFLUXDB_BUCKET_DEFAULT = "bco-persistence";
    private static final Integer READ_TIMEOUT = 60;
    private static final Integer WRITE_TIMEOUT = 60;
    private static final Integer CONNECT_TIMOUT = 40;
    private static final char[] TOKEN;
    private static final String INFLUXDB_ORG_ID;
    private static final String INFLUXDB_URL;
    private static final String INFLUXDB_PROPERTIES = "influxdb.properties";
    private static Properties influxDBProperties = new Properties();

    static {
        loadInfluxDBProperties();
        TOKEN = influxDBProperties.get("token").toString().toCharArray();
        INFLUXDB_ORG_ID = influxDBProperties.get("influxdb_org_id").toString();
        INFLUXDB_URL = influxDBProperties.get("influxdb_url").toString();
    }

    private static void loadInfluxDBProperties() {
        try {
            final File propertiesFile = new File(JPService.getProperty(JPBCOHomeDirectory.class).getValue(), INFLUXDB_PROPERTIES);
            if (propertiesFile.exists()) {
                influxDBProperties.load(new FileInputStream(propertiesFile));
                LOGGER.debug("Load influxdb properties from " + propertiesFile.getAbsolutePath());
            }
        } catch (Exception ex) {
            ExceptionPrinter.printHistory("No influxdb properties found!", ex, LOGGER);
        }
    }

    /**
     * Sends a query to the database.
     *
     * @param query Flux query.
     * @return List of FluxTables with the result
     * @throws CouldNotPerformException
     */
    private static List<FluxTable> sendQuery(String query) throws CouldNotPerformException {
        LOGGER.info(query);
        InfluxDBClient influxDBClient = InfluxDBClientFactory
                .create(INFLUXDB_URL + "?readTimeout=" + READ_TIMEOUT + "&connectTimeout=" + CONNECT_TIMOUT + "&writeTimeout=" + WRITE_TIMEOUT + "&logLevel=BASIC", TOKEN);

        if (influxDBClient.health().getStatus().getValue() != "pass") {
            throw new CouldNotPerformException("Could not connect to database server at " + INFLUXDB_URL + "!");
        }
        QueryApi queryApi = influxDBClient.getQueryApi();
        List<FluxTable> tables = queryApi.query(query, INFLUXDB_ORG_ID);
        try {
            if (influxDBClient != null) {
                influxDBClient.close();
            }
        } catch (Exception ex) {
            ExceptionPrinter.printHistory("Could not shutdown database connection!", ex, LOGGER);
        }
        LOGGER.info(tables.toString());
        return tables;
    }

    /**
     * Some query return just a single value.
     * This Method returns it.
     *
     * @param tables Tables which a query returned
     * @return
     */
    private static Double getSingleValueFromTables(List<FluxTable> tables) {
        for (FluxTable fluxTable : tables) {
            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord fluxRecord : records) {
                // just one entry:
                LOGGER.info(fluxRecord.getValueByKey("_value").toString());
                return (Double) fluxRecord.getValueByKey("_value");
            }
        }
        return Double.valueOf(0);
    }

    /**
     * Returns the average value of specific field from the power_consumption_state_service in a time window.
     *
     * @param window    Time interval in which the measurement is carried out (e.g every 1m, 1s, 1d ...)
     * @param field     Name of the field which should be checked (e.g consumption, current, voltage)
     * @param timeStart Timestamp when the measurement should start
     * @param timeStop  Timestamp when the measurement should stop
     * @return average value
     * @throws CouldNotPerformException
     */
    public static List<FluxTable> getAveragePowerConsumptionTables(String window, Long timeStart, Long timeStop, String field) throws CouldNotPerformException {
        String query = "from(bucket: \"" + INFLUXDB_BUCKET_DEFAULT + "\")" +
                " |> range(start: " + timeStart + ", stop: " + timeStop + ")" +
                " |> filter(fn: (r) => r._measurement == \"power_consumption_state_service\")" +
                " |> filter(fn: (r) => r._field == \"" + field + "\")" +
                " |> aggregateWindow(every:" + window + " , fn: mean)" +
                " |> group(columns: [\"_time\"], mode:\"by\")" +
                "|> mean(column: \"_value\")";

        List<FluxTable> tables = sendQuery(query);
        LOGGER.info(tables.toString());
        return tables;
    }

    /**
     * Returns the average value of specific field from the power_consumption_state_service in a time window.
     *
     * @param window    Time interval in which the measurement is carried out (e.g every 1m, 1s, 1d ...)
     * @param field     Name of the field which should be checked (e.g consumption, current, voltage)
     * @param timeStart Timestamp when the measurement should start
     * @param timeStop  Timestamp when the measurement should stop
     * @return average value
     * @throws CouldNotPerformException
     */
    public static Double getAveragePowerConsumption(String window, Long timeStart, Long timeStop, String field) throws CouldNotPerformException {

        String query = "from(bucket: \"" + INFLUXDB_BUCKET_DEFAULT + "\")" +
                " |> range(start: " + timeStart + ", stop: " + timeStop + ")" +
                " |> filter(fn: (r) => r._measurement == \"power_consumption_state_service\")" +
                " |> filter(fn: (r) => r._field == \"" + field + "\")" +
                " |> aggregateWindow(every:" + window + " , fn: mean)" +
                " |> group(columns: [\"_field\"], mode:\"by\")" +
                " |> mean(column: \"_value\")";

        List<FluxTable> tables = sendQuery(query);
        return getSingleValueFromTables(tables);
    }

    /**
     * Returns the average value of specific field and location from the power_consumption_state_service in a time window.
     *
     * @param window         Time interval in which the measurement is carried out (e.g every 1m, 1s, 1d ...)
     * @param field          Name of the field which should be checked (e.g consumption, current, voltage)
     * @param timeStart      Timestamp when the measurement should start
     * @param timeStop       Timestamp when the measurement should stop
     * @param location_alias Alias of location which should be filtered
     * @return average value
     * @throws CouldNotPerformException
     */
    public static Double getAveragePowerConsumption(String window, Long timeStart, Long timeStop, String field, String location_alias) throws CouldNotPerformException {

        String query = "from(bucket: \"" + INFLUXDB_BUCKET_DEFAULT + "\")" +
                " |> range(start: " + timeStart + ", stop: " + timeStop + ")" +
                " |> filter(fn: (r) => r._measurement == \"power_consumption_state_service\")" +
                " |> filter(fn: (r) => r._field == \"" + field + "\")" +
                " |> filter(fn: (r) => r.location_alias == \"" + location_alias + "\")" +
                " |> aggregateWindow(every:" + window + " , fn: mean)" +
                " |> group(columns: [\"_field\"], mode:\"by\")" +
                " |> mean(column: \"_value\")";

        List<FluxTable> tables = sendQuery(query);
        return getSingleValueFromTables(tables);
    }

    /**
     * Returns the average value of specific field  and unit from the power_consumption_state_service in a time window.
     *
     * @param window    Time interval in which the measurement is carried out (e.g every 1m, 1s, 1d ...)
     * @param unit_id   Id of Unit
     * @param field     Name of the field which should be checked (e.g consumption, current, voltage)
     * @param timeStart Timestamp when the measurement should start
     * @param timeStop  Timestamp when the measurement should stop
     * @return average value
     * @throws CouldNotPerformException
     */
    public static Double getAveragePowerConsumption(String window, String unit_id, Long timeStart, Long timeStop, String field) throws CouldNotPerformException {

        String query = "from(bucket: \"" + INFLUXDB_BUCKET_DEFAULT + "\")" +
                " |> range(start: " + timeStart + ", stop: " + timeStop + ")" +
                " |> filter(fn: (r) => r._measurement == \"power_consumption_state_service\")" +
                " |> filter(fn: (r) => r._field == \"" + field + "\")" +
                " |> filter(fn: (r) => r.unit_id == \"" + unit_id + "\")" +
                " |> aggregateWindow(every:" + window + " , fn: mean)" +
                " |> group(columns: [\"_field\"], mode:\"by\")" +
                " |> mean(column: \"_value\")";

        List<FluxTable> tables = sendQuery(query);
        return getSingleValueFromTables(tables);
    }

    /**
     * Get the full data from a field from the power_consumption_state_service in a specific time window.
     *
     * @param timeStart Timestamp when the measurement should start
     * @param timeStop  Timestamp when the measurement should stop
     * @param field     Name of the field which should be checked (e.g consumption, current, voltage)
     * @return List of FluxTables
     * @throws CouldNotPerformException
     */
    public static List<FluxTable> getPowerConsumption(Long timeStart, Long timeStop, String field) throws CouldNotPerformException {
        String query = "from(bucket: \"" + INFLUXDB_BUCKET_DEFAULT + "\")" +
                " |> range(start: " + timeStart + ", stop: " + timeStop + ")" +
                " |> filter(fn: (r) => r._measurement == \"power_consumption_state_service\")" +
                " |> filter(fn: (r) => r._field == \"" + field + "\")" +
                " |> group(columns: [\"_field\"], mode:\"by\")";

        List<FluxTable> tables = sendQuery(query);
        return tables;
    }

    /**
     * Get the full data of a field and location_alias from the power_consumption_state_service in a specific time window.
     *
     * @param timeStart      Timestamp when the measurement should start
     * @param timeStop       Timestamp when the measurement should stop
     * @param location_alias Alias of location which should be filtered
     * @param field          Name of the field which should be checked (e.g consumption, current, voltage)
     * @return List of FluxTables
     * @throws CouldNotPerformException
     */
    public static List<FluxTable> getPowerConsumption(Long timeStart, Long timeStop, String field, String location_alias) throws CouldNotPerformException {
        String query = "from(bucket: \"" + INFLUXDB_BUCKET_DEFAULT + "\")" +
                " |> range(start: " + timeStart + ", stop: " + timeStop + ")" +
                " |> filter(fn: (r) => r._measurement == \"power_consumption_state_service\")" +
                " |> filter(fn: (r) => r._field == \"" + field + "\")" +
                " |> filter(fn: (r) => r.location_alias == \"" + location_alias + "\")" +
                " |> group(columns: [\"_field\"], mode:\"by\")";

        List<FluxTable> tables = sendQuery(query);
        return tables;
    }

    /**
     * Get the full data of a field and location_alias from the power_consumption_state_service in a specific time window.
     *
     * @param timeStart Timestamp when the measurement should start
     * @param timeStop  Timestamp when the measurement should stop
     * @param unit_id   Id of Unit
     * @param field     Name of the field which should be checked (e.g consumption, current, voltage)
     * @return List of FluxTables
     * @throws CouldNotPerformException
     */
    public static List<FluxTable> getPowerConsumption(String unit_id, Long timeStart, Long timeStop, String field) throws CouldNotPerformException {
        String query = "from(bucket: \"" + INFLUXDB_BUCKET_DEFAULT + "\")" +
                " |> range(start: " + timeStart + ", stop: " + timeStop + ")" +
                " |> filter(fn: (r) => r._measurement == \"power_consumption_state_service\")" +
                " |> filter(fn: (r) => r._field == \"" + field + "\")" +
                " |> filter(fn: (r) => r.unit_id == \"" + unit_id + "\")" +
                " |> group(columns: [\"_field\"], mode:\"by\")";

        List<FluxTable> tables = sendQuery(query);
        return tables;
    }

    /**
     * Get the total sum of a field from the power_consumption_state_service in a specific time window.
     *
     * @param timeStart Timestamp when the measurement should start
     * @param timeStop  Timestamp when the measurement should stop
     * @param field     Name of the field which should be checked (e.g consumption, current, voltage)
     * @return Total sum of the field
     * @throws CouldNotPerformException
     */
    public static Double getPowerConsumptionTotal(Long timeStart, Long timeStop, String field) throws CouldNotPerformException {
        String query = "from(bucket: \"" + INFLUXDB_BUCKET_DEFAULT + "\")" +
                " |> range(start: " + timeStart + ", stop: " + timeStop + ")" +
                " |> filter(fn: (r) => r._measurement == \"power_consumption_state_service\")" +
                " |> filter(fn: (r) => r._field == \"" + field + "\")" +
                " |> group(columns: [\"_field\"], mode:\"by\")" +
                " |> sum(column: \"_value\")";

        List<FluxTable> tables = sendQuery(query);
        return getSingleValueFromTables(tables);
    }

    /**
     * Get the total sum of a field from the power_consumption_state_service in a specific time window.
     *
     * @param timeStart      Timestamp when the measurement should start
     * @param timeStop       Timestamp when the measurement should stop
     * @param location_alias Alias of location which should be filtered
     * @param field          Name of the field which should be checked (e.g consumption, current, voltage)
     * @return Total sum of the field
     * @throws CouldNotPerformException
     */
    public static Double getPowerConsumptionTotal(Long timeStart, Long timeStop, String field, String location_alias) throws CouldNotPerformException {
        String query = "from(bucket: \"" + INFLUXDB_BUCKET_DEFAULT + "\")" +
                " |> range(start: " + timeStart + ", stop: " + timeStop + ")" +
                " |> filter(fn: (r) => r._measurement == \"power_consumption_state_service\")" +
                " |> filter(fn: (r) => r._field == \"" + field + "\")" +
                " |> filter(fn: (r) => r.location_alias == \"" + location_alias + "\")" +
                " |> group(columns: [\"_field\"], mode:\"by\")" +
                " |> sum(column: \"_value\")";

        List<FluxTable> tables = sendQuery(query);
        return getSingleValueFromTables(tables);
    }

    /**
     * Get the total sum of a field from the power_consumption_state_service in a specific time window.
     *
     * @param timeStart Timestamp when the measurement should start
     * @param timeStop  Timestamp when the measurement should stop
     * @param unit_id   Id of Unit
     * @param field     Name of the field which should be checked (e.g consumption, current, voltage)
     * @return Total sum of the field
     * @throws CouldNotPerformException
     */
    public static Double getPowerConsumptionTotal(String unit_id, Long timeStart, Long timeStop, String field) throws CouldNotPerformException {
        String query = "from(bucket: \"" + INFLUXDB_BUCKET_DEFAULT + "\")" +
                " |> range(start: " + timeStart + ", stop: " + timeStop + ")" +
                " |> filter(fn: (r) => r._measurement == \"power_consumption_state_service\")" +
                " |> filter(fn: (r) => r._field == \"" + field + "\")" +
                " |> filter(fn: (r) => r.unit_id == \"" + unit_id + "\")" +
                " |> group(columns: [\"_field\"], mode:\"by\")" +
                " |> sum(column: \"_value\")";

        List<FluxTable> tables = sendQuery(query);
        return getSingleValueFromTables(tables);
    }
}
