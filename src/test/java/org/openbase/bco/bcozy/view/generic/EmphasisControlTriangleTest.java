package org.openbase.bco.bcozy.view.generic;

import org.junit.Test;
import org.openbase.jul.exception.NotAvailableException;

import javax.vecmath.Point2d;

import static org.junit.Assert.*;

public class EmphasisControlTriangleTest {

    @Test
    public void calculateIntersectionPoint() throws NotAvailableException {
        final Point2d point2d = EmphasisControlTriangle.calculateTriangleOuterBoundsIntersection(-1, -2, 2, 1, -1, 3, 3, -1, true);
        assertEquals(1.5, point2d.x, 0.1);
        assertEquals(0.5, point2d.y, 0.1);

    }
}