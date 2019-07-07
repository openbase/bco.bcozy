package org.openbase.bco.bcozy.controller.powerterminal;

import com.google.protobuf.Message;
import eu.hansolo.fx.charts.ChartType;
import eu.hansolo.fx.charts.MatrixPane;
import eu.hansolo.fx.charts.PixelMatrix;
import eu.hansolo.fx.charts.data.MatrixChartItem;
import eu.hansolo.fx.charts.heatmap.HeatMap;
import eu.hansolo.fx.charts.series.MatrixItemSeries;
import javafx.animation.Interpolator;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import javafx.stage.Screen;
import org.openbase.bco.bcozy.controller.powerterminal.heatmapattributes.SpotsPosition;
import org.openbase.bco.dal.lib.layer.unit.PowerConsumptionSensor;
import org.openbase.bco.dal.lib.layer.unit.UnitRemote;
import org.openbase.bco.dal.remote.layer.unit.CustomUnitPool;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.pattern.Filter;
import org.openbase.type.domotic.unit.UnitConfigType;
import org.openbase.type.domotic.unit.UnitTemplateType;
import org.openbase.type.geometry.AxisAlignedBoundingBox3DFloatType;
import org.openbase.type.math.Vec3DDoubleType;
import org.slf4j.LoggerFactory;

import javax.vecmath.Point3d;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Heatmap extends Pane {

    private CustomUnitPool unitPool;

    public static final int TILE_WIDTH = (int) Screen.getPrimary().getVisualBounds().getWidth();
    public static final int TILE_HEIGHT = (int) Screen.getPrimary().getVisualBounds().getHeight();

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(eu.hansolo.fx.charts.heatmap.HeatMap.class);


    public Heatmap() {
        double factorx = 52;
        double factory = 43;
        try {
            unitPool = new CustomUnitPool();

            //init in own method
            unitPool.init((Filter<UnitConfigType.UnitConfig>) unitConfig -> {
                //return unitConfig.getUnitType() != UnitTemplateType.UnitTemplate.UnitType.TEMPERATURE_SENSOR;
                return unitConfig.getUnitType() != UnitTemplateType.UnitTemplate.UnitType.POWER_CONSUMPTION_SENSOR;
            });

            unitPool.activate();

            UnitConfigType.UnitConfig rootLocationConfig = Registries.getUnitRegistry().getRootLocationConfig();
            AxisAlignedBoundingBox3DFloatType.AxisAlignedBoundingBox3DFloat rootBoundingBox = rootLocationConfig.getPlacementConfig().getShape().getBoundingBox();

            //Walls
            List<Vec3DDoubleType.Vec3DDouble> floorList = rootLocationConfig.getPlacementConfig().getShape().getFloorList();

            double[][] u = new double[(int)(rootBoundingBox.getWidth()*factorx)][(int)(rootBoundingBox.getDepth()*factory)];


            this.setLayoutX(0);
            this.setTranslateX(-295);
            this.setLayoutY(0);
            this.setTranslateY(-295);
            this.getChildren().add(updateHeatmap(u, factorx, factory));

        } catch (CouldNotPerformException  ex) {
            ExceptionPrinter.printHistory("Could not instantiate CustomUnitPool", ex, logger);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            ExceptionPrinter.printHistory("Could not instantiate CustomUnitPool", ex, logger);
        }
    }

     private HeatMap updateHeatmap(double[][] u, double factorx, double factory) {

        double current = 0;
        int runnings = 3;
        List<SpotsPosition> spots = new ArrayList<>();
        for (UnitRemote<? extends Message> unit : unitPool.getInternalUnitList()) {
             try {
                 Point3d unitPositionGlobalPoint3d = unit.getUnitPositionGlobalPoint3d();
                 PowerConsumptionSensor powerConsumptionUnit = (PowerConsumptionSensor) unit;
                 //TemperatureSensor temperatureUnit = (TemperatureSensor) unit;
                 current = powerConsumptionUnit.getPowerConsumptionState().getCurrent() / 16;
                 current = 1;
                 //current = temperatureUnit.getTemperatureState().getTemperature();

                 System.out.println(unit.getLabel());
                 //u[(int)(unitPositionGlobalPoint3d.x*factorx)][(int)(unitPositionGlobalPoint3d.y*factory)] = current;
                 //spots.add(new SpotsPosition((int)(unitPositionGlobalPoint3d.x*factorx), (int)(unitPositionGlobalPoint3d.y*factory), current));
             } catch (NotAvailableException ex) {
                 //ExceptionPrinter.printHistory("Could not get Position", ex, logger);
             }
        }

        System.out.println("RaumGroesse: " + u.length + " " + u[0].length);
        for (SpotsPosition spot : spots) {
            System.out.println("x: " + spot.spotsPositionx + " y: " +spot.spotsPositiony + " value: " + spot.value);
        }

        u[0][0] = 1;
        u[u.length-1][0] = 1;
        u[0][u[0].length-1] = 1;
        u[u.length-1][u[0].length-1] = 1;

        spots.add(new SpotsPosition(0, 0, 1));
        spots.add(new SpotsPosition(u.length-1, 0, 1));
        spots.add(new SpotsPosition(0, u[0].length-1, 1));
         spots.add(new SpotsPosition(u.length-1, u[0].length-1, 1));

        return generateHeatmapWithLibrary(u, spots, runnings);
     }


    private HeatMap generateHeatmapWithLibrary(double[][] u, List<SpotsPosition> spots, int runnings) {
        calculateHeatMap(u, runnings, spots);

        HeatMap heatmap = new eu.hansolo.fx.charts.heatmap.HeatMap();
        heatmap.setSize(TILE_WIDTH, TILE_HEIGHT);

        for (int j = 0; j < spots.size(); j++) {
            SpotsPosition spot = spots.get(j);
            heatmap.addSpot(spot.spotsPositionx, spot.spotsPositiony, createEventImage(runnings, u, spot), 0, 0);
        }
        return heatmap;
    }

    public Image createEventImage(int runnings, double[][] u, SpotsPosition spot) {
        Double radius = (double) runnings*100;

        Stop[] stops = new Stop[runnings+1];
        for (int i = 0; i < runnings + 1; i++) {
            double[] opacity = new double[4];
            if (i != runnings) {
                if (spot.spotsPositionx+i < u.length)
                    opacity[0] = u[spot.spotsPositionx+i][spot.spotsPositiony];
                else
                    opacity[0] = 1;

                if (spot.spotsPositiony+i < u[spot.spotsPositiony].length)
                    opacity[1] = u[spot.spotsPositionx][spot.spotsPositiony+i];
                else
                    opacity[1] = 1;

                if (spot.spotsPositionx-i > 0)
                    opacity[2] = u[spot.spotsPositionx-i][spot.spotsPositiony];
                else
                    opacity[2] = 1;

                if (spot.spotsPositiony-i > 0)
                    opacity[3] = u[spot.spotsPositionx][spot.spotsPositiony-i];
                else
                    opacity[3] = 1;
            }

            Arrays.sort(opacity);
            stops[i] = new Stop(i * 0.1, Color.rgb(255, 255, 255, opacity[0]));
        }

        int size = (int) (radius * 2);
        WritableImage raster        = new WritableImage(size, size);
        PixelWriter pixelWriter   = raster.getPixelWriter();
        double        maxDistFactor = 1 / radius;
        Color pixelColor;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                double deltaX   = radius - x;
                double deltaY   = radius - y;
                double distance = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
                double fraction = maxDistFactor * distance;
                for (int i = 0; i < stops.length-1; i++) {
                    if (Double.compare(fraction, stops[i].getOffset()) >= 0 && Double.compare(fraction, stops[i + 1].getOffset()) <= 0) {
                        pixelColor = (Color) Interpolator.LINEAR.interpolate(stops[i].getColor(), stops[i + 1].getColor(), (fraction - stops[i].getOffset()) / 0.1);
                        pixelWriter.setColor(x, y, pixelColor);
                        break;
                    }
                }
            }
        }
        return raster;
    }

    private void calculateHeatMap (double[][] u, int runnings, List<SpotsPosition> spots) {
        //CFL Bedingung: delta_t/h^2 <= 1/4
        double h = 1;
        double delta_t = 0.1;
        double[][] v = new double[u.length][u[0].length];
        for (int runs = 0; runs < runnings; runs ++) {

            for (int col = 1; col < u.length - 1; col++) {
                for (int row = 1; row < u[col].length - 1; row++) {
                    v[col][row] = (u[col - 1][row] + u[col + 1][row] + u[col][row - 1] + u[col][row + 1]
                            - 4*u[col][row]) / (h * h);
                }
            }

            for (int col = 0; col < u.length; col++) {
                for (int row = 0; row < u[col].length; row++) {
                    u[col][row] = u[col][row] + delta_t * v[col][row];
                }
            }

            for (SpotsPosition spot : spots) {
                u[spot.spotsPositionx][spot.spotsPositiony] = spot.value;
            }
        }
    }
}
