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

import javafx.beans.value.ChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.iface.Identifiable;
import org.openbase.jul.iface.provider.LabelProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public abstract class DynamicPolygon extends Polygon implements Colorable, LabelProvider, Identifiable<String> {

    /**
     * The value how much the custom color will be weighted against the main color.
     */
    public static final double CUSTOM_COLOR_WEIGHT = 0.5;

    protected static final Logger LOGGER = LoggerFactory.getLogger(DynamicPolygon.class);

    private Color mainColor;
    private Color customColor;

    private boolean selected;
    private boolean selectable;

    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    private boolean horizontal;
    private boolean editMode;

    private List<AnchorPoint> anchorPointList;
    private final StackPane ancorPointLayer;
    private final LocationMap locationMap;

    private final ChangeListener<Number> shapeChangeListener;

    private final List<Polygon> cuttingShapePolygonList;

    /**
     * creates a new instance of polygon.
     */
    public DynamicPolygon(final LocationMap locationMap) {
        this.locationMap = locationMap;
        this.mainColor = Color.TRANSPARENT;
        this.customColor = Color.TRANSPARENT;

        this.selected = false;
        this.selectable = false;
        this.editMode = false;

        this.ancorPointLayer = new StackPane();
        this.anchorPointList = new ArrayList<>();

        this.ancorPointLayer.setPickOnBounds(false);

        this.shapeChangeListener = (observable, oldValue, newValue) -> {
            updateShape();
        };

        this.cuttingShapePolygonList = new ArrayList<>();

        setOnMouseClicked(event -> {
            try {
                if (event.isStillSincePress()) {
                    if (event.getClickCount() == 1) {
                        locationMap.setSelectedUnit((LocationPolygon) this);
                    } else if (event.getClickCount() == 2) {
                        if (locationMap.getLastClickTarget().equals(this)) {
                            if (event.isControlDown()) {
                                if (event.isShiftDown()) {
                                    saveChanges();
                                    setEditMode(false);
                                } else {
                                    setEditMode(!editMode);
                                    locationMap.setSelectedUnit((LocationPolygon) this);
                                }
                            } else {
                                locationMap.autoFocusPolygonAnimated(this);
                            }
                        } else {
                            locationMap.getLastClickTarget().fireEvent(event.copyFor(null, locationMap.getLastClickTarget()));
                        }
                    }
                    event.consume();
                }
            } catch (CouldNotPerformException ex) {
                ExceptionPrinter.printHistory("Could not handle mouse event!", ex, LOGGER);
            }
        });

        // needed to handle background pane selection of the root pane
        setOnMouseEntered(event -> {
            locationMap.handleHoverUpdate(DynamicPolygon.this, true);
        });

        setOnMouseExited(event -> {
            locationMap.handleHoverUpdate(DynamicPolygon.this, false);
        });

        hoverProperty().addListener((observable, oldValue, newValue) -> locationMap.handleHoverUpdate(DynamicPolygon.this, newValue));

    }

    private void updateShape() {
        getPoints().clear();
        for (AnchorPoint anchorPoint : anchorPointList) {
            getPoints().add(anchorPoint.getY());
            getPoints().add(anchorPoint.getX());
        }
    }

    public int getVertexCount() {
        return anchorPointList.size();
    }

    protected final synchronized void updateVertices() throws CouldNotPerformException, InterruptedException {

        // cleanup old anchor points
        for (AnchorPoint anchorPoint : new ArrayList<>(anchorPointList)) {
            ancorPointLayer.getChildren().remove(anchorPoint);
            anchorPointList.remove(anchorPoint);
            anchorPoint.shutdown();
        }

        // init vars
        double tmpMinX = Double.MAX_VALUE;
        double tmpMaxX = -Double.MAX_VALUE;
        double tmpMinY = Double.MAX_VALUE;
        double tmpMaxY = -Double.MAX_VALUE;
        int index = 0;

        // load new anchors
        for (Point2D vertex : loadShapeVertices()) {
            final AnchorPoint anchorPoint = new AnchorPoint(this, locationMap);
            anchorPoint.init(vertex, Integer.toString(index++));
            anchorPoint.translateXProperty().addListener(shapeChangeListener);
            anchorPoint.translateYProperty().addListener(shapeChangeListener);
            ancorPointLayer.getChildren().add(anchorPoint);
            anchorPointList.add(anchorPoint);

            // update min max
            tmpMinX = Math.min(vertex.getX(), tmpMinX);
            tmpMaxX = Math.max(vertex.getX(), tmpMaxX);
            tmpMinY = Math.min(vertex.getY(), tmpMinY);
            tmpMaxY = Math.max(vertex.getY(), tmpMaxY);

        }

        // store values into global vars
        minX = tmpMinX;
        maxX = tmpMaxX;
        minY = tmpMinY;
        maxY = tmpMaxY;
        horizontal = (Math.abs(maxX - minX) > Math.abs(maxY - minY));

        // update shape
        updateShape();


        // Paint debug information
//            if (editMode) {
//
//                // Paint LocationBase
//                text = new Text("X");
//                text.setStroke(Color.BLACK);
//
//                coordinate = new Circle(COORDINATE_BLOCK_SIZE);
//                coordinate.setFill(Color.CORNFLOWERBLUE);
//                coordinate.setEffect(new Lighting());
//
//                locationBaseStack.getChildren().addAll(coordinate, text);
//                locationBaseStack.autosize();
//                locationBaseStack.setLayoutX(locationUnitConfig.getPlacementConfig().getPose().getTranslation().getY() * Constants.METER_TO_PIXEL - (locationBaseStack.getWidth() / 2));
//                locationBaseStack.setLayoutY(locationUnitConfig.getPlacementConfig().getPose().getTranslation().getX() * Constants.METER_TO_PIXEL - (locationBaseStack.getHeight() / 2));
//                locationBaseStack.hoverProperty().addListener((observable, oldValue, newValue) -> {
//                    InfoPane.info("This is the base of the " + LabelProcessor.getBestMatch(locationUnitConfig.getLabel(),"?"));
//                });
//                debugNodes.add(locationBaseStack);
//
//                // Paint Gloabl Base
//                text = new Text("O");
//                text.setStroke(Color.BLACK);
//
//                coordinate = new Circle(COORDINATE_BLOCK_SIZE);
//                coordinate.setFill(Color.DARKRED);
//                coordinate.setEffect(new Lighting());
//
//                globalBaseStack.getChildren().addAll(coordinate, text);
//                globalBaseStack.autosize();
//                globalBaseStack.setLayoutX(0 - (globalBaseStack.getWidth() / 2));
//                globalBaseStack.setLayoutY(0 - (globalBaseStack.getHeight() / 2));
//                globalBaseStack.hoverProperty().addListener((observable, oldValue, newValue) -> {
//                    InfoPane.info("This is the global base.");
//                });
//                debugNodes.add(globalBaseStack);
//            }
    }

    /**
     * Getter method for the X Coordinate of the center.
     *
     * @return x center as a double value
     */
    public double getCenterX() {
        return (getLayoutBounds().getMaxX() + getLayoutBounds().getMinX()) / 2;
    }

    /**
     * Getter method for the Y Coordinate of the center.
     *
     * @return y center as a double value
     */
    public double getCenterY() {
        return (getLayoutBounds().getMaxY() + getLayoutBounds().getMinY()) / 2;
    }

    /**
     * Setter method for the mainColor.
     *
     * @param mainColor as a color
     */
    protected void setMainColor(final Color mainColor) {
        this.mainColor = mainColor;
        onColorChange(this.mainColor, this.customColor);
    }

    /**
     * Getter method for the mainColor.
     *
     * @return The main color
     */
    protected Color getMainColor() {
        return this.mainColor;
    }

    /**
     * Calling the colorize method will paint the instance in the specified color.
     *
     * @param color The specified color
     */
    @Override
    public void setCustomColor(final Color color) {
        this.customColor = color;
        onColorChange(this.mainColor, this.customColor);
    }

    /**
     * Calling the getCustomColor method will return the specified color.
     *
     * @return The specified color
     */
    @Override
    public Color getCustomColor() {
        return this.customColor;
    }

    /**
     * Will be called when either the main or the custom color changes.
     * The initial values for both colors are Color.TRANSPARENT.
     *
     * @param mainColor   The main color
     * @param customColor The custom color
     */
    protected abstract void onColorChange(final Color mainColor, final Color customColor);

    /**
     * Method loads all vertices of the shape of this polygon.
     *
     * @return the points forming the polygons shape.
     *
     * @throws InterruptedException     is thrown when the thread was internally interrupted since this method takes some time to resolve all transformations.
     * @throws CouldNotPerformException is thrown when the points are not yet accessible.
     **/
    protected abstract List<Point2D> loadShapeVertices() throws InterruptedException, CouldNotPerformException;

    /**
     * Getter method for the selected boolean.
     *
     * @return selected as a boolean value
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Setter method for the selected boolean.
     *
     * @param selected as a boolean value
     */
    public void setSelected(final boolean selected) {
        System.out.println("selected " + getLabel("?") + " " + selected);
        this.selected = selected;
        this.changeStyleOnSelection(selected);
        updateOverlay();
    }

    public void setEditMode(final boolean enable) {
        this.editMode = enable;
        applyClipping();
        updateOverlay();
    }

    private void updateOverlay() {
        if (selected && editMode) {
            if (!locationMap.getEditOverlay().getChildren().contains(ancorPointLayer)) {
                locationMap.getEditOverlay().getChildren().add(ancorPointLayer);
                for (AnchorPoint anchorPoint : anchorPointList) {
                    anchorPoint.updateDynamicComponents();
                }
            }
        } else {
            locationMap.getEditOverlay().getChildren().remove(ancorPointLayer);
        }
    }

    /**
     * Will cut an additional Shape out of the polygon.
     *
     * @param additionalCuttingShape The shape to be cut out
     */
    public void addCuttingShape(final Polygon additionalCuttingShape) {
        //cuttingShapePolygonList.add(additionalCuttingShape);
        //applyClipping();
    }

    private void applyClipping() {

//        // skip clipping for empty polygons
//        if (getPoints().isEmpty()) {
//            return;
//        }
//
//        // compute cutting shape
//        Shape cuttingShape = this;
//        for (Polygon polygon : cuttingShapePolygonList) {
//
//            // skip empty polygons
//            if (polygon.getPoints().isEmpty()) {
//                continue;
//            }
//            cuttingShape = Path.subtract(cuttingShape, polygon);
//        }
//
//        if (editMode) {
//            setClip(null);
//        } else {
//            setClip(cuttingShape);
//        }
    }

    public boolean isEditModeEnabled() {
        return editMode;
    }

    /**
     * This method configure if this polygon can be accessed and selected via ui events.
     *
     * @param selectable Whether the Region should be selectable or not.
     */
    public void setSelectable(final boolean selectable) {
        if (selectable) {
            this.selectable = true;
            this.getStrokeDashArray().addAll(Constants.REGION_DASH_WIDTH, Constants.REGION_DASH_WIDTH);
            this.setStrokeWidth(Constants.REGION_STROKE_WIDTH);
            this.setMouseTransparent(false);
        } else {
            this.selectable = false;
            this.getStrokeDashArray().clear();
            this.setStrokeWidth(0.0);
            this.setMouseTransparent(true);
        }
    }

    /**
     * Getter for the selectable status.
     *
     * @return The selectable status.
     */
    public boolean isSelectable() {
        return selectable;
    }

    /**
     * Will be called when the selection of the Polygon has been toggled.
     *
     * @param selected boolean for the selection status
     */
    protected abstract void changeStyleOnSelection(final boolean selected);

    /**
     * Getter method for the minX value.
     *
     * @return minX
     */
    protected double getMinX() {
        return minX;
    }

    /**
     * Getter method for the maxX value.
     *
     * @return maxX
     */
    protected double getMaxX() {
        return maxX;
    }

    /**
     * Getter method for the minY value.
     *
     * @return minY
     */
    protected double getMinY() {
        return minY;
    }

    /**
     * Getter method for the maxY value.
     *
     * @return maxY
     */
    protected double getMaxY() {
        return maxY;
    }

    /**
     * Getter method for the horizontal value.
     *
     * @return horizontal as a boolean value
     */
    protected boolean isHorizontal() {
        return horizontal;
    }

    abstract public int getLevel();

    public boolean isAllAnchorsSelected() {
        return anchorPointList.stream().allMatch(AnchorPoint::isSelected);
    }

    abstract public void saveChanges();

    public List<AnchorPoint> getAnchorPointList() {
        return anchorPointList;
    }

}
