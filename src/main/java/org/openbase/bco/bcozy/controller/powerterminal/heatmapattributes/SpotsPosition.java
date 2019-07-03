package org.openbase.bco.bcozy.controller.powerterminal.heatmapattributes;

public class SpotsPosition {
        public int spotsPositionx;
        public int spotsPositiony;
        public double value;

        public SpotsPosition(int x, int y, double value) {
            spotsPositionx = x;
            spotsPositiony = y;
            this.value = value;
        }
}
