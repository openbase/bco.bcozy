package org.openbase.bco.bcozy.view.generic;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.type.domotic.action.ActionEmphasisType.ActionEmphasis.Category;
import org.openbase.type.domotic.state.EmphasisStateType.EmphasisState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.vecmath.Point2d;
import java.text.DecimalFormat;

import static org.openbase.bco.dal.lib.layer.service.provider.EmphasisStateProviderService.*;
import static org.openbase.bco.dal.lib.layer.service.provider.EmphasisStateProviderService.EMPHASIS_TRIANGLE_HEIGHT;

public class EmphasisControlTriangle extends Triangle {

    private Logger LOGGER = LoggerFactory.getLogger(EmphasisControlTriangle.class);

    private double handlePosX = EMPHASIS_TRIANGLE_OUTER_LINE_HALF;
    private double handlePosY = EMPHASIS_TRIANGLE_HEIGHT_HALF;

    private final double brightness = 0.8d;
    private final double saturation = 0.7d;

    private final double securityHue = 222;
    private final double economyHue = 149;
    private final double comfortHue = 270;
    private final double errorHue = 0.7d;

    private Color emphasisColor;

    public static final double HANDLE_SIZE = 0.3d;
    public static final double HANDLE_SIZE_HALF = HANDLE_SIZE / 2;
    public static final double HANDLE_INNER_SIZE = HANDLE_SIZE * 0.6;
    public static final double HANDLE_INNER_SIZE_HALF = HANDLE_INNER_SIZE / 2;

    private final double[] mainTriangleX = new double[3];
    private final double[] mainTriangleY = new double[3];
    private final double[] economyTriangleX = new double[3];
    private final double[] economyTriangleY = new double[3];
    private final double[] comfortTriangleX = new double[3];
    private final double[] comfortTriangleY = new double[3];
    private final double[] securityTriangleX = new double[3];
    private final double[] securityTriangleY = new double[3];

    private SimpleDoubleProperty comfortProperty = new SimpleDoubleProperty(computeComfortTriangleArea(handlePosX, (EMPHASIS_TRIANGLE_HEIGHT - handlePosY)));
    private SimpleDoubleProperty economyProperty = new SimpleDoubleProperty(computeEconomyTriangleArea(handlePosX, (EMPHASIS_TRIANGLE_HEIGHT - handlePosY)));
    private SimpleDoubleProperty securityProperty = new SimpleDoubleProperty(computeSecurityTriangleArea(handlePosX, (EMPHASIS_TRIANGLE_HEIGHT - handlePosY)));
    private SimpleObjectProperty<Category> primaryEmphasisCategoryProperty = new SimpleObjectProperty<>(Category.UNKNOWN);

    private double securityBrightness = Math.max(0d, Math.min(brightness, securityProperty.get() * 2));
    private double economyBrightness = Math.max(0d, Math.min(brightness, economyProperty.get() * 2));
    private double comfortBrightness = Math.max(0d, Math.min(brightness, comfortProperty.get() * 2));

    public EmphasisControlTriangle() {
        super(0, 0, EMPHASIS_TRIANGLE_OUTER_LINE_HALF, EMPHASIS_TRIANGLE_HEIGHT, EMPHASIS_TRIANGLE_OUTER_LINE, 0);

        // set outer main triangle
        this.mainTriangleX[0] = 0;
        this.mainTriangleY[0] = EMPHASIS_TRIANGLE_HEIGHT;
        this.mainTriangleX[1] = EMPHASIS_TRIANGLE_OUTER_LINE_HALF;
        this.mainTriangleY[1] = 0;
        this.mainTriangleX[2] = EMPHASIS_TRIANGLE_OUTER_LINE;
        this.mainTriangleY[2] = EMPHASIS_TRIANGLE_HEIGHT;

        // set inner comfort triangle
        this.comfortTriangleX[0] = 0;
        this.comfortTriangleY[0] = EMPHASIS_TRIANGLE_HEIGHT;
        this.comfortTriangleX[1] = EMPHASIS_TRIANGLE_OUTER_LINE_HALF;
        this.comfortTriangleY[1] = 0;
        this.comfortTriangleX[2] = handlePosX;
        this.comfortTriangleY[2] = handlePosY;

        // set inner economy triangle
        this.economyTriangleX[0] = handlePosX;
        this.economyTriangleY[0] = handlePosY;
        this.economyTriangleX[1] = EMPHASIS_TRIANGLE_OUTER_LINE_HALF;
        this.economyTriangleY[1] = 0;
        this.economyTriangleX[2] = EMPHASIS_TRIANGLE_OUTER_LINE;
        this.economyTriangleY[2] = EMPHASIS_TRIANGLE_HEIGHT;

        // set inner security triangle
        this.securityTriangleX[0] = 0;
        this.securityTriangleY[0] = EMPHASIS_TRIANGLE_HEIGHT;
        this.securityTriangleX[1] = handlePosX;
        this.securityTriangleY[1] = handlePosY;
        this.securityTriangleX[2] = EMPHASIS_TRIANGLE_OUTER_LINE;
        this.securityTriangleY[2] = EMPHASIS_TRIANGLE_HEIGHT;
    }

