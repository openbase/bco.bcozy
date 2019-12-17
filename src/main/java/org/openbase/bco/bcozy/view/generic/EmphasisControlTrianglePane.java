package org.openbase.bco.bcozy.view.generic;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.transform.Affine;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.processing.StringProcessor;
import org.openbase.jul.visual.javafx.geometry.svg.SVGGlyphIcon;
import org.openbase.jul.visual.javafx.iface.DynamicPane;
import org.openbase.type.domotic.action.ActionEmphasisType.ActionEmphasis.Category;
import org.openbase.type.domotic.state.EmphasisStateType.EmphasisState;

import static org.openbase.bco.dal.lib.layer.service.provider.EmphasisStateProviderService.*;

public class EmphasisControlTrianglePane extends BorderPane implements DynamicPane {

    private final SimpleObjectProperty<EmphasisState> emphasisStateProperty;

    private final EmphasisControlTriangle emphasisControlTriangle;
    private final Canvas canvas;
    private final Label emphasisLabel = new Label("Emphasis");
    private final SVGGlyphIcon emphasisIcon;
    private GraphicsContext gc;
    private double scale;
    private final Pane trianglePane;
    private transient boolean emphasisStateUpdate;

    public EmphasisControlTrianglePane() {

        this.emphasisStateProperty = new SimpleObjectProperty<>(EmphasisState.getDefaultInstance());
        this.emphasisControlTriangle = new EmphasisControlTriangle();
        this.emphasisStateUpdate = false;
        this.emphasisControlTriangle.primaryEmphasisCategoryProperty().addListener((observable, oldValue, primaryEmphasisCategory) -> {
            updateEmphasisCategory(primaryEmphasisCategory);
        });

        this.emphasisControlTriangle.setEmphasisStateChangeListener(() -> {
            computeEmphasisState();
        });

        this.emphasisControlTriangle.setHandlePositionChangeListener(() -> {
            updateIcon(false);
        });

        this.emphasisStateProperty.addListener((observable, oldValue, newValue) -> {

            // only update if update is not already in progress
            if (emphasisStateUpdate) {
                return;
            }

            emphasisControlTriangle.updateEmphasisState(newValue.getComfort(), newValue.getEconomy(), newValue.getSecurity(), false, gc);
        });

        this.emphasisIcon = new SVGGlyphIcon(MaterialDesignIcon.HELP, 0, false);
        this.emphasisIcon.setMouseTransparent(true);

        this.canvas = new Canvas();
        this.trianglePane = new Pane();

        this.gc = canvas.getGraphicsContext2D();

        this.trianglePane.setMinHeight(50);
        this.trianglePane.setMinWidth(50);

        this.trianglePane.setPrefSize(200, 185);

        //this.trianglePane.prefHeightProperty().bind(trianglePane.widthProperty());

        // this.trianglePane.prefWidthProperty()/;

        this.canvas.setCache(true);

        canvas.widthProperty().bind(trianglePane.widthProperty());
        canvas.heightProperty().bind(trianglePane.heightProperty());
//        trianglePane.widthProperty().addListener((observable, oldValue, newWidth) -> {
//            if(newWidth.doubleValue() < trianglePane.getHeight()) {
//                this.canvas.widthProperty().unbind();
//                this.canvas.heightProperty().unbind();
//
//                this.canvas.widthProperty().bind(trianglePane.widthProperty());
//                this.canvas.heightProperty().bind(trianglePane.widthProperty().multiply(0.85d));
//            }
//        });
//
//        trianglePane.heightProperty().addListener((observable, oldValue, newHeight) -> {
//            if(newHeight.doubleValue() <= trianglePane.getWidth()) {
//
//                this.canvas.widthProperty().unbind();
//                this.canvas.heightProperty().unbind();
//
//                this.canvas.heightProperty().bind(trianglePane.heightProperty());
//                this.canvas.widthProperty().bind(trianglePane.heightProperty().multiply(1.15d));
//            }
//        });

        this.trianglePane.heightProperty().addListener((observable, oldValue, newValue) -> {
            this.updateDynamicContent();
        });

        this.trianglePane.widthProperty().addListener((observable, oldValue, newValue) -> {
            this.updateDynamicContent();
        });

        this.canvas.setOnMouseDragged(event -> {
            applyMousePositionUpdate(event.getX(), event.getY(), scale, true, gc);
            event.consume();
        });

        this.canvas.setOnMouseDragReleased(event -> {
            applyMousePositionUpdate(event.getX(), event.getY(), scale, false, gc);
            event.consume();
        });

        this.canvas.setOnMousePressed(event -> {
            applyMousePositionUpdate(event.getX(), event.getY(), scale, true, gc);
            event.consume();
        });

        this.canvas.setOnMouseClicked(event -> {
            applyMousePositionUpdate(event.getX(), event.getY(), scale, false, gc);
            event.consume();
        });

        this.canvas.setOnMouseReleased(event -> {
            applyMousePositionUpdate(event.getX(), event.getY(), scale, false, gc);
            event.consume();
        });

        this.trianglePane.getChildren().addAll(canvas, emphasisIcon);

        final HBox triangleOuterPane = new HBox();
        triangleOuterPane.setAlignment(Pos.CENTER);
        triangleOuterPane.setFillHeight(true);
        triangleOuterPane.getChildren().add(trianglePane);

        //innerPane.getChildren().add(triangleOuterPane);

        final HBox labelBox = new HBox(emphasisLabel);
        labelBox.setAlignment(Pos.CENTER);

        this.setCenter(triangleOuterPane);
        this.setBottom(labelBox);

        this.initContent();
        this.updateDynamicContent();
    }

