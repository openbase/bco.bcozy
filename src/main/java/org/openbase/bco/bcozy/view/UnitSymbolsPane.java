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
 * along with org.openbase.bco.bcozy. If not, see
 * <http://www.gnu.org/licenses/>.
 * ==================================================================
 */
package org.openbase.bco.bcozy.view;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javax.vecmath.Point3d;
import org.openbase.bco.bcozy.view.location.LocationPolygon;
import org.openbase.bco.bcozy.view.location.UnitButton;
import org.openbase.bco.bcozy.view.pane.unit.UnitPaneFactoryImpl;
import org.openbase.bco.dal.lib.layer.unit.UnitRemote;
import org.openbase.bco.dal.remote.unit.Units;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import rct.Transform;
import rst.domotic.unit.UnitConfigType;
import rst.domotic.unit.UnitTemplateType;
import rst.geometry.PoseType;
import rst.math.Vec3DDoubleType;

/**
 *
 * @author lili
 */
public class UnitSymbolsPane extends Pane {

    private BackgroundPane background;

    public UnitSymbolsPane(BackgroundPane bg) {
        this.background = bg;
        this.addUnit(new SVGIcon(FontAwesomeIcon.LIGHTBULB_ALT, 30.0, true), null, new Point2D(20, 20));
    }

    //does not work because registry is not initialized....TODO find solution
    public final void drawUnitSymbols() {
        try {

            final List<UnitConfigType.UnitConfig> locationUnitConfigList = Registries.getLocationRegistry().getLocationConfigs();

            //iterate through rooms
            for (final UnitConfigType.UnitConfig locationUnitConfig : locationUnitConfigList) {

                //iterate through the room's units
                for (final Map.Entry<UnitTemplateType.UnitTemplate.UnitType, List<UnitRemote>> nextEntry
                        : Units.getUnit(locationUnitConfig.getId(), false, Units.LOCATION).getUnitMap().entrySet()) {
                    if (nextEntry.getValue().isEmpty()) {
                        continue;
                    }
                    for (UnitRemote<?> unitInLocation : nextEntry.getValue()) {

                        PoseType.Pose pose = unitInLocation.getConfig().getPlacementConfig().getPosition();
                        // SVGIcon icon = UnitPaneFactoryImpl.getInstance().newInstance(UnitPaneFactoryImpl.loadUnitPaneClass(unitInLocation.getType())).getIcon();
                        final Future<Transform> transform2 = Registries.getLocationRegistry().
                                getUnitTransformation(unitInLocation.getConfig(), Registries.getLocationRegistry().getRootLocationConfig());
                        final Point3d vertex = new Point3d(pose.getTranslation().getX(), pose.getTranslation().getY(), pose.getTranslation().getZ());
                        transform2.get(Constants.TRANSFORMATION_TIMEOUT, TimeUnit.MILLISECONDS).getTransform().transform(vertex);
                        Point2D coord = new Point2D(vertex.x, vertex.y);
                        this.addUnit(new SVGIcon(FontAwesomeIcon.AMBULANCE, 6, true), null, new Point2D(vertex.y * Constants.METER_TO_PIXEL, vertex.x * Constants.METER_TO_PIXEL));

                        /*for(TilePolygon x: background.getTileMap().values()) {
			x.getUnitRemote().getConfig().getPlacementConfig().getPosition();
		}
	
		for (final Map.Entry<UnitTemplateType.UnitTemplate.UnitType, List<UnitRemote>> nextEntry : Units.getUnit(locationUnitConfig.getId(), false, Units.LOCATION).getUnitMap().entrySet()) {
			if (nextEntry.getValue().isEmpty()) {
				continue;
			}
			AbstractUnitPane blubs = UnitPaneFactoryImpl.getInstance().newInstance(nextEntry.getKey());

			this.addUnit(blubs.getIcon(), null, new Point2D(5, 5));
		}*/
                    }
                }
            }
        } catch (CouldNotPerformException | InterruptedException | ExecutionException | TimeoutException e) {
            //
        }
    }

