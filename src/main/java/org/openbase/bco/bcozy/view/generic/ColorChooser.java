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
package org.openbase.bco.bcozy.view.generic;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.jul.visual.javafx.iface.DynamicPane;

/**
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class ColorChooser extends HBox implements DynamicPane {

    //    private static final double COLOR_BOX_SIZE = 150.0;
    private static final double COLOR_BOX_SIZE = 75.0;

    private final ObjectProperty<Color> selectedColorProperty;

    private final DoubleProperty hueProperty;
    private final DoubleProperty saturationProperty;
    private final DoubleProperty brightnessProperty;

    private final Rectangle hueValueSelector;
    private double rectX;
    private double rectY;
    private double angle;

    public ColorChooser() {
        this.hueProperty = new SimpleDoubleProperty(0.0);
        this.saturationProperty = new SimpleDoubleProperty(0.0);
        this.brightnessProperty = new SimpleDoubleProperty(0.0);
        this.hueValueSelector = new Rectangle();
        this.selectedColorProperty = new SimpleObjectProperty<>(Color.BLACK);
    }

    @Override
    public void initContent() {
        final Pane colorRectContainer = new StackPane();
        final Pane colorHue = new Pane();
        final Circle circle = circleSelector();
        final Shape hollowCircle = hollowCircle();
        final Rectangle clip = new Rectangle(0, 0, 0, 0);
        final Pane colorCircleContainer = new Pane();

        this.minWidth(COLOR_BOX_SIZE);
//        this.minHeight(COLOR_BOX_SIZE);

        colorCircleContainer.setPrefSize(COLOR_BOX_SIZE, COLOR_BOX_SIZE);
//        colorCircleContainer.setMinSize(COLOR_BOX_SIZE, COLOR_BOX_SIZE);
        colorCircleContainer.maxHeightProperty().bind(colorCircleContainer.prefWidthProperty());

//        colorHue.setMinSize(COLOR_BOX_SIZE, COLOR_BOX_SIZE);
        colorHue.setPrefSize(COLOR_BOX_SIZE, COLOR_BOX_SIZE);

        colorHue.backgroundProperty().bind(new ObjectBinding<Background>() {
            {
                bind(hueProperty);
            }

            @Override
            protected Background computeValue() {
                return new Background(new BackgroundFill(Color.hsb(hueProperty.getValue(), 1.0, 1.0), CornerRadii.EMPTY, Insets.EMPTY));
            }
        });
        circle.layoutXProperty().bind(saturationProperty.multiply(COLOR_BOX_SIZE));
        circle.layoutYProperty().bind(Bindings.subtract(1, brightnessProperty).multiply(COLOR_BOX_SIZE));

        final EventHandler<MouseEvent> colorContainerMouseHandler = event -> {
            final double xMouse = event.getX();
            final double yMouse = event.getY();
            saturationProperty.set(clamp(xMouse / COLOR_BOX_SIZE));
            brightnessProperty.set(1 - (clamp(yMouse / COLOR_BOX_SIZE)));

            updateColor();  
            event.consume();
        };

//        colorRectContainer.setMinSize(COLOR_BOX_SIZE, COLOR_BOX_SIZE);
        colorRectContainer.setPrefSize(COLOR_BOX_SIZE, COLOR_BOX_SIZE);
        colorRectContainer.maxHeightProperty().bind(colorRectContainer.prefWidthProperty());
        colorRectContainer.getChildren().addAll(colorHue, saturationRect(), brightnessRect(), circle);
        colorRectContainer.setOnMousePressed(colorContainerMouseHandler);
        colorRectContainer.setOnMouseDragged(colorContainerMouseHandler);

        hueValueSelector.setWidth(COLOR_BOX_SIZE / Constants.TEN);
        hueValueSelector.setHeight(COLOR_BOX_SIZE / Constants.SIX);
        hueValueSelector.setFill(Color.web(Constants.WHITE, 0.0));
        hueValueSelector.getStyleClass().add("rectangle-selector");
        hueValueSelector.setMouseTransparent(true);
        hueValueSelector.setTranslateX(-hueValueSelector.getWidth() / 2.0);
        hueValueSelector.setTranslateY(-hueValueSelector.getHeight() / 2.0);
        hueValueSelector.setLayoutX(COLOR_BOX_SIZE / 2.0);
        hueValueSelector.setLayoutY(COLOR_BOX_SIZE / 2.0);
        hueValueSelector.setY((hueValueSelector.getHeight() - COLOR_BOX_SIZE) / 2.0);
        hueValueSelector.setStroke(Color.web(Constants.WHITE, 1.0));
        hueValueSelector.setCache(true);
        hueValueSelector.setManaged(false);
        hueValueSelector.setEffect(dropShadow());

        final EventHandler<MouseEvent> colorCircleMouseHandler = event -> {
            double yMouse = event.getY();
            double xMouse = event.getX();

            angle = (Math.toDegrees(Math.atan2(yMouse, xMouse)) + Constants.ROUND_ANGLE + Constants.RIGHT_ANGLE) % Constants.ROUND_ANGLE;
            hueProperty.set(angle);

            rectSelectorCoordinates();
            hueValueSelector.setLayoutX(rectX);
            hueValueSelector.setLayoutY(rectY);
            hueValueSelector.setRotate(angle);

            updateColor();
            event.consume();
        };

        hollowCircle.setOnMousePressed(colorCircleMouseHandler);
        hollowCircle.setOnMouseDragged(colorCircleMouseHandler);

        colorCircleContainer.getChildren().addAll(hollowCircle, hueValueSelector);
        getChildren().addAll(colorRectContainer, colorCircleContainer);
        prefHeightProperty().set(COLOR_BOX_SIZE + Constants.INSETS);

        //  clipBorderPane (Body) to be sure, that no content overlaps the pane
        clip.widthProperty().bind(widthProperty());
        clip.heightProperty().bind(heightProperty());
        setClip(clip);
        initEffectAndSwitch();
    }

    private void initEffectAndSwitch() {
        hueValueSelector.setY(0);
        rectSelectorCoordinates();
        hueValueSelector.setLayoutX(rectX);
        hueValueSelector.setLayoutY(rectY);
        hueValueSelector.setRotate(angle);
    }

    public void updateColor() {
        selectedColorProperty.setValue(Color.hsb(hueProperty.get(), saturationProperty.get(), brightnessProperty.get()));
    }

    @Override
    public void updateDynamicContent() {
        hueProperty.set(selectedColorProperty.get().getHue());
        angle = selectedColorProperty.get().getHue();
    }

    public ObjectProperty<Color> selectedColorProperty() {
        return selectedColorProperty;
    }

    public void setSelectedColor(final Color color) {
        if (!isHover()) {
            hueProperty.set(color.getHue());
            angle = color.getHue();
        }
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

    public Color getSelectedColor() {
        return selectedColorProperty.get();
    }

    private Pane saturationRect() {
        final Pane colorRectSaturation = new Pane();

        colorRectSaturation.setPrefSize(COLOR_BOX_SIZE, COLOR_BOX_SIZE);
//        colorRectSaturation.setMinSize(COLOR_BOX_SIZE, COLOR_BOX_SIZE);
        colorRectSaturation.setBackground(new Background(new BackgroundFill(new LinearGradient(0.0, 0.0, 1.0, 0.0, true,
            CycleMethod.NO_CYCLE, new Stop(0.0, Color.rgb(Constants.RGB255, Constants.RGB255,
                Constants.RGB255, 1.0)), new Stop(1, Color.rgb(Constants.RGB255, Constants.RGB255,
                Constants.RGB255, 0.0))), CornerRadii.EMPTY, Insets.EMPTY)));

        return colorRectSaturation;
    }

    private Pane brightnessRect() {
        final Pane colorRectBrightness = new Pane();

        colorRectBrightness.setPrefSize(COLOR_BOX_SIZE, COLOR_BOX_SIZE);
//        colorRectBrightness.setMinSize(COLOR_BOX_SIZE, COLOR_BOX_SIZE);
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
        rectX = Math.round(COLOR_BOX_SIZE / 2.0 + (COLOR_BOX_SIZE - hueValueSelector.getHeight()) / 2.0
            * Math.cos(Math.toRadians((angle + Constants.OBTUSE_ANGLE_270) % Constants.ROUND_ANGLE)));
        rectY = Math.round(COLOR_BOX_SIZE / 2.0 + (COLOR_BOX_SIZE - hueValueSelector.getHeight()) / 2.0
            * Math.sin(Math.toRadians((angle + Constants.OBTUSE_ANGLE_270) % Constants.ROUND_ANGLE)));
    }
}
