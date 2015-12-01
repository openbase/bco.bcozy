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

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import org.controlsfx.control.ToggleSwitch;
import org.dc.bco.bcozy.view.ImageEffect;

/**
 * Created by agatting on 25.11.15.
 */
public class LightBulbPane extends VBox {

    /**
     *
     */
    public LightBulbPane() {

        //SvgImageLoaderFactory.install();

        final Button lightBulb = new Button();
        lightBulb.setBackground(Background.EMPTY);

        final Image bottomImage = new Image("/icons/lightbulb_mask.svg");
        final Image topImage = new Image("/icons/lightbulb.svg", 512, 512, false, false);


        final ImageView lightOff = new ImageView(topImage);

        //lightOff.setTranslateX(-lightOff.getFitWidth()/2);
        //lightOff.setTranslateY(-lightOff.getFitHeight()/2);
        //lightOff.setScaleX(0.1);
        //lightOff.setScaleY(0.1);
        //lightOff.setTranslateX(lightOff.getFitWidth()/20);
        //lightOff.setTranslateY(lightOff.getFitHeight()/20);
        lightOff.setClip(new ImageView(topImage));

        final Group imageEffect = new ImageEffect().imageBlendEffect(bottomImage, topImage, Color.YELLOW);

        final TitledPane titledPane = widgetPaneElement(lightOff, imageEffect, "Lightbulb");

        this.getChildren().add(titledPane);
    }

    /**
     *
     * @param imageOff Off Icon
     * @param imageOn On Icon
     * @param title Title of the widget
     * @return widgetPane TitlePane is the whole widget
     */
    public TitledPane widgetPaneElement(final ImageView imageOff, final Group imageOn, final String title) {

        final TitledPane widgetPane = new TitledPane();
        widgetPane.getStyleClass().add("widgetPane");

        final ToggleSwitch toggleSwitch = new ToggleSwitch();

        //imageOff.setFitHeight(400);
        //imageOff.setFitWidth(400);

        final BorderPane borderPane = new BorderPane();

        borderPane.setLeft(imageOff);
        //TODO change image
        borderPane.setCenter(new Label(title));
        //Label label = new Label(" ");
        //borderPane.setCenter(label);
        borderPane.setRight(toggleSwitch);
        widgetPane.setGraphic(borderPane);




        final BorderPane borderPane1 = new BorderPane();

        final ColorPicker colorPicker = new ColorPicker();
        colorPicker.setValue(Color.BLUE);

        borderPane1.setLeft(colorPicker);

        //final PaneElement paneElement = new PaneElement(borderPane1);


        //...

        final HBox hBox = new HBox();

        final Pane colorRectangleContainer = new StackPane();
        final DoubleProperty hueProperty = new SimpleDoubleProperty(-1);


        //CHECKSTYLE.OFF: MagicNumber
        // color rectangle wide: hue/saturation
        final Pane colorRectangleWide = new Pane();
        //TODO size generic...
        colorRectangleWide.setPrefSize(200, 200);
        colorRectangleWide.setBackground(new Background(new BackgroundFill(new LinearGradient(0, 0, 1, 0, true,
                CycleMethod.NO_CYCLE, new Stop(0, Color.rgb(255, 255, 255, 1)),
                new Stop(1, Color.rgb(255, 255, 255, 0))), CornerRadii.EMPTY, Insets.EMPTY)));

        // color rectangle high: hue/saturation
        final Pane colorRectangleHigh = new Pane();
        //TODO size generic...
        colorRectangleHigh.setPrefSize(200, 200);
        colorRectangleHigh.setBackground(new Background(new BackgroundFill(new LinearGradient(0, 0, 0, 1, true,
                CycleMethod.NO_CYCLE, new Stop(0, Color.rgb(0, 0, 0, 0)), new Stop(1, Color.rgb(0, 0, 0, 1))),
                CornerRadii.EMPTY, Insets.EMPTY)));

        final Pane colorHue = new Pane();
        colorHue.backgroundProperty().bind(new ObjectBinding<Background>() {
            {
                bind(hueProperty);
            }

            @Override protected Background computeValue() {
                return new Background(new BackgroundFill(Color.hsb(hueProperty.getValue(), 1.0, 1.0),
                        CornerRadii.EMPTY, Insets.EMPTY));
            }
        });

        // create the colorbar
        double offset;
        Stop[] stop = new Stop[255];
        for (int i = 0; i < 255; i++) {
            offset = (double) ((1.0 / 255) * i);
            final int hue = (int) ((i / 255.0) * 360);
            stop[i] = new Stop(offset, Color.hsb(hue, 1.0, 1.0));
        }

        final LinearGradient hueLinearGradient = new LinearGradient(0f, 0f, 0f, 1f, true, CycleMethod.REFLECT, stop);

        final Pane colorBar = new Pane();
        //TODO size generic...
        colorBar.setPrefSize(20, 200);
        colorBar.setBackground(new Background(new BackgroundFill(hueLinearGradient, CornerRadii.EMPTY,
                Insets.EMPTY)));


        // new color Area
        final Pane newColorArea = new Pane();
        newColorArea.setPrefSize(100, 100);
        newColorArea.setPadding(new Insets(0, 0, 0, 0));
        //TODO get color value
        newColorArea.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY,
                Insets.EMPTY)));

        // old color Area
        final Pane oldColorArea = new Pane();
        oldColorArea.setPrefSize(100, 100);
        oldColorArea.setPadding(new Insets(0, 0, 0, 0));
        //TODO get color value
        oldColorArea.setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY,
                Insets.EMPTY)));

        final VBox vBox = new VBox();
        vBox.getChildren().addAll(oldColorArea, newColorArea);






        //TODO all rectangles/areas with borders?

        colorRectangleContainer.getChildren().addAll(colorHue, colorRectangleWide, colorRectangleHigh);
        colorRectangleContainer.setPadding(new Insets(0, 10, 0, 10));
        colorBar.setPadding(new Insets(0, 10, 0, 10));
        vBox.setPadding(new Insets(0, 10, 0, 10));

        hBox.setPadding(new Insets(0, 10, 0, 10));
        hBox.getChildren().addAll(colorRectangleContainer, colorBar, vBox);
        //...

        //final ShutterPane shutterPane = new ShutterPane(new ShutterInstance("Shutter Living", 50.0));
        widgetPane.setContent(hBox);
        //CHECKSTYLE.ON: MagicNumber


        return widgetPane;
    }


}
