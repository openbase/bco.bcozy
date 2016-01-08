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
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.dc.bco.bcozy.view.Constants;
import org.dc.bco.bcozy.view.ForegroundPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class LocationPane extends Pane {

    /**
     * Application logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LocationPane.class);

    private LocationPolygon selectedLocation;
    private LocationPolygon rootRoom;
    private final ForegroundPane foregroundPane;
    private final Map<String, LocationPolygon> locationMap;
    private final SimpleStringProperty selectedLocationId;

    private LocationPolygon lastFirstClickTarget;
    private LocationPolygon lastSelectedTile;
    private final EventHandler<MouseEvent> onEmptyAreaClickHandler;

    /**
     * Constructor for the LocationPane.
     *
     * @param foregroundPane The foregroundPane
     */
    public LocationPane(final ForegroundPane foregroundPane) {
        super();

        this.foregroundPane = foregroundPane;

        locationMap = new HashMap<>();

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

                foregroundPane.getContextMenu().getRoomInfo().setText(selectedLocation.getLocationLabel());
            }
        };
    }

    /**
     * Adds a room to the location Pane and use the controls to add a mouse event handler.
     *
     * If a room with the same id already exists, it will be overwritten.
     *
     * @param locationId The location id
     * @param locationLabel The location label
     * @param childIds The ids of the children
     * @param vertices A list of vertices which defines the shape of the room
     * @param locationType The type of the location {ZONE,REGION,TILE}
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
                break;
            case "REGION":
                locationPolygon = new RegionPolygon(locationLabel, locationId, childIds, points);
                addMouseEventHandlerToRegion((RegionPolygon) locationPolygon);
                break;
            case "ZONE":
                locationPolygon = new ZonePolygon(locationLabel, locationId, childIds, points);
                rootRoom = locationPolygon; //TODO: handle the situation where several zones exist
                break;
            default:
                LOGGER.warn("The following location has an unknown LocationType and will be ignored:"
                        + "\n  UUID:  " + locationId
                        + "\n  Label: " + locationLabel
                        + "\n  Type:  " + locationType);
                return;
        }
        locationMap.put(locationId, locationPolygon);
        this.getChildren().add(locationPolygon);
    }

    /**
     * Erases all locations from the locationPane.
     */
    public void clearLocations() {
        locationMap.forEach((locationId, locationPolygon) -> this.getChildren().remove(locationPolygon));
        locationMap.clear();
        rootRoom = null;
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
            foregroundPane.getInfoFooter().getMouseOverText().setText(tile.getLocationLabel());
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
            foregroundPane.getInfoFooter().getMouseOverText().setText(region.getLocationLabel());
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
                        ((RegionPolygon) locationMap.get(childId)).changeStyleOnSelectable(false));
            }

            if (newSelectedLocation.getClass().equals(TilePolygon.class)) {
                this.lastSelectedTile = newSelectedLocation;
                newSelectedLocation.getChildIds().forEach(childId ->
                        ((RegionPolygon) locationMap.get(childId)).changeStyleOnSelectable(true));
            }

            this.selectedLocation.setSelected(false);
            newSelectedLocation.setSelected(true);
            this.selectedLocation = newSelectedLocation;
            this.selectedLocationId.set(newSelectedLocation.getLocationId());

            foregroundPane.getContextMenu().getRoomInfo().setText(selectedLocation.getLocationLabel());
        }
    }

    /**
     * ZoomFits to the root if available. Otherwise to the first location in the locationMap.
     */
    public void zoomFit() {
        if (rootRoom != null) { //NOPMD
            autoFocusPolygon(rootRoom);
        } else if (!locationMap.isEmpty()) {
            autoFocusPolygon(locationMap.values().iterator().next());
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
     * @return The EventHandler.
     */
    public EventHandler<MouseEvent> getOnEmptyAreaClickHandler() {
        return onEmptyAreaClickHandler;
    }

    private void autoFocusPolygon(final LocationPolygon polygon) {
        final double xScale =
                (getLayoutBounds().getWidth() / polygon.prefWidth(0)) * Constants.ZOOM_FIT_PERCENTAGE_WIDTH;
        final double yScale =
                (getLayoutBounds().getHeight() / polygon.prefHeight(0)) * Constants.ZOOM_FIT_PERCENTAGE_HEIGHT;
        final double scale = (xScale < yScale) ? xScale : yScale;

        this.setScaleX(scale);
        this.setScaleY(scale);

        this.setTranslateX(-polygon.getCenterX() * scale + (getLayoutBounds().getWidth() * scale) / 2);
        this.setTranslateY(-polygon.getCenterY() * scale + (getLayoutBounds().getHeight() * scale) / 2);
    }

    private void autoFocusPolygonAnimated(final LocationPolygon polygon) {
        final double xScale =
                (getLayoutBounds().getWidth() / polygon.prefWidth(0)) * Constants.ZOOM_FIT_PERCENTAGE_WIDTH;
        final double yScale =
                (getLayoutBounds().getHeight() / polygon.prefHeight(0)) * Constants.ZOOM_FIT_PERCENTAGE_HEIGHT;
        final double scale = (xScale < yScale) ? xScale : yScale;

        final ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(500));
        scaleTransition.setToX(scale);
        scaleTransition.setToY(scale);
        scaleTransition.setCycleCount(1);
        scaleTransition.setAutoReverse(true);

        final TranslateTransition translateTransition = new TranslateTransition(Duration.millis(500));
        translateTransition.setToX(-polygon.getCenterX() * scale + (getLayoutBounds().getWidth() * scale) / 2);
        translateTransition.setToY(-polygon.getCenterY() * scale + (getLayoutBounds().getHeight() * scale) / 2);
        translateTransition.setCycleCount(1);
        translateTransition.setAutoReverse(true);

        final ParallelTransition parallelTransition =
                new ParallelTransition(this, scaleTransition, translateTransition);
        parallelTransition.play();
    }
}
