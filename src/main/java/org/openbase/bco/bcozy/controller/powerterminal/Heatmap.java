package org.openbase.bco.bcozy.controller.powerterminal;

import com.google.protobuf.Message;
import eu.hansolo.fx.charts.heatmap.HeatMap;
import javafx.animation.Interpolator;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import org.openbase.bco.bcozy.controller.powerterminal.heatmapattributes.HeatmapValues;
import org.openbase.bco.bcozy.controller.powerterminal.heatmapattributes.HeatmapSpot;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.location.DynamicUnitPolygon;
import org.openbase.bco.dal.lib.layer.unit.PowerConsumptionSensor;
import org.openbase.bco.dal.lib.layer.unit.UnitRemote;
import org.openbase.bco.dal.remote.layer.unit.CustomUnitPool;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.openbase.jul.schedule.GlobalScheduledExecutorService;
import org.openbase.rct.Transform;
import org.openbase.type.domotic.unit.UnitConfigType;
import org.openbase.type.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;
import org.openbase.type.domotic.unit.location.LocationConfigType;
import org.openbase.type.geometry.AxisAlignedBoundingBox3DFloatType.AxisAlignedBoundingBox3DFloat;
import org.openbase.type.geometry.TranslationType;
import org.slf4j.LoggerFactory;

import javax.vecmath.Point3d;
import java.util.*;
import java.util.concurrent.*;

public class Heatmap extends Pane {

