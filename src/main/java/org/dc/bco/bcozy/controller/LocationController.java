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
import org.dc.bco.bcozy.model.RoomInstance;
import org.dc.bco.bcozy.view.ForegroundPane;
import org.dc.bco.bcozy.view.location.LocationPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rct.Transform;
import rct.TransformReceiver;
import rct.TransformerException;
import rct.TransformerFactory;
import rst.math.Vec3DDoubleType;
import rst.spatial.LocationConfigType;

import javax.vecmath.Point3d;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private TransformReceiver receiver;

    private final Map<String, RoomInstance> roomInstanceMap;

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
                              final RemotePool remotePool)
            throws InstantiationException {
        this.foregroundPane = foregroundPane;
        this.locationPane = locationPane;
        this.remotePool = remotePool;
        this.roomInstanceMap = new HashMap<>();

        try {
            this.receiver = TransformerFactory.getInstance().createTransformReceiver();
        } catch (TransformerFactory.TransformerFactoryException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }

        this.foregroundPane.getMainMenu().addLocationButtonEventHandler(event -> connectLocationRemote());
    }

    /**
     * Add a simple Dummy Room.
     */
    public void addDummyRoom() {

        //CHECKSTYLE.OFF: MagicNumber
        final List<Point2D> vertices = new ArrayList<>();
        vertices.add(new Point2D(10, 10));
        vertices.add(new Point2D(120, 10));
        vertices.add(new Point2D(120, 140));
        vertices.add(new Point2D(10, 140));
        //CHECKSTYLE.ON: MagicNumber

        this.locationPane.addRoom("Blub", vertices);
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

        //search for root and get child list
        List<String> childList = new ArrayList<>();
        String rootId = "";

        for (final LocationConfigType.LocationConfig locationConfig : list) {
            if (locationConfig.getRoot()) {
                childList = locationConfig.getChildIdList();
                rootId = locationConfig.getId();
            }
        }

        //check which children have a shape
        for (final LocationConfigType.LocationConfig locationConfig : list) {
            if (childList.toString().contains(locationConfig.getId())
                    && locationConfig.getPlacementConfig().hasShape()) {
                LOGGER.info(locationConfig.getId() + " is first child and has a shape!");

                RoomInstance newRoom;
                if (roomInstanceMap.containsKey(locationConfig.getId())) {
                    newRoom = roomInstanceMap.get(locationConfig.getId());
                    newRoom.deleteAllVertices();
                } else {
                    newRoom = new RoomInstance(locationConfig.getId());
                }

                try {
                    // Get the transformation for the current room
                    final Transform transform =
                            receiver.lookupTransform(rootId, locationConfig.getId(), System.currentTimeMillis());

                    // Get the shape of the room
                    final List<Vec3DDoubleType.Vec3DDouble> shape =
                            locationConfig.getPlacementConfig().getShape().getFloorList();

                    // Iterate over all vertices
                    for (final Vec3DDoubleType.Vec3DDouble rstVertex : shape) {
                        // Convert vertex into java type
                        final Point3d vertex = new Point3d(rstVertex.getX(), rstVertex.getY(), rstVertex.getZ());
                        // Transform
                        transform.getTransform().transform(vertex);
                        // Add vertex to room
                        newRoom.addVertex(vertex);
                    }

                    locationPane.addRoom(locationConfig.getId(), newRoom.getVertices());
                } catch (TransformerException e) {
                    ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                    LOGGER.warn("Could not gather transformation for room: " + locationConfig.getId());
                }
            }
        }
    }
}
