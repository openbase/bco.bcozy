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

import ch.qos.logback.core.CoreConstants;
import de.jensd.fx.glyphs.GlyphIcons;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import org.openbase.bco.bcozy.view.Constants;
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
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.openbase.bco.bcozy.view.SVGIcon;
import org.openbase.bco.bcozy.view.pane.unit.UnitPaneFactory;
import org.openbase.bco.bcozy.view.pane.unit.UnitPaneFactoryImpl;
import org.openbase.bco.dal.lib.layer.unit.UnitRemote;
import org.openbase.bco.dal.remote.unit.Units;
import org.openbase.bco.registry.remote.Registries;
import rst.domotic.unit.UnitTemplateType;
import rst.geometry.AxisAlignedBoundingBox3DFloatType;
import rst.geometry.PoseType;
import rst.geometry.PoseType.Pose;

/**
 * @author julian
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class LocationPaneController {

    /**
     * Application logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LocationPaneController.class);

    private final LocationPane locationPane;

    /**
     * The constructor.
     *
     * @param locationPane the location pane
     */
    public LocationPaneController(final LocationPane locationPane) {
        this.locationPane = locationPane;
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

        for (final UnitConfig locationUnitConfig : locationUnitConfigList) {
            try {
                //skip locations without a shape    
                if (locationUnitConfig.getPlacementConfig().getShape().getFloorCount() == 0) {
                    continue;
                }

                final List<Point2D> vertices = new LinkedList<>();

                // Get the transformation for the current room
//                final Future<Transform> transform = Units.getUnitTransformation(locationUnitConfig, Registries.getUnitRegistry().getUnitConfigById(locationUnitConfig.getPlacementConfig().getLocationId()));
                final Future<Transform> transform = Registries.getLocationRegistry().getUnitTransformation(locationUnitConfig, Registries.getLocationRegistry().getRootLocationConfig());

                // Get the shape of the room
                final List<Vec3DDoubleType.Vec3DDouble> shape = locationUnitConfig.getPlacementConfig().getShape().getFloorList();

                // Iterate over all vertices
                for (final Vec3DDoubleType.Vec3DDouble rstVertex : shape) {
                    // Convert vertex into java type
                    final Point3d vertex = new Point3d(rstVertex.getX(), rstVertex.getY(), rstVertex.getZ());
                    // Transform
                    transform.get(Constants.TRANSFORMATION_TIMEOUT, TimeUnit.MILLISECONDS).getTransform().transform(vertex);
                    // Add vertex to list of vertices
                    vertices.add(new Point2D(vertex.x, vertex.y));
                }

                // locationPane.addLocation(locationUnitConfig.getId(), locationUnitConfig.getLocationConfig().getChildIdList(), vertices, locationUnitConfig.getLocationConfig().getType().toString());
                locationPane.addLocation(locationUnitConfig, vertices);
				
				//Units.getUnit(locationUnitConfig.getId(), false, Units.LOCATION).getUnitMap().

				//get all units in location
				for (final Map.Entry<UnitTemplateType.UnitTemplate.UnitType, List<UnitRemote>> nextEntry : Units.getUnit(locationUnitConfig.getId(), false, Units.LOCATION).getUnitMap().entrySet()) {
                if (nextEntry.getValue().isEmpty()) {
                    continue;
                }
               // AbstractUnitPane blubs = UnitPaneFactoryImpl.getInstance().newInstance(nextEntry.getKey());
					nextEntry.getKey().name();
				//addUnit(blubs.getIcon(), null, new Point2D(5,5));
					for(UnitRemote<?> u: nextEntry.getValue()) {

						Pose pose = u.getConfig().getPlacementConfig().getPosition();
                                                //if ( u.getConfig().getId().equals("932b4f48-59d9-474a-b83e-82c4218b5ecf") ){
                                                if(pose.getTranslation().getX()!=0 && pose.getTranslation().getY()!=0 && !locationUnitConfig.getLabel().equals("Home")) {
                                                    try{
                                                        SVGIcon icon = UnitPaneFactoryImpl.getInstance().newInstance( UnitPaneFactoryImpl.loadUnitPaneClass(u.getType())).getIcon();
                                                        //double x = pose.getTranslation().getX()+(vertices.get(0).getX()*Constants.METER_TO_PIXEL);
                                                        //double y = pose.getTranslation().getY()+(vertices.get(0).getY() *Constants.METER_TO_PIXEL);
                                                        //locationPane.addUnit(icon, new Point2D(x,y ));
                                                        final Future<Transform> transform2 = Registries.getLocationRegistry().getUnitTransformation( u.getConfig(),Registries.getLocationRegistry().getRootLocationConfig());
                                                        final Point3d vertex = new Point3d(pose.getTranslation().getX(), pose.getTranslation().getY(), pose.getTranslation().getZ());
                                                        transform2.get(Constants.TRANSFORMATION_TIMEOUT, TimeUnit.MILLISECONDS).getTransform().transform(vertex);
                                                        
                                                        locationPane.addUnit(icon, new Point2D(vertex.y*Constants.METER_TO_PIXEL-50,vertex.x*Constants.METER_TO_PIXEL-50  ));
                                                    }catch (CouldNotPerformException e){
                                                        //just leave out unit
                                                    }
                                                }

						//Registries.getUnitRegistry().getBaseUnitConfigs().get(u.getId());
						if (u.getConfig().getId().equals("8d310f30-d60a-4627-8884-373c5e2dcbdd")) {
							double test = u.getConfig().getPlacementConfig().getPosition().getTranslation().getX();
						}

						
						//final Point2d test = new Point2d(bb.getLeftFrontBottom().getX(), bb.getWidth()+ bb.getLeftFrontBottom());
						//locationPane.addUnit(new SVGIcon(FontAwesomeIcon.ARROW_LEFT, 10.0, true),bb.);
					}
				}
            } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                ExceptionPrinter.printHistory("Error while fetching transformation for location \"" + locationUnitConfig.getLabel() + "\", locationID: " + locationUnitConfig.getId(), ex, LOGGER, LogLevel.ERROR);
            }
        }
    }

    private void fetchConnections() throws CouldNotPerformException, InterruptedException {
        final List<UnitConfig> connectionUnitConfigList = Registries.getLocationRegistry().getConnectionConfigs();

        locationPane.clearConnections();

        //check which connection has a shape
        for (final UnitConfig connectionUnitConfig : connectionUnitConfigList) {
            try {
                //skip connections without a shape
                if (connectionUnitConfig.getPlacementConfig().getShape().getFloorCount() == 0) {
                    continue;
                }

                final List<Point2D> vertices = new LinkedList<>();

//                final Future<Transform> transform = Registries.getLocationRegistry().getUnitTransformation(connectionUnitConfig, Registries.getUnitRegistry().getUnitConfigById(connectionUnitConfig.getPlacementConfig().getLocationId()));
                final Future<Transform> transform = Registries.getLocationRegistry().getUnitTransformation(connectionUnitConfig, Registries.getLocationRegistry().getRootLocationConfig());

                // Get the shape of the room
                final List<Vec3DDoubleType.Vec3DDouble> shape = connectionUnitConfig.getPlacementConfig().getShape().getFloorList();

                // Iterate over all vertices
                for (final Vec3DDoubleType.Vec3DDouble rstVertex : shape) {
                    // Convert vertex into java type
                    final Point3d vertex = new Point3d(rstVertex.getX(), rstVertex.getY(), rstVertex.getZ());
                    // Transform
                    transform.get(Constants.TRANSFORMATION_TIMEOUT, TimeUnit.MILLISECONDS).getTransform().transform(vertex);
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
     */
    public void updateAndZoomFit() {
        Platform.runLater(() -> {
            try {
                fetchLocations();
                fetchConnections();
                locationPane.updateLocationPane();
                locationPane.zoomFit();
				//locationPane.addUnit(new SVGIcon(FontAwesomeIcon.APPLE, 30.0, true), null, new Point2D(5,5));
            } catch (CouldNotPerformException | InterruptedException e) {
                ExceptionPrinter.printHistory(e, LOGGER);
            }
        });
    }
}