    double mousePosX;
    double mousePosY;

    public void updateHandle(final double sceneX, final double sceneY, final double scale, final boolean mouseClicked, final GraphicsContext gc) {
        mousePosX = sceneX / scale;
        mousePosY = sceneY / scale;

        // compute handle position
        if (contains(mousePosX, (EMPHASIS_TRIANGLE_HEIGHT - mousePosY))) {
            // mouse is within the triangle, therefore just use mouse position for handle.
            handlePosX = mousePosX;
            handlePosY = mousePosY;
        } else {
            // mouse is not within the triangle, therefore we need to compute the triangle border intersection and use those position for the handle

            // compute bottom triangle intersectien
            Point2d bottomTriangleCorner = null;
            Point2d leftTriangleCorner = null;
            Point2d rightTriangleCorner = null;

            final Point2d mousePos = new Point2d(mousePosX, mousePosY);
            double bottomDistance;
            double leftDistance;

            try {
                bottomTriangleCorner = calculateTriangleOuterBoundsIntersection(mainTriangleX[0], mainTriangleY[0], mainTriangleX[2], mainTriangleY[2], mainTriangleX[1], mainTriangleY[1], mousePosX, mousePosY, true);
                bottomDistance = mousePos.distance(bottomTriangleCorner);
                handlePosX = bottomTriangleCorner.x;
                handlePosY = bottomTriangleCorner.y;
            } catch (NotAvailableException ex) {
                bottomDistance = Double.MAX_VALUE;
                ExceptionPrinter.printHistory(ex, LOGGER);
            }

            try {
                leftTriangleCorner = calculateTriangleOuterBoundsIntersection(mainTriangleX[1], mainTriangleY[1], mainTriangleX[0], mainTriangleY[0], mainTriangleX[2], mainTriangleY[2], mousePosX, mousePosY, true);
                leftDistance = mousePos.distance(leftTriangleCorner);
                if (leftDistance < bottomDistance) {
                    handlePosX = leftTriangleCorner.x;
                    handlePosY = leftTriangleCorner.y;
                }
            } catch (NotAvailableException ex) {
                leftDistance = Double.MAX_VALUE;
                ExceptionPrinter.printHistory(ex, LOGGER);
            }

            try {
                rightTriangleCorner = calculateTriangleOuterBoundsIntersection(mainTriangleX[1], mainTriangleY[1], mainTriangleX[2], mainTriangleY[2], mainTriangleX[0], mainTriangleY[0], mousePosX, mousePosY, true);
                if (mousePos.distance(rightTriangleCorner) < Math.min(bottomDistance, leftDistance)) {
                    handlePosX = rightTriangleCorner.x;
                    handlePosY = rightTriangleCorner.y;
                }
            } catch (NotAvailableException ex) {
                // no nothing
                ExceptionPrinter.printHistory(ex, LOGGER);
            }
        }

        comfortTriangleX[2] = handlePosX;
        comfortTriangleY[2] = handlePosY;
        economyTriangleX[0] = handlePosX;
        economyTriangleY[0] = handlePosY;
        securityTriangleX[1] = handlePosX;
        securityTriangleY[1] = handlePosY;

        //System.out.println("x:" + x);
        //System.out.println("y:" + (EMPHASIS_TRIANGLE_HEIGHT - y));

        // compute values
        comfortProperty.set(computeComfortTriangleArea(handlePosX, (EMPHASIS_TRIANGLE_HEIGHT - handlePosY)));
        economyProperty.set(computeEconomyTriangleArea(handlePosX, (EMPHASIS_TRIANGLE_HEIGHT - handlePosY)));
        securityProperty.set(computeSecurityTriangleArea(handlePosX, (EMPHASIS_TRIANGLE_HEIGHT - handlePosY)));

        securityBrightness = Math.max(0d, Math.min(brightness, securityProperty.get() * 2));
        economyBrightness = Math.max(0d, Math.min(brightness, economyProperty.get() * 2));
        comfortBrightness = Math.max(0d, Math.min(brightness, comfortProperty.get() * 2));

        // print
        DecimalFormat df = new DecimalFormat("###");
//        System.out.println("============================================");
//        System.out.println("comfortProperty.get():  " + df.format((int) ((comfortProperty.get()) * 100d)) + " %");
//        System.out.println("economyProperty.get():  " + df.format((int) ((economyProperty.get()) * 100d)) + " %");
//        System.out.println("securityProperty.get(): " + df.format((int) ((securityProperty.get()) * 100d)) + " %");

        final Point2d handlePosition = computeTriangleHandlePosition(EmphasisState.newBuilder().setComfort(comfortProperty.get()).setEconomy(economyProperty.get()).setSecurity(securityProperty.get()).build());

        if (Math.abs(handlePosX - handlePosition.x) > 0.0001) {
            System.out.println("invalid x " + handlePosX + " = " + handlePosition.x);
        }

        if (Math.abs((EMPHASIS_TRIANGLE_HEIGHT - handlePosY) - handlePosition.y) > 0.0001) {
            System.out.println("invalid y " + (EMPHASIS_TRIANGLE_HEIGHT - handlePosY) + " = " + handlePosition.y);
        }

//        System.out.println("===========================================");
//        System.out.println("x        " + x);
//        System.out.println("x square " + x * Math.sqrt(3));
//        System.out.println("y        " + (EMPHASIS_TRIANGLE_HEIGHT - y));


        //System.out.println("handle is and computed: " + Math.abs());
        //System.out.println("handle is and computed: " + Math.abs((EMPHASIS_TRIANGLE_HEIGHT - y) - handlePosition.y));

//        final Category lastEmphasisCategory = primaryEmphasisCategory;
        if (securityProperty.get() > economyProperty.get() && securityProperty.get() > comfortProperty.get()) {
            primaryEmphasisCategoryProperty.set(Category.SECURITY);
            emphasisColor = Color.hsb(securityHue, saturation, brightness);
        } else if (comfortProperty.get() > economyProperty.get() && comfortProperty.get() > securityProperty.get()) {
            primaryEmphasisCategoryProperty.set(Category.COMFORT);
            emphasisColor = Color.hsb(comfortHue, saturation, brightness);
        } else if (economyProperty.get() > comfortProperty.get() && economyProperty.get() > securityProperty.get()) {
            primaryEmphasisCategoryProperty.set(Category.ECONOMY);
            emphasisColor = Color.hsb(economyHue, saturation, brightness);
        } else {
            primaryEmphasisCategoryProperty.set(Category.UNKNOWN);
            // error
            emphasisColor = Color.hsb(errorHue, saturation, brightness);
        }

        drawShapes(mouseClicked, gc);
    }

