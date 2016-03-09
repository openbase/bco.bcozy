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
public class AdvancedHorizontalSlider extends Pane {

    private final Rectangle sliderBackground;
    private final Rectangle sliderActualStatus;
    private final Circle thumb;
    private final StackPane topLabelPane;
    private final StackPane bottomLabelPane;
    private final SVGIcon topLabelShape;
    private final SVGIcon bottomLabelShape;
    private final Label topLabel;
    private final Label bottomLabel;
    private final VBox verticalLayout;
    private final StackPane slider;
    private final double minValue;
    private final double maxValue;

    /**
     * Constructor for the AdvancedHorizontalSlider.
     * @param minValue minValue.
     * @param maxValue maxValue.
     */
    public AdvancedHorizontalSlider(final double minValue, final double maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        //CHECKSTYLE.OFF: MagicNumber
        sliderBackground = new Rectangle(200, 5);
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
        topLabelShape.getStyleClass().addAll(Constants.SMALL_LABEL, "advanced-horizontal-slider-label-top");
        bottomLabelShape = new SVGIcon(MaterialIcon.CHAT_BUBBLE, Constants.MIDDLE_ICON, false);
        bottomLabelShape.setRotate(180.0);
        //CHECKSTYLE.ON: MagicNumber
        bottomLabel = new Label("");
        bottomLabel.getStyleClass().add(Constants.SMALL_LABEL);
        bottomLabelShape.getStyleClass().add("advanced-horizontal-slider-label-bottom");
        topLabelPane = new StackPane(topLabelShape, topLabel);
        bottomLabelPane = new StackPane(bottomLabelShape, bottomLabel);
        verticalLayout = new VBox(topLabelPane, slider, bottomLabelPane);
        this.getChildren().add(verticalLayout);
    }

    private void addMouseEventHandler(final MouseEvent event) {
        final double xValue = event.getX();
        if (xValue < sliderBackground.getWidth() + thumb.getRadius() //right border
                && xValue > (sliderBackground.getX() + (thumb.getRadius() / 2))) { //left border
            sliderActualStatus.setWidth(xValue);
            thumb.setTranslateX(xValue - thumb.getRadius());
            bottomLabel.setText(((maxValue - minValue) * xValue / sliderBackground.getWidth() + minValue)
                    + Constants.CELSIUS);
            bottomLabelPane.setTranslateX(xValue); //
        }
    }

}
