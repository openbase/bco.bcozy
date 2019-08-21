package org.openbase.bco.bcozy.view.generic;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Affine;
import org.openbase.jul.processing.StringProcessor;
import org.openbase.jul.visual.javafx.geometry.svg.SVGGlyphIcon;
import org.openbase.type.domotic.action.ActionEmphasisType.ActionEmphasis.Category;

import java.awt.geom.AffineTransform;

import static org.openbase.bco.dal.lib.layer.service.provider.EmphasisStateProviderService.EMPHASIS_TRIANGLE_HEIGHT;
import static org.openbase.bco.dal.lib.layer.service.provider.EmphasisStateProviderService.EMPHASIS_TRIANGLE_OUTER_LINE;

public class EmphasisControlTrianglePane extends VBox {

    private final EmphasisControlTriangle emphasisControlTriangle;
    private final Canvas canvas;
    private final Label emphasisLabel = new Label("Emphasis");
    private final SVGGlyphIcon emphasisIcon;
    private GraphicsContext gc;
    private double scale;
    private final Pane trianglePane;

    public EmphasisControlTrianglePane() {

        this.emphasisControlTriangle = new EmphasisControlTriangle();
        this.emphasisControlTriangle.primaryEmphasisCategoryPropertyProperty().addListener(new ChangeListener<Category>() {
            @Override
            public void changed(ObservableValue<? extends Category> observable, Category oldValue, Category primaryEmphasisCategory) {
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
        });

        this.emphasisIcon = new SVGGlyphIcon(MaterialDesignIcon.HELP, 45, false);
        this.emphasisIcon.setMouseTransparent(true);

        this.canvas = new Canvas();
        this.trianglePane = new Pane();

        this.gc = canvas.getGraphicsContext2D();

        this.canvas.heightProperty().bind(heightProperty());
        this.canvas.widthProperty().bind(widthProperty());

        this.updateDynamicComponents();

        this.heightProperty().addListener((observable, oldValue, newValue) -> {
            updateDynamicComponents();
        });

        this.widthProperty().addListener((observable, oldValue, newValue) -> {
            updateDynamicComponents();
        });

        this.canvas.setOnMouseDragged(event -> {
            updateHandle(event.getX(), event.getY(), scale, true, gc);
        });

        this.canvas.setOnMouseDragReleased(event -> {
            updateHandle(event.getX(), event.getY(), scale, false, gc);
        });

        this.canvas.setOnMousePressed(event -> {
            updateHandle(event.getX(), event.getY(), scale, true, gc);
        });

        this.canvas.setOnMouseClicked(event -> {
            updateHandle(event.getX(), event.getY(), scale, false, gc);
        });

        //trianglePane.setPadding(new Insets(50, 50,50, 50));

        trianglePane.getChildren().addAll(canvas, emphasisIcon);
        this.getChildren().add(trianglePane);
        this.getChildren().add(new HBox(emphasisLabel));
    }

    private void updateDynamicComponents() {

        System.out.println("update..");

        scale = Math.min(getWidth(), getHeight()) / EMPHASIS_TRIANGLE_OUTER_LINE;
//        System.out.println("canvas.getWidth(): " + canvas.getWidth());
//        System.out.println("canvas.getHeight(): " + canvas.getHeight());
//        System.out.println("getHeight(): " + getHeight());
//        System.out.println("getWidth: " + getWidth());
//        System.out.println("scale: " + scale);

        // reset previous scale value
        gc.setTransform(new Affine());

        // set new scale
        gc.scale(scale, scale);

        // initial triangle draw
        emphasisControlTriangle.drawShapes(false, gc);

        // setup initial icon position
        emphasisIcon.setLayoutX((emphasisControlTriangle.getHandlePosX() * scale - emphasisIcon.getWidth() / 2));
        emphasisIcon.setLayoutY((emphasisControlTriangle.getHandlePosY() * scale - emphasisIcon.getHeight() / 2));

        emphasisLabel.setStyle("-fx-color-label-visible: WHITE");

        // todo need to be optimized
//        emphasisLabel.setLayoutX((scale * EMPHASIS_TRIANGLE_OUTER_LINE / 2) * 0.8);
//        emphasisLabel.setLayoutY(scale * EMPHASIS_TRIANGLE_HEIGHT * 1.05);
//
        emphasisLabel.setScaleX(scale * 0.01);
        emphasisLabel.setScaleY(scale * 0.01);

        emphasisIcon.setSize(EmphasisControlTriangle.HANDLE_INNER_SIZE * scale * 0.80);
    }

    public void updateHandle(final double sceneX, final double sceneY, final double scale, final boolean mouseClicked, final GraphicsContext gc) {
        emphasisControlTriangle.updateHandle(sceneX, sceneY, scale, mouseClicked, gc);

        // setup icon animation
        if (!mouseClicked) {
            emphasisIcon.setForegroundIconColorAnimated(emphasisControlTriangle.getEmphasisColor(), 2);
        }

        emphasisIcon.setLayoutX(emphasisControlTriangle.getHandlePosX() * scale - (emphasisIcon.getWidth() / 2));
        emphasisIcon.setLayoutY(emphasisControlTriangle.getHandlePosY() * scale - (emphasisIcon.getHeight() / 2));
    }
}