    @Override
    public void initContent() {
        updateEmphasisCategory(emphasisControlTriangle.primaryEmphasisCategoryProperty().getValue());
    }

    private void updateEmphasisCategory(Category primaryEmphasisCategory) {
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

    public EmphasisControlTriangle getEmphasisControlTriangle() {
        return emphasisControlTriangle;
    }

    private void computeEmphasisState() {

        final EmphasisState.Builder emphasisState = EmphasisState.newBuilder();
        emphasisState
                .setComfort(emphasisControlTriangle.getComfort())
                .setSecurity(emphasisControlTriangle.getSecurity())
                .setEconomy(emphasisControlTriangle.getEconomy());
        emphasisStateUpdate = true;
        emphasisStateProperty.setValue(emphasisState.build());
        emphasisStateUpdate = false;
    }

    @Override
    public void updateDynamicContent() {

        scale = Math.min(trianglePane.getWidth(), trianglePane.getHeight()) / (EMPHASIS_TRIANGLE_OUTER_LINE + EmphasisControlTriangle.PADDING * 2);
//        System.out.println("canvas.getWidth(): " + canvas.getWidth());`
//        System.out.println("canvas.getHeight(): " + canvas.getHeight());
//        System.out.println("getHeight(): " + getHeight());
//        System.out.println("getWidth: " + getWidth());
//        System.out.println("scale: " + scale);

        // reset previous scale value
        gc.setTransform(new Affine());

        // set new scale
        gc.scale(scale, scale);

        // translate regarding frame
        gc.translate(EmphasisControlTriangle.PADDING, EmphasisControlTriangle.PADDING);

        // initial triangle draw
        emphasisControlTriangle.drawShapes(false, gc);

        //gc.strokeRect(- EmphasisControlTriangle.PADDING, - EmphasisControlTriangle.PADDING, EMPHASIS_TRIANGLE_OUTER_LINE + EmphasisControlTriangle.PADDING * 2, EMPHASIS_TRIANGLE_OUTER_LINE + EmphasisControlTriangle.PADDING * 2);

        // setup initial icon position
        emphasisIcon.setSize(EmphasisControlTriangle.HANDLE_INNER_SIZE * scale * 0.80);

        // update icon pos
        updateIcon(false);

        // required to update icon position
        updateEmphasisCategory(emphasisControlTriangle.getPrimaryEmphasisCategory());

        requestLayout();
    }

    private void applyMousePositionUpdate(final double sceneX, final double sceneY, final double scale, final boolean mouseClicked, final GraphicsContext gc) {
        emphasisControlTriangle.updateHandlePosition(sceneX, sceneY, scale, mouseClicked, gc);
    }


    private void updateIcon(final boolean mouseClicked) {
        // setup icon animation
        if (!mouseClicked) {
            emphasisIcon.setForegroundIconColorAnimated(emphasisControlTriangle.getEmphasisColor(), 2);
        }

        emphasisIcon.setLayoutX(emphasisControlTriangle.getHandlePosX() * scale - (emphasisIcon.getSize() / 2));
        emphasisIcon.setLayoutY(emphasisControlTriangle.getHandlePosY() * scale - (emphasisIcon.getSize() / 2));
    }


    public SimpleObjectProperty<EmphasisState> emphasisStateProperty() {
        return emphasisStateProperty;
    }

    public SimpleDoubleProperty economyProperty() {
        return emphasisControlTriangle.economyProperty();
    }

    public SimpleDoubleProperty securityProperty() {
        return emphasisControlTriangle.securityProperty();
    }

    public SimpleDoubleProperty comfortProperty() {
        return emphasisControlTriangle.comfortProperty();
    }

    public void setTrianglePrefSize(double prefWidth, double prefHeight) {
        trianglePane.setPrefSize(prefWidth, prefHeight);
    }
}
