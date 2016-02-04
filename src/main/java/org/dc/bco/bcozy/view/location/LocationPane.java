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
package org.dc.bco.bcozy.view.location;

import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.dc.bco.bcozy.view.Constants;
import org.dc.bco.bcozy.view.ForegroundPane;
import org.dc.bco.bcozy.view.SVGIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public final class LocationPane extends Pane {

    /**
     * Singleton instance.
     */
    private static LocationPane instance;

    /**
     * Application logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LocationPane.class);

    private LocationPolygon selectedLocation;
    private ZonePolygon rootRoom;
    private final ForegroundPane foregroundPane;
    private final Map<String, TilePolygon> tileMap;
    private final Map<String, RegionPolygon> regionMap;
    private final SimpleStringProperty selectedLocationId;

    private final Map<String, ConnectionPolygon> connectionMap;

    private LocationPolygon lastFirstClickTarget;
    private LocationPolygon lastSelectedTile;
    private final EventHandler<MouseEvent> onEmptyAreaClickHandler;

    /**
     * Private constructor to deny manual instantiation.
     *
     * @param foregroundPane The foregroundPane
     */
    private LocationPane(final ForegroundPane foregroundPane) {
        super();

        this.foregroundPane = foregroundPane;

        tileMap = new HashMap<>();
        regionMap = new HashMap<>();
        connectionMap = new HashMap<>();

        //Dummy Room
        selectedLocation = new ZonePolygon(
                Constants.DUMMY_ROOM_NAME, Constants.DUMMY_ROOM_NAME, new LinkedList<>(), 0.0, 0.0, 0.0, 0.0);
        selectedLocationId = new SimpleStringProperty(Constants.DUMMY_ROOM_NAME);

        rootRoom = null;
        lastSelectedTile = selectedLocation;
        lastFirstClickTarget = selectedLocation;
        onEmptyAreaClickHandler = event -> {
            if (event.isStillSincePress() && rootRoom != null) {
                if (event.getClickCount() == 1) {
                    if (!selectedLocation.equals(rootRoom)) {
                        selectedLocation.setSelected(false);
                        rootRoom.setSelected(true);
                        this.setSelectedLocation(rootRoom);
                    }
                } else if (event.getClickCount() == 2) {
                    this.autoFocusPolygonAnimated(rootRoom);
                }

                foregroundPane.getContextMenu().getRoomInfo().setText(selectedLocation.getLabel());
            }
        };

        this.heightProperty().addListener((observable, oldValue, newValue) ->
                this.setTranslateY(this.getTranslateY()
                        - ((oldValue.doubleValue() - newValue.doubleValue()) / 2) * this.getScaleY()));

        this.widthProperty().addListener((observable, oldValue, newValue) ->
                this.setTranslateX(this.getTranslateX()
                        - ((oldValue.doubleValue() - newValue.doubleValue()) / 2) * this.getScaleX()));

        this.foregroundPane.getMainMenuWidthProperty().addListener((observable, oldValue, newValue) ->
                this.setTranslateX(this.getTranslateX()
                        - ((oldValue.doubleValue() - newValue.doubleValue()) / 2)));
    }

    /**
     * Singleton Pattern. This method call can not be used to instantiate the singleton.
     * @return the singleton instance of the location pane
     * @throws InstantiationException thrown if no getInstance(ForegroundPane foregroundPane) is called before
     */
    public static LocationPane getInstance() throws InstantiationException {
        synchronized (LocationPane.class) {
            if (LocationPane.instance == null) {
                throw new InstantiationException();
            }
        }
        return LocationPane.instance;
    }

    /**
     * Singleton Pattern.
     * @return the singleton instance of the location pane
     * @param foregroundPane the foreground pane needed for instantiation
     */
    public static LocationPane getInstance(final ForegroundPane foregroundPane) {
        synchronized (LocationPane.class) {
            if (LocationPane.instance == null) {
                LocationPane.instance = new LocationPane(foregroundPane);
            }
        }
        return LocationPane.instance;
    }

    /**
     * Adds a room to the location Pane and use the controls to add a mouse event handler.
     *
     * If a room with the same id already exists, it will be overwritten.
     *
     * @param locationId    The location id
     * @param locationLabel The location label
     * @param childIds      The ids of the children
     * @param vertices      A list of vertices which defines the shape of the room
     * @param locationType  The type of the location {ZONE,REGION,TILE}
     */
    public void addLocation(final String locationId, final String locationLabel, final List<String> childIds,
                            final List<Point2D> vertices, final String locationType) {
        // Fill the list of vertices into an array of points
        double[] points = new double[vertices.size() * 2];
        for (int i = 0; i < vertices.size(); i++) {
            // TODO: X and Y are swapped in the world of the csra... make it more generic...
            points[i * 2] = vertices.get(i).getY() * Constants.METER_TO_PIXEL;
            points[i * 2 + 1] = vertices.get(i).getX() * Constants.METER_TO_PIXEL;
        }

        LocationPolygon locationPolygon;

        switch (locationType) {
            case "TILE":
                locationPolygon = new TilePolygon(locationLabel, locationId, childIds, points);
                addMouseEventHandlerToTile((TilePolygon) locationPolygon);
                tileMap.put(locationId, (TilePolygon) locationPolygon);
                break;
            case "REGION":
                locationPolygon = new RegionPolygon(locationLabel, locationId, childIds, points);
                addMouseEventHandlerToRegion((RegionPolygon) locationPolygon);
                regionMap.put(locationId, (RegionPolygon) locationPolygon);
                break;
            case "ZONE":
                locationPolygon = new ZonePolygon(locationLabel, locationId, childIds, points);
                rootRoom = (ZonePolygon) locationPolygon; //TODO: handle the situation where several zones exist
                break;
            default:
                LOGGER.warn("The following location has an unknown LocationType and will be ignored:"
                        + "\n  location UUID:  " + locationId
                        + "\n  location Label: " + locationLabel
                        + "\n  location Type:  " + locationType);
                break;
        }
    }

    /**
     * Adds a connection to the location Pane.
     *
     * If a connection with the same id already exists, it will be overwritten.
     *
     * @param connectionId    The connection id
     * @param connectionLabel The connection label
     * @param vertices        A list of vertices which defines the shape of the connection
     * @param connectionType  The type of the connection {DOOR,WINDOW,PASSAGE}
     * @param locationIds     The IDs of the location that will be connected by this connection
     */
    public void addConnection(final String connectionId, final String connectionLabel,
                              final List<Point2D> vertices, final String connectionType,
                              final List<String> locationIds) {
        // Fill the list of vertices into an array of points
        double[] points = new double[vertices.size() * 2];
        for (int i = 0; i < vertices.size(); i++) {
            // TODO: X and Y are swapped in the world of the csra... make it more generic...
            points[i * 2] = vertices.get(i).getY() * Constants.METER_TO_PIXEL;
            points[i * 2 + 1] = vertices.get(i).getX() * Constants.METER_TO_PIXEL;
        }

        ConnectionPolygon connectionPolygon;

        switch (connectionType) {
            case "DOOR":
                connectionPolygon = new DoorPolygon(connectionLabel, connectionId, points);
                break;
            case "WINDOW":
                connectionPolygon = new WindowPolygon(connectionLabel, connectionId, points);
                break;
            case "PASSAGE":
                connectionPolygon = new PassagePolygon(connectionLabel, connectionId, points);
                break;
            default:
                LOGGER.warn("The following connection has an unknown LocationType and will be ignored:"
                        + "\n  connection UUID:  " + connectionId
                        + "\n  connection Label: " + connectionLabel
                        + "\n  connection Type:  " + connectionType);
                return;
        }

        connectionMap.put(connectionId, connectionPolygon);

        locationIds.forEach(locationId -> {
            if (tileMap.containsKey(locationId)) {
                final LocationPolygon locationPolygon = tileMap.get(locationId);
                locationPolygon.addCuttingShape(connectionPolygon);
            } else {
                LOGGER.error("Location ID \"" + locationId + "\" can not be found in the location Map. "
                        + "No Cutting will be applied");
            }
        });
    }

    /**
     * Will add a UnitIcon to the locationPane.
     * @param svgIcon The icon
     * @param onActionHandler The Handler that gets activated when the button is pressed
     * @param position The position where the button is to be placed
     */
    public void addUnit(final SVGIcon svgIcon, final EventHandler<ActionEvent> onActionHandler,
                        final Point2D position) {
        final UnitButton unitButton = new UnitButton(svgIcon, onActionHandler);
        unitButton.setTranslateX(position.getX());
        unitButton.setTranslateY(position.getY());
        this.getChildren().add(unitButton);
    }

    /**
     * Erases all locations from the locationPane.
     */
    public void clearLocations() {
        tileMap.forEach((locationId, locationPolygon) -> this.getChildren().remove(locationPolygon));
        tileMap.clear();

        regionMap.forEach((locationId, locationPolygon) -> this.getChildren().remove(locationPolygon));
        regionMap.clear();

        rootRoom = null;
    }

    /**
     * Erases all connections from the locationPane.
     */
    public void clearConnections() {
        connectionMap.forEach((connectionId, connectionPolygon) -> this.getChildren().remove(connectionPolygon));
        connectionMap.clear();
    }

    /**
     * Will clear everything on the location Pane and then add everything that is saved in the maps.
     * Also adds a cutting shape for every Polygon to the root.
     */
    public void updateLocationPane() {
        this.getChildren().clear();

        tileMap.forEach((locationId, locationPolygon) -> {
            rootRoom.addCuttingShape(locationPolygon);
            this.getChildren().add(locationPolygon);
        });

        regionMap.forEach((locationId, locationPolygon) -> {
            rootRoom.addCuttingShape(locationPolygon);
            this.getChildren().add(locationPolygon);
        });

        connectionMap.forEach((connectionId, connectionPolygon) -> {
            rootRoom.addCuttingShape(connectionPolygon);
            this.getChildren().add(connectionPolygon);
        });

        if (rootRoom != null) {
            this.getChildren().add(rootRoom);
        }
    }

    /**
     * Adds a mouse eventHandler to the tile.
     *
     * @param tile The tile
     */
    public void addMouseEventHandlerToTile(final TilePolygon tile) {
        tile.setOnMouseClicked(event -> {
            event.consume();

            if (event.isStillSincePress()) {
                if (event.getClickCount() == 1) {
                    this.setSelectedLocation(tile);
                    this.lastFirstClickTarget = tile;
                } else if (event.getClickCount() == 2) {
                    autoFocusPolygonAnimated(tile);
                }
            }
        });
        tile.setOnMouseEntered(event -> {
            event.consume();
            tile.mouseEntered();
            foregroundPane.getInfoFooter().getMouseOverText().setText(tile.getLabel());
        });
        tile.setOnMouseExited(event -> {
            event.consume();
            tile.mouseLeft();
            foregroundPane.getInfoFooter().getMouseOverText().setText("");
        });
    }

    /**
     * Adds a mouse eventHandler to the region.
     *
     * @param region The region
     */
    public void addMouseEventHandlerToRegion(final RegionPolygon region) {
        region.setOnMouseClicked(event -> {
            event.consume();

            if (event.isStillSincePress()) {
                if (event.getClickCount() == 1) {
                    this.setSelectedLocation(region);
                    this.lastFirstClickTarget = region;
                } else if (event.getClickCount() == 2) {
                    if (this.lastFirstClickTarget.equals(region)) {
                        autoFocusPolygonAnimated(region);
                    } else {
                        selectedLocation.fireEvent(event.copyFor(null, selectedLocation));
                    }
                }
            }
        });
        region.setOnMouseEntered(event -> {
            event.consume();
            region.mouseEntered();
            foregroundPane.getInfoFooter().getMouseOverText().setText(region.getLabel());
        });
        region.setOnMouseExited(event -> {
            event.consume();
            region.mouseLeft();
            foregroundPane.getInfoFooter().getMouseOverText().setText("");
        });
    }

    private void setSelectedLocation(final LocationPolygon newSelectedLocation) {
        if (!this.selectedLocation.equals(newSelectedLocation)) {
            if (!newSelectedLocation.getClass().equals(RegionPolygon.class)) {
                this.lastSelectedTile.getChildIds().forEach(childId ->
                        (regionMap.get(childId)).changeStyleOnSelectable(false));
            }

            if (newSelectedLocation.getClass().equals(TilePolygon.class)) {
                this.lastSelectedTile = newSelectedLocation;
                newSelectedLocation.getChildIds().forEach(childId ->
                        (regionMap.get(childId)).changeStyleOnSelectable(true));
            }

            this.selectedLocation.setSelected(false);
            newSelectedLocation.setSelected(true);
            this.selectedLocation = newSelectedLocation;
            this.selectedLocationId.set(newSelectedLocation.getUuid());

            foregroundPane.getContextMenu().getRoomInfo().setText(selectedLocation.getLabel());
        }
    }

    /**
     * ZoomFits to the root if available. Otherwise to the first location in the tileMap.
     */
    public void zoomFit() {
        if (rootRoom != null) { //NOPMD
            autoFocusPolygon(rootRoom);
        } else if (!tileMap.isEmpty()) {
            autoFocusPolygon(tileMap.values().iterator().next());
        }
    }

    /**
     * Adds a change listener to the selectedRoomID property.
     *
     * @param changeListener The change Listener
     */
    public void addSelectedLocationIdListener(final ChangeListener<? super String> changeListener) {
        selectedLocationId.addListener(changeListener);
    }

    /**
     * Remove the specified change listener from the selectedRoomID property.
     *
     * @param changeListener The change Listener
     */
    public void removeSelectedLocationIdListener(final ChangeListener<? super String> changeListener) {
        selectedLocationId.removeListener(changeListener);
    }

    /**
     * Getter for the OnEmptyAreaClickHandler.
     *
     * @return The EventHandler.
     */
    public EventHandler<MouseEvent> getOnEmptyAreaClickHandler() {
        return onEmptyAreaClickHandler;
    }

    private void autoFocusPolygon(final LocationPolygon polygon) {
        final double xScale = (foregroundPane.getBoundingBox().getWidth() / polygon.prefWidth(0))
                * Constants.ZOOM_FIT_PERCENTAGE_WIDTH;
        final double yScale = (foregroundPane.getBoundingBox().getHeight() / polygon.prefHeight(0))
                * Constants.ZOOM_FIT_PERCENTAGE_HEIGHT;
        final double scale = (xScale < yScale) ? xScale : yScale;

        this.setScaleX(scale);
        this.setScaleY(scale);

        final Point2D transition = calculateTransition(scale, polygon);

        this.setTranslateX(transition.getX());
        this.setTranslateY(transition.getY());
    }

    private void autoFocusPolygonAnimated(final LocationPolygon polygon) {
        final double xScale = (foregroundPane.getBoundingBox().getWidth() / polygon.prefWidth(0))
                * Constants.ZOOM_FIT_PERCENTAGE_WIDTH;
        final double yScale = (foregroundPane.getBoundingBox().getHeight() / polygon.prefHeight(0))
                * Constants.ZOOM_FIT_PERCENTAGE_HEIGHT;
        final double scale = (xScale < yScale) ? xScale : yScale;

        final ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(500));
        scaleTransition.setToX(scale);
        scaleTransition.setToY(scale);
        scaleTransition.setCycleCount(1);
        scaleTransition.setAutoReverse(true);

        final Point2D transition = calculateTransition(scale, polygon);

        final TranslateTransition translateTransition = new TranslateTransition(Duration.millis(500));
        translateTransition.setToX(transition.getX());
        translateTransition.setToY(transition.getY());
        translateTransition.setCycleCount(1);
        translateTransition.setAutoReverse(true);

        final ParallelTransition parallelTransition =
                new ParallelTransition(this, scaleTransition, translateTransition);
        parallelTransition.play();
    }

    private Point2D calculateTransition(final double scale, final LocationPolygon polygon) {
        final double polygonDistanceToCenterX = (-(polygon.getCenterX() - (getLayoutBounds().getWidth() / 2))) * scale;
        final double polygonDistanceToCenterY = (-(polygon.getCenterY() - (getLayoutBounds().getHeight() / 2))) * scale;
        final double boundingBoxCenterX =
                (foregroundPane.getBoundingBox().getMinX() + foregroundPane.getBoundingBox().getMaxX()) / 2;
        final double boundingBoxCenterY =
                (foregroundPane.getBoundingBox().getMinY() + foregroundPane.getBoundingBox().getMaxY()) / 2;
        final double bbCenterDistanceToCenterX = ((getLayoutBounds().getWidth() / 2) - boundingBoxCenterX);
        final double bbCenterDistanceToCenterY = ((getLayoutBounds().getHeight() / 2) - boundingBoxCenterY);
        final double transitionX = polygonDistanceToCenterX - bbCenterDistanceToCenterX;
        final double transitionY = polygonDistanceToCenterY - bbCenterDistanceToCenterY;

        return new Point2D(transitionX, transitionY);
    }
}
