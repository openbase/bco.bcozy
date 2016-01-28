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
package org.dc.bco.bcozy.view.devicepanes; //NOPMD //TODO: Split up in several classes

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import org.controlsfx.control.ToggleSwitch;
import org.dc.bco.bcozy.view.Constants;
import org.dc.bco.bcozy.view.SVGIcon;
import org.dc.bco.dal.remote.unit.AmbientLightRemote;
import org.dc.bco.dal.remote.unit.DALRemoteService;
import org.dc.jul.exception.CouldNotPerformException;
import org.dc.jul.exception.printer.ExceptionPrinter;
import org.dc.jul.exception.printer.LogLevel;
import org.dc.jul.pattern.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.homeautomation.state.PowerStateType;
import rst.homeautomation.state.PowerStateType.PowerState.State;
import rst.homeautomation.unit.AmbientLightType.AmbientLight;
import rst.vision.HSVColorType.HSVColor;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created on 03.12.15.
 */
public class AmbientLightPane extends UnitPane {
    private static final Logger LOGGER = LoggerFactory.getLogger(AmbientLightPane.class);

    private final AmbientLightRemote ambientLightRemote;
    private final SVGIcon lightbulbIcon;
    private final ToggleSwitch toggleSwitch;
    private final BorderPane headContent;
    private Pane colorCircleContainer;
    private HBox bodyContent;

    /**
     * Constructor for the AmbientLightPane.
     * @param ambientLightRemote ambientLightRemote
     */
    public AmbientLightPane(final DALRemoteService ambientLightRemote) {
        this.ambientLightRemote = (AmbientLightRemote) ambientLightRemote;

        toggleSwitch = new ToggleSwitch();
        lightbulbIcon =
                new SVGIcon(MaterialDesignIcon.LIGHTBULB, MaterialDesignIcon.LIGHTBULB_OUTLINE, Constants.SMALL_ICON);
        headContent = new BorderPane();

        initUnitLabel();
        initTitle();
        initContent();
        createWidgetPane(headContent, bodyContent);

        initEffectAndSwitch();

        this.ambientLightRemote.addObserver(this);
    }

