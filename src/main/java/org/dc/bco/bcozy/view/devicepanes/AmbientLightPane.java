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
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
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
import org.dc.jul.extension.rsb.com.AbstractIdentifiableRemote;
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
 * Created by agatting on 03.12.15.
 */
public class AmbientLightPane extends UnitPane {
    private static final Logger LOGGER = LoggerFactory.getLogger(AmbientLightPane.class);

    private final AmbientLightRemote ambientLightRemote;
    private final SVGIcon lightBulbIcon;
    private final SVGIcon unknownForegroundIcon;
    private final SVGIcon unknownBackgroundIcon;
    private final ToggleSwitch toggleSwitch;
    private final BorderPane headContent;
    private final DoubleProperty hueValue = new SimpleDoubleProperty(0.0);
    private final DoubleProperty saturation = new SimpleDoubleProperty(Constants.ONE_HUNDRED / 2);
    private final DoubleProperty brightness = new SimpleDoubleProperty(Constants.ONE_HUNDRED / 2);
    private final Rectangle rectangleSelector;
    private final Pane colorCircleContainer;
    private final Tooltip tooltip;
    private final GridPane iconPane;
    private double rectX;
    private double rectY;
    private double angle;
    private HBox bodyContent;

    /**
     * Constructor for the AmbientLightPane.
     * @param ambientLightRemote ambientLightRemote
     */
    public AmbientLightPane(final AbstractIdentifiableRemote ambientLightRemote) {
        this.ambientLightRemote = (AmbientLightRemote) ambientLightRemote;

        lightBulbIcon =
                new SVGIcon(MaterialDesignIcon.LIGHTBULB, MaterialDesignIcon.LIGHTBULB_OUTLINE, Constants.SMALL_ICON);
        unknownBackgroundIcon = new SVGIcon(MaterialDesignIcon.CHECKBOX_BLANK_CIRCLE, Constants.SMALL_ICON - 2, false);
        unknownForegroundIcon = new SVGIcon(MaterialDesignIcon.HELP_CIRCLE, Constants.SMALL_ICON, false);
        rectangleSelector = new Rectangle();
        toggleSwitch = new ToggleSwitch();
        colorCircleContainer = new Pane();
        headContent = new BorderPane();
        iconPane = new GridPane();
        tooltip = new Tooltip();

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
            if (powerState == State.UNKNOWN) {
                powerState = State.OFF;
            }
            currentColor = ambientLightRemote.getColor();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
        Color color = Color.hsb(currentColor.getHue(), currentColor.getSaturation() / Constants.ONE_HUNDRED,
                currentColor.getValue() / Constants.ONE_HUNDRED);

        if (color.getBrightness() != 0.0 && color.getSaturation() != 0.0 && color.getHue() != 0.0) {
            saturation.set(color.getSaturation() * Constants.ONE_HUNDRED);
            brightness.set(color.getBrightness() * Constants.ONE_HUNDRED);
        }

        hueValue.set(color.getHue());
        angle = color.getHue();
        rectangleSelector.setY(0);
        rectSelectorCoordinates();
        rectangleSelector.setLayoutX(rectX);
        rectangleSelector.setLayoutY(rectY);
        rectangleSelector.setRotate(angle);

        setEffectColorAndSwitch(powerState, color);
    }

