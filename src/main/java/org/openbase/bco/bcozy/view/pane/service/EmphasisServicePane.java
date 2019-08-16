package org.openbase.bco.bcozy.view.pane.service;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.openbase.bco.bcozy.view.generic.EmphasisControlTriangle;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.processing.StringProcessor;
import org.openbase.jul.visual.javafx.control.AbstractFXController;
import org.openbase.jul.visual.javafx.geometry.svg.SVGGlyphIcon;
import org.openbase.type.domotic.action.ActionEmphasisType;
import org.openbase.type.domotic.action.ActionEmphasisType.ActionEmphasis.Category;
import org.openbase.type.domotic.state.EmphasisStateType.EmphasisState;

import javax.vecmath.Point2d;
import java.text.DecimalFormat;

import static org.openbase.bco.dal.lib.layer.service.provider.EmphasisStateProviderService.*;
import static org.openbase.bco.dal.lib.layer.service.provider.EmphasisStateProviderService.EMPHASIS_TRIANGLE_HEIGHT;

public class EmphasisServicePane extends AbstractFXController {


    private double x = EMPHASIS_TRIANGLE_OUTER_LINE_HALF;
    private double y = EMPHASIS_TRIANGLE_HEIGHT_HALF;

    private final double brightness = 0.8d;
    private final double saturation = 0.7d;

    private final double securityHue = 222;
    private final double economyHue = 149;
    private final double comfortHue = 270;
    private final double errorHue = 0.7d;

    private Color emphasisColor;

    private final double indicatorSize = 0.3d;
    private final double indicatorSizeHalf = indicatorSize / 2;
    private final double indicatorInnerSize = indicatorSize * 0.6;
    private final double indicatorInnerSizeHalf = indicatorInnerSize / 2;

    private final double[] mainTriangleX = new double[3];
    private final double[] mainTriangleY = new double[3];
    private final double[] economyTriangleX = new double[3];
    private final double[] economyTriangleY = new double[3];
    private final double[] comfortTriangleX = new double[3];
    private final double[] comfortTriangleY = new double[3];
    private final double[] securityTriangleX = new double[3];
    private final double[] securityTriangleY = new double[3];

    private double comfortValue = computeComfortTriangleArea(x, (EMPHASIS_TRIANGLE_HEIGHT - y));
    private double economyValue = computeEconomyTriangleArea(x, (EMPHASIS_TRIANGLE_HEIGHT - y));
    private double securityValue = computeSecurityTriangleArea(x, (EMPHASIS_TRIANGLE_HEIGHT - y));

    private double securityBrightness = Math.max(0d, Math.min(brightness, securityValue * 2));
    private double economyBrightness = Math.max(0d, Math.min(brightness, economyValue * 2));
    private double comfortBrightness = Math.max(0d, Math.min(brightness, comfortValue * 2));

    private final EmphasisControlTriangle emphasisTriangle = new EmphasisControlTriangle(0, 0, EMPHASIS_TRIANGLE_OUTER_LINE_HALF, EMPHASIS_TRIANGLE_HEIGHT, EMPHASIS_TRIANGLE_OUTER_LINE, 0);
    private final SVGGlyphIcon emphasisIcon = new SVGGlyphIcon(MaterialDesignIcon.HELP, 45, false);
    private final Label emphasisLabel = new Label("Economy");
    private ActionEmphasisType.ActionEmphasis.Category primaryEmphasisCategory;

    @Override
    public void updateDynamicContent() throws CouldNotPerformException {

    }

    @Override
    public void initContent() throws InitializationException {

    }

