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
    private static final String INFLUXDB_ORG_ID_DEFAULT = "03d9ff2d05e4b000";
    private static final Integer READ_TIMEOUT = 60;
    private static final Integer WRITE_TIMEOUT = 60;
    private static final Integer CONNECT_TIMOUT = 40;
    private static final char[] TOKEN = "L8Z1fNDp5F2dvGbDkgyUgIeqYwi5ot54sCWR5WnCNK9NC5ur-SKYjTCfNSsEIGPeVPxtAtR7quLlsZpjkGYtbA==".toCharArray();


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
    public static double getAveragePowerConsumption(String window, Long timeStart, Long timeStop, String field) throws CouldNotPerformException {
        LOGGER.info("get average");

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
                " |> group(columns: [\"_field\"], mode:\"by\")" +
                " |> mean(column: \"_value\")";
        LOGGER.info(query);


        List<FluxTable> tables = queryApi.query(query, INFLUXDB_ORG_ID_DEFAULT);
        // logger.info("----");

        for (FluxTable fluxTable : tables) {
            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord fluxRecord : records) {
                return (double) fluxRecord.getValueByKey("_value");
                // just one entry:
                //return (double) fluxRecord.getValueByKey("_value");


            }
        }

        return 0;

    }


}
