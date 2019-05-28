package org.openbase.bco.bcozy.model;

import com.google.protobuf.Descriptors;
import org.openbase.jul.exception.CouldNotPerformException;
import org.influxdata.client.*;
import org.influxdata.query.FluxRecord;
import org.influxdata.query.FluxTable;

import java.util.List;

public class InfluxDBHandler {
    private static final String INFLUXDB_BUCKET_DEFAULT = "bco-persistence";
    private static final String INFLUXDB_URL_DEFAULT = "http://localhost:9999";
    private static final String INFLUXDB_ORG_DEFAULT = "openbase";
    private static final String INFLUXDB_ORG_ID_DEFAULT = "";
    private static final Integer READ_TIMEOUT = 60;
    private static final Integer WRITE_TIMEOUT = 60;
    private static final Integer CONNECT_TIMOUT = 40;
    private static final char[] TOKEN = "TOKEN".toCharArray();


    /**
     * Returns the average value of specific field from the power_consumption_state_service in a time window.
     * @param window Time interval in which the measurement is carried out (e.g every 1m, 1s, 1d ...)
     * @param field Name of the field which should be checked (e.g consumption, current, voltage)
     * @param timeStart Timestamp when the measurement should start
     * @param timeStop Timestamp when the measurement should stop
     * @return average value
     * @throws CouldNotPerformException
     */
    private static double getAveragePowerConsumption(String window, String field, Long timeStart, Long timeStop) throws CouldNotPerformException {


        InfluxDBClient influxDBClient = InfluxDBClientFactory
                .create(INFLUXDB_URL_DEFAULT + "?readTimeout=" + READ_TIMEOUT + "&connectTimeout=" + CONNECT_TIMOUT + "&writeTimeout=" + WRITE_TIMEOUT + "&logLevel=BASIC", TOKEN);

        if (influxDBClient.health().getStatus().getValue() != "pass") {
            throw new CouldNotPerformException("Could not connect to database server at " + INFLUXDB_URL_DEFAULT + "!");

        }


        QueryApi queryApi = influxDBClient.getQueryApi();
        String query = "from(bucket: \"" + INFLUXDB_BUCKET_DEFAULT + "\")" +
                " |> range(start: " + timeStart + ", stop: " + timeStop + ")" +
                " |> filter(fn: (r) => r._measurement == \"power_consumption_state_service\")" +
                " |> filter(fn: (r) => r._field == \"" + field + "\")" +
                " |> aggregateWindow(every:" + window + " , fn: mean)" +
                " |> mean(column: \"_value\")";


        List<FluxTable> tables = queryApi.query(query, INFLUXDB_ORG_ID_DEFAULT);
        // logger.info("----");

        for (FluxTable fluxTable : tables) {
            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord fluxRecord : records) {
                // just one entry:
               return (double) fluxRecord.getValueByKey("_value");


            }
        }

        return 0;

    }
}
