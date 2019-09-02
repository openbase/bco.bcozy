package org.openbase.bco.bcozy;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.openbase.bco.bcozy.view.generic.EmphasisControlTrianglePane;
import org.openbase.bco.bcozy.view.generic.Triangle;
import org.openbase.jul.processing.StringProcessor;
import org.openbase.jul.visual.javafx.geometry.svg.SVGGlyphIcon;
import org.openbase.jul.visual.javafx.launch.AbstractFXApplication;
import org.openbase.type.domotic.action.ActionEmphasisType;
import org.openbase.type.domotic.action.ActionEmphasisType.ActionEmphasis.Category;
import org.openbase.type.domotic.state.EmphasisStateType.EmphasisState;

import javax.vecmath.Point2d;
import java.text.DecimalFormat;

import static org.openbase.bco.dal.lib.layer.service.provider.EmphasisStateProviderService.*;

public class EmphasisControlPanePrototype extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Emphasis Triangle");
        EmphasisControlTrianglePane emphasisControlTrianglePane = new EmphasisControlTrianglePane();
        emphasisControlTrianglePane.setPrefHeight(100);
        emphasisControlTrianglePane.setPrefWidth(100);
        emphasisControlTrianglePane.setStyle("-fx-background-color: GREY");
        EmphasisControlTrianglePane emphasisControlTrianglePane2 = new EmphasisControlTrianglePane();
        emphasisControlTrianglePane2.setPrefHeight(500);
        emphasisControlTrianglePane2.setPrefWidth(500);
        emphasisControlTrianglePane.setStyle("-fx-background-color: BLUE");
        Pane rootPane = new HBox();
        rootPane.getChildren().addAll(emphasisControlTrianglePane, emphasisControlTrianglePane2);
        primaryStage.setScene(new Scene(rootPane));
        primaryStage.show();
    }
}