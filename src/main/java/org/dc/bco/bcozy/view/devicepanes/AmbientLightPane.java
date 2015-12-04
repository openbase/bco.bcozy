/**
 * ==================================================================
 *
 * This file is part of org.dc.bco.bcozy.
 *
 * org.dc.bco.bcozy is free software: you can redistribute it and modify
 * it under the terms of the GNU General Public License (Version 3)
 * as published by the Free Software Foundation.
 *
 * org.dc.bco.bcozy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with org.dc.bco.bcozy. If not, see <http://www.gnu.org/licenses/>.
 * ==================================================================
 */
package org.dc.bco.bcozy.view.devicepanes;

import de.citec.dal.remote.unit.AmbientLightRemote;
import de.citec.dal.remote.unit.DALRemoteService;
import de.citec.jul.exception.CouldNotPerformException;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import org.controlsfx.control.ToggleSwitch;
import org.dc.bco.bcozy.view.Constants;
import org.dc.bco.bcozy.view.ImageEffect;

/**
 * Created on 03.12.15.
 */
public class AmbientLightPane extends UnitPane {

    private static final String WHITE = "white";

    private final AmbientLightRemote ambientLightRemote;
    private final Image bottomImage;
    private final Image topImage;
    private final Group imageEffect;

    /**
     * Constructor for the AmbientLightPane.
     * @param ambientLightRemote ambientLightRemote
     */
    public AmbientLightPane(final DALRemoteService ambientLightRemote) {
        this.ambientLightRemote = (AmbientLightRemote) ambientLightRemote;

        final Button lightBulb = new Button();
        lightBulb.setBackground(Background.EMPTY);

        bottomImage = new Image("/icons/lightbulb_mask.png");
        topImage    = new Image("/icons/lightbulb.png");
        imageEffect = new ImageEffect().imageBlendEffect(bottomImage, topImage, Color.YELLOW);

        try {
            super.setUnitLabel(this.ambientLightRemote.getData().getLabel());
        } catch (CouldNotPerformException e) {
            e.printStackTrace();
            super.setUnitLabel("UnknownID");
        }

        initTitle();
        initContent();
    }

    @Override
    protected void initTitle() {
        final ToggleSwitch toggleSwitch;
        final ImageView lightOff;
        final BorderPane borderPane;

        //button
        toggleSwitch = new ToggleSwitch();

        //image
        lightOff = new ImageView(topImage);
        lightOff.setClip(new ImageView(topImage));
        lightOff.setFitHeight(Constants.MIDDLEICON);
        lightOff.setFitWidth(Constants.MIDDLEICON);

        //borderPane for header of titledPane as "graphic"
        borderPane = new BorderPane();
        borderPane.setLeft(lightOff);
        borderPane.setCenter(new Label(super.getUnitLabel()));
        borderPane.setRight(toggleSwitch);

        this.getStyleClass().add("widgetPane");
        this.setGraphic(borderPane);
    }

    @Override
    protected void initContent() {
        final HBox hBox;
        final Pane colorContainer;
        final Pane colorRectBrightness;
        final Pane colorHue;
        final Pane colorBar;
        final Circle circle;
        final Rectangle rectangle;

        final DoubleProperty hueValue = new SimpleDoubleProperty(-1);
        final DoubleProperty saturation = new SimpleDoubleProperty(-1);
        final DoubleProperty brightness = new SimpleDoubleProperty(-1);

        //CHECKSTYLE.OFF: MagicNumber

        //mouse event for saturation & brightness rectangle
        final EventHandler<MouseEvent> rectangleSatBrightMouseHandler = event -> {
            final double xMouse = event.getX();
            final double yMouse = event.getY();
            saturation.set(clamp(xMouse / 200) * 100);
            brightness.set(100 - (clamp(yMouse / 200) * 100));
        };

        //color rectangle high: lightness/brightness
        colorRectBrightness = brightnessRect();
        colorRectBrightness.setOnMousePressed(rectangleSatBrightMouseHandler);
        colorRectBrightness.setOnMouseDragged(rectangleSatBrightMouseHandler);

        //saturation & brightness depend on hue value
        colorHue = new Pane();
        colorHue.backgroundProperty().bind(new ObjectBinding<Background>() {
            {
                bind(hueValue);
            }

            @Override protected Background computeValue() {
                return new Background(new BackgroundFill(Color.hsb(hueValue.getValue(), 1.0, 1.0),
                        CornerRadii.EMPTY, Insets.EMPTY));
            }
        });

        //mouse event for colorBar
        final EventHandler<MouseEvent> colorBarMouseHandler = event -> {
            final double yMouse = event.getY();
            hueValue.set(clamp(yMouse / 200) * 360);
        };

        //circle as selector for colorRectangleContainer
        circle = circleSelector();
        circle.layoutXProperty().bind(saturation.divide(100).multiply(200));
        circle.layoutYProperty().bind(Bindings.subtract(1, brightness.divide(100)).
                multiply(200));

        //container/stackPane for saturation/brightness area
        colorContainer = new StackPane();
        colorContainer.getChildren().addAll(colorHue, saturationRect(), colorRectBrightness);
        colorContainer.setPadding(new Insets(0, 10, 0, 10));
        colorContainer.getChildren().add(circle);

        //rectangle as selector for colorBar
        rectangle = rectangleSelector();
        rectangle.layoutYProperty().bind(hueValue.divide(360).multiply(200));

        //create the colorBar
        colorBar = colorBarCreator();
        colorBar.setOnMousePressed(colorBarMouseHandler);
        colorBar.setOnMouseDragged(colorBarMouseHandler);
        colorBar.getChildren().add(rectangle);
        colorBar.setPadding(new Insets(0, 10, 0, 10));

        //hBox includes color elements (colorContainer & colorBar)
        hBox = new HBox();
        hBox.setPadding(new Insets(0, 10, 0, 10));
        hBox.getChildren().addAll(colorContainer, colorBar);

        this.setContent(hBox);
    }

