package org.openbase.bco.bcozy.controller.powerterminal.heatmapattributes;

import javafx.geometry.Point2D;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Datatype containing the relevant heatmap values like a list of the locations, a list with the spots,
 * the matrix for calulating the heatmap and the x and y Translation of the rooms
 */
public class HeatmapValues {
    // todo: bco does not offer any representation of rooms, please refactor to location or tile instead.
    private List<Polygon> rooms;
    private List<HeatmapSpot> spots;
    private double[][] u;

    public HeatmapValues(List<List<Point2D>> roomPoints, List<HeatmapSpot> spots, double[][] u, double xTranslation, double yTranslation) {
        this.rooms = generatePolygons(roomPoints, xTranslation, yTranslation);
        this.spots = spots;
        this.u = u;
    }

    private List<Polygon> generatePolygons(List<List<Point2D>> roomPoints, double xTranslation, double yTranslation) {
        List<Polygon> roomTemp = new ArrayList<>();

        for (List<Point2D> roomPoint : roomPoints) {
            Polygon temp = new Polygon();
            for (Point2D point2D : roomPoint)
                temp.addPoint((int) (point2D.getY() + yTranslation), (int) (point2D.getX() + xTranslation));
            roomTemp.add(temp);
        }
        return roomTemp;
    }

    public List<Polygon> getRooms() {
        return rooms;
    }

    public List<HeatmapSpot> getSpots() {
        return spots;
    }

    // todo: what is "U" please use selfExplainable method names.

    public double[][] getU() {
        return u;
    }

    public void setU(double[][] uNew) {
        this.u = uNew;
    }

    public void setSpots(List<HeatmapSpot> spotsNew) {
        this.spots = spotsNew;
    }

    public boolean isInsideRoom(double x, double y, double centerx, double centery) {
        for (Polygon room : rooms) {
            if (room.contains(centerx, centery) && room.contains(x, y))
                return true;
        }
        return false;
    }

    public boolean isInsideRoom(double centerx, double centery) {
        for (Polygon room : rooms) {
            if (room.contains(centerx, centery))
                return true;
        }
        return false;
    }
}
