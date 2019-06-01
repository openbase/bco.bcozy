package org.openbase.bco.bcozy.model;

import org.openbase.jul.exception.CouldNotPerformException;
import org.influxdata.client.*;
import org.influxdata.query.FluxRecord;
import org.influxdata.query.FluxTable;
import org.slf4j.LoggerFactory;
import org.influxdata.client.*;

import java.util.List;
import java.util.logging.Logger;

public class InfluxDBHandler {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(InfluxDBHandler.class);
    private static final String INFLUXDB_BUCKET_DEFAULT = "bco-persistence";
    private static final String INFLUXDB_URL_DEFAULT = "http://localhost:9999";
    private static final String INFLUXDB_ORG_DEFAULT = "openbase";
    private static final String INFLUXDB_ORG_ID_DEFAULT = "";
    private static final Integer READ_TIMEOUT = 60;
    private static final Integer WRITE_TIMEOUT = 60;
    private static final Integer CONNECT_TIMOUT = 40;
    private static final char[] TOKEN = "TOKEN".toCharArray();


    /**
     * Sends a query to the database.
     * @param query Flux query.
     * @return List of FluxTables with the result
     * @throws CouldNotPerformException
     */
    private static List<FluxTable> sendQuery(String query) throws CouldNotPerformException {
        InfluxDBClient influxDBClient = InfluxDBClientFactory
                .create(INFLUXDB_URL_DEFAULT + "?readTimeout=" + READ_TIMEOUT + "&connectTimeout=" + CONNECT_TIMOUT + "&writeTimeout=" + WRITE_TIMEOUT + "&logLevel=BASIC", TOKEN);

        if (influxDBClient.health().getStatus().getValue() != "pass") {
            throw new CouldNotPerformException("Could not connect to database server at " + INFLUXDB_URL_DEFAULT + "!");

        }
        QueryApi queryApi = influxDBClient.getQueryApi();


        List<FluxTable> tables = queryApi.query(query, INFLUXDB_ORG_ID_DEFAULT);
        // logger.info("----");
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
        return getSingleAverageValueFromTables(tables);


    }

    /**
     * Returns the   value from a getAverageQuery  (only one value)
     *
     * @param tables Tables which a query returned
     * @return
     */
    private static Double getSingleAverageValueFromTables(List<FluxTable> tables) {
        for (FluxTable fluxTable : tables) {
            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord fluxRecord : records) {
                // just one entry:

                return (Double) fluxRecord.getValueByKey("_value");

            }
        }

        return Double.valueOf(0);

    }

    /**
     * Returns the average value of specific field from the power_consumption_state_service in a time window.
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
        return getSingleAverageValueFromTables(tables);


    }
}
