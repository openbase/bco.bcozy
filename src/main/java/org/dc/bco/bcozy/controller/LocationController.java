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

import de.citec.jul.exception.CouldNotPerformException;
import de.citec.jul.exception.InstantiationException;
import de.citec.jul.exception.printer.ExceptionPrinter;
import de.citec.jul.exception.printer.LogLevel;
import de.citec.lm.remote.LocationRegistryRemote;
import javafx.geometry.Point2D;
import org.dc.bco.bcozy.view.ForegroundPane;
import org.dc.bco.bcozy.view.location.LocationPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rct.Transform;
import rct.TransformerException;
import rst.math.Vec3DDoubleType;
import rst.spatial.LocationConfigType;

import javax.vecmath.Point3d;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class LocationController {

    /**
     * Application logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LocationController.class);

    private final ForegroundPane foregroundPane;
    private final LocationPane locationPane;
    private final RemotePool remotePool;
    private LocationRegistryRemote locationRegistryRemote;

//    private final Map<String, LocationPolygon> locationPolygonMap;

    /**
     * The constructor.
     *
     * @param foregroundPane the foreground pane
     * @param locationPane the location pane
     * @param remotePool the remotePool
     *
     * @throws InstantiationException This exception will be thrown if no LocationRegistryRemote could be instantiated
     */
    public LocationController(final ForegroundPane foregroundPane, final LocationPane locationPane,
                              final RemotePool remotePool) throws InstantiationException {
        this.foregroundPane = foregroundPane;
        this.locationPane = locationPane;
        this.remotePool = remotePool;

        this.foregroundPane.getMainMenu().addFetchLocationButtonEventHandler(event -> connectLocationRemote());
    }

    /**
     * Establishes the connection with the RemoteRegistry.
     */
    public void connectLocationRemote() {
        List<LocationConfigType.LocationConfig> list;

        try {
            locationRegistryRemote = remotePool.getLocationRegistryRemote();
            list = locationRegistryRemote.getLocationConfigs();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
            list = new ArrayList<>();
        }

        //search for root
        String rootId = "";

        for (final LocationConfigType.LocationConfig locationConfig : list) {
            if (locationConfig.getRoot()) {
                rootId = locationConfig.getId();
            }
        }

        //check which location have a shape
        for (final LocationConfigType.LocationConfig locationConfig : list) {
            if (locationConfig.getPlacementConfig().hasShape()) {
                try {
                    final List<Point2D> vertices = new LinkedList<>();

                    // Get the transformation for the current room
                    final Transform transform =
                            remotePool.getTransformReceiver()
                                    .lookupTransform(rootId, locationConfig.getId(), System.currentTimeMillis());

                    // Get the shape of the room
                    final List<Vec3DDoubleType.Vec3DDouble> shape =
                            locationConfig.getPlacementConfig().getShape().getFloorList();

                    // Iterate over all vertices
                    for (final Vec3DDoubleType.Vec3DDouble rstVertex : shape) {
                        // Convert vertex into java type
                        final Point3d vertex = new Point3d(rstVertex.getX(), rstVertex.getY(), rstVertex.getZ());
                        // Transform
                        transform.getTransform().transform(vertex);
                        // Add vertex to list of vertices
                        vertices.add(new Point2D(vertex.x, vertex.y));
                    }

                    locationPane.addRoom(locationConfig.getId(), locationConfig.getLabel(), vertices,
                            locationConfig.getType().toString());
                } catch (TransformerException e) {
                    ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                    LOGGER.warn("Could not gather transformation for room: " + locationConfig.getId());
                } catch (CouldNotPerformException e) {
                    ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                    LOGGER.warn("TransformReceiver was not properly initialized.");
                }
            }
        }

        locationPane.zoomFit();
    }
}
