/**
 * ==================================================================
 *
 * This file is part of org.dc.bco.bcozy.
 *
 * org.dc.bco.bcozy is free software: you can redistribute it and modify
 * it under the terms of the GNU General Public License (Version 3)
 * as published by the Free Software Foundation.
 *
 * org.dc.bco.bcozy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with org.dc.bco.bcozy. If not, see <http://www.gnu.org/licenses/>.
 * ==================================================================
 */
package org.dc.bco.bcozy.controller;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import org.dc.bco.bcozy.view.Constants;
import org.dc.bco.bcozy.view.ForegroundPane;
import org.dc.bco.bcozy.view.location.LocationPane;
import org.dc.bco.registry.location.remote.LocationRegistryRemote;
import org.dc.jul.exception.CouldNotPerformException;
import org.dc.jul.exception.printer.ExceptionPrinter;
import org.dc.jul.exception.printer.LogLevel;
import org.dc.jul.pattern.Observable;
import org.dc.jul.pattern.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rct.Transform;
import rst.math.Vec3DDoubleType;
import rst.spatial.ConnectionConfigType;
import rst.spatial.LocationConfigType;
import rst.spatial.LocationRegistryType;

import javax.vecmath.Point3d;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 */
public class LocationController implements Observer<LocationRegistryType.LocationRegistry> {

    /**
     * Application logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LocationController.class);

    private final ForegroundPane foregroundPane;
    private final LocationPane locationPane;
    private final RemotePool remotePool;
    private LocationRegistryRemote locationRegistryRemote;

    /**
     * The constructor.
     *
     * @param foregroundPane the foreground pane
     * @param locationPane   the location pane
     * @param remotePool     the remotePool
     */
    public LocationController(final ForegroundPane foregroundPane, final LocationPane locationPane,
                              final RemotePool remotePool) {
        this.foregroundPane = foregroundPane;
        this.locationPane = locationPane;
        this.remotePool = remotePool;
        this.foregroundPane.getMainMenu().addFetchLocationButtonEventHandler(event -> connectLocationRemote());
    }

    /**
     * Establishes the connection with the RemoteRegistry.
     */
    public void connectLocationRemote() {
        if (remotePool.isInit()) {
            try {
                locationRegistryRemote = remotePool.getLocationRegistryRemote();
                locationRegistryRemote.addObserver(this);
                updateAndZoomFit();
            } catch (Exception e) { //NOPMD
                ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
            }
        } else {
            LOGGER.warn("Registry Remotes are not initialized. Thus a Dummy Location will be loaded.");
            this.fetchDummyLocation();
        }
    }

    private void fetchLocations() throws CouldNotPerformException {
        final List<LocationConfigType.LocationConfig> list = locationRegistryRemote.getLocationConfigs();

        locationPane.clearLocations();

        //lookup root location frame id
        final String rootLocationFrameId =
                locationRegistryRemote.getRootLocationConfig().getPlacementConfig().getTransformationFrameId();

        for (final LocationConfigType.LocationConfig locationConfig : list) {
            try {
                //skip locations without a shape
                if (locationConfig.getPlacementConfig().getShape().getFloorCount() == 0) {
                    continue;
                }

                final List<Point2D> vertices = new LinkedList<>();

                // Get the transformation for the current room
                final Future<Transform> transform = remotePool.getTransformReceiver().requestTransform(
                        rootLocationFrameId,
                        locationConfig.getPlacementConfig().getTransformationFrameId(),
                        System.currentTimeMillis());

                // Get the shape of the room
                final List<Vec3DDoubleType.Vec3DDouble> shape
                        = locationConfig.getPlacementConfig().getShape().getFloorList();

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

                locationPane.addLocation(locationConfig.getId(), locationConfig.getLabel(),
                        locationConfig.getChildIdList(), vertices, locationConfig.getType().toString());
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                LOGGER.error("Error while fetching transformation for location \"" + locationConfig.getLabel()
                        + "\", locationID: " + locationConfig.getId());
            }
        }
    }

    private void fetchConnections() throws CouldNotPerformException {
        final List<ConnectionConfigType.ConnectionConfig> list = locationRegistryRemote.getConnectionConfigs();

        locationPane.clearConnections();

        //lookup root location frame id
        final String rootLocationFrameId =
                locationRegistryRemote.getRootLocationConfig().getPlacementConfig().getTransformationFrameId();

        //check which connection has a shape
        for (final ConnectionConfigType.ConnectionConfig connectionConfig : list) {
            try {
                //skip connections without a shape
                if (connectionConfig.getPlacementConfig().getShape().getFloorCount() == 0) {
                    continue;
                }

                final List<Point2D> vertices = new LinkedList<>();

                final Future<Transform> transform = remotePool.getTransformReceiver().requestTransform(
                        rootLocationFrameId,
                        connectionConfig.getPlacementConfig().getTransformationFrameId(),
                        System.currentTimeMillis());

                // Get the shape of the room
                final List<Vec3DDoubleType.Vec3DDouble> shape
                        = connectionConfig.getPlacementConfig().getShape().getFloorList();

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

                locationPane.addConnection(connectionConfig.getId(), connectionConfig.getLabel(),
                        vertices, connectionConfig.getType().toString(), connectionConfig.getTileIdList());
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                LOGGER.error("Error while fetching transformation for connection \"" + connectionConfig.getLabel()
                        + "\", connectionID: " + connectionConfig.getId());
            }
        }
    }

    private void fetchDummyLocation() {
        locationPane.clearLocations();

        //CHECKSTYLE.OFF: MagicNumber
        final List<Point2D> zoneVertices = new LinkedList<>();
        zoneVertices.add(new Point2D(0, 0));
        zoneVertices.add(new Point2D(10, 0));
        zoneVertices.add(new Point2D(10, 10));
        zoneVertices.add(new Point2D(0, 10));
        locationPane.addLocation("DummyID0", "DummyLabel0", new LinkedList<>(), zoneVertices,
                LocationConfigType.LocationConfig.LocationType.ZONE.toString());

        final List<Point2D> tile0Vertices = new LinkedList<>();
        tile0Vertices.add(new Point2D(1, 1));
        tile0Vertices.add(new Point2D(5, 1));
        tile0Vertices.add(new Point2D(5, 3));
        tile0Vertices.add(new Point2D(1, 3));
        locationPane.addLocation("DummyID1", "DummyLabel1", new LinkedList<>(), tile0Vertices,
                LocationConfigType.LocationConfig.LocationType.TILE.toString());

        final List<Point2D> tile1Vertices = new LinkedList<>();
        tile1Vertices.add(new Point2D(6, 1));
        tile1Vertices.add(new Point2D(6, 8));
        tile1Vertices.add(new Point2D(8, 8));
        tile1Vertices.add(new Point2D(8, 1));
        locationPane.addLocation("DummyID2", "DummyLabel2", new LinkedList<>(), tile1Vertices,
                LocationConfigType.LocationConfig.LocationType.TILE.toString());
        //CHECKSTYLE.ON: MagicNumber

        locationPane.zoomFit();
    }

    @Override
    public void update(final Observable<LocationRegistryType.LocationRegistry> observable,
                       final LocationRegistryType.LocationRegistry locationRegistry) throws Exception { //NOPMD
        Platform.runLater(() -> {
            try {
                fetchLocations();
                fetchConnections();
                locationPane.updateLocationPane();
            } catch (CouldNotPerformException e) {
                ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
            }
        });
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
            } catch (CouldNotPerformException e) {
                ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
            }
        });
    }
}