    private void initEffectAndSwitch() {
        State powerState = State.OFF;
        HSVColor currentColor = HSVColor.newBuilder().build();

        try {
            powerState = ambientLightRemote.getPower().getValue();
            currentColor = ambientLightRemote.getColor();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
        final Color color = Color.hsb(currentColor.getHue(),
                currentColor.getSaturation() / Constants.ONE_HUNDRED,
                currentColor.getValue() / Constants.ONE_HUNDRED);

        setEffectColorAndSwitch(powerState, color);
    }

    private void setEffectColorAndSwitch(final State powerState, final Color color) {
        if (powerState.equals(State.ON)) {
            lightbulbIcon.setBackgroundIconColorAnimated(color);

            if (!toggleSwitch.isSelected()) {
                toggleSwitch.setSelected(true);
            }
        } else {
            lightbulbIcon.setBackgroundIconColorAnimated(Color.TRANSPARENT);

            if (toggleSwitch.isSelected()) {
                toggleSwitch.setSelected(false);
            }
        }
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
                    angle = Constants.RIGHT_ANGLE - angle;
                } else if (deltaX >= 0 && deltaY >= 0) {
                    angle += Constants.RIGHT_ANGLE;
                } else if (deltaX <= 0 && deltaY >= 0) {
                    angle += Constants.RIGHT_ANGLE;
                } else if (deltaX <= 0 && deltaY <= 0) {
                    angle = Constants.ROUND_ANGLE + Constants.RIGHT_ANGLE - angle;
                }
                for (int i = 0; i < (stops.length - 1); i++) {
                    final double offset = stops[i].getOffset();
                    final double nextOffset = stops[i + 1].getOffset();
                    if (angle >= (offset * Constants.ROUND_ANGLE) && angle < (nextOffset * Constants.ROUND_ANGLE)) {
                        final double fraction = (angle - offset * Constants.ROUND_ANGLE)
                                / ((nextOffset - offset) * Constants.ROUND_ANGLE);
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
        Stop[] stops = new Stop[Constants.RGB_8_BIT];

        for (int i = 0; i < Constants.RGB_8_BIT; i++) {
            offset = (1.0 / Constants.RGB_8_BIT) * i;
            hue = (int) ((i / (float) Constants.RGB_8_BIT) * Constants.ROUND_ANGLE);
            stops[i] = new Stop(offset, Color.hsb(hue, 1, 1));
        }
        return stops;
    }

    /**
     * Method creates a circle shape as visual selector in saturation/brightness area.
     * @return circle shape with settings.
     */
    private Circle circleSelector() {
        final Circle circle = new Circle(colorCircleContainer.getPrefWidth() / Constants.FIFTEEN,
                Color.web(Constants.WHITE, 0.0));

        circle.getStyleClass().add("circle-selector");
        circle.setMouseTransparent(true);
        circle.setStroke(Color.web(Constants.WHITE, 1.0));
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
        final Rectangle rectangle = new Rectangle(colorCircleContainer.getPrefWidth() / Constants.TEN,
                colorCircleContainer.getPrefHeight() / Constants.SIX, Color.web(Constants.WHITE, 0.0));

        rectangle.getStyleClass().add("rectangle-selector");
        rectangle.setMouseTransparent(true);
        rectangle.setTranslateX(-rectangle.getWidth() / 2);
        rectangle.setTranslateY(-rectangle.getHeight() / 2);
        rectangle.setLayoutX(colorCircleContainer.getPrefWidth() / 2);
        rectangle.setLayoutY(colorCircleContainer.getPrefHeight() / 2);
        rectangle.setY((rectangle.getHeight() - colorCircleContainer.getPrefHeight()) / 2);
        rectangle.setStroke(Color.web(Constants.WHITE, 1.0));
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

        colorRectSaturation.setPrefSize(colorCircleContainer.getPrefWidth(), colorCircleContainer.getPrefHeight());
        colorRectSaturation.setBackground(new Background(new BackgroundFill(new LinearGradient(0, 0, 1, 0, true,
                CycleMethod.NO_CYCLE, new Stop(0, Color.rgb(Constants.RGB_8_BIT, Constants.RGB_8_BIT,
                Constants.RGB_8_BIT, 1)), new Stop(1, Color.rgb(Constants.RGB_8_BIT, Constants.RGB_8_BIT,
                Constants.RGB_8_BIT, 0))), CornerRadii.EMPTY, Insets.EMPTY)));

        return colorRectSaturation;
    }

    /**
     * Method creates a pane for the brightness rectangle (piece of the colorContainer).
     * @return colorRectBrightness is a colored shape (linear gradient of brightness).
     */
    private Pane brightnessRect() {
        final Pane colorRectBrightness = new Pane();

        colorRectBrightness.setPrefSize(colorCircleContainer.getPrefWidth(), colorCircleContainer.getPrefHeight());
        colorRectBrightness.setBackground(new Background(new BackgroundFill(new LinearGradient(0, 0, 0, 1, true,
                CycleMethod.NO_CYCLE, new Stop(0, Color.rgb(0, 0, 0, 0)), new Stop(1, Color.rgb(0, 0, 0, 1))),
                CornerRadii.EMPTY, Insets.EMPTY)));

        return colorRectBrightness;
    }

    /**
     * Method creates a hollow circle of a tall and small circle with the color spectrum as filled image.
     * @return hollowCircle is a shape.
     */
    private Shape hollowCircle() {
        final Circle circleTall = new Circle(colorCircleContainer.getPrefWidth() / 2);
        final Circle circleSmall = new Circle(circleTall.getRadius() - colorCircleContainer.getPrefWidth()
                / Constants.SIX);
        final Shape hollowCircle = Path.subtract(circleTall, circleSmall);
        final Stop[] hueFraction = hueStops();
        final ImagePattern imagePattern = new ImagePattern(colorSpectrumImage((int) colorCircleContainer.getPrefWidth(),
                (int) colorCircleContainer.getPrefHeight(), hueFraction));
        hollowCircle.setLayoutX(circleTall.getRadius());
        hollowCircle.setLayoutY(circleTall.getRadius());
        hollowCircle.setFill(imagePattern);

        return hollowCircle;
    }

    @Override
    protected void initTitle() {
        final String setPowerString = "setPower";
        lightbulbIcon.setBackgroundIconColorAnimated(Color.TRANSPARENT);

        toggleSwitch.setOnMouseClicked(event -> {
            new Thread(new Task() {
                @Override
                protected Object call() throws java.lang.Exception {
                    if (toggleSwitch.isSelected()) {
                        try {
                            ambientLightRemote.callMethodAsync(setPowerString, PowerStateType.PowerState.newBuilder()
                                    .setValue(PowerStateType.PowerState.State.ON).build())
                                    .get(Constants.THREAD_MILLI_TIMEOUT, TimeUnit.MILLISECONDS);
                            //ambientLightRemote.setPower(PowerStateType.PowerState.State.ON);
                        } catch (InterruptedException | ExecutionException | TimeoutException
                                | CouldNotPerformException e) {
                            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                            setWidgetPaneDisable(true);
                        }
                    } else {
                        try {
                            ambientLightRemote.callMethodAsync(setPowerString, PowerStateType.PowerState.newBuilder()
                                    .setValue(PowerStateType.PowerState.State.OFF).build())
                                    .get(Constants.THREAD_MILLI_TIMEOUT, TimeUnit.MILLISECONDS);
                            //ambientLightRemote.setPower(PowerStateType.PowerState.State.OFF);
                        } catch (InterruptedException | ExecutionException | TimeoutException
                                | CouldNotPerformException e) {
                            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                            setWidgetPaneDisable(true);
                        }
                    }
                    return null;
                }
            }).start();
        });

        headContent.setLeft(lightbulbIcon);
        headContent.setCenter(getUnitLabel());
        headContent.setAlignment(getUnitLabel(), Pos.CENTER_LEFT);
        headContent.setRight(toggleSwitch);
        headContent.prefHeightProperty().set(lightbulbIcon.getSize() + Constants.INSETS + 1);
    }

    @Override
    protected void initContent() {
        final Pane colorRectContainer;
        final Pane colorHue;
        final Circle circle;
        final Rectangle rectangleSelector;
        final Shape hollowCircle;

        final DoubleProperty hueValue = new SimpleDoubleProperty(-1);
        final DoubleProperty saturation = new SimpleDoubleProperty(-1);
        final DoubleProperty brightness = new SimpleDoubleProperty(-1);

        //pane to set color circle and color selector
        colorCircleContainer = new Pane();
        //CHECKSTYLE.OFF: MagicNumber
        colorCircleContainer.setPrefSize(150, 150);
        colorCircleContainer.setMinSize(150, 150);
        //CHECKSTYLE.ON: MagicNumber
        colorCircleContainer.getStyleClass().clear();
        colorCircleContainer.getStyleClass().add("color-circle-container");

        //mouse event for saturation & brightness rectangle
        final EventHandler<MouseEvent> colorContainerMouseHandler = event -> {
            final double xMouse = event.getX();
            final double yMouse = event.getY();
            saturation.set(clamp(xMouse / colorCircleContainer.getPrefWidth()) * Constants.ONE_HUNDRED);
            brightness.set(Constants.ONE_HUNDRED - (clamp(yMouse / colorCircleContainer.getPrefHeight())
                    * Constants.ONE_HUNDRED));
        };

        final EventHandler<MouseEvent> sendingColorHandler = event -> {
            new Thread(new Task() {
                @Override
                protected Object call() {
                    HSVColor hsvColor = HSVColor.newBuilder().setHue(hueValue.floatValue())
                            .setSaturation(saturation.floatValue()).setValue(brightness.floatValue()).build();
                    try {
                        ambientLightRemote.callMethodAsync("setColor", hsvColor)
                                .get(Constants.THREAD_MILLI_TIMEOUT, TimeUnit.MILLISECONDS);
                        //ambientLightRemote.setColor(hsvColor);
                    } catch (InterruptedException | ExecutionException | TimeoutException
                            | CouldNotPerformException e) {
                        ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                        setWidgetPaneDisable(true);
                    }
                    return null;
                }
            }).start();
        };

        //saturation & brightness depend on hue value
        colorHue = new Pane();
        colorHue.backgroundProperty().bind(new ObjectBinding<Background>() {
            {
                bind(hueValue);
            }

            @Override protected Background computeValue() {
                return new Background(new BackgroundFill(Color.hsb(hueValue.getValue(), 1, 1),
                        CornerRadii.EMPTY, Insets.EMPTY));
            }
        });

        //rectangle as color selector
        rectangleSelector = rectangleSelector();
        //circle as selector for colorRectangleContainer
        circle = circleSelector();
        circle.layoutXProperty().bind(saturation.divide(Constants.ONE_HUNDRED)
                .multiply(colorCircleContainer.getPrefWidth()));
        circle.layoutYProperty().bind(Bindings.subtract(1, brightness.divide(Constants.ONE_HUNDRED))
                .multiply(colorCircleContainer.getPrefHeight()));

        //container/stackPane for saturation/brightness area
        colorRectContainer = new StackPane();
        colorRectContainer.getChildren().addAll(colorHue, saturationRect(), brightnessRect());
        colorRectContainer.setOnMousePressed(colorContainerMouseHandler);
        colorRectContainer.setOnMouseDragged(colorContainerMouseHandler);
        colorRectContainer.setOnMouseReleased(sendingColorHandler);
        colorRectContainer.getStyleClass().clear();
        colorRectContainer.getStyleClass().add("color-rect-container");
        colorRectContainer.getChildren().add(circle);

        final EventHandler<MouseEvent> colorCircleMouseHandler = event -> {
            double yMouse = event.getY() + colorCircleContainer.getPrefHeight() / 2;
            double xMouse = event.getX() + colorCircleContainer.getPrefWidth() / 2;

            double angle = (Math.toDegrees(Math.atan2(yMouse - colorCircleContainer.getPrefHeight() / 2,
                    colorCircleContainer.getPrefWidth() / 2 - xMouse)) + Constants.RIGHT_ANGLE) % Constants.ROUND_ANGLE;
            angle = Constants.ROUND_ANGLE - angle;
            hueValue.set(angle);

            rectangleSelector.setY(0);
            final int rectX = (int) Math.round(colorCircleContainer.getPrefWidth() / 2
                    + (colorCircleContainer.getPrefHeight() - rectangleSelector.getHeight()) / 2
                    * Math.cos(Math.toRadians((angle + Constants.OBTUSE_ANGLE_270) % Constants.ROUND_ANGLE)));
            final int rectY = (int) Math.round(colorCircleContainer.getPrefHeight() / 2
                    + (colorCircleContainer.getPrefHeight() - rectangleSelector.getHeight()) / 2
                    * Math.sin(Math.toRadians((angle + Constants.OBTUSE_ANGLE_270) % Constants.ROUND_ANGLE)));
            rectangleSelector.setLayoutX(rectX);
            rectangleSelector.setLayoutY(rectY);
            rectangleSelector.setRotate(angle);
        };

        //hollow circle with color spectrum
        hollowCircle = hollowCircle();
        hollowCircle.setOnMousePressed(colorCircleMouseHandler);
        hollowCircle.setOnMouseDragged(colorCircleMouseHandler);
        hollowCircle.setOnMouseReleased(sendingColorHandler);

        colorCircleContainer.getChildren().addAll(hollowCircle, rectangleSelector);

        //hBox includes color elements (colorContainer & pane)
        bodyContent = new HBox();
        bodyContent.getChildren().addAll(colorRectContainer, colorCircleContainer);
        bodyContent.prefHeightProperty().set(colorCircleContainer.getPrefHeight() + (Constants.INSETS * 2));
    }

    @Override
    protected void initUnitLabel() {
        String unitLabel = Constants.UNKNOWN_ID;
        try {
            unitLabel = this.ambientLightRemote.getData().getLabel();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
        setUnitLabelString(unitLabel);
    }

    @Override
    public DALRemoteService getDALRemoteService() {
        return ambientLightRemote;
    }

    @Override
    void removeObserver() {
        this.ambientLightRemote.removeObserver(this);
    }

    @Override
    public void update(final Observable observable, final Object ambientLight) throws java.lang.Exception {
        Platform.runLater(() -> {
            if (this.isDisabled()) {
                setWidgetPaneDisable(false);
            }

            final State powerState = ((AmbientLight) ambientLight).getPowerState().getValue();
            final Color color = Color.hsb(((AmbientLight) ambientLight).getColor().getHue(),
                    ((AmbientLight) ambientLight).getColor().getSaturation() / Constants.ONE_HUNDRED,
                    ((AmbientLight) ambientLight).getColor().getValue() / Constants.ONE_HUNDRED);
            setEffectColorAndSwitch(powerState, color);
        });
    }
}