    public Color getEmphasisColor() {
        return emphasisColor;
    }

    public void drawShapes(final boolean mouseClicked, final GraphicsContext gc) {

        // clear canvas
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        // main triangle
        // =============

        // format
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(0.03);


        // =============
        // format
        gc.setFill(Color.hsb(economyHue, saturation, economyBrightness));
        // paint
        gc.fillPolygon(economyTriangleX, economyTriangleY, 3);

        // comfort triangle
        // =============
        // format
        gc.setFill(Color.hsb(comfortHue, saturation, comfortBrightness));
        // paint
        gc.fillPolygon(comfortTriangleX, comfortTriangleY, 3);

        // security triangle
        // =============
        // format
        gc.setFill(Color.hsb(securityHue, saturation, securityBrightness));
        // paint
        gc.fillPolygon(securityTriangleX, securityTriangleY, 3);

        // paint frames
//        gc.strokePolygon(economyTriangleX, economyTriangleY, 3);
//        gc.strokePolygon(comfortTriangleX, comfortTriangleY, 3);
//        gc.strokePolygon(securityTriangleX, securityTriangleY, 3);

        gc.strokeLine(comfortTriangleX[0], comfortTriangleY[0], comfortTriangleX[2], comfortTriangleY[2]);
        gc.strokeLine(economyTriangleX[0], economyTriangleY[0], economyTriangleX[1], economyTriangleY[1]);
        gc.strokeLine(securityTriangleX[2], securityTriangleY[2], securityTriangleX[1], securityTriangleY[1]);

        gc.setLineWidth(0.05);
        gc.strokePolygon(mainTriangleX, mainTriangleY, 3);

        gc.setLineWidth(0.03);


        // draw handle
        gc.setStroke(Color.BLACK);

        if (mouseClicked) {
            gc.setFill(Color.GRAY);
        } else {
            gc.setFill(Color.WHITE);
        }

        gc.fillOval(handlePosX - HANDLE_SIZE_HALF, handlePosY - HANDLE_SIZE_HALF, HANDLE_SIZE, HANDLE_SIZE);
        gc.strokeOval(handlePosX - HANDLE_SIZE_HALF, handlePosY - HANDLE_SIZE_HALF, HANDLE_SIZE, HANDLE_SIZE);
        gc.setFill(emphasisColor);
        gc.fillOval(handlePosX - HANDLE_INNER_SIZE_HALF, handlePosY - HANDLE_INNER_SIZE_HALF, HANDLE_INNER_SIZE, HANDLE_INNER_SIZE);
        gc.strokeOval(handlePosX - HANDLE_INNER_SIZE_HALF, handlePosY - HANDLE_INNER_SIZE_HALF, HANDLE_INNER_SIZE, HANDLE_INNER_SIZE);
    }

