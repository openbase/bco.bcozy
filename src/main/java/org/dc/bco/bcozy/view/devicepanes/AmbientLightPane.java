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
import de.citec.jul.exception.printer.ExceptionPrinter;
import de.citec.jul.exception.printer.LogLevel;
import de.citec.jul.pattern.Observable;
import de.citec.jul.pattern.Observer;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import javafx.scene.shape.Path;
import org.controlsfx.control.ToggleSwitch;
import org.dc.bco.bcozy.view.Constants;
import org.dc.bco.bcozy.view.ImageEffect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.homeautomation.unit.AmbientLightType.AmbientLight;
import rst.homeautomation.state.PowerStateType.PowerState.State;

/**
 * Created on 03.12.15.
 */
public class AmbientLightPane extends UnitPane implements Observer<AmbientLight> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AmbientLightPane.class);

    private static final String WHITE = "white";

    private final AmbientLightRemote ambientLightRemote;
    private final Image bottomImage;
    private final Image topImage;
    private final ToggleSwitch toggleSwitch;
    private final BorderPane borderPane;

    /**
     * Constructor for the AmbientLightPane.
     * @param ambientLightRemote ambientLightRemote
     */
    public AmbientLightPane(final DALRemoteService ambientLightRemote) {
        this.ambientLightRemote = (AmbientLightRemote) ambientLightRemote;

        toggleSwitch = new ToggleSwitch();

        final Button lightBulb = new Button();
        lightBulb.setBackground(Background.EMPTY);

        bottomImage = new Image("/icons/lightbulb_mask.png");
        topImage    = new Image("/icons/lightbulb.png");

        this.ambientLightRemote.addObserver(this);

        try {
            super.setUnitLabel(this.ambientLightRemote.getData().getLabel());
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
            super.setUnitLabel("UnknownID");
        }

        borderPane = new BorderPane();
        this.setGraphic(borderPane);

        initTitle();
        initContent();
        try {
            initEffectAndSwitch();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
    }

    private void setColorToImageEffect(final Color color) {
        final ImageEffect testGroup = new ImageEffect(bottomImage, topImage, color);
        borderPane.setLeft(testGroup);
    }

    private void initEffectAndSwitch() throws CouldNotPerformException {
        final Color color = Color.hsb(ambientLightRemote.getColor().getHue(),
                ambientLightRemote.getColor().getSaturation() / Constants.ONEHUNDRED,
                ambientLightRemote.getColor().getValue() / Constants.ONEHUNDRED);

        setColorToImageEffect(color);

        if (ambientLightRemote.getPower().getValue().equals(State.ON) && !toggleSwitch.isSelected()) {
            toggleSwitch.setSelected(true);
        } else if (ambientLightRemote.getPower().getValue().equals(State.OFF) && toggleSwitch.isSelected()) {
            toggleSwitch.setSelected(false);
        }
    }

    /**
     * Method creates the header content of the titledPane.
     */
    @Override
    protected void initTitle() {
        final ImageView lightOff;

        toggleSwitch.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent event) {
                if (toggleSwitch.isSelected()) {
                    try {
                        ambientLightRemote.setPower(State.ON);
                    } catch (CouldNotPerformException e) {
                        ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                    }
                } else {
                    try {
                        ambientLightRemote.setPower(State.OFF);
                    } catch (CouldNotPerformException e) {
                        ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                    }
                }
            }
        });

        //image
        lightOff = new ImageView(topImage);
        lightOff.setFitHeight(Constants.SMALL_ICON);
        lightOff.setFitWidth(Constants.SMALL_ICON);
        lightOff.setSmooth(true);

        //borderPane for header of titledPane as "graphic"
        borderPane.setLeft(lightOff);
        borderPane.setCenter(new Label(super.getUnitLabel()));
        borderPane.setRight(toggleSwitch);

        this.getStyleClass().add("widget-pane");
    }

    /**
     * Method creates the body content of the titledPane.
     */
    @Override
    protected void initContent() {
        final HBox hBox;
        final Pane colorContainer;
        final Pane colorHue;
        final Circle circle;
        final Rectangle rectangleSelector;
        final Pane colorCircleContainer;
        final Shape hollowCircle;

        final DoubleProperty hueValue = new SimpleDoubleProperty(-1);
        final DoubleProperty saturation = new SimpleDoubleProperty(-1);
        final DoubleProperty brightness = new SimpleDoubleProperty(-1);

        //CHECKSTYLE.OFF: MagicNumber

        //mouse event for saturation & brightness rectangle
        final EventHandler<MouseEvent> colorContainerMouseHandler = event -> {
            final double xMouse = event.getX();
            final double yMouse = event.getY();
            saturation.set(clamp(xMouse / 150) * 100);
            brightness.set(100 - (clamp(yMouse / 150) * 100));
        };

        //TODO implement set color (hueValue, saturation, brightness) from ambientlight unit

        final EventHandler<MouseEvent> sendingColorHandler = event -> {
            //TODO implement color (hueValue, saturation, brightness) sending to ambientlight unit

            java.awt.Color color = java.awt.Color.getHSBColor(hueValue.floatValue() / 360,
                    saturation.floatValue() / 100, brightness.floatValue() / 100);
            try {
                ambientLightRemote.setColor(color);
            } catch (CouldNotPerformException e) {
                ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
            }
        };

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

        //circle as selector for colorRectangleContainer
        circle = circleSelector();
        circle.layoutXProperty().bind(saturation.divide(100).multiply(150));
        circle.layoutYProperty().bind(Bindings.subtract(1, brightness.divide(100)).
                multiply(150));

        //container/stackPane for saturation/brightness area
        colorContainer = new StackPane();
        colorContainer.getChildren().addAll(colorHue, saturationRect(), brightnessRect());
        colorContainer.setOnMousePressed(colorContainerMouseHandler);
        colorContainer.setOnMouseDragged(colorContainerMouseHandler);
        colorContainer.setOnMouseReleased(sendingColorHandler);
        colorContainer.setPadding(new Insets(0, 10, 0, 10));
        colorContainer.getChildren().add(circle);

        //rectangle as color selector
        rectangleSelector = rectangleSelector();

        final EventHandler<MouseEvent> colorCircleMouseHandler = event -> {
            double yMouse = event.getY() + 75;
            double xMouse = event.getX() + 75;

            double angle = (Math.toDegrees(Math.atan2(yMouse - 75.0, 75.0 - xMouse)) + 90.0) % 360.0;
            angle = 360 - angle;

            hueValue.set(angle);

            rectangleSelector.setY(0);
            final int rectX = (int) Math.round(75 + 62 * Math.cos(Math.toRadians((angle + 270) % 360)));
            final int rectY = (int) Math.round(75 + 62 * Math.sin(Math.toRadians((angle + 270) % 360)));
            rectangleSelector.setLayoutX(rectX);
            rectangleSelector.setLayoutY(rectY);
            rectangleSelector.setRotate(angle);
        };

        //hollow circle with color spectrum
        hollowCircle = hollowCircle(75, 50);
        hollowCircle.setOnMousePressed(colorCircleMouseHandler);
        hollowCircle.setOnMouseDragged(colorCircleMouseHandler);
        hollowCircle.setOnMouseReleased(sendingColorHandler);

        //pane to set color circle and color selector
        colorCircleContainer = new Pane();
        colorCircleContainer.setPrefSize(150, 150);
        colorCircleContainer.getChildren().addAll(hollowCircle, rectangleSelector);

        //hBox includes color elements (colorContainer & pane)
        hBox = new HBox();
        hBox.setPadding(new Insets(0, 10, 0, 10));
        hBox.getChildren().addAll(colorContainer, colorCircleContainer);

        this.setContent(hBox);
    }

    /**
     * Method creates an writableImage with the color spectrum directed towards the center of the image.
     * @param width width of the image.
     * @param height height of the image.
     * @param stops stops represents the color angle of the hsv color space.
     * @return WritableImage is a custom graphical image with color spectrum.
     */
    public Image colorSpectrumImage(final int width, final int height, final Stop... stops) {
        final WritableImage writableImage = new WritableImage(width, height);
        final PixelWriter pixelWriter = writableImage.getPixelWriter();
        Color color = Color.TRANSPARENT;
        final Point2D center = new Point2D(width / 2, height / 2);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final double deltaX = x - center.getX();
                final double deltaY = y - center.getY();
                final double distance = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
                double angle = Math.abs(Math.toDegrees(Math.acos(deltaX / distance)));
                if (deltaX >= 0 && deltaY <= 0) {
                    angle = 90.0 - angle;
                } else if (deltaX >= 0 && deltaY >= 0) {
                    angle += 90.0;
                } else if (deltaX <= 0 && deltaY >= 0) {
                    angle += 90.0;
                } else if (deltaX <= 0 && deltaY <= 0) {
                    angle = 450.0 - angle;
                }
                for (int i = 0; i < (stops.length - 1); i++) {
                    final double offset = stops[i].getOffset();
                    final double nextOffset = stops[i + 1].getOffset();
                    if (angle >= (offset * 360) && angle < (nextOffset * 360)) {
                        final double fraction = (angle - offset * 360) / ((nextOffset - offset) * 360);
                        color = interpolateColor(stops[i].getColor(), stops[i + 1].getColor(), fraction);
                    }
                }
                pixelWriter.setColor(x, y, color);
            }
        }
        return writableImage;
    }

    /**
     * Method interpolates two color pixel to get a color with new dues or rather a color transition.
     * @param colorOne first color pixel.
     * @param colorTwo next color pixel.
     * @param fraction fraction of the new color.
     * @return Color.
     */
    private Color interpolateColor(final Color colorOne, final Color colorTwo, final double fraction) {
        double red   = colorOne.getRed() + (colorTwo.getRed() - colorOne.getRed()) * fraction;
        double green = colorOne.getGreen() + (colorTwo.getGreen() - colorOne.getGreen()) * fraction;
        double blue  = colorOne.getBlue() + (colorTwo.getBlue() - colorOne.getBlue()) * fraction;
        double opacity = colorOne.getOpacity() + (colorTwo.getOpacity() - colorOne.getOpacity()) * fraction;
        red = clamp(red);
        green = clamp(green);
        blue = clamp(blue);
        opacity = clamp(opacity);
        return Color.color(red, green, blue, opacity);
    }

    /**
     * Method checks input to ensure a value between zero and one.
     * @param value ratio of mouse coordinate and element height.
     * @return value between zero and one.
     */
    static double clamp(final double value) {
        return value < 0 ? 0 : value > 1 ? 1 : value;
    }

    /**
     * Method creates a Stop array for hue calculation.
     * @return stops includes hsv hue.
     */
    private Stop[] hueStops() {
        double offset;
        int hue;
        Stop[] stops = new Stop[255];

        for (int i = 0; i < 255; i++) {
            offset = (1.0 / 255) * i;
            hue = (int) ((i / 255.0) * 360);
            stops[i] = new Stop(offset, Color.hsb(hue, 1.0, 1.0));
        }
        return stops;
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
        final Rectangle rectangle = new Rectangle(15.0, 25.0, Color.web(WHITE, 0.0));

        rectangle.setMouseTransparent(true);
        rectangle.setTranslateX(-15 / 2);
        rectangle.setTranslateY(-25 / 2);
        rectangle.setLayoutX(75);
        rectangle.setLayoutY(75);
        rectangle.setY(-62);
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
     * Method creates a pane for the saturation rectangle (piece of the colorContainer).
     * @return colorRectSaturation is a colored shape (linear gradient of saturation).
     */
    private Pane saturationRect() {
        final Pane colorRectSaturation = new Pane();

        //TODO size generic...
        colorRectSaturation.setPrefSize(150, 150);
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
        colorRectBrightness.setPrefSize(150, 150);
        colorRectBrightness.setBackground(new Background(new BackgroundFill(new LinearGradient(0, 0, 0, 1, true,
                CycleMethod.NO_CYCLE, new Stop(0, Color.rgb(0, 0, 0, 0)), new Stop(1, Color.rgb(0, 0, 0, 1))),
                CornerRadii.EMPTY, Insets.EMPTY)));

        return colorRectBrightness;
    }

    /**
     * Method creates a hollow circle of a tall and small circle with the color spectrum as filled image.
     * @param circleTallRadius is the tall circle radius
     * @param circleSmallRadius is the small circle radius.
     * @return hollowCircle is a shape.
     */
    private Shape hollowCircle(final int circleTallRadius, final int circleSmallRadius) {
        final Circle circleTall = new Circle(circleTallRadius);
        final Circle circleSmall = new Circle(circleSmallRadius);
        final Shape hollowCircle = Path.subtract(circleTall, circleSmall);
        final Stop[] hueFraction = hueStops();
        final ImagePattern imagePattern = new ImagePattern(colorSpectrumImage(150, 150, hueFraction));

        hollowCircle.setLayoutX(75);
        hollowCircle.setLayoutY(75);
        hollowCircle.setFill(imagePattern);

        return hollowCircle;
    }

    //CHECKSTYLE.ON: MagicNumber

    @Override
    public DALRemoteService getDALRemoteService() {
        return ambientLightRemote;
    }

    @Override
    void removeObserver() {
        this.ambientLightRemote.removeObserver(this);
    }

    @Override
    public void update(final Observable<AmbientLight> observable, final AmbientLight ambientLight) throws Exception {
        final Color color = Color.hsb(ambientLight.getColor().getHue(),
                ambientLight.getColor().getSaturation() / Constants.ONEHUNDRED,
                ambientLight.getColor().getValue() / Constants.ONEHUNDRED);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                setColorToImageEffect(color); //TODO: Set color to gray if powerstate is off

                if (ambientLight.getPowerState().getValue().equals(State.ON) && !toggleSwitch.isSelected()) {
                    toggleSwitch.setSelected(true);
                } else if (ambientLight.getPowerState().getValue().equals(State.OFF) && toggleSwitch.isSelected()) {
                    toggleSwitch.setSelected(false);
                }
            }
        });
    }
}