    /**
     * Method computes a part of the linear gradient of hue value.
     * @param value ratio of mouse coordinate and element height.
     * @return value of ratio as 0/1/input value.
     */
    static double clamp(final double value) {
        return value < 0 ? 0 : value > 1 ? 1 : value;
    }

    /**
     * Method creates a linear gradient for hue value.
     * @return new LinearGradient with properties of hue value.
     */
    private static LinearGradient hueGradient() {
        double offset;
        int hue;
        Stop[] stop = new Stop[255];

        for (int i = 0; i < 255; i++) {
            offset = (double) ((1.0 / 255) * i);
            hue = (int) ((i / 255.0) * 360);
            stop[i] = new Stop(offset, Color.hsb(hue, 1.0, 1.0));
        }
        return new LinearGradient(0f, 0f, 0f, 1f, true, CycleMethod.NO_CYCLE, stop);
    }

    /**
     * Method creates a circle shape as visual selector in saturation/brightness area.
     * @return circle shape with settings.
     */
    private Circle circleSelector() {
        final Circle circle = new Circle(10, Color.web(WHITE, 0.0));

        circle.setMouseTransparent(true);
        circle.setTranslateX(10);
        circle.setStrokeType(StrokeType.OUTSIDE);
        circle.setStroke(Color.web(WHITE, 1.0));
        circle.setStrokeWidth(2.0);
        circle.setCache(true);
        circle.setManaged(false);
        circle.setEffect(shadowMaker());

        return circle;
    }

    /**
     * Method creates a rectangle shape as visual selector in hue area.
     * @return rectangle shape with settings.
     */
    private Rectangle rectangleSelector() {
        final Rectangle rectangle = new Rectangle(colorBarCreator().getPrefWidth() + 10.0, 10.0,
                Color.web(WHITE, 0.0));

        rectangle.setMouseTransparent(true);
        rectangle.setTranslateX(-(rectangle.getWidth() - colorBarCreator().getPrefWidth()) / 2);
        rectangle.setTranslateY(-rectangle.getHeight() / 2);
        rectangle.setStrokeType(StrokeType.OUTSIDE);
        rectangle.setStroke(Color.web(WHITE, 1.0));
        rectangle.setStrokeWidth(2.0);
        rectangle.setCache(true);
        rectangle.setManaged(false);
        rectangle.setEffect(shadowMaker());

        return rectangle;
    }

    /**
     * Method creates a shadow effect.
     * @return dropShadow with settings.
     */
    private DropShadow shadowMaker() {
        final DropShadow dropShadow = new DropShadow();

        dropShadow.setOffsetX(1.0);
        dropShadow.setOffsetY(1.0);
        dropShadow.setColor(Color.BLACK);

        return dropShadow;
    }

    /**
     * Method creates a pane for the colorBar (hue area).
     * @return colorBar with settings.
     */
    private Pane colorBarCreator() {
        final Pane colorBar = new Pane();

        //TODO size generic...
        colorBar.setPrefSize(20, 200);
        colorBar.setBackground(new Background(new BackgroundFill(hueGradient(), CornerRadii.EMPTY, Insets.EMPTY)));

        return colorBar;
    }

    /**
     * Method creates a pane for the saturation rectangle (piece of the colorContainer).
     * @return colorRectSaturation is a colored shape (linear gradient of saturation).
     */
    private Pane saturationRect() {
        final Pane colorRectSaturation = new Pane();

        //TODO size generic...
        colorRectSaturation.setPrefSize(200, 200);
        colorRectSaturation.setBackground(new Background(new BackgroundFill(new LinearGradient(0, 0, 1, 0, true,
                CycleMethod.NO_CYCLE, new Stop(0, Color.rgb(255, 255, 255, 1)),
                new Stop(1, Color.rgb(255, 255, 255, 0))), CornerRadii.EMPTY, Insets.EMPTY)));

        return colorRectSaturation;
    }

    /**
     * Method creates a pane for the brightness rectangle (piece of the colorContainer).
     * @return colorRectBrightness is a colored shape (linear gradient of brightness).
     */
    private Pane brightnessRect() {
        final Pane colorRectBrightness = new Pane();

        //TODO size generic...
        colorRectBrightness.setPrefSize(200, 200);
        colorRectBrightness.setBackground(new Background(new BackgroundFill(new LinearGradient(0, 0, 0, 1, true,
                CycleMethod.NO_CYCLE, new Stop(0, Color.rgb(0, 0, 0, 0)), new Stop(1, Color.rgb(0, 0, 0, 1))),
                CornerRadii.EMPTY, Insets.EMPTY)));

        return colorRectBrightness;
    }
    //CHECKSTYLE.ON: MagicNumber

    @Override
    public DALRemoteService getDALRemoteService() {
        return ambientLightRemote;
    }
}