    public static Point2d calculateTriangleOuterBoundsIntersection(double a1x, double a1y, double a2x, double a2y, double b1x, double b1y, double b2x, double b2y, boolean trim) throws NotAvailableException {

        double am = (a2y - a1y) / (a2x - a1x);
        double ab = a1y - (am * a1x);

        double bm = ((b2x - b1x != 0) ? ((b2y - b1y) / (b2x - b1x)) : Double.POSITIVE_INFINITY);
        double bb = b1y - (bm * b1x);

        if (am == bm) {
            throw new NotAvailableException("No intersection found! m = "+ am);
        }

        // if vector a is vertical, than we already know the value of if
        double x = bm == Double.POSITIVE_INFINITY ? b2x : (bb - ab) / (am - bm);
        double y = am * x + ab;

        // trim to triangle border
        if(trim) {
            if (a1x < a2x) {
                x = Math.max(a1x, Math.min(a2x, x));
            } else {
                x = Math.max(a2x, Math.min(a1x, x));
            }

            if (a1y < a2y) {
                y = Math.max(a1y, Math.min(a2y, y));
            } else {
                y = Math.max(a2y, Math.min(a1y, y));
            }
        }

        return new Point2d(x, y);
    }

    public double getHandlePosX() {
        return handlePosX;
    }

    public double getHandlePosY() {
        return handlePosY;
    }

    public double comfortProperty() {
        return comfortProperty.get();
    }

    public SimpleDoubleProperty comfortPropertyProperty() {
        return comfortProperty;
    }

    public double getEconomyProperty() {
        return economyProperty.get();
    }

    public SimpleDoubleProperty economyPropertyProperty() {
        return economyProperty;
    }

    public double getSecurityProperty() {
        return securityProperty.get();
    }

    public SimpleDoubleProperty securityPropertyProperty() {
        return securityProperty;
    }

    public Category getPrimaryEmphasisCategoryProperty() {
        return primaryEmphasisCategoryProperty.get();
    }

    public SimpleObjectProperty<Category> primaryEmphasisCategoryPropertyProperty() {
        return primaryEmphasisCategoryProperty;
    }
}