    public void drawIcons() throws CouldNotPerformException, InterruptedException {

        final List<UnitConfigType.UnitConfig> locationUnitConfigList = Registries.getLocationRegistry().getLocationConfigs();

        final List<Point2D> vertices = new LinkedList<>();

        for (final UnitConfigType.UnitConfig locationUnitConfig : locationUnitConfigList) {
            try {
                //skip locations without a shape    
                if (locationUnitConfig.getPlacementConfig().getShape().getFloorCount() == 0) {
                    continue;
                }

                //  final List<Point2D> vertices = new LinkedList<>();
                // Get the transformation for the current room
//                final Future<Transform> transform = Units.getUnitTransformation(locationUnitConfig, Registries.getUnitRegistry().getUnitConfigById(locationUnitConfig.getPlacementConfig().getLocationId()));
                final Future<Transform> transform = Registries.getLocationRegistry().getUnitTransformation(locationUnitConfig, Registries.getLocationRegistry().getRootLocationConfig());

                // Get the shape of the room
                final List<Vec3DDoubleType.Vec3DDouble> shape = locationUnitConfig.getPlacementConfig().getShape().getFloorList();

                // Iterate over all vertices
                for (final Vec3DDoubleType.Vec3DDouble rstVertex : shape) {
                    // Convert vertex into java type
                    final Point3d vertex = new Point3d(rstVertex.getX(), rstVertex.getY(), rstVertex.getZ());
                    if (locationUnitConfig.getId().equals("81b9efa4-2dc9-432e-b47c-1d73021ff0f3")) {
                        System.out.print("x");
                    }
                    // Transform
                    transform.get(Constants.TRANSFORMATION_TIMEOUT, TimeUnit.MILLISECONDS).getTransform().transform(vertex);
                    // Add vertex to list of vertices
                    vertices.add(new Point2D(vertex.x, vertex.y));
                }

                // locationPane.addLocation(locationUnitConfig.getId(), locationUnitConfig.getLocationConfig().getChildIdList(), vertices, locationUnitConfig.getLocationConfig().getType().toString());
                //  this.addLocation(locationUnitConfig, vertices);
                //Units.getUnit(locationUnitConfig.getId(), false, Units.LOCATION).getUnitMap().
                //get all units in location
                for (final Map.Entry<UnitTemplateType.UnitTemplate.UnitType, List<UnitRemote>> nextEntry : Units.getUnit(locationUnitConfig.getId(), false, Units.LOCATION).getUnitMap().entrySet()) {
                    if (nextEntry.getValue().isEmpty()) {
                        continue;
                    }
                    // AbstractUnitPane blubs = UnitPaneFactoryImpl.getInstance().newInstance(nextEntry.getKey());
                    String type = nextEntry.getKey().name();
                    //addUnit(blubs.getIcon(), null, new Point2D(5,5));
                    for (UnitRemote<?> u : nextEntry.getValue()) {

                        //if ( u.getConfig().getId().equals("932b4f48-59d9-474a-b83e-82c4218b5ecf") ){
                        // if(pose.getTranslation().getX()!=0 && pose.getTranslation().getY()!=0 //&& !locationUnitConfig.getLabel().equals("Home")
                        //  && u.getConfig().getId().equals("02067c8e-eb24-46f7-a725-5e6ba535dea2")) {
                        if (u.getConfig().getId().equals("066a42fb-7850-481a-a0e9-c11648064e2b")) {
                            // || type.equals("COLORABLE_LIGHT")) {
                            //   if(locationUnitConfig.getId().equals("cd696027-fb4f-497c-af30-144859a462da")){
                            //     System.out.println("org.openbase.bco.bcozy.controller.LocationPaneController.fetchLocations()");
                            //  }
                            try {
                                PoseType.Pose pose = u.getConfig().getPlacementConfig().getPosition();
                                //   SVGIcon icon = UnitPaneFactoryImpl.getInstance().newInstance(UnitPaneFactoryImpl.loadUnitPaneClass(u.getType())).getIcon();
                                //double x = pose.getTranslation().getX()+(vertices.get(0).getX()*Constants.METER_TO_PIXEL);
                                //double y = pose.getTranslation().getY()+(vertices.get(0).getY() *Constants.METER_TO_PIXEL);
                                //locationPane.addUnit(icon, new Point2D(x,y ));
                                //final Future<Transform> transform2 = Registries.getLocationRegistry().getUnitTransformation(u.getConfig(), Registries.getLocationRegistry().getRootLocationConfig());
                                final Point3d vertex = new Point3d(pose.getTranslation().getX(), pose.getTranslation().getY(), pose.getTranslation().getZ());
                                transform.get(Constants.TRANSFORMATION_TIMEOUT / 10, TimeUnit.MILLISECONDS).getTransform().transform(vertex);
                                Point2D coord = new Point2D(vertex.x, vertex.y);

                                this.addUnit(new SVGIcon(FontAwesomeIcon.LIGHTBULB_ALT, 10.0, true), null, new Point2D(vertex.y * Constants.METER_TO_PIXEL, vertex.x * Constants.METER_TO_PIXEL));
                            } catch (CouldNotPerformException | TimeoutException e) {
                                //  ExceptionPrinter.printHistory("Error while transforming \"" + u.getConfig().getLabel() + "\", ID: " + u.getConfig().getId(), e, LOGGER, LogLevel.ERROR);
                            }
                        }
                        /*
                        try {
                            //Registries.getUnitRegistry().getBaseUnitConfigs().get(u.getId());
                            // if (u.getConfig().getId().equals("02067c8e-eb24-46f7-a725-5e6ba535dea2")) {
                            double test = u.getConfig().getPlacementConfig().getPosition().getTranslation().getX();
                            final Future<Transform> transform2 = Registries.getLocationRegistry().getUnitTransformation(u.getConfig(), Registries.getLocationRegistry().getRootLocationConfig());
                            final Point3d vertex = new Point3d(pose.getTranslation().getX(), pose.getTranslation().getY(), pose.getTranslation().getZ());
                            transform2.get(Constants.TRANSFORMATION_TIMEOUT, TimeUnit.MILLISECONDS).getTransform().transform(vertex);
                            Point2D coord = new Point2D(vertex.x, vertex.y);

                            locationPane.addUnit(new SVGIcon(FontAwesomeIcon.AMBULANCE, 6, true), new Point2D(vertex.y * Constants.METER_TO_PIXEL, vertex.x * Constants.METER_TO_PIXEL));
                            //    }

                            //final Point2d test = new Point2d(bb.getLeftFrontBottom().getX(), bb.getWidth()+ bb.getLeftFrontBottom());
                            //locationPane.addUnit(new SVGIcon(FontAweonsomeIcon.ARROW_LEFT, 10.0, true),bb.);
                        } catch (CouldNotPerformException e) {
                            //just leave out unit
                        }*/
                    }
                }
            } catch (InterruptedException | ExecutionException | TimeoutException | NotAvailableException ex) {
                //ExceptionPrinter.printHistory("Error while fetching transformation for location \"" + locationUnitConfig.getLabel() + "\", locationID: " + locationUnitConfig.getId(), ex, LOGGER, LogLevel.ERROR);
            }
            vertices.clear();
        }
        autofocus();
    }

