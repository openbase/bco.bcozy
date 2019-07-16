package org.openbase.bco.bcozy.controller.powerterminal.heatmapattributes;





import javafx.geometry.Point2D;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HeatmapValues {
    public List<Polygon> rooms;
    public List<SpotsPosition> spots;
    public double[][] u;

    public HeatmapValues(List<List<Point2D>> roomPoints, List<SpotsPosition> spots, double[][] u) {
        this.rooms = generatePolygons(roomPoints, 6, 6);
        this.spots = spots;
        this.u = u;
    }

    private List<Polygon> generatePolygons(List<List<Point2D>> roomPoints, double xTranslation, double yTranslation) {
        List<Polygon> roomTemp = new ArrayList<>();

        for (List<Point2D> roomPoint : roomPoints) {
            Polygon temp = new Polygon();
            for (Point2D point2D : roomPoint)
                temp.addPoint((int) (point2D.getY()+yTranslation), (int)(point2D.getX()+xTranslation));
            roomTemp.add(temp);
        }
        return roomTemp;
    }

    public List<Polygon> getRooms() {
        return rooms;
    }

    public List<SpotsPosition> getSpots () {
        return spots;
    }

    public double[][] getU () {
        return u;
    }

    public void setU (double[][] uNew) {
        this.u = uNew;
    }

    public void setSpots (List<SpotsPosition> spotsNew) {this.spots = spotsNew;}

    public boolean isInsideRoom (double x, double y) {
        for (Polygon room : rooms) {
            if (room.contains(x, y))
                return true;
        }
        return false;
    }
}
