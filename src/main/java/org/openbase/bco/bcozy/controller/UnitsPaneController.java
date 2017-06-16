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

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javax.vecmath.Point3d;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.UnitSymbolsPane;
import org.openbase.bco.bcozy.view.location.LocationPane;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.openbase.jul.pattern.Observable;
import org.openbase.jul.pattern.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rct.Transform;
import rst.domotic.registry.UnitRegistryDataType.UnitRegistryData;
import rst.domotic.unit.UnitConfigType;
import rst.domotic.unit.UnitTemplateType;
import rst.geometry.PoseType;
/**
 *
 * @author lili
 */
public class UnitsPaneController {

    /**
     * Application logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LocationPaneController.class);

    private final LocationPane locationPane;
    private final UnitSymbolsPane unitPane;

    public UnitsPaneController(UnitSymbolsPane unitPane, LocationPane locationPane) {
        this.locationPane = locationPane;
        this.unitPane = unitPane;

        unitPane.scaleXProperty().bind(locationPane.scaleXProperty());
        unitPane.scaleYProperty().bind(locationPane.scaleYProperty());
        unitPane.translateXProperty().bind(locationPane.translateXProperty());
        unitPane.translateYProperty().bind(locationPane.translateYProperty());
    }

    /**
     * Establishes the connection with the RemoteRegistry.
     */
    public void connectUnitRemote() {
        try {
            Registries.getUnitRegistry().waitForData();
            Registries.getUnitRegistry().addDataObserver(new Observer<UnitRegistryData>() {
                @Override
                public void update(Observable<UnitRegistryData> source, UnitRegistryData data) throws Exception {
                    Platform.runLater(() -> {
                        try {
                            fetchUnits();
                            unitPane.updateUnitsPane();
                        } catch (CouldNotPerformException | InterruptedException e) {
                            ExceptionPrinter.printHistory(e, LOGGER);
                        }
                    });
                }
            });
            updateUnits();
            // TODO: update view!! (zoom)
        } catch (Exception e) { //NOPMD
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
    }

    public void fetchUnits() throws CouldNotPerformException, InterruptedException {   //TODO extend this to all needed Device configs.

        final List<UnitConfigType.UnitConfig> unitConfigList = Registries.getUnitRegistry().
            getUnitConfigs(UnitTemplateType.UnitTemplate.UnitType.COLORABLE_LIGHT);
        
        unitConfigList.addAll(Registries.getUnitRegistry().
            getUnitConfigs(UnitTemplateType.UnitTemplate.UnitType.BATTERY));

        for (final UnitConfigType.UnitConfig unitConfig : unitConfigList) {
            try {
                PoseType.Pose pose = unitConfig.getPlacementConfig().getPosition();
                final Future<Transform> transform = Registries.getLocationRegistry().
                    getUnitTransformation(unitConfig, Registries.getLocationRegistry().getRootLocationConfig());
                final Point3d vertex = new Point3d(pose.getTranslation().getX(), pose.getTranslation().getY(), pose.getTranslation().getZ());
                transform.get(Constants.TRANSFORMATION_TIMEOUT / 10, TimeUnit.MILLISECONDS).getTransform().transform(vertex);
                Point2D coord = new Point2D(vertex.x * Constants.METER_TO_PIXEL, vertex.y * Constants.METER_TO_PIXEL);

                unitPane.addUnit(unitConfig, coord);

            } catch (ExecutionException | TimeoutException ex) {
                java.util.logging.Logger.getLogger(UnitsPaneController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void updateUnits() {
        Platform.runLater((() -> {
            try {
                fetchUnits();
                unitPane.updateUnitsPane();
            } catch (CouldNotPerformException | InterruptedException e) {
                ExceptionPrinter.printHistory(e, LOGGER);
            }
        }));
    }
}

/*
//Notes
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
                       if (u.getConfig().getId().equals("066a42fb-7850-481a-a0e9-c11648064e2b") && !locationUnitConfig.getLabel().equals("Home")){
                           // || type.equals("COLORABLE_LIGHT")) {
                        //   if(locationUnitConfig.getId().equals("cd696027-fb4f-497c-af30-144859a462da")){
                          //     System.out.println("org.openbase.bco.bcozy.controller.LocationPaneController.fetchLocations()");
                         //  }
                            try {
                                Pose pose = u.getConfig().getPlacementConfig().getPosition();
                                //   SVGIcon icon = UnitPaneFactoryImpl.getInstance().newInstance(UnitPaneFactoryImpl.loadUnitPaneClass(u.getType())).getIcon();
                                //double x = pose.getTranslation().getX()+(vertices.get(0).getX()*Constants.METER_TO_PIXEL);
                                //double y = pose.getTranslation().getY()+(vertices.get(0).getY() *Constants.METER_TO_PIXEL);
                                //locationPane.addUnit(icon, new Point2D(x,y ));
                                final Future<Transform> transform2 = Registries.getLocationRegistry().getUnitTransformation(u.getConfig(), Registries.getLocationRegistry().getRootLocationConfig());
                                final Point3d vertex = new Point3d(pose.getTranslation().getX(), pose.getTranslation().getY(), pose.getTranslation().getZ());
                                transform.get(Constants.TRANSFORMATION_TIMEOUT/10, TimeUnit.MILLISECONDS).getTransform().transform(vertex);
                                Point2D coord = new Point2D(vertex.x, vertex.y);

//                                locationPane.addUnit(new SVGIcon(FontAwesomeIcon.LIGHTBULB_ALT, 10.0, true), new Point2D(vertex.y * Constants.METER_TO_PIXEL, vertex.x * Constants.METER_TO_PIXEL));
                            } catch (CouldNotPerformException | TimeoutException e) {
                                ExceptionPrinter.printHistory("Error while transforming \"" + u.getConfig().getLabel() + "\", ID: " + u.getConfig().getId(), e, LOGGER, LogLevel.ERROR);
                            }
                        } 
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
                        }
                    }
                }*/