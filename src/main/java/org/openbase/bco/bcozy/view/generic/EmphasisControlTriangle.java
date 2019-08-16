package org.openbase.bco.bcozy.view.generic;

public class EmphasisControlTriangle {

    private final double x3, y3;
    private final double y23, x32, y31, x13;
    private final double det, minD, maxD;

    public EmphasisControlTriangle(final double x1, final double y1, final double x2, final double y2, final double x3, final double y3) {
        this.x3 = x3;
        this.y3 = y3;
        y23 = y2 - y3;
        x32 = x3 - x2;
        y31 = y3 - y1;
        x13 = x1 - x3;
        det = y23 * x13 - x32 * y31;
        minD = Math.min(det, 0);
        maxD = Math.max(det, 0);
    }

    /**
     * Method checks if the given point is within the triangle.
     *
     * @param x pos
     * @param y pos
     *
     * @return true if the point is within the triangle, otherwise false.
     */
    public boolean contains(final double x, final double y) {

        final double dx = x - x3;
        final double dy = y - y3;
        final double a = y23 * dx + x32 * dy;

        if (a < minD || a > maxD) {
            return false;
        }

        final double b = y31 * dx + x13 * dy;

        if (b < minD || b > maxD) {
            return false;
        }

        final double c = det - a - b;

        if (c < minD || c > maxD) {
            return false;
        }

        return true;
    }

}
