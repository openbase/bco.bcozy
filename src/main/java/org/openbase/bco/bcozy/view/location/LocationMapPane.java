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
 * along with org.openbase.bco.bcozy. If not, see
 * <http://www.gnu.org/licenses/>.
 * ==================================================================
 */
package org.openbase.bco.bcozy.view.location;

import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.openbase.bco.bcozy.controller.powerterminal.Heatmap;
import org.openbase.bco.bcozy.view.BackgroundPane;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.ForegroundPane;
import org.openbase.bco.bcozy.view.InfoPane;
import org.openbase.bco.bcozy.view.generic.MultiTouchPane;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jps.core.JPService;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.EnumNotSupportedException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.extension.type.processing.LabelProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openbase.type.domotic.unit.UnitConfigType.UnitConfig;

import java.util.*;

/**
 * @author julian
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public final class LocationMapPane extends MultiTouchPane implements LocationMap {

    /**
     * Application logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LocationMapPane.class);
    private static boolean initialized;
    public final SimpleObjectProperty<DynamicUnitPolygon<?,?>> selectedUnit;
    private final ForegroundPane foregroundPane;
    private final BackgroundPane backgroundPane;
    private final List<DynamicPolygon> locationHoverLevelList;
    private final Map<String, LocationPolygon> tileMap;
    private final Map<String, LocationPolygon> regionMap;
    private final Map<String, LocationPolygon> zoneMap;
    private final Map<String, ConnectionPolygon> connectionMap;
    private final List<Node> debugNodes;
    private DynamicPolygon selectedLocation;
    private LocationPolygon rootLocation;
    private DynamicPolygon lastClickTarget;
    private LocationPolygon lastSelectedTile;

    //private final EventHandler<MouseEvent> mouseEventHandler;

    private final List<AnchorPoint> anchorPointList;

    private final StackPane editOverlay;
    private final Pane heatMap;

    private SelectionMode anchorManipulationMode;

    private double prevMouseCordX;
    private double prevMouseCordY;

    public enum SelectionMode {NON, HORIZONTAL, VERTICAL, BOTH}

    /**
     * Private constructor to deny manual instantiation.
     *
     * @param foregroundPane The foregroundPane
     */
    public LocationMapPane(final ForegroundPane foregroundPane, final BackgroundPane backgroundPane) {
        super();
        this.anchorManipulationMode = SelectionMode.NON;
        this.foregroundPane = foregroundPane;
        this.backgroundPane = backgroundPane;
        this.locationHoverLevelList = new ArrayList<>();
        this.tileMap = new HashMap<>();
        this.regionMap = new HashMap<>();
        this.zoneMap = new HashMap<>();
        this.connectionMap = new HashMap<>();
        this.debugNodes = new ArrayList<>();
        this.editOverlay = new StackPane();
        this.heatMap = new Heatmap();
        this.anchorPointList = new ArrayList<>();

        this.selectedUnit = new SimpleObjectProperty<>();
        this.rootLocation = null;

        this.heightProperty().addListener((observable, oldValue, newValue)
                -> this.setTranslateY(this.getTranslateY() - ((oldValue.doubleValue() - newValue.doubleValue()) / 2) * this.getScaleY()));

        this.widthProperty().addListener((observable, oldValue, newValue)
                -> this.setTranslateX(this.getTranslateX() - ((oldValue.doubleValue() - newValue.doubleValue()) / 2) * this.getScaleX()));

        this.foregroundPane.getMainMenuWidthProperty().addListener((observable, oldValue, newValue)
                -> this.setTranslateX(this.getTranslateX() - ((oldValue.doubleValue() - newValue.doubleValue()) / 2)));

        this.editOverlay.setPickOnBounds(false);


        // handle node mouse translations
        editOverlay.setOnMousePressed(event -> {

            // filter touch events
            if (event.isSynthesized()) {
                return;
            }

            if (anchorManipulationMode == SelectionMode.NON) {
                return;
            }

            if (!event.isSecondaryButtonDown()) {
                return;
            }

            // System.out.println("handle mouse pressed...");
            this.prevMouseCordX = event.getX();
            this.prevMouseCordY = event.getY();
            event.consume();
        });
        editOverlay.setOnMouseDragged(event -> {

            // filter touch events
            if (event.isSynthesized()) {
                return;
            }

            if (anchorManipulationMode == SelectionMode.NON) {
                return;
            }

            if (!event.isSecondaryButtonDown()) {
                return;
            }

            final double deltaX = event.getX() - prevMouseCordX;
            final double deltaY = event.getY() - prevMouseCordY;
            prevMouseCordX = event.getX();
            prevMouseCordY = event.getY();

            for (AnchorPoint anchorPoint : anchorPointList) {
                anchorPoint.translate(deltaX, deltaY, anchorManipulationMode);
            }

            event.consume();
        });
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean init) {
        initialized = init;
    }

    @Override
    public void deselectAnchorPoint(final AnchorPoint anchorPoint) {
        anchorPointList.remove(anchorPoint);
        anchorManipulationMode = computeAnchorManipulationMode();
        if (anchorPointList.isEmpty()) {
            editOverlay.setPickOnBounds(false);
        }
    }

    @Override
    public void selectAnchorPoint(final AnchorPoint anchorPoint) {
        editOverlay.setPickOnBounds(true);
        anchorPointList.add(anchorPoint);
        anchorManipulationMode = computeAnchorManipulationMode();
    }

    public void clearAncorpointSelection() {
        editOverlay.setPickOnBounds(false);
        anchorPointList.clear();
        anchorManipulationMode = computeAnchorManipulationMode();
    }

    public boolean isSelected(final AnchorPoint anchorPoint) {
        return anchorPointList.contains(anchorPoint);
    }

    private SelectionMode computeAnchorManipulationMode() {

        if (anchorPointList.isEmpty()) {
            return SelectionMode.NON;
        } else if (anchorPointList.size() == 1 && anchorPointList.get(0).getPolygon().isAllAnchorsSelected()) {
            return SelectionMode.BOTH;
        }

        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;

        for (AnchorPoint point : anchorPointList) {
            minX = Math.min(point.getTranslateX(), minX);
            maxX = Math.max(point.getTranslateX(), maxX);
            minY = Math.min(point.getTranslateY(), minY);
            maxY = Math.max(point.getTranslateY(), maxY);
        }

        if (Math.abs(maxX - minX) > Math.abs(maxY - minY)) {
            return SelectionMode.HORIZONTAL;
        } else {
            return SelectionMode.VERTICAL;
        }
    }

    /**
     * Adds a room to the location Pane and use the controls to add a mouse
     * event handler.
     * <p>
     * If a room with the same id already exists, it will be overwritten.
     *
     * @param locationUnitConfig the configuration of the location to add.
     *
     * @throws org.openbase.jul.exception.CouldNotPerformException
     * @throws java.lang.InterruptedException
     */
    public void addLocation(final UnitConfig locationUnitConfig) throws CouldNotPerformException, InterruptedException {
        try {

            LocationPolygon locationPolygon;
            switch (locationUnitConfig.getLocationConfig().getLocationType()) {
                case TILE:
                    locationPolygon = new TilePolygon(this);
                    tileMap.put(locationUnitConfig.getId(), locationPolygon);
                    break;
                case REGION:
                    locationPolygon = new RegionPolygon(this);
                    regionMap.put(locationUnitConfig.getId(), locationPolygon);
                    break;
                case ZONE:
                    locationPolygon = new ZonePolygon(this);
                    zoneMap.put(locationUnitConfig.getId(), locationPolygon);
                    break;
                default:
                    throw new EnumNotSupportedException(locationUnitConfig.getLocationConfig().getLocationType(), this);
            }

            locationPolygon.init(locationUnitConfig);
            locationPolygon.activate();

            // configure root location if detected
            if (locationUnitConfig.getLocationConfig().getRoot()) {
                setRootLocation(locationPolygon);
            }

        } catch (Exception ex) {
            throw new CouldNotPerformException("Could not add location!", ex);
        }
    }

    /**
     * Adds a connection to the location Pane.
     * <p>
     * If a connection with the same id already exists, it will be overwritten.
     *
     * @param connectionUnitConfig the unit config of this connection.
     *
     * @throws org.openbase.jul.exception.CouldNotPerformException
     * @throws java.lang.InterruptedException
     */
    public void addConnection(final UnitConfig connectionUnitConfig) throws CouldNotPerformException, InterruptedException {

        try {
            ConnectionPolygon connectionPolygon;
            switch (connectionUnitConfig.getConnectionConfig().getConnectionType()) {
                case DOOR:
                    connectionPolygon = new DoorPolygon(this);
                    break;
                case WINDOW:
                    connectionPolygon = new WindowPolygon(this);
                    break;
                case PASSAGE:
                    connectionPolygon = new PassagePolygon(this);
                    break;
                default:
                    throw new EnumNotSupportedException(connectionUnitConfig.getConnectionConfig().getConnectionType(), this);
            }

            connectionPolygon.init(connectionUnitConfig);
            connectionPolygon.activate();

            connectionMap.put(connectionUnitConfig.getId(), connectionPolygon);

            connectionUnitConfig.getConnectionConfig().getTileIdList().forEach(locationId -> {
                if (tileMap.containsKey(locationId)) {

                    tileMap.get(locationId).addCuttingShape(connectionPolygon);
                } else {
                    String unitLabel = locationId;
                    try {
                        unitLabel = LabelProcessor.getBestMatch(Registries.getUnitRegistry(false).getUnitConfigById(locationId).getLabel());
                    } catch (CouldNotPerformException | InterruptedException ex) {
                        // id is used instead.
                    }
                    LOGGER.debug("Location " + unitLabel + " can not be found in the location Map. No Cutting will be applied.");
                }
            });
        } catch (CouldNotPerformException ex) {
            throw new CouldNotPerformException("Could not add connection!", ex);
        }
    }

    /**
     * Will add a UnitIcon to the locationPane.
     *
     * @param SVGGlyphIcon The icon
     * @param onActionHandler The Handler that gets activated when the button is
     * pressed
     * @param position The position where the button is to be placed
     *
     * public void addUnit(final SVGGlyphIcon SVGGlyphIcon, final EventHandler<ActionEvent> onActionHandler,
     * final Point2D position) {
     * final UnitButton unitButton = new UnitButton(SVGGlyphIcon, onActionHandler);
     * unitButton.setTranslateX(position.getX());
     * unitButton.setTranslateY(position.getY());
     * this.getChildren().add(unitButton);
     * } *
     * public void addUnit(final SVGGlyphIcon SVGGlyphIcon,
     * final Point2D position) {
     * final UnitButton unitButton = new UnitButton(SVGGlyphIcon, null);
     * unitButton.setTranslateX(position.getX());
     * unitButton.setTranslateY(position.getY());
     * //unitSymbols.add(unitButton);
     * }
     */
    /**
     * Erases all locations from the locationPane.
     */
    public void clearLocations() {

        zoneMap.forEach((locationId, locationPolygon) -> {
                    locationPolygon.shutdown();
                    this.getChildren().remove(locationPolygon);
                }
        );
        zoneMap.clear();

        tileMap.forEach((locationId, locationPolygon) -> {
                    locationPolygon.shutdown();
                    this.getChildren().remove(locationPolygon);
                }
        );
        tileMap.clear();

        regionMap.forEach((locationId, locationPolygon) -> {
                    locationPolygon.shutdown();
                    this.getChildren().remove(locationPolygon);
                }
        );
        regionMap.clear();

        debugNodes.clear();

        // clear root location
        setRootLocation(null);
    }

    private synchronized void setRootLocation(final LocationPolygon rootLocation) {

        // filter if nothing has been changed.
        if (this.rootLocation == rootLocation) {
            return;
        }

        // deregister old root location bindings
        if (this.rootLocation != null) {
            onMouseClickedProperty().unbind();
            onMouseEnteredProperty().unbind();
            onMouseExitedProperty().unbind();
        }

        // setup new root location
        this.rootLocation = rootLocation;

        // apply new binding if the new root location is valid
        if (this.rootLocation != null) {
            onMouseClickedProperty().bind(rootLocation.onMouseClickedProperty());
            onMouseEnteredProperty().bind(rootLocation.onMouseEnteredProperty());
            onMouseExitedProperty().bind(rootLocation.onMouseExitedProperty());
        }
    }

    /**
     * Erases all connections from the locationPane.
     */
    public void clearConnections() {
        connectionMap.forEach((connectionId, connectionPolygon) -> {
                    connectionPolygon.shutdown();
                    this.getChildren().remove(connectionPolygon);
                }
        );
        connectionMap.clear();
    }

    /**
     * Will clear everything on the location Pane and then add everything that
     * is saved in the maps. Also adds a cutting shape for every Polygon to the
     * root.
     */
    public void updateLocationPane() {
        this.getChildren().clear();


        zoneMap.forEach((locationId, locationPolygon) -> {
//            if (rootLocation != null) {
//                rootLocation.addCuttingShape(locationPolygon);
//            }
            this.getChildren().add(locationPolygon);
        });

        tileMap.forEach((locationId, locationPolygon) -> {
//            if (rootLocation != null) {
//                rootLocation.addCuttingShape(locationPolygon);
//            }
            this.getChildren().add(locationPolygon);
        });

//        if (rootLocation != null) {
//            this.getChildren().add(rootLocation);
//        }

        regionMap.forEach((locationId, locationPolygon) -> {
//            if (rootLocation != null) {
//                rootLocation.addCuttingShape(locationPolygon);
//            }
            this.getChildren().add(locationPolygon);
        });

        connectionMap.forEach((connectionId, connectionPolygon) -> {
//            if (rootLocation != null) {
//                rootLocation.addCuttingShape(connectionPolygon);
//            }
            this.getChildren().add(connectionPolygon);
        });

        getChildren().add(editOverlay);

        if (backgroundPane.getheatmapActiveProperty().get()) {
            System.out.println("heatmap hinzufügen");
        }
        getChildren().add(heatMap);



        if (JPService.debugMode()) {
            // debug print
            for (final Node debugNode : debugNodes) {
                this.getChildren().add(debugNode);
            }
        }

        if (!isLocationSelected()) {
            selectRootLocation();
        }
    }

    @Override
    public Pane getEditOverlay() {
        return editOverlay;
    }

    @Override
    public DynamicPolygon getLastClickTarget() {
        return lastClickTarget;
    }

    @Override
    public void setSelectedUnit(final DynamicPolygon newSelectedLocation) throws CouldNotPerformException {
        try {
            lastClickTarget = newSelectedLocation;
            if (selectedLocation != null && selectedLocation.equals(newSelectedLocation)) {
                // already selected
                return;
            }

            // deselect anchors of other locations
            for (AnchorPoint anchorPoint : new ArrayList<>(anchorPointList)) {
                if (!anchorPoint.getPolygon().equals(newSelectedLocation)) {
                    deselectAnchorPoint(anchorPoint);
                }
            }

            if (lastSelectedTile != null) {
                // make sub sub regions unselectable
                if (!newSelectedLocation.getClass().equals(RegionPolygon.class)) {
                    lastSelectedTile.getChildIds().forEach(childId -> {
                        try {
                            // make all regions non selectable
                            if (regionMap.containsKey(childId)) {
                                regionMap.get(childId).setSelectable(false);
                            }
                        } catch (Exception ex) {
                            ExceptionPrinter.printHistory(ex, LOGGER);
                        }
                    });
                }
            }

            // allow selection of sub regions.
            if (newSelectedLocation.getClass().equals(TilePolygon.class)) {
                lastSelectedTile = (TilePolygon) newSelectedLocation;
                lastSelectedTile.getChildIds().forEach(childId -> {
                    try {
                        regionMap.get(childId).setSelectable(true);
                    } catch (Exception ex) {
                        ExceptionPrinter.printHistory(ex, LOGGER);
                    }
                });
            }

            if (selectedLocation != null) {
                selectedLocation.setSelected(false);
            }
            newSelectedLocation.setSelected(true);
            selectedLocation = newSelectedLocation;
            selectedUnit.set((DynamicUnitPolygon) newSelectedLocation);
            foregroundPane.getUnitMenu().getRoomInfo().setText(selectedLocation.getLabel());

        } catch (CouldNotPerformException ex) {
            throw new CouldNotPerformException("Could not select location polygon!", ex);
        }
    }

    @Override
    public void zoomFit() {
        if (rootLocation != null) {
            autoFocusPolygon(rootLocation);
        } else if (!tileMap.isEmpty()) {
            autoFocusPolygon(tileMap.values().iterator().next());
        }
    }

    @Override
    public void addSelectedUnitListener(final ChangeListener<? super DynamicUnitPolygon> changeListener) {
        selectedUnit.addListener(changeListener);
    }

    @Override
    public void removeSelectedUnitListener(final ChangeListener<? super DynamicUnitPolygon> changeListener) {
        selectedUnit.removeListener(changeListener);
    }


    @Override
    public void autoFocusPolygonAnimated(final DynamicPolygon polygon) {
        final ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100));
        final double scale = computeScale(polygon);
        scaleTransition.setToX(scale);
        scaleTransition.setToY(scale);
        scaleTransition.setCycleCount(1);
        scaleTransition.setAutoReverse(true);

        final Point2D transition = calculateTransition(scale, polygon);

        final TranslateTransition translateTransition = new TranslateTransition(Duration.millis(100));
        translateTransition.setToX(transition.getX());
        translateTransition.setToY(transition.getY());
        translateTransition.setCycleCount(1);
        translateTransition.setAutoReverse(true);

        final ParallelTransition parallelTransition
                = new ParallelTransition(this, scaleTransition, translateTransition);
        parallelTransition.play();
    }

    @Override
    public void handleHoverUpdate(final DynamicPolygon locationPolygon, boolean hover) {

        if (hover) {
            locationHoverLevelList.add(locationPolygon);
        } else {
            locationHoverLevelList.remove(locationPolygon);

            // only deselect
            if (!locationHoverLevelList.contains(locationPolygon)) {
                locationPolygon.setStrokeWidth(Constants.ROOM_STROKE_WIDTH);
            }
        }

        // sort by location level
        Collections.sort(locationHoverLevelList, (o1, o2) -> Integer.compare(o2.getLevel(), o1.getLevel()));

        InfoPane.info("");

        boolean first = true;
        DynamicPolygon lastPolygon = null;
        for (DynamicPolygon polygon : locationHoverLevelList) {

            // only handle ones
            if (lastPolygon == polygon) {
                continue;
            }

            // select top level location
            if (first) {
                first = false;
                polygon.setStrokeWidth(Constants.ROOM_STROKE_WIDTH_MOUSE_OVER);
                InfoPane.info(polygon.getLabel("?"));
            } else {
                polygon.setStrokeWidth(Constants.ROOM_STROKE_WIDTH);
            }

            lastPolygon = polygon;
        }
    }

    @Override
    public Point2D calculateTransition(double scale, DynamicPolygon polygon) {
        final double polygonDistanceToCenterX = (-(polygon.getCenterX() - (getLayoutBounds().getWidth() / 2))) * scale;
        final double polygonDistanceToCenterY = (-(polygon.getCenterY() - (getLayoutBounds().getHeight() / 2))) * scale;
        final double boundingBoxCenterX = (foregroundPane.getBoundingBox().getMinX() + foregroundPane.getBoundingBox().getMaxX()) / 2;
        final double boundingBoxCenterY = (foregroundPane.getBoundingBox().getMinY() + foregroundPane.getBoundingBox().getMaxY()) / 2;
        final double bbCenterDistanceToCenterX = ((getLayoutBounds().getWidth() / 2) - boundingBoxCenterX);
        final double bbCenterDistanceToCenterY = ((getLayoutBounds().getHeight() / 2) - boundingBoxCenterY);
        final double transitionX = polygonDistanceToCenterX - bbCenterDistanceToCenterX;
        final double transitionY = polygonDistanceToCenterY - bbCenterDistanceToCenterY;

        return new Point2D(transitionX, transitionY);
    }

    @Override
    public void autoFocusPolygon(DynamicPolygon polygon) {
        final double scale = computeScale(polygon);
        this.setScaleX(scale);
        this.setScaleY(scale);

        final Point2D transition = calculateTransition(scale, polygon);
        this.setTranslateX(transition.getX());
        this.setTranslateY(transition.getY());
    }

    @Override
    public double computeScale(DynamicPolygon polygon) {
        final double xScale = (foregroundPane.getBoundingBox().getWidth() / polygon.prefWidth(0))
                * Constants.ZOOM_FIT_PERCENTAGE_WIDTH;
        final double yScale = (foregroundPane.getBoundingBox().getHeight() / polygon.prefHeight(0))
                * Constants.ZOOM_FIT_PERCENTAGE_HEIGHT;
        return (xScale < yScale) ? xScale : yScale;
    }

    @Override
    public void selectRootLocation() {

        // check if exists
        if (rootLocation == null) {
            LocationMapPane.LOGGER.debug("Could not select root because its not available.");
            return;
        }

        // check is not already selected
        if (rootLocation.equals(selectedLocation)) {
            return;
        }

        // deselect selected location
        if (selectedLocation != null) {
            selectedLocation.setSelected(false);
        }
        rootLocation.setSelected(true);
        try {
            setSelectedUnit(rootLocation);
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory("Could not select root loaction!", ex, LocationMapPane.LOGGER);
        }
    }

    @Override
    public boolean isLocationSelected() {
        return selectedLocation != null;
    }
}
