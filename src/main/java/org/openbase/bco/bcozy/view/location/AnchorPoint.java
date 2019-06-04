package org.openbase.bco.bcozy.view.location;

import javafx.geometry.Point2D;
import javafx.scene.effect.Lighting;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.location.LocationMapPane.SelectionMode;

public class AnchorPoint extends StackPane {

    // Paint Location Coordinates
    public static final double COORDINATE_BLOCK_SIZE = 0.20 * Constants.METER_TO_PIXEL;

    private Text text;
    private Circle coordinate;
    private final LocationMap locationMap;
    private final DynamicPolygon dynamicPolygon;


    public AnchorPoint(final DynamicPolygon dynamicPolygon, final LocationMap locationMap) {
        this.locationMap = locationMap;
        this.dynamicPolygon = dynamicPolygon;

        text = new Text();
        text.setStroke(Color.BLACK);
        text.setMouseTransparent(true);


        coordinate = new Circle(COORDINATE_BLOCK_SIZE);
        coordinate.setFill(Color.WHITE);
        coordinate.setEffect(new Lighting());
        coordinate.setMouseTransparent(false);
        coordinate.setPickOnBounds(true);
        coordinate.setOnMouseClicked(event -> {
            if (event.isStillSincePress()) {
                toggleSelection();
                event.consume();
            }
        });

        getChildren().addAll(coordinate, text);
        autosize();
    }

    public void updateDynamicComponents() {
        if (isSelected()) {
            coordinate.setFill(Color.hsb(200, 1, 1));
        } else {
            coordinate.setFill(Color.WHITE);
        }
    }

    private void toggleSelection() {
        if (!isSelected()) {
            locationMap.selectAnchorPoint(this);
        } else {
            locationMap.deselectAnchorPoint(this);
        }
        updateDynamicComponents();
    }

    public void init(final Point2D position, final String label) {
        text.setText(label);
        setTranslateX(position.getY() - (getWidth() / 2));
        setTranslateY(position.getX() - (getHeight() / 2));
    }

    public Point2D getPosition() {
        return new Point2D(getX(), getY());
    }

    public double getX() {
        return getTranslateY() + (getWidth() / 2);
    }

    public double getY() {
        return getTranslateX() + (getHeight() / 2);
    }

    public boolean isSelected() {
        return locationMap.isSelected(this);
    }

    public DynamicPolygon getPolygon() {
        return dynamicPolygon;
    }

    public void translate(final double deltaX, final double deltaY, final SelectionMode selectionMode) {
        switch (selectionMode) {
            case BOTH:
                setTranslateX(getTranslateX() + deltaX);
                setTranslateY(getTranslateY() + deltaY);
                break;
            case HORIZONTAL:
                setTranslateY(getTranslateY() + deltaY);
                break;
            case VERTICAL:
                setTranslateX(getTranslateX() + deltaX);
                break;
        }
    }

    public void shutdown() {
        if (isSelected()) {
            locationMap.deselectAnchorPoint(this);
        }
        getChildren().clear();
    }
}
