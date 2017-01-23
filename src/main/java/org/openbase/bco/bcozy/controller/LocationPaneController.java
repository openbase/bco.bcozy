/**
 * ==================================================================
 *
 * This file is part of org.openbase.bco.bcozy.
 *
 * org.openbase.bco.bcozy is free software: you can redistribute it and modify
 * it under the terms of the GNU General Public License (Version 3)
 * as published by the Free Software Foundation.
 *
 * org.openbase.bco.bcozy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with org.openbase.bco.bcozy. If not, see <http://www.gnu.org/licenses/>.
 * ==================================================================
 */
package org.openbase.bco.bcozy.controller;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.ForegroundPane;
import org.openbase.bco.bcozy.view.location.LocationPane;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.openbase.jul.pattern.Observable;
import org.openbase.jul.pattern.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rct.Transform;
import rst.domotic.registry.LocationRegistryDataType.LocationRegistryData;
import rst.domotic.unit.UnitConfigType.UnitConfig;
import rst.math.Vec3DDoubleType;
import javax.vecmath.Point3d;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.iface.DefaultInitializable;

/**
 *
 */
public class LocationPaneController implements DefaultInitializable {

    /**
     * Application logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LocationPaneController.class);

    private final ForegroundPane foregroundPane;
    private final LocationPane locationPane;
    private final RemotePool remotePool;

    /**
     * The constructor.
     *
     * @param foregroundPane the foreground pane
     * @param locationPane the location pane
     * @param remotePool the remotePool
     */
    public LocationPaneController(final ForegroundPane foregroundPane, final LocationPane locationPane, final RemotePool remotePool) {
        this.foregroundPane = foregroundPane;
        this.locationPane = locationPane;
        this.remotePool = remotePool;
        this.foregroundPane.getMainMenu().addFetchLocationButtonEventHandler(event -> connectLocationRemote());
    }

    @Override
    public void init() throws InitializationException, InterruptedException {
        try {
            Registries.getLocationRegistry().waitForData();
        } catch (CouldNotPerformException ex) {
            throw new InitializationException(this, ex);
        }
    }

    /**
     * Establishes the connection with the RemoteRegistry.
     */
    public void connectLocationRemote() {
        try {
            Registries.getLocationRegistry().waitForData();
            Registries.getLocationRegistry().addDataObserver(new Observer<LocationRegistryData>() {
                @Override
                public void update(Observable<LocationRegistryData> source, LocationRegistryData data) throws Exception {
                    Platform.runLater(() -> {
                        try {
                            fetchLocations();
                            fetchConnections();
                            locationPane.updateLocationPane();
                        } catch (CouldNotPerformException | InterruptedException e) {
                            ExceptionPrinter.printHistory(e, LOGGER);
                        }
                    });
                }
            });
            updateAndZoomFit();
        } catch (Exception e) { //NOPMD
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
    }

    private void fetchLocations() throws CouldNotPerformException, InterruptedException {
        final List<UnitConfig> locationUnitConfigList = Registries.getLocationRegistry().getLocationConfigs();

        locationPane.clearLocations();

        //lookup root location frame id
        final String rootLocationFrameId = Registries.getLocationRegistry().getRootLocationConfig().getPlacementConfig().getTransformationFrameId();

        for (final UnitConfig locationUnitConfig : locationUnitConfigList) {
            try {
                //skip locations without a shape
                if (locationUnitConfig.getPlacementConfig().getShape().getFloorCount() == 0) {
                    continue;
                }

                final List<Point2D> vertices = new LinkedList<>();

                // Get the transformation for the current room
                final Future<Transform> transform = remotePool.getTransformReceiver().requestTransform(
                        rootLocationFrameId,
                        locationUnitConfig.getPlacementConfig().getTransformationFrameId(),
                        System.currentTimeMillis());

                // Get the shape of the room
                final List<Vec3DDoubleType.Vec3DDouble> shape
                        = locationUnitConfig.getPlacementConfig().getShape().getFloorList();

                // Iterate over all vertices
                for (final Vec3DDoubleType.Vec3DDouble rstVertex : shape) {
                    // Convert vertex into java type
                    final Point3d vertex = new Point3d(rstVertex.getX(), rstVertex.getY(), rstVertex.getZ());
                    // Transform
                    transform.get(Constants.TRANSFORMATION_TIMEOUT, TimeUnit.MILLISECONDS)
                            .getTransform().transform(vertex);
                    // Add vertex to list of vertices
                    vertices.add(new Point2D(vertex.x, vertex.y));
                }

//                locationPane.addLocation(locationUnitConfig.getId(), locationUnitConfig.getLocationConfig().getChildIdList(), vertices, locationUnitConfig.getLocationConfig().getType().toString());
                locationPane.addLocation(locationUnitConfig, vertices);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                LOGGER.error("Error while fetching transformation for location \"" + locationUnitConfig.getLabel()
                        + "\", locationID: " + locationUnitConfig.getId());
            }
        }
    }

    private void fetchConnections() throws CouldNotPerformException, InterruptedException {
        final List<UnitConfig> connectionUnitConfigList = Registries.getLocationRegistry().getConnectionConfigs();

        locationPane.clearConnections();

        //lookup root location frame id
        final String rootLocationFrameId
                = Registries.getLocationRegistry().getRootLocationConfig().getPlacementConfig().getTransformationFrameId();

        //check which connection has a shape
        for (final UnitConfig connectionUnitConfig : connectionUnitConfigList) {
            try {
                //skip connections without a shape
                if (connectionUnitConfig.getPlacementConfig().getShape().getFloorCount() == 0) {
                    continue;
                }

                final List<Point2D> vertices = new LinkedList<>();

                final Future<Transform> transform = remotePool.getTransformReceiver().requestTransform(
                        rootLocationFrameId,
                        connectionUnitConfig.getPlacementConfig().getTransformationFrameId(),
                        System.currentTimeMillis());

                // Get the shape of the room
                final List<Vec3DDoubleType.Vec3DDouble> shape
                        = connectionUnitConfig.getPlacementConfig().getShape().getFloorList();

                // Iterate over all vertices
                for (final Vec3DDoubleType.Vec3DDouble rstVertex : shape) {
                    // Convert vertex into java type
                    final Point3d vertex = new Point3d(rstVertex.getX(), rstVertex.getY(), rstVertex.getZ());
                    // Transform
                    transform.get(Constants.TRANSFORMATION_TIMEOUT, TimeUnit.MILLISECONDS)
                            .getTransform().transform(vertex);
                    // Add vertex to list of vertices
                    vertices.add(new Point2D(vertex.x, vertex.y));
                }

                locationPane.addConnection(connectionUnitConfig, vertices);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                LOGGER.error("Error while fetching transformation for connection \"" + connectionUnitConfig.getLabel()
                        + "\", connectionID: " + connectionUnitConfig.getId());
            }
        }
    }

    /**
     * Method to trigger a complete update of the locationPane.
     * Will furthermore apply a zoomFit after everything is finished.
     *
     * @throws Exception The Exception thrown by the runLater method.
     */
    public void updateAndZoomFit() throws Exception { //NOPMD
        Platform.runLater(() -> {
            try {
                fetchLocations();
                fetchConnections();
                locationPane.updateLocationPane();
                locationPane.zoomFit();
            } catch (CouldNotPerformException | InterruptedException e) {
                ExceptionPrinter.printHistory(e, LOGGER);
            }
        });
    }
}