    private CustomUnitPool unitPool;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HeatMap.class);

    public Heatmap() {
        try {
            unitPool = new CustomUnitPool();

            unitPool.init(unitConfig ->
                    unitConfig.getUnitType() != UnitType.POWER_CONSUMPTION_SENSOR);

            unitPool.activate();

            UnitConfigType.UnitConfig rootLocationConfig = Registries.getUnitRegistry().getRootLocationConfig();

            HeatmapValues heatmapValues = initHeatmap(rootLocationConfig);

            // todo: make sure the heatmap is only updated when displayed. Otherwise this needs a lot of unused computation time.
            ScheduledFuture refreshSchedule = GlobalScheduledExecutorService.scheduleAtFixedRate(() -> Platform.runLater(() -> updateHeatmap(heatmapValues)),
                    10, 10, TimeUnit.SECONDS);

        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory("Could not instantiate CustomUnitPool", ex, logger);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            ExceptionPrinter.printHistory("Could not instantiate CustomUnitPool", ex, logger);
        }
    }


    /**
     * Initializes the heatmap
     *
     * @param rootLocationConfig rootLocation to get the position and energy consumption of the consumer
     *
     * @return HeatmapValues
     */
    private HeatmapValues initHeatmap(UnitConfigType.UnitConfig rootLocationConfig) {
        List<List<Point2D>> rooms = loadTiles();
        double xTranslation = 0;
        double yTranslation = 0;

        AxisAlignedBoundingBox3DFloat rootBoundingBox = rootLocationConfig.getPlacementConfig().getShape().getBoundingBox();

        double[][] u = new double[(int) Math.ceil(rootBoundingBox.getDepth() * Constants.METER_TO_PIXEL)][(int) Math.ceil(rootBoundingBox.getWidth() * Constants.METER_TO_PIXEL)];
        List<HeatmapSpot> spots = new ArrayList<>();

        try {
            List<Point2D> point2DS = DynamicUnitPolygon.loadShapeVertices(rootLocationConfig);
            xTranslation = Math.abs(point2DS.get(0).getX());
            yTranslation = Math.abs(point2DS.get(0).getY());
            this.setTranslateY(-xTranslation);
            this.setTranslateX(-yTranslation);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            ExceptionPrinter.printHistory("Could not get Vertices of the locations", e, logger);
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory("Could not get Vertices of the locations", e, logger);
        }

        HeatmapValues heatmapValues = new HeatmapValues(rooms, spots, u, xTranslation, yTranslation);

        int unitListPosition = -1;
        for (UnitRemote<? extends Message> unit : unitPool.getInternalUnitList()) {
            unitListPosition++;
            try {
                unit.waitForData(Constants.TRANSFORMATION_TIMEOUT, TimeUnit.MILLISECONDS);
                Future<Transform> transform = Registries.getUnitRegistry().getUnitTransformationFuture(unit.getConfig(), rootLocationConfig);
                TranslationType.Translation unitPosition = unit.getUnitPosition();
                Point3d unitPoint = new Point3d(unitPosition.getX(), unitPosition.getY(), unitPosition.getZ());

                //Wait for transformation of unitPoint but use getUnitPositionGlobalPoint3D because Position of unitPoint is wrong
                // todo: what do you mean with "wrong"? Could you please create a bug report for this issue?
                transform.get(Constants.TRANSFORMATION_TIMEOUT, TimeUnit.MILLISECONDS).getTransform().transform(unitPoint);

                int unitPointGlobalX = (int) (unit.getUnitPositionGlobalPoint3d().x * Constants.METER_TO_PIXEL + xTranslation);
                int unitPointGlobalY = (int) (unit.getUnitPositionGlobalPoint3d().y * Constants.METER_TO_PIXEL + yTranslation);

                // todo: where is this "+1" and "-1" coming from? At least use a global const if there is a good reason for it.

                if (heatmapValues.isInsideLocation(unitPointGlobalY, unitPointGlobalX))
                    spots.add(new HeatmapSpot(unitPointGlobalY, unitPointGlobalX, 0, unitListPosition));
                else if (heatmapValues.isInsideLocation(unitPointGlobalY - 1, unitPointGlobalX))
                    spots.add(new HeatmapSpot(unitPointGlobalY - 1, unitPointGlobalX, 0, unitListPosition));
                else if (heatmapValues.isInsideLocation(unitPointGlobalY + 1, unitPointGlobalX))
                    spots.add(new HeatmapSpot(unitPointGlobalY + 1, unitPointGlobalX, 0, unitListPosition));
                else if (heatmapValues.isInsideLocation(unitPointGlobalY, unitPointGlobalX - 1))
                    spots.add(new HeatmapSpot(unitPointGlobalY, unitPointGlobalX - 1, 0, unitListPosition));
                else if (heatmapValues.isInsideLocation(unitPointGlobalY, unitPointGlobalX + 1))
                    spots.add(new HeatmapSpot(unitPointGlobalY, unitPointGlobalX + 1, 0, unitListPosition));
            } catch (CouldNotPerformException ex) {
                ExceptionPrinter.printHistory("Could not get location units", ex, logger, LogLevel.DEBUG);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                ExceptionPrinter.printHistory("Could not get location units", ex, logger, LogLevel.DEBUG);
            } catch (ExecutionException ex) {
                ExceptionPrinter.printHistory("Could not get location units", ex, logger, LogLevel.DEBUG);
            } catch (TimeoutException ex) {
                ExceptionPrinter.printHistory("Could not get location units", ex, logger, LogLevel.DEBUG);
            }
        }
        heatmapValues.setSpots(spots);
        return heatmapValues;
    }

    /**
     * Loads the tiles and their shape out of the UnitRegistry
     *
     * @return List<List < Point2D>> List of tiles with the vertices of the tile
     */
    private List<List<Point2D>> loadTiles() {
        List<UnitConfigType.UnitConfig> roomConfigs = null;
        List<List<Point2D>> rooms = new ArrayList<>();

        try {
            roomConfigs = Registries.getUnitRegistry().getUnitConfigsByUnitType(UnitType.LOCATION);
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory("Could not get location units", ex, logger);
            // todo: forward exception instead printing it, otherwise this causes a nullpointer in the next code line since roomConfigs is null.
        }
        for (UnitConfigType.UnitConfig roomConfig : roomConfigs) {
            LocationConfigType.LocationConfig.LocationType locationType = roomConfig.getLocationConfig().getLocationType();

            // filter non tiles
            if (!locationType.equals(LocationConfigType.LocationConfig.LocationType.TILE)) {
                continue;
            }

            try {
                rooms.add(DynamicUnitPolygon.loadShapeVertices(roomConfig));
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                ExceptionPrinter.printHistory("Could not get location units", ex, logger);
            } catch (CouldNotPerformException ex) {
                ExceptionPrinter.printHistory("Could not get location units", ex, logger);
            }
        }
        return rooms;
    }


    /**
     * Updates the energy consumption of the heatmap values
     *
     * @param heatmapValues Class with relevant data (position, energy consumption) of the consumers
     */
    private void updateHeatmap(final HeatmapValues heatmapValues) {
        final int runnings = 3;
        final List<HeatmapSpot> spots = heatmapValues.getSpots();
        final double[][] u = heatmapValues.getGrid();

        for (HeatmapSpot spot : spots) {
            PowerConsumptionSensor powerConsumptionUnit = (PowerConsumptionSensor) unitPool.getInternalUnitList().get(spot.unitListPosition);
            try {
                double current = powerConsumptionUnit.getPowerConsumptionState().getCurrent() / 10;
                current = Math.pow(current, 0.5);
                current = Math.min(1,current);
                u[spot.x][spot.y] = current;
                spot.value = current;
                System.out.println("current of spot at: " + spot.x + " y position " + spot.y + " currrent " + current);
            } catch (NotAvailableException ex) {
                ExceptionPrinter.printHistory("Could not get power consumption", ex, logger);
            }
        }
        heatmapValues.setSpots(spots);
        heatmapValues.setGrid(u);
        this.getChildren().clear();
        this.getChildren().add(generateHeatmapWithLibrary(heatmapValues, runnings));
    }

    /**
     * Generates the heatmap with the hansolo library
     *
     * @param heatmapValues Class with relevant data (position, energy consumption) of the consumers
     * @param spreadingIteration      parameter how often the heat spreads
     *
     * @return HeatMap from the library generated heatmap
     */
    private HeatMap generateHeatmapWithLibrary(final HeatmapValues heatmapValues, final int spreadingIteration) {
        calculateHeatMap(heatmapValues, spreadingIteration);

        final HeatMap heatmap = new eu.hansolo.fx.charts.heatmap.HeatMap(heatmapValues.getGrid().length, heatmapValues.getGrid()[0].length);
        heatmap.setOpacity(0.8);

        for (HeatmapSpot spot : heatmapValues.getSpots()) {
            heatmap.addSpot(spot.x, spot.y, createEventImage(heatmapValues, spot, spreadingIteration), Constants.RADIUS_SPOTS * spreadingIteration, Constants.RADIUS_SPOTS * spreadingIteration);
        }
        return heatmap;
    }

    /**
     * Image of a single spot needed for generating the heatmap
     *
     * @param heatmapValues Class with relevant data (position, energy consumption) of the consumers
     * @param spot          Position and value of a single heatmap spot
     * @param spreadingIteration      parameter how often the heat spreads
     *
     * @return Image of a single spot
     */
    public Image createEventImage(final HeatmapValues heatmapValues, final HeatmapSpot spot, final int spreadingIteration) {
        final Double radius = (double) spreadingIteration * Constants.RADIUS_SPOTS;
        final double[][] u = heatmapValues.getGrid();
        final Stop[] stops = new Stop[spreadingIteration + 1];

        for (int i = 0; i < spreadingIteration + 1; i++) {
            double[] opacity = new double[4];
            if (i != spreadingIteration) {
                if (spot.x + i < u.length)
                    opacity[0] = u[spot.x + i][spot.y];
                else
                    opacity[0] = 1;

                if (spot.y + i < u[spot.y].length)
                    opacity[1] = u[spot.x][spot.y + i];
                else
                    opacity[1] = 1;

                if (spot.x - i > 0)
                    opacity[2] = u[spot.x - i][spot.y];
                else
                    opacity[2] = 1;

                if (spot.y - i > 0)
                    opacity[3] = u[spot.x][spot.y - i];
                else
                    opacity[3] = 1;
            }

            Arrays.sort(opacity);
            // todo: please define used colors as well as the interpolation step as constants in the Constants.class
            stops[i] = new Stop(i * 0.1, Color.rgb(255, 255, 255, opacity[0]));
        }

        int size = (int) (radius * 2);
        WritableImage raster = new WritableImage(size, size);
        PixelWriter pixelWriter = raster.getPixelWriter();
        double maxDistFactor = 1 / radius;
        Color pixelColor;

        //Goes through the squared Image of the heatmap spot
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                double deltaX = radius - x;
                double deltaY = radius - y;
                double distance = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
                double fraction = maxDistFactor * distance;

                //if the point in the image lies on a circle the correct color (depends on where the point lies in the circle - defined in stops) is set
                for (int i = 0; i < stops.length - 1; i++) {
                    if (Double.compare(fraction, stops[i].getOffset()) >= 0 && Double.compare(fraction, stops[i + 1].getOffset()) <= 0) {
                        int xGlobal = spot.x + (size / 2 - x);
                        int yGlobal = spot.y + (size / 2 - y);
                        int xRotated = size - x;
                        int yRotated = size - y;
                        if (!heatmapValues.isInsideLocation(xGlobal, yGlobal, spot.x, spot.y))
                            continue;

                        pixelColor = (Color) Interpolator.LINEAR.interpolate(stops[i].getColor(), stops[i + 1].getColor(), (fraction - stops[i].getOffset()) / 0.1);
                        pixelWriter.setColor(xRotated, yRotated, pixelColor);
                        break;
                    }
                }
            }
        }
        return raster;
    }

    /**
     * Calculate the heatmap values given a formula
     * // todo: can you offer any link to the used formula?
     *
     * @param heatmapValues Class with relevant data (position, energy consumption) of the consumers
     * @param spreadingIteration      parameter how often the heat spreads
     */
    private void calculateHeatMap(HeatmapValues heatmapValues, int spreadingIteration) {
        // todo please use intuitive variable names
        double[][] u = heatmapValues.getGrid();
        List<HeatmapSpot> spots = heatmapValues.getSpots();
        // todo please use intuitive variable names
        double h = 1;
        double delta_t = 0.1;
        // todo please use intuitive variable names
        double[][] v = new double[u.length][u[0].length];
        for (int runs = 0; runs < spreadingIteration; runs++) {

            // todo: please explain what this iteration is good for.
            for (int col = 1; col < u.length - 1; col++) {
                for (int row = 1; row < u[col].length - 1; row++) {
                    v[col][row] = (u[col - 1][row] + u[col + 1][row] + u[col][row - 1] + u[col][row + 1]
                            - 4 * u[col][row]) / (h * h);
                }
            }

            // todo: please explain what this iterration is good for.
            for (int col = 0; col < u.length; col++) {
                for (int row = 0; row < u[col].length; row++) {
                    u[col][row] = u[col][row] + delta_t * v[col][row];
                }
            }

            // todo: please explain what this iteration is good for.
            for (HeatmapSpot spot : spots) {
                u[spot.x][spot.y] = spot.value;
            }
        }
        heatmapValues.setGrid(u);
    }
}
