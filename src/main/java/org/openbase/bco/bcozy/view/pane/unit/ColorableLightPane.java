package org.openbase.bco.bcozy.view.pane.unit;


/**
 * ==================================================================
 *
 * This file is part of org.openbase.bco.bcozy.
 *
 * org.openbase.bco.bcozy is free software: you can redistribute it and modify
 * it under the terms of the GNU General Public License (Version 3)
 * as published by the Free Software Foundation.
 *
 * org.openbase.bco.bcozy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with org.openbase.bco.bcozy. If not, see <http://www.gnu.org/licenses/>.
 * ==================================================================
 */
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
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
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.openbase.jul.schedule.RecurrenceEventFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.state.PowerStateType.PowerState.State;
import rst.domotic.unit.dal.ColorableLightDataType.ColorableLightData;
import rst.vision.HSBColorType.HSBColor;
import java.util.concurrent.Future;
import org.openbase.bco.dal.remote.unit.ColorableLightRemote;
import org.openbase.jul.schedule.GlobalCachedExecutorService;
import rst.domotic.state.PowerStateType.PowerState;

/**
 * Created by agatting on 03.12.15.
 */
public class ColorableLightPane extends AbstractUnitPane<ColorableLightRemote, ColorableLightData> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ColorableLightPane.class);
    private static final double COLOR_BOX_SIZE = 150.0;

    private final DoubleProperty hueValue = new SimpleDoubleProperty(0.0);
    private final DoubleProperty saturation = new SimpleDoubleProperty(0.0);
    private final DoubleProperty brightness = new SimpleDoubleProperty(0.0);
    private final Rectangle rectangleSelector;
    private double rectX;
    private double rectY;
    private double angle;

    private RecurrenceEventFilter recurrenceEventFilterHSV = new RecurrenceEventFilter(Constants.RECURRENCE_EVENT_FILTER_MILLI_TIMEOUT) {
        @Override
        public void relay() {
//            getUnitRemote().setColor(color);
        }
    };

    /**
     * Constructor for the AmbientLightPane.
     *
     * @param colorableLightRemote colorableLightRemotexẑ
     */
    public ColorableLightPane() {
        super(ColorableLightRemote.class, true);
        this.setIcon(MaterialDesignIcon.LIGHTBULB, MaterialDesignIcon.LIGHTBULB_OUTLINE);
        this.rectangleSelector = new Rectangle();
    }

    private void initEffectAndSwitch() {
        State powerState = State.OFF;
        HSBColor currentColor = HSBColor.newBuilder().build();

        try {
            powerState = getUnitRemote().getPowerState().getValue();
            currentColor = getUnitRemote().getHSBColor();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
        final Color color = Color.hsb(currentColor.getHue(), currentColor.getSaturation() / Constants.ONE_HUNDRED,
                currentColor.getBrightness() / Constants.ONE_HUNDRED);

        hueValue.set(color.getHue());
        angle = color.getHue();
        rectangleSelector.setY(0);
        rectSelectorCoordinates();
        rectangleSelector.setLayoutX(rectX);
        rectangleSelector.setLayoutY(rectY);
        rectangleSelector.setRotate(angle);

//        setEffectColorAndSwitch(powerState, color);
    }

    private Image colorSpectrumImage(final int width, final int height, final Stop... stops) {
        final WritableImage writableImage = new WritableImage(width, height);
        final PixelWriter pixelWriter = writableImage.getPixelWriter();
        Color color = Color.TRANSPARENT;
        final Point2D center = new Point2D(width / 2.0, height / 2.0);
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

    private Color interpolateColor(final Color colorOne, final Color colorTwo, final double fraction) {
        double red = colorOne.getRed() + (colorTwo.getRed() - colorOne.getRed()) * fraction;
        double green = colorOne.getGreen() + (colorTwo.getGreen() - colorOne.getGreen()) * fraction;
        double blue = colorOne.getBlue() + (colorTwo.getBlue() - colorOne.getBlue()) * fraction;
        double opacity = colorOne.getOpacity() + (colorTwo.getOpacity() - colorOne.getOpacity()) * fraction;
        red = clamp(red);
        green = clamp(green);
        blue = clamp(blue);
        opacity = clamp(opacity);
        return Color.color(red, green, blue, opacity);
    }

    static double clamp(final double value) {
        return value < 0 ? 0 : value > 1 ? 1 : value;
    }

    private Stop[] hueStops() {
        double offset;
        int hue;
        Stop[] stops = new Stop[Constants.RGB255];

        for (int i = 0; i < Constants.RGB255; i++) {
            offset = (1.0 / Constants.RGB255) * i;
            hue = (int) ((i / (float) Constants.RGB255) * Constants.ROUND_ANGLE);
            stops[i] = new Stop(offset, Color.hsb(hue, 1, 1));
        }
        return stops;
    }

    private Circle circleSelector() {
        final Circle circle = new Circle(COLOR_BOX_SIZE / Constants.FIFTEEN, Color.web(Constants.WHITE, 0.0));

        circle.getStyleClass().add("circle-selector");
        circle.setMouseTransparent(true);
        circle.setStroke(Color.web(Constants.WHITE, 1.0));
        circle.setCache(true);
        circle.setManaged(false);
        circle.setEffect(dropShadow());

        return circle;
    }

    private DropShadow dropShadow() {
        final DropShadow dropShadow = new DropShadow();

        dropShadow.setOffsetX(1.0);
        dropShadow.setOffsetY(1.0);
        dropShadow.setColor(Color.BLACK);

        return dropShadow;
    }

    private Pane saturationRect() {
        final Pane colorRectSaturation = new Pane();

        colorRectSaturation.setPrefSize(COLOR_BOX_SIZE, COLOR_BOX_SIZE);
        colorRectSaturation.setMinSize(COLOR_BOX_SIZE, COLOR_BOX_SIZE);
        colorRectSaturation.setBackground(new Background(new BackgroundFill(new LinearGradient(0.0, 0.0, 1.0, 0.0, true,
                CycleMethod.NO_CYCLE, new Stop(0.0, Color.rgb(Constants.RGB255, Constants.RGB255,
                        Constants.RGB255, 1.0)), new Stop(1, Color.rgb(Constants.RGB255, Constants.RGB255,
                        Constants.RGB255, 0.0))), CornerRadii.EMPTY, Insets.EMPTY)));

        return colorRectSaturation;
    }

    private Pane brightnessRect() {
        final Pane colorRectBrightness = new Pane();

        colorRectBrightness.setPrefSize(COLOR_BOX_SIZE, COLOR_BOX_SIZE);
        colorRectBrightness.setMinSize(COLOR_BOX_SIZE, COLOR_BOX_SIZE);
        colorRectBrightness.setBackground(new Background(new BackgroundFill(new LinearGradient(0, 0, 0, 1, true,
                CycleMethod.NO_CYCLE, new Stop(0, Color.rgb(0, 0, 0, 0)), new Stop(1, Color.rgb(0, 0, 0, 1))),
                CornerRadii.EMPTY, Insets.EMPTY)));

        return colorRectBrightness;
    }

    private Shape hollowCircle() {
        final Circle circleTall = new Circle(COLOR_BOX_SIZE / 2);
        final Circle circleSmall = new Circle(circleTall.getRadius() - COLOR_BOX_SIZE / Constants.SIX);
        final Shape hollowCircle = Path.subtract(circleTall, circleSmall);
        final Stop[] hueFraction = hueStops();
        final ImagePattern imagePattern = new ImagePattern(colorSpectrumImage((int) COLOR_BOX_SIZE,
                (int) COLOR_BOX_SIZE, hueFraction));
        hollowCircle.setLayoutX(circleTall.getRadius());
        hollowCircle.setLayoutY(circleTall.getRadius());
        hollowCircle.setFill(imagePattern);

        return hollowCircle;
    }

    private void rectSelectorCoordinates() {
        rectX = Math.round(COLOR_BOX_SIZE / 2.0 + (COLOR_BOX_SIZE - rectangleSelector.getHeight()) / 2.0
                * Math.cos(Math.toRadians((angle + Constants.OBTUSE_ANGLE_270) % Constants.ROUND_ANGLE)));
        rectY = Math.round(COLOR_BOX_SIZE / 2.0 + (COLOR_BOX_SIZE - rectangleSelector.getHeight()) / 2.0
                * Math.sin(Math.toRadians((angle + Constants.OBTUSE_ANGLE_270) % Constants.ROUND_ANGLE)));
    }

    @Override
    protected void initBodyContent(final Pane bodyPane) {
        final Pane colorRectContainer = new StackPane();
        final Pane colorHue = new Pane();
        final Circle circle = circleSelector();
        final Shape hollowCircle = hollowCircle();
        final Rectangle clip = new Rectangle(0, 0, 0, 0);
        final Pane colorCircleContainer = new Pane();

        colorCircleContainer.setPrefSize(COLOR_BOX_SIZE, COLOR_BOX_SIZE);
        colorCircleContainer.setMinSize(COLOR_BOX_SIZE, COLOR_BOX_SIZE);
        colorCircleContainer.maxHeightProperty().bind(colorCircleContainer.prefWidthProperty());

        colorHue.setMinSize(COLOR_BOX_SIZE, COLOR_BOX_SIZE);
        colorHue.setPrefSize(COLOR_BOX_SIZE, COLOR_BOX_SIZE);

        colorHue.backgroundProperty().bind(new ObjectBinding<Background>() {
            {
                bind(hueValue);
            }

            @Override
            protected Background computeValue() {
                return new Background(new BackgroundFill(Color.hsb(hueValue.getValue(), 1.0, 1.0),
                        CornerRadii.EMPTY, Insets.EMPTY));
            }
        });

        circle.layoutXProperty().bind(saturation.divide(Constants.ONE_HUNDRED).multiply(COLOR_BOX_SIZE));
        circle.layoutYProperty().bind(Bindings.subtract(1, brightness.divide(Constants.ONE_HUNDRED))
                .multiply(COLOR_BOX_SIZE));

        final EventHandler<MouseEvent> colorContainerMouseHandler = event -> GlobalCachedExecutorService.submit(new Task() {
            @Override
            protected Object call() {
                final double xMouse = event.getX();
                final double yMouse = event.getY();
                saturation.set(clamp(xMouse / COLOR_BOX_SIZE) * Constants.ONE_HUNDRED);
                brightness.set(Constants.ONE_HUNDRED - (clamp(yMouse / COLOR_BOX_SIZE) * Constants.ONE_HUNDRED));

                recurrenceEventFilterHSV.trigger();

                return null;
            }
        });

        colorRectContainer.setMinSize(COLOR_BOX_SIZE, COLOR_BOX_SIZE);
        colorRectContainer.setPrefSize(COLOR_BOX_SIZE, COLOR_BOX_SIZE);
        colorRectContainer.maxHeightProperty().bind(colorRectContainer.prefWidthProperty());
        colorRectContainer.getChildren().addAll(colorHue, saturationRect(), brightnessRect(), circle);
        colorRectContainer.setOnMousePressed(colorContainerMouseHandler);
        colorRectContainer.setOnMouseDragged(colorContainerMouseHandler);

        rectangleSelector.setWidth(COLOR_BOX_SIZE / Constants.TEN);
        rectangleSelector.setHeight(COLOR_BOX_SIZE / Constants.SIX);
        rectangleSelector.setFill(Color.web(Constants.WHITE, 0.0));
        rectangleSelector.getStyleClass().add("rectangle-selector");
        rectangleSelector.setMouseTransparent(true);
        rectangleSelector.setTranslateX(-rectangleSelector.getWidth() / 2.0);
        rectangleSelector.setTranslateY(-rectangleSelector.getHeight() / 2.0);
        rectangleSelector.setLayoutX(COLOR_BOX_SIZE / 2.0);
        rectangleSelector.setLayoutY(COLOR_BOX_SIZE / 2.0);
        rectangleSelector.setY((rectangleSelector.getHeight() - COLOR_BOX_SIZE) / 2.0);
        rectangleSelector.setStroke(Color.web(Constants.WHITE, 1.0));
        rectangleSelector.setCache(true);
        rectangleSelector.setManaged(false);
        rectangleSelector.setEffect(dropShadow());

        final EventHandler<MouseEvent> colorCircleMouseHandler = event -> GlobalCachedExecutorService.submit(new Task() {
            @Override
            protected Object call() {
                double yMouse = event.getY();
                double xMouse = event.getX();

                angle = (Math.toDegrees(Math.atan2(yMouse, xMouse)) + Constants.ROUND_ANGLE + Constants.RIGHT_ANGLE) % Constants.ROUND_ANGLE;
                hueValue.set(angle);

                rectSelectorCoordinates();
                rectangleSelector.setLayoutX(rectX);
                rectangleSelector.setLayoutY(rectY);
                rectangleSelector.setRotate(angle);

                recurrenceEventFilterHSV.trigger();

                return null;
            }
        });

        hollowCircle.setOnMousePressed(colorCircleMouseHandler);
        hollowCircle.setOnMouseDragged(colorCircleMouseHandler);

        colorCircleContainer.getChildren().addAll(hollowCircle, rectangleSelector);
        bodyPane.getChildren().addAll(colorRectContainer, colorCircleContainer);
        bodyPane.prefHeightProperty().set(COLOR_BOX_SIZE + Constants.INSETS);

        // clipBorderPane (Body) to be sure, that no content overlaps the pane
        clip.widthProperty().bind(bodyPane.widthProperty());
        clip.heightProperty().bind(bodyPane.heightProperty());
        bodyPane.setClip(clip);

        initEffectAndSwitch();
    }

    @Override
    public void updateDynamicContent() {
        super.updateDynamicContent();

        // detect power state
        PowerState.State state = PowerState.State.UNKNOWN;
        try {
            state = getUnitRemote().getData().getPowerState().getValue();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.DEBUG);
        }

        // detect color
        Color color = Color.TRANSPARENT;
        try {
            color = Color.hsb(getData().getColorState().getColor().getHsbColor().getHue(),
                    getData().getColorState().getColor().getHsbColor().getSaturation() / 100,
                    getData().getColorState().getColor().getHsbColor().getBrightness() / 100);
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.DEBUG);
        }

        switch (state) {
            case OFF:
                getIcon().setBackgroundIconColorAnimated(Color.BLACK);
                setInfoText("lightOff");
                primaryActivationProperty().setValue(Boolean.FALSE);
                break;
            case ON:
                getIcon().setBackgroundIconColorAnimated(color);
                setInfoText("lightOn");
                primaryActivationProperty().setValue(Boolean.TRUE);
                break;
            default:
                setInfoText("unknown");
                break;
        }
    }

    @Override
    protected Future applyMainFunctionUpdate(final boolean activation) throws CouldNotPerformException {
        return (activation) ? getUnitRemote().setPowerState(PowerState.State.ON) : getUnitRemote().setPowerState(PowerState.State.OFF);
    }
}
