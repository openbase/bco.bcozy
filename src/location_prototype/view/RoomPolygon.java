package view;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

/**
 * Created by julian on 05.11.15.
 */
public class RoomPolygon extends Polygon {

    private double centerX, centerY;
    private boolean selected;

    public RoomPolygon(double... points) {
        super(points);
        this.setFill(Color.TRANSPARENT);
        this.setStroke(Color.WHITE);
        this.setStrokeWidth(2.5);
        this.centerX = (super.getLayoutBounds().getMaxX()+super.getLayoutBounds().getMinX())/2;
        this.centerY = (super.getLayoutBounds().getMaxY()+super.getLayoutBounds().getMinY())/2;
        this.selected = false;
    }

    public double getCenterX() {
        return centerX;
    }

    public double getCenterY() {
        return centerY;
    }

    public boolean isSelected() {
        return selected;
    }

    public void toggleSelected() {
        if (!this.selected) {
            this.setFill(new Color(0.8, 0.8, 0.8, 0.4));
            this.selected = true;
        } else {
            this.setFill(Color.TRANSPARENT);
            this.selected = false;
        }
    }
}