    private void setEffectColorAndSwitch(final State powerState, Color color) {
        iconPane.getChildren().clear();

        if (powerState.equals(State.ON)) {
            if (color.getBrightness() == 0.0 && color.getSaturation() == 0.0 && color.getHue() == 0.0) {
                sendHSV2Remote();
            } else {
                iconPane.add(lightBulbIcon, 0, 0);
                lightBulbIcon.setBackgroundIconColorAnimated(color);
                tooltip.setText(Constants.LIGHT_ON);

                if (!toggleSwitch.isSelected()) {
                    toggleSwitch.setSelected(true);
                }
            }
        } else if (powerState.equals(State.OFF)) {
            iconPane.add(lightBulbIcon, 0, 0);
            lightBulbIcon.setBackgroundIconColorAnimated(Color.TRANSPARENT);
            tooltip.setText(Constants.LIGHT_OFF);

            if (toggleSwitch.isSelected()) {
                toggleSwitch.setSelected(false);
            }
        } else {
            iconPane.add(unknownBackgroundIcon, 0, 0);
            iconPane.add(unknownForegroundIcon, 0, 0);
            tooltip.setText(Constants.UNKNOWN);
        }
        Tooltip.install(iconPane, tooltip);
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
        Stop[] stops = new Stop[Constants.RGB255];

        for (int i = 0; i < Constants.RGB255; i++) {
            offset = (1.0 / Constants.RGB255) * i;
            hue = (int) ((i / (float) Constants.RGB255) * Constants.ROUND_ANGLE);
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
    private void rectSelector() {
        rectangleSelector.setWidth(colorCircleContainer.getPrefWidth() / Constants.TEN);
        rectangleSelector.setHeight(colorCircleContainer.getPrefHeight() / Constants.SIX);
        rectangleSelector.setFill(Color.web(Constants.WHITE, 0.0));
        rectangleSelector.getStyleClass().add("rectangle-selector");
        rectangleSelector.setMouseTransparent(true);
        rectangleSelector.setTranslateX(-rectangleSelector.getWidth() / 2.0);
        rectangleSelector.setTranslateY(-rectangleSelector.getHeight() / 2.0);
        rectangleSelector.setLayoutX(colorCircleContainer.getPrefWidth() / 2.0);
        rectangleSelector.setLayoutY(colorCircleContainer.getPrefHeight() / 2.0);
        rectangleSelector.setY((rectangleSelector.getHeight() - colorCircleContainer.getPrefHeight()) / 2.0);
        rectangleSelector.setStroke(Color.web(Constants.WHITE, 1.0));
        rectangleSelector.setCache(true);
        rectangleSelector.setManaged(false);
        rectangleSelector.setEffect(shadowMaker());
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
        colorRectSaturation.setMinSize(colorCircleContainer.getMinWidth(), colorCircleContainer.getMinHeight());
        colorRectSaturation.setBackground(new Background(new BackgroundFill(new LinearGradient(0.0, 0.0, 1.0, 0.0, true,
                CycleMethod.NO_CYCLE, new Stop(0.0, Color.rgb(Constants.RGB255, Constants.RGB255,
                Constants.RGB255, 1.0)), new Stop(1, Color.rgb(Constants.RGB255, Constants.RGB255,
                Constants.RGB255, 0.0))), CornerRadii.EMPTY, Insets.EMPTY)));

        return colorRectSaturation;
    }

    /**
     * Method creates a pane for the brightness rectangle (piece of the colorContainer).
     * @return colorRectBrightness is a colored shape (linear gradient of brightness).
     */
    private Pane brightnessRect() {
        final Pane colorRectBrightness = new Pane();

        colorRectBrightness.setPrefSize(colorCircleContainer.getPrefWidth(), colorCircleContainer.getPrefHeight());
        colorRectBrightness.setMinSize(colorCircleContainer.getMinWidth(), colorCircleContainer.getMinHeight());
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

        toggleSwitch.setOnMouseClicked(event -> new Thread(new Task() {
            @Override
            protected Object call() throws Exception {
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
        }).start());

        unknownForegroundIcon.setForegroundIconColor(Color.BLUE);
        unknownBackgroundIcon.setForegroundIconColor(Color.WHITE);
        lightBulbIcon.setBackgroundIconColorAnimated(Color.TRANSPARENT);

        headContent.setLeft(iconPane);
        headContent.setCenter(getUnitLabel());
        headContent.setAlignment(getUnitLabel(), Pos.CENTER_LEFT);
        headContent.setRight(toggleSwitch);
        headContent.prefHeightProperty().set(lightBulbIcon.getSize() + Constants.INSETS + 1);
    }

    private void sendHSV2Remote() {
        HSVColor hsvColor = HSVColor.newBuilder().setHue(hueValue.floatValue())
                .setSaturation(saturation.floatValue()).setValue(brightness.floatValue()).build();
        try {
            ambientLightRemote.callMethodAsync("setColor", hsvColor)
                    .get(Constants.THREAD_MILLI_TIMEOUT, TimeUnit.MILLISECONDS);
            //ambientLightRemote.setColor(hsvColor);
        } catch (InterruptedException | ExecutionException | TimeoutException | CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
            setWidgetPaneDisable(true);
        }
    }

    @Override
    protected void initContent() {
        final Pane colorRectContainer;
        final Pane colorHue;
        final Circle circle;
        final Shape hollowCircle;
        final Rectangle clip = new Rectangle(0, 0, 0, 0);

        //CHECKSTYLE.OFF: MagicNumber
        colorCircleContainer.setPrefSize(150, 150);
        colorCircleContainer.setMinSize(150, 150);
        //CHECKSTYLE.ON: MagicNumber
        colorCircleContainer.maxHeightProperty().bind(colorCircleContainer.prefWidthProperty());

        rectSelector();

        final EventHandler<MouseEvent> sendingColorHandler = event -> new Thread(new Task() {
            @Override
            protected Object call() {
                sendHSV2Remote();
                return null;
            }
        }).start();

        //saturation & brightness depend on hue value
        colorHue = new Pane();
        colorHue.setMinSize(colorCircleContainer.getMinWidth(), colorCircleContainer.getMinHeight());
        colorHue.setPrefSize(colorCircleContainer.getPrefWidth(), colorCircleContainer.getPrefHeight());

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
        circle.layoutXProperty().bind(saturation.divide(Constants.ONE_HUNDRED)
                .multiply(colorCircleContainer.getPrefWidth()));
        circle.layoutYProperty().bind(Bindings.subtract(1, brightness.divide(Constants.ONE_HUNDRED))
                .multiply(colorCircleContainer.getPrefHeight()));

        //mouse event for saturation & brightness rectangle
        final EventHandler<MouseEvent> colorContainerMouseHandler = event -> {
            final double xMouse = event.getX();
            final double yMouse = event.getY();
            saturation.set(clamp(xMouse / colorCircleContainer.getPrefWidth()) * Constants.ONE_HUNDRED);
            brightness.set(Constants.ONE_HUNDRED - (clamp(yMouse / colorCircleContainer.getPrefHeight())
                    * Constants.ONE_HUNDRED));
        };

        //container/stackPane for saturation/brightness area
        colorRectContainer = new StackPane();
        colorRectContainer.setMinSize(colorCircleContainer.getMinWidth(), colorCircleContainer.getMinHeight());
        colorRectContainer.setPrefSize(colorCircleContainer.getPrefWidth(), colorCircleContainer.getPrefHeight());
        colorRectContainer.maxHeightProperty().bind(colorRectContainer.prefWidthProperty());
        colorRectContainer.getChildren().addAll(colorHue, saturationRect(), brightnessRect(), circle);
        colorRectContainer.setOnMousePressed(colorContainerMouseHandler);
        colorRectContainer.setOnMouseDragged(colorContainerMouseHandler);
        colorRectContainer.setOnMouseReleased(sendingColorHandler);

        final EventHandler<MouseEvent> colorCircleMouseHandler = event -> {
            double yMouse = event.getY();
            double xMouse = event.getX();

            angle = (Math.toDegrees(Math.atan2(yMouse, xMouse)) + Constants.ROUND_ANGLE + Constants.RIGHT_ANGLE)
                    % Constants.ROUND_ANGLE;
            hueValue.set(angle);

            rectSelectorCoordinates();
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
        bodyContent.prefHeightProperty().set(colorCircleContainer.getPrefHeight() + Constants.INSETS);

        // clipBorderPane (Body) to be sure, that no content overlaps the pane
        clip.widthProperty().bind(bodyContent.widthProperty());
        clip.heightProperty().bind(bodyContent.heightProperty());
        bodyContent.setClip(clip);
    }

    private void rectSelectorCoordinates() {
        rectX = Math.round(colorCircleContainer.getPrefWidth() / 2.0
                + (colorCircleContainer.getPrefHeight() - rectangleSelector.getHeight()) / 2.0
                * Math.cos(Math.toRadians((angle + Constants.OBTUSE_ANGLE_270) % Constants.ROUND_ANGLE)));
        rectY = Math.round(colorCircleContainer.getPrefHeight() / 2.0
                + (colorCircleContainer.getPrefHeight() - rectangleSelector.getHeight()) / 2.0
                * Math.sin(Math.toRadians((angle + Constants.OBTUSE_ANGLE_270) % Constants.ROUND_ANGLE)));
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
    public AbstractIdentifiableRemote getDALRemoteService() {
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
