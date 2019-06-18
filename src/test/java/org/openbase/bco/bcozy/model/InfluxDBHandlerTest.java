package org.openbase.bco.bcozy.model;

import org.junit.Ignore;
import org.junit.Test;
import org.openbase.jul.exception.CouldNotPerformException;

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class InfluxDBHandlerTest {

    @Test
    @Ignore
    public void getAveragePowerConsumption() throws InterruptedException {
        int period = 5;
        try {
            System.out.println("test power consumption");
            if (InfluxDBHandler.getAveragePowerConsumption(
                    "1m", new Timestamp(System.currentTimeMillis()/1000).getTime() - period,
                    new Timestamp(System.currentTimeMillis()/1000).getTime(), "consumption") == null)
                System.out.println("Energie ist null");
            else
                System.out.println("Energie ist "+ InfluxDBHandler.getAveragePowerConsumption(
                        "1m", new Timestamp(System.currentTimeMillis()/1000).getTime() - period,
                        new Timestamp(System.currentTimeMillis()/1000).getTime(), "consumption"));


          /*  if (InfluxDBHandler.getAveragePowerConsumption(
                    "1m", new Timestamp(System.currentTimeMillis()/1000).getTime() - period, new Timestamp(System.currentTimeMillis()/1000).getTime(), "consumption") == null)
                System.out.println("Energie ist null");
            else
                System.out.println("Energie ist "+ InfluxDBHandler.getAveragePowerConsumption(
                        "1m", new Timestamp(System.currentTimeMillis()/1000).getTime() - period, new Timestamp(System.currentTimeMillis()/1000).getTime(), "consumption"));


            if (InfluxDBHandler.getAveragePowerConsumption(
                    "1m", new Timestamp(System.currentTimeMillis()/1000).getTime() - period, new Timestamp(System.currentTimeMillis()/1000).getTime(), "consumption") == null)
                System.out.println("Energie ist null");
            else
                System.out.println("Energie ist "+ InfluxDBHandler.getAveragePowerConsumption(
                        "1m", new Timestamp(System.currentTimeMillis()/1000).getTime() - period, new Timestamp(System.currentTimeMillis()/1000).getTime(), "consumption"));
 */

        } catch (CouldNotPerformException e) {
            e.printStackTrace();
        }
    }
}