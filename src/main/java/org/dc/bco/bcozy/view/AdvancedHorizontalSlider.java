/**
 * ==================================================================
 *
 * This file is part of org.dc.bco.bcozy.view.
 *
 * org.dc.bco.bcozy.view is free software: you can redistribute it and modify
 * it under the terms of the GNU General Public License (Version 3)
 * as published by the Free Software Foundation.
 *
 * org.dc.bco.bcozy.view is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with org.dc.bco.bcozy.view. If not, see <http://www.gnu.org/licenses/>.
 * ==================================================================
 */
package org.dc.bco.bcozy.view;

import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

/**
 * Created by hoestreich on 1/28/16.
 */
public class AdvancedHorizontalSlider extends Pane{

    private Rectangle sliderBackground;
    private Rectangle sliderActualStatus;
    private Circle thumb;
    private StackPane topLabelPane;
    private StackPane bottomLabelPane;
    private SVGIcon topLabelShape;
    private SVGIcon bottomLabelShape;
    private Label topLabel;
    private Label bottomLabel;
    private VBox verticalLayout;
    private StackPane slider;
    private double value;
    private double minValue;
    private double maxValue;

    public AdvancedHorizontalSlider(final double minValue, final double maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        sliderBackground = new Rectangle(200,5);
        sliderBackground.getStyleClass().add("advanced-horizontal-slider-background");
        sliderActualStatus = new Rectangle(0, 5);
        sliderActualStatus.getStyleClass().add("advanced-horizontal-slider-highlight");
        thumb = new Circle(10);
        thumb.getStyleClass().add("advanced-horizontal-slider-thumb");
        thumb.setFill(Color.SLATEGRAY);
        slider = new StackPane(sliderBackground, sliderActualStatus, thumb);
        slider.setOnMouseDragged(event -> addMouseEventHandler(event));
        slider.setOnMouseClicked(event -> addMouseEventHandler(event));
        slider.setAlignment(sliderBackground, Pos.CENTER_LEFT);
        slider.setAlignment(sliderActualStatus, Pos.CENTER_LEFT);
        slider.setAlignment(thumb, Pos.CENTER_LEFT);
        topLabelShape = new SVGIcon(MaterialIcon.CHAT_BUBBLE, Constants.MIDDLE_ICON, false);
        topLabel = new Label("");
        topLabelShape.getStyleClass().addAll("small-label", "advanced-horizontal-slider-label-top");
        bottomLabelShape = new SVGIcon(MaterialIcon.CHAT_BUBBLE, Constants.MIDDLE_ICON, false);
        bottomLabelShape.setRotate(180.0);
        bottomLabel = new Label("");
        bottomLabel.getStyleClass().add("small-label");
        bottomLabelShape.getStyleClass().add("advanced-horizontal-slider-label-bottom");
        topLabelPane = new StackPane(topLabelShape, topLabel);
        bottomLabelPane = new StackPane(bottomLabelShape, bottomLabel);
        verticalLayout = new VBox(topLabelPane, slider, bottomLabelPane);
        this.getChildren().add(verticalLayout);
    }

    private void addMouseEventHandler(MouseEvent event) {
        final double xValue = event.getX();
        if(xValue < sliderBackground.getWidth()+thumb.getRadius() //right border
                && xValue > (sliderBackground.getX() + (thumb.getRadius() / 2))) { //left border
            sliderActualStatus.setWidth(xValue);
            thumb.setTranslateX(xValue - thumb.getRadius());
            bottomLabel.setText(((maxValue - minValue) * xValue / sliderBackground.getWidth() + minValue)
                    + Constants.CELSIUS);
            bottomLabelPane.setTranslateX(xValue); //
        }
    }

}