    @Override
    public void start(Stage primaryStage) {

        // set
        mainTriangleX[0] = 0;
        mainTriangleY[0] = EMPHASIS_TRIANGLE_HEIGHT;
        mainTriangleX[1] = EMPHASIS_TRIANGLE_OUTER_LINE_HALF;
        mainTriangleY[1] = 0;
        mainTriangleX[2] = EMPHASIS_TRIANGLE_OUTER_LINE;
        mainTriangleY[2] = EMPHASIS_TRIANGLE_HEIGHT;

        // set
        comfortTriangleX[0] = 0;
        comfortTriangleY[0] = EMPHASIS_TRIANGLE_HEIGHT;
        comfortTriangleX[1] = EMPHASIS_TRIANGLE_OUTER_LINE_HALF;
        comfortTriangleY[1] = 0;
        comfortTriangleX[2] = x;
        comfortTriangleY[2] = y;

        // set
        economyTriangleX[0] = x;
        economyTriangleY[0] = y;
        economyTriangleX[1] = EMPHASIS_TRIANGLE_OUTER_LINE_HALF;
        economyTriangleY[1] = 0;
        economyTriangleX[2] = EMPHASIS_TRIANGLE_OUTER_LINE;
        economyTriangleY[2] = EMPHASIS_TRIANGLE_HEIGHT;

        // set
        securityTriangleX[0] = 0;
        securityTriangleY[0] = EMPHASIS_TRIANGLE_HEIGHT;
        securityTriangleX[1] = x;
        securityTriangleY[1] = y;
        securityTriangleX[2] = EMPHASIS_TRIANGLE_OUTER_LINE;
        securityTriangleY[2] = EMPHASIS_TRIANGLE_HEIGHT;

        primaryStage.setTitle("Emphasis Triangle");
        Pane root = new Pane();
        root.setStyle("-fx-background-color: GREY");

        Canvas canvas = new Canvas(500, 500);
        //canvas.heightProperty().bind(root.heightProperty());
        //canvas.widthProperty().bind(root.widthProperty());

        emphasisIcon.setMouseTransparent(true);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        final double scale = Math.min(canvas.getWidth(), canvas.getHeight()) / EMPHASIS_TRIANGLE_OUTER_LINE;
        //System.out.println("canvas.getWidth(): " + canvas.getWidth());
        //System.out.println("canvas.getHeight(): " + canvas.getHeight());
        //System.out.println("scale: " + scale);
        gc.scale(scale, scale);
        drawShapes(gc, false);

        canvas.heightProperty().addListener((observable, oldValue, newValue) -> {
            drawShapes(gc, false);
        });

        canvas.widthProperty().addListener((observable, oldValue, newValue) -> {
            drawShapes(gc, false);
        });

        canvas.setOnMouseDragged(event -> {
            updateHandle(event.getSceneX(), event.getSceneY(), scale, true, gc);
        });

        canvas.setOnMouseDragReleased(event -> {
            updateHandle(event.getSceneX(), event.getSceneY(), scale, false, gc);
        });

        canvas.setOnMousePressed(event -> {
            updateHandle(event.getSceneX(), event.getSceneY(), scale, true, gc);
        });

        canvas.setOnMouseClicked(event -> {
            updateHandle(event.getSceneX(), event.getSceneY(), scale, false, gc);
        });

        emphasisIcon.setLayoutX((x * scale - emphasisIcon.getWidth() / 2));
        emphasisIcon.setLayoutY((y * scale - emphasisIcon.getHeight() / 2));

        emphasisLabel.setStyle("-fx-color-label-visible: WHITE");
        emphasisLabel.setLayoutX((scale * EMPHASIS_TRIANGLE_OUTER_LINE / 2) - 50);
        emphasisLabel.setLayoutY(scale * EMPHASIS_TRIANGLE_HEIGHT + 25);
        emphasisLabel.setScaleX(3);
        emphasisLabel.setScaleY(3);

        root.getChildren().add(emphasisLabel);
        root.getChildren().add(canvas);
        root.getChildren().add(emphasisIcon);

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private void updateHandle(final double sceneX, final double sceneY, final double scale, final boolean mouseClicked, final GraphicsContext gc) {
        double posX = sceneX / scale;
        double posY = sceneY / scale;

        // filter if not within triangle
        if (!emphasisTriangle.contains(posX, (EMPHASIS_TRIANGLE_HEIGHT - posY))) {
            return;
        }

        // update coordinates
        x = posX;
        y = posY;

        comfortTriangleX[2] = x;
        comfortTriangleY[2] = y;
        economyTriangleX[0] = x;
        economyTriangleY[0] = y;
        securityTriangleX[1] = x;
        securityTriangleY[1] = y;

        //System.out.println("x:" + x);
        //System.out.println("y:" + (EMPHASIS_TRIANGLE_HEIGHT - y));

        // compute values
        comfortValue = computeComfortTriangleArea(x, (EMPHASIS_TRIANGLE_HEIGHT - y));
        economyValue = computeEconomyTriangleArea(x, (EMPHASIS_TRIANGLE_HEIGHT - y));
        securityValue = computeSecurityTriangleArea(x, (EMPHASIS_TRIANGLE_HEIGHT - y));

        securityBrightness = Math.max(0d, Math.min(brightness, securityValue * 2));
        economyBrightness = Math.max(0d, Math.min(brightness, economyValue * 2));
        comfortBrightness = Math.max(0d, Math.min(brightness, comfortValue * 2));

        // print
        DecimalFormat df = new DecimalFormat("###");
//        System.out.println("============================================");
//        System.out.println("comfortValue:  " + df.format((int) ((comfortValue) * 100d)) + " %");
//        System.out.println("economyValue:  " + df.format((int) ((economyValue) * 100d)) + " %");
//        System.out.println("securityValue: " + df.format((int) ((securityValue) * 100d)) + " %");

        final Point2d handlePosition = computeTriangleHandlePosition(EmphasisState.newBuilder().setComfort(comfortValue).setEconomy(economyValue).setSecurity(securityValue).build());

        if (Math.abs(x - handlePosition.x) > 0.0001) {
            System.out.println("invalid x " + x + " = " + handlePosition.x);
        }

        if (Math.abs((EMPHASIS_TRIANGLE_HEIGHT - y) - handlePosition.y) > 0.0001) {
            System.out.println("invalid y " + (EMPHASIS_TRIANGLE_HEIGHT - y) + " = " + handlePosition.y);
        }

//        System.out.println("===========================================");
//        System.out.println("x        " + x);
//        System.out.println("x square " + x * Math.sqrt(3));
//        System.out.println("y        " + (EMPHASIS_TRIANGLE_HEIGHT - y));


        //System.out.println("handle is and computed: " + Math.abs());
        //System.out.println("handle is and computed: " + Math.abs((EMPHASIS_TRIANGLE_HEIGHT - y) - handlePosition.y));

        final Category lastEmphasisCategory = primaryEmphasisCategory;
        if (securityValue > economyValue && securityValue > comfortValue) {
            primaryEmphasisCategory = Category.SECURITY;
            emphasisColor = Color.hsb(securityHue, saturation, brightness);
        } else if (comfortValue > economyValue && comfortValue > securityValue) {
            primaryEmphasisCategory = Category.COMFORT;
            emphasisColor = Color.hsb(comfortHue, saturation, brightness);
        } else if (economyValue > comfortValue && economyValue > securityValue) {
            primaryEmphasisCategory = Category.ECONOMY;
            emphasisColor = Color.hsb(economyHue, saturation, brightness);
        } else {
            primaryEmphasisCategory = Category.UNKNOWN;
            // error
            emphasisColor = Color.hsb(errorHue, saturation, brightness);
        }

        if (primaryEmphasisCategory != lastEmphasisCategory) {
            switch (primaryEmphasisCategory) {
                case ECONOMY:
                    emphasisIcon.setForegroundIcon(MaterialDesignIcon.LEAF);
                    break;
                case COMFORT:
                    emphasisIcon.setForegroundIcon(MaterialDesignIcon.EMOTICON);
                    break;
                case SECURITY:
                    emphasisIcon.setForegroundIcon(MaterialDesignIcon.SECURITY);
                    break;
                case UNKNOWN:
                    emphasisIcon.setForegroundIcon(MaterialDesignIcon.FLASH);
                    break;
            }
            emphasisLabel.setText("Optimize " + StringProcessor.transformFirstCharToUpperCase(primaryEmphasisCategory.name().toLowerCase()));
        }

        // setup icon animation
        if (!mouseClicked) {
            emphasisIcon.setForegroundIconColorAnimated(emphasisColor, 2);
        }

        emphasisIcon.setLayoutX(sceneX - (emphasisIcon.getWidth() / 2));
        emphasisIcon.setLayoutY(sceneY - (emphasisIcon.getHeight() / 2));

        drawShapes(gc, mouseClicked);
    }

    private void drawShapes(final GraphicsContext gc, final boolean mouseClicked) {

        // clear canvas
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        // main triangle
        // =============

        // format
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(0.03);

        // paint
        gc.strokePolygon(mainTriangleX, mainTriangleY, 3);

        // economy triangle
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
        gc.strokePolygon(mainTriangleX, mainTriangleY, 3);
        gc.strokePolygon(economyTriangleX, economyTriangleY, 3);
        gc.strokePolygon(comfortTriangleX, comfortTriangleY, 3);
        gc.strokePolygon(securityTriangleX, securityTriangleY, 3);

        // draw current value indicator
        gc.setStroke(Color.BLACK);

        if (mouseClicked) {
            gc.setFill(Color.GRAY);
        } else {
            gc.setFill(Color.WHITE);
        }

        gc.fillOval(x - indicatorSizeHalf, y - indicatorSizeHalf, indicatorSize, indicatorSize);
        gc.strokeOval(x - indicatorSizeHalf, y - indicatorSizeHalf, indicatorSize, indicatorSize);
        gc.setFill(emphasisColor);
        gc.fillOval(x - indicatorInnerSizeHalf, y - indicatorInnerSizeHalf, indicatorInnerSize, indicatorInnerSize);
        gc.strokeOval(x - indicatorInnerSizeHalf, y - indicatorInnerSizeHalf, indicatorInnerSize, indicatorInnerSize);
    }
}