    public void autofocus() {
        double scale = background.getLocP().getScaleX();
        this.setScaleX(scale);
        this.setScaleY(scale);

        for(Node b: this.getChildren()) {
            final Point2D transition = calculateTransition(scale, (UnitButton)b);

            b.setTranslateX(transition.getX());
            b.setTranslateY(transition.getY());
        }        
    }

    private Point2D calculateTransition(final double scale, final UnitButton button) {
        final double polygonDistanceToCenterX = (-(button.getCenterX() - (getLayoutBounds().getWidth() / 2))) * scale;
        final double polygonDistanceToCenterY = (-(button.getCenterY() - (getLayoutBounds().getHeight() / 2))) * scale;
        final double boundingBoxCenterX
                = (background.getLocP().getForeground().getBoundingBox().getMinX() + background.getLocP().getForeground().getBoundingBox().getMaxX()) / 2;
        final double boundingBoxCenterY
                = (background.getLocP().getForeground().getBoundingBox().getMinY() + background.getLocP().getForeground().getBoundingBox().getMaxY()) / 2;
        final double bbCenterDistanceToCenterX = ((getLayoutBounds().getWidth() / 2) - boundingBoxCenterX);
        final double bbCenterDistanceToCenterY = ((getLayoutBounds().getHeight() / 2) - boundingBoxCenterY);
        final double transitionX = polygonDistanceToCenterX - bbCenterDistanceToCenterX;
        final double transitionY = polygonDistanceToCenterY - bbCenterDistanceToCenterY;

        return new Point2D(transitionX, transitionY);
    }

    public void addUnit(final SVGIcon svgIcon, final EventHandler<ActionEvent> onActionHandler,
            final Point2D position) {
        final UnitButton unitButton = new UnitButton(svgIcon, onActionHandler);
        unitButton.setTranslateX(position.getX());
        unitButton.setTranslateY(position.getY());
        this.getChildren().add(unitButton);
    }

   
}
