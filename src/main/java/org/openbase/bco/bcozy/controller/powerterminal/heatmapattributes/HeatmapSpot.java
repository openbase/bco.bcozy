package org.openbase.bco.bcozy.controller.powerterminal.heatmapattributes;

/**
 * Datatype describing a spot at the heatmap.
 */
public class HeatmapSpot {

    public int x;
    public int y;
    public double value;
    public int unitListPosition;

    public HeatmapSpot(int x, int y, double value, int unitListPosition) {
        this.x = x;
        this.y = y;
        this.value = value;
        this.unitListPosition = unitListPosition;
    }
}
