/**
 * ==================================================================
 * <p>
 * This file is part of org.openbase.bco.bcozy.
 * <p>
 * org.openbase.bco.bcozy is free software: you can redistribute it and modify
 * it under the terms of the GNU General Public License (Version 3)
 * as published by the Free Software Foundation.
 * <p>
 * org.openbase.bco.bcozy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with org.openbase.bco.bcozy. If not, see <http://www.gnu.org/licenses/>.
 * ==================================================================
 */
package org.openbase.bco.bcozy.view.location;

import com.google.protobuf.Message;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.dal.lib.layer.unit.UnitRemote;
import org.openbase.bco.dal.remote.layer.unit.Units;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.VerificationFailedException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.iface.Manageable;
import org.openbase.jul.pattern.Observer;
import org.openbase.jul.pattern.controller.ConfigurableRemote;
import org.openbase.jul.pattern.provider.DataProvider;
import org.openbase.rct.Transform;
import org.openbase.type.domotic.unit.UnitConfigType.UnitConfig;
import org.openbase.type.math.Vec3DDoubleType;
import org.openbase.type.math.Vec3DDoubleType.Vec3DDouble;
import org.openbase.type.spatial.ShapeType.Shape.Builder;

import javax.vecmath.Point3d;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public abstract class DynamicUnitPolygon<M extends Message, UR extends UnitRemote<M>> extends DynamicPolygon implements Manageable<UnitConfig> {

    private UR unitRemote;
    private Observer<DataProvider<M>, M> dataObserver;
    private Observer<ConfigurableRemote<String, M, UnitConfig>, UnitConfig> configObserver;
    private boolean active;

    public DynamicUnitPolygon(final LocationMap locationMap) {
        super(locationMap);
        this.dataObserver = (source, data) -> {
            Platform.runLater(() -> {
                applyDataUpdate(data);
            });
        };
        this.configObserver = (source, config) -> {
            Platform.runLater(() -> {
                try {
                    applyConfigUpdate(config);
                } catch (InterruptedException ex) {
                    ExceptionPrinter.printHistory("Could not save placement changes!", ex, LOGGER);
                }
            });
        };
    }

    @Override
    public void init(final UnitConfig unitConfig) throws InitializationException, InterruptedException {
        try {
            this.unitRemote = (UR) Units.getUnit(unitConfig, false);
        } catch (final CouldNotPerformException ex) {
            throw new InitializationException(this, ex);
        }
    }

    @Override
    public void activate() throws CouldNotPerformException, InterruptedException {
        active = true;
        unitRemote.addDataObserver(dataObserver);
        unitRemote.addConfigObserver(configObserver);

        if (unitRemote.isConfigAvailable()) {
            applyConfigUpdate(unitRemote.getConfig());
        }

        if (unitRemote.isDataAvailable()) {
            applyDataUpdate(unitRemote.getData());
        }
    }

    @Override
    public void deactivate() throws CouldNotPerformException, InterruptedException {
        active = false;
        unitRemote.removeDataObserver(dataObserver);
        unitRemote.removeConfigObserver(configObserver);
    }

    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Method returns the internal unit remote.
     *
     * @return
     *
     * @throws NotAvailableException
     */
    public UR getUnitRemote() throws NotAvailableException {
        if (unitRemote == null) {
            throw new NotAvailableException("UnitRemote");
        }
        return unitRemote;
    }

    /**
     * Returns the id of the internal unit.
     */
    public String getUnitId() throws NotAvailableException {
        return getUnitRemote().getId();
    }

    /**
     * Method return the label of the unit.
     *
     * @return the label as a String
     */
    public String getLabel() {
        try {
            return unitRemote.getLabel();
        } catch (NotAvailableException e) {
            return "?";
        }
    }

    /**
     * Method return the config of the unit.
     *
     * @return the unit config.
     */
    public UnitConfig getConfig() throws NotAvailableException {
        return unitRemote.getConfig();
    }

    public abstract void applyDataUpdate(final M unitData);

    public void applyConfigUpdate(final UnitConfig unitConfig) throws InterruptedException {
        try {
            updateVertices();
        } catch (CouldNotPerformException | InterruptedException ex) {
            ExceptionPrinter.printHistory("Could not update unit shape of " + getLabel(), ex, LOGGER);
        }
    }

    /**
     * Method loads all vertices of the shape of this unit.
     *
     * @return the points forming the unit shape.
     *
     * @throws InterruptedException     is thrown when the thread was internally interrupted since this method takes some time to resolve all transformations.
     * @throws CouldNotPerformException is thrown when the points are not yet accessible.
     **/
    public List<Point2D> loadShapeVertices() throws InterruptedException, CouldNotPerformException {
        return loadShapeVertices(getConfig());
    }

    /**
     * Method loads all vertices of the shape of the given unit.
     *
     * @param unitConfig the config to resolve the shape.
     *
     * @return the points forming the unit shape.
     *
     * @throws InterruptedException     is thrown when the thread was internally interrupted since this method takes some time to resolve all transformations.
     * @throws CouldNotPerformException is thrown when the points are not yet accessible.
     **/
    public static List<Point2D> loadShapeVertices(UnitConfig unitConfig) throws InterruptedException, CouldNotPerformException {
        try {
            final List<Point2D> vertices = new LinkedList<>();

            // Get the transformation for the current room
            final Future<Transform> transform = Registries.getUnitRegistry().getUnitTransformationFuture(unitConfig, Registries.getUnitRegistry().getRootLocationConfig());

            // Get the shape of the room
            final List<Vec3DDoubleType.Vec3DDouble> shape = unitConfig.getPlacementConfig().getShape().getFloorList();

            // Iterate over all vertices
            for (final Vec3DDoubleType.Vec3DDouble rstVertex : shape) {
                // Convert vertex into java type
                final Point3d vertex = new Point3d(rstVertex.getX(), rstVertex.getY(), rstVertex.getZ());
                // Transform
                transform.get(Constants.TRANSFORMATION_TIMEOUT, TimeUnit.MILLISECONDS).getTransform().transform(vertex);
                // Add vertex to list of vertices
                vertices.add(new Point2D(vertex.x * Constants.METER_TO_PIXEL, vertex.y * Constants.METER_TO_PIXEL));
            }
            return vertices;
        } catch (TimeoutException | ExecutionException ex) {
            throw new CouldNotPerformException(ex);
        }
    }

    @Override
    public void saveChanges() {
        try {
            final UnitConfig.Builder configBuilder = getConfig().toBuilder();

            // Get the transformation for the current room
            final Future<Transform> transform = Registries.getUnitRegistry().getUnitTransformationFuture(Registries.getUnitRegistry().getRootLocationConfig(), configBuilder.build());


            final Builder shapeBuilder = configBuilder.getPlacementConfigBuilder().getShapeBuilder();

            final List<Vec3DDouble> floorListBackup = shapeBuilder.getFloorList();
            final List<Vec3DDouble> ceilingListBackup = shapeBuilder.getCeilingList();
            final List<AnchorPoint> anchorPointList = getAnchorPointList();

            // validate compatibility
            if( anchorPointList.size() != floorListBackup.size()) {
                throw new VerificationFailedException("Anchor point count differs from floor point count!");
            }

            final boolean handleCeiling;
            if (ceilingListBackup.size() == floorListBackup.size()) {
                handleCeiling = true;
                shapeBuilder.clearCeiling();
            } else {
                handleCeiling = false;
            }

            shapeBuilder.clearFloor();
            for (int i = 0; i < anchorPointList.size(); i++) {

                // reconstruct point
                final Point3d point3d = new Point3d(
                        anchorPointList.get(i).getX() / Constants.METER_TO_PIXEL,
                        anchorPointList.get(i).getY() / Constants.METER_TO_PIXEL,
                        0);
                transform.get(5, TimeUnit.SECONDS).getTransform().transform(point3d);
                final Vec3DDouble.Builder pointBuilder = floorListBackup.get(i).toBuilder();
                pointBuilder.setX(point3d.x);
                pointBuilder.setY(point3d.y);
                shapeBuilder.addFloor(pointBuilder.build());

                if (handleCeiling) {
                    try {
                        // update ceiling if bound to floor
                        if ((Math.abs(floorListBackup.get(i).getX() - ceilingListBackup.get(i).getX()) < 0.01)
                                && (Math.abs(floorListBackup.get(i).getY() - ceilingListBackup.get(i).getY()) < 0.01)) {

                            // restore z
                            pointBuilder.setZ(ceilingListBackup.get(i).getZ());

                            // store updated point
                            shapeBuilder.addCeiling(pointBuilder.build());
                        } else {
                            // restore old one
                            shapeBuilder.addCeiling(ceilingListBackup.get(i));
                        }
                    } catch (IndexOutOfBoundsException ex) {
                        // restore old one
                        shapeBuilder.addCeiling(ceilingListBackup.get(i));
                    }
                }
            }
            Registries.getUnitRegistry().updateUnitConfig(configBuilder.build()).get(30, TimeUnit.SECONDS);
        } catch (CouldNotPerformException | ExecutionException | TimeoutException | InterruptedException ex) {
            ExceptionPrinter.printHistory("Could not save placement changes!", ex, LOGGER);
        }
    }
}
