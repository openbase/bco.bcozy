package org.openbase.bco.bcozy.controller.powerterminal;

import com.google.protobuf.Message;
import eu.hansolo.fx.charts.ChartType;
import eu.hansolo.fx.charts.MatrixPane;
import eu.hansolo.fx.charts.PixelMatrix;
import eu.hansolo.fx.charts.data.MatrixChartItem;
import eu.hansolo.fx.charts.heatmap.HeatMap;
import eu.hansolo.fx.charts.series.MatrixItemSeries;
import javafx.animation.Interpolator;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import javafx.stage.Screen;
import org.openbase.bco.bcozy.controller.powerterminal.heatmapattributes.SpotsPosition;
import org.openbase.bco.dal.lib.layer.service.ServiceStateProvider;
import org.openbase.bco.dal.lib.layer.unit.PowerConsumptionSensor;
import org.openbase.bco.dal.lib.layer.unit.UnitRemote;
import org.openbase.bco.dal.remote.layer.unit.CustomUnitPool;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.pattern.Filter;
import org.openbase.jul.pattern.Observer;
import org.openbase.type.domotic.unit.UnitConfigType;
import org.openbase.type.domotic.unit.UnitTemplateType;
import org.openbase.type.geometry.AxisAlignedBoundingBox3DFloatType;
import org.openbase.type.math.Vec3DDoubleType;
import org.slf4j.LoggerFactory;

import javax.vecmath.Point3d;
import java.util.ArrayList;
import java.util.List;

public class Heatmap {

    private CustomUnitPool unitPool;

    public static final int TILE_WIDTH = (int) Screen.getPrimary().getVisualBounds().getWidth();
    public static final int TILE_HEIGHT = (int) Screen.getPrimary().getVisualBounds().getHeight();

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(eu.hansolo.fx.charts.heatmap.HeatMap.class);


    public Heatmap() {
        try {
            unitPool = new CustomUnitPool();

            //init in own method
            unitPool.init((Filter<UnitConfigType.UnitConfig>) unitConfig -> {
                return unitConfig.getUnitType() != UnitTemplateType.UnitTemplate.UnitType.POWER_CONSUMPTION_SENSOR;
            });

            unitPool.activate();

            UnitConfigType.UnitConfig rootLocationConfig = Registries.getUnitRegistry().getRootLocationConfig();
            AxisAlignedBoundingBox3DFloatType.AxisAlignedBoundingBox3DFloat rootBoundingBox = rootLocationConfig.getPlacementConfig().getShape().getBoundingBox();

            double[][] u = new double[(int)(rootBoundingBox.getWidth()*100)][(int)(rootBoundingBox.getDepth()*100)];

            unitPool.addObserver(new Observer<ServiceStateProvider<Message>, Message>() {
                @Override
                public void update(ServiceStateProvider<Message> source, Message data) throws Exception {
                    updateHeatmap(u);
                }
            });

            //Walls
            List<Vec3DDoubleType.Vec3DDouble> floorList = rootLocationConfig.getPlacementConfig().getShape().getFloorList();

        } catch (CouldNotPerformException  ex) {
            ExceptionPrinter.printHistory("Could not instantiate CustomUnitPool", ex, logger);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            ExceptionPrinter.printHistory("Could not instantiate CustomUnitPool", ex, logger);
        }
    }

     private void updateHeatmap(double[][] u) {

     /*   List<SpotsPosition> spots = new ArrayList<>();

        u[200][200] = 0.9;
        u[500][500] = 0.5;
        spots.add(new SpotsPosition(200,200));
        spots.add(new SpotsPosition(500,500));

        int runnings = 3;

        generateHeatmapWithLibrary(u, spots, runnings); */

        for (UnitRemote<? extends Message> unit : unitPool.getInternalUnitList()) {
             try {
                 Point3d unitPositionGlobalPoint3d = unit.getUnitPositionGlobalPoint3d();
                 
             } catch (NotAvailableException ex) {
                 ExceptionPrinter.printHistory("Could not reach CustomUnitPool", ex, logger);
             }

             PowerConsumptionSensor powerConsumptionUnit = (PowerConsumptionSensor) unit;
             double current = 0;
             try {
                 current = powerConsumptionUnit.getPowerConsumptionState().getCurrent();
             } catch (NotAvailableException e) {
                 e.printStackTrace();
             }
             current /= 16;
         }

     }

    private MatrixPane<MatrixChartItem> generateHeatmapOld (double[][] u, int runnings) {
        calculateHeatMap(u, runnings);

        List<MatrixChartItem> matrixData = new ArrayList<>();
        for (int col = 0; col < u.length; col++) {
            for (int row = 0; row < u[col].length; row++) {
                matrixData.add(new MatrixChartItem(col, row, u[col][row]));
            }
        }

        MatrixPane<MatrixChartItem> matrixHeatMap =
                new MatrixPane(new MatrixItemSeries(matrixData, ChartType.MATRIX_HEATMAP));
        matrixHeatMap.getMatrix().setPixelShape(PixelMatrix.PixelShape.SQUARE);

        matrixHeatMap.getMatrix().setColsAndRows(u.length,u[0].length);
        matrixHeatMap.setPrefSize(TILE_WIDTH/2, TILE_HEIGHT);
        matrixHeatMap.setMaxSize(TILE_WIDTH/2, TILE_HEIGHT);

        return matrixHeatMap;
    }


    private void generateHeatmapWithLibrary(double[][] u, List<SpotsPosition> spots, int runnings) {
        calculateHeatMap(u, runnings);

        HeatMap heatmap = new eu.hansolo.fx.charts.heatmap.HeatMap();
        heatmap.setSize(TILE_WIDTH/2, TILE_HEIGHT);

        for (int j = 0; j < spots.size(); j++) {
            SpotsPosition spot = spots.get(j);
            heatmap.addSpot(spot.spotsPositionx, spot.spotsPositiony, createEventImage(runnings, u, spot), 0, 0);
        }
    }

    public Image createEventImage(int runnings, double[][] u, SpotsPosition spot) {
        Double radius = (double) runnings*100;

        Stop[] stops = new Stop[runnings+1];
        for (int i = 0; i < runnings + 1; i++) {
            double opacity = 0;
            if (i != runnings)
                opacity = u[spot.spotsPositionx+i][spot.spotsPositiony];
            if (opacity < 0)
                opacity = 0;
            stops[i] = new Stop(i * 0.1, Color.rgb(255, 255, 255, opacity));
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

    private void calculateHeatMap (double[][] u, int runnings) {
        //CFL Bedingung: delta_t/h^2 <= 1/4
        double h = 1;
        double delta_t = 0.1;
        double biggestData = 0;
        double[][] v = new double[u.length][u[0].length];
        for (int runs = 0; runs < runnings; runs ++) {
            for (int col = 1; col < u.length - 1; col++) {
                for (int row = 1; row < u[col].length - 1; row++) {
                    v[col][row] = (u[col - 1][row] + u[col + 1][row] + u[col][row - 1] + u[col][row + 1] - 4*u[col][row]) / (h * h);
                }
            }
            for (int col = 0; col < u.length; col++) {
                for (int row = 0; row < u[col].length; row++) {
                    u[col][row] = u[col][row] + delta_t * v[col][row];
                    if (u[col][row] > biggestData && runs == runnings-1)
                        biggestData = u[col][row];
                }
            }
        }
        //Map all values to one
        for (int col = 0; col < u.length; col++) {
            for (int row = 0; row < u[col].length; row++) {
                u[col][row]  = u[col][row] / biggestData;
            }
        }
    }
}
