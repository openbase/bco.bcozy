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
        this.rooms = generatePolygons(roomPoints);
        this.spots = spots;
        this.u = u;
    }

    private List<Polygon> generatePolygons(List<List<Point2D>> roomPoints) {
        List<Polygon> roomTemp = new ArrayList<>();

        for (List<Point2D> roomPoint : roomPoints) {
            int[] xPoints = new int[roomPoint.size()];
            int[] yPoints = new int[roomPoint.size()];
            for (int i = 0; i < roomPoint.size(); i++) {
                xPoints[i] = (int) roomPoint.get(i).getX();
                yPoints[i] = (int) roomPoint.get(i).getY();
            }
            roomTemp.add(new Polygon(xPoints, yPoints, roomPoint.size()));
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
            if (room.contains(y, x))
                return true;
        }
        return false;
    }
}
