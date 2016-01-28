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


import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.dc.bco.bcozy.view.Constants;
import org.dc.bco.bcozy.view.SVGIcon;
import org.dc.bco.dal.remote.unit.DALRemoteService;
import org.dc.bco.dal.remote.unit.TemperatureControllerRemote;
import org.dc.jul.exception.CouldNotPerformException;
import org.dc.jul.exception.printer.ExceptionPrinter;
import org.dc.jul.exception.printer.LogLevel;
import org.dc.jul.pattern.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.homeautomation.unit.TemperatureControllerType;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by agatting on 17.01.16.
 */
public class TemperatureControllerPane extends UnitPane {
    private static final Logger LOGGER = LoggerFactory.getLogger(TemperatureControllerPane.class);

    private final TemperatureControllerRemote temperatureControllerRemote;
    private final SVGIcon temperatureControllerIcon;
    private final BorderPane headContent;
    private final VBox bodyContent;
    private final Slider slider;
    private final VBox vBox;
    private final Label actual;
    private final Label target;
    private final SVGIcon triangleIconActual;
    private final SVGIcon triangleIconTarget;
    private final DecimalFormat decimalFormat;
    private double actualTemperature;
    private double targetTemperature;

    /**
     * Constructor for a TemperatureControllerPane.
     * @param temperatureControllerRemote DALRemoteService
     */
    public TemperatureControllerPane(final DALRemoteService temperatureControllerRemote) {
        this.temperatureControllerRemote = (TemperatureControllerRemote) temperatureControllerRemote;

        temperatureControllerIcon = new SVGIcon(MaterialDesignIcon.RADIATOR, Constants.SMALL_ICON, true);
        triangleIconActual = new SVGIcon(FontAwesomeIcon.CARET_DOWN, Constants.SMALL_ICON, true);
        triangleIconTarget = new SVGIcon(FontAwesomeIcon.CARET_UP, Constants.SMALL_ICON, true);
        headContent = new BorderPane();
        bodyContent = new VBox();
        slider = new Slider();
        vBox = new VBox();
        actual = new Label();
        target = new Label();
        decimalFormat = new DecimalFormat("#.#");

        initUnitLabel();
        initTitle();
        initContent();
        createWidgetPane(headContent, bodyContent);
        initEffectSlider();

        this.temperatureControllerRemote.addObserver(this);
    }

    private void initEffectSlider() {
        actualTemperature = slider.getMin();
        targetTemperature = slider.getMin();
        slider.setValue(0.0);

        try {
            actualTemperature = temperatureControllerRemote.getTemperature();
            targetTemperature = temperatureControllerRemote.getTargetTemperature();
            slider.setValue(targetTemperature);
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
        setLabelValues();
    }

    private void setLabelValues() {
        decimalFormat.setRoundingMode(RoundingMode.CEILING);
        //CHECKSTYLE.OFF: MagicNumber
        actualTemperature = (actualTemperature < 10.0) ? 10.0 : actualTemperature;
        targetTemperature = (targetTemperature < 10.0) ? 10.0 : targetTemperature;

        actual.setText("Actual: " + decimalFormat.format(actualTemperature) + Constants.CELSIUS);
        actual.setTranslateX((actualTemperature - slider.getMin())
                * (slider.getMinWidth() / (slider.getMax() - slider.getMin())) - (slider.getMinWidth() / 2));
        target.setText("Target: " + decimalFormat.format(targetTemperature) + Constants.CELSIUS);
        target.setTranslateX((targetTemperature - slider.getMin())
                * (slider.getMinWidth() / (slider.getMax() - slider.getMin())) - (slider.getMinWidth() / 2));
    }

    @Override
    protected void initTitle() {
        headContent.setLeft(temperatureControllerIcon);
        headContent.setCenter(getUnitLabel());
        headContent.setAlignment(getUnitLabel(), Pos.CENTER_LEFT);
        headContent.prefHeightProperty().set(temperatureControllerIcon.getSize() + Constants.INSETS);
    }

    @Override
    protected void initContent() {
        //CHECKSTYLE.OFF: MagicNumber
        final double sliderWidth = 200;

        slider.setPrefHeight(25);
        slider.setMinHeight(25);
        slider.setMin(10);
        slider.setMax(35);
        slider.setMinWidth(sliderWidth);
        slider.setMaxWidth(sliderWidth);
        slider.getStyleClass().add("temperature-slider");

        actual.setGraphic(triangleIconActual);
        actual.setContentDisplay(ContentDisplay.BOTTOM);
        actual.setGraphicTextGap(-12.0);
        actual.setTranslateX(-slider.getMinWidth() / 2);
        target.setGraphic(triangleIconTarget);
        target.setContentDisplay(ContentDisplay.TOP);
        target.setGraphicTextGap(-12.0);
        target.setTranslateX(-slider.getMinWidth() / 2);
        //CHECKSTYLE.ON: MagicNumber

        //TODO add progressbar in slider...?

        final EventHandler<MouseEvent> sendingTargetTemperature = event -> new Thread(new Task() {
            @Override
            protected Object call() {
                try {
                    temperatureControllerRemote.setTargetTemperature(slider.getValue());
                } catch (CouldNotPerformException e) {
                    ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                }
                return null;
            }
        }).start();

        slider.setOnMousePressed(sendingTargetTemperature);
        slider.setOnMouseDragged(sendingTargetTemperature);

        vBox.getChildren().addAll(actual, slider, target);
        vBox.setAlignment(Pos.CENTER);

        bodyContent.getChildren().addAll(vBox);
        //CHECKSTYLE.OFF: MagicNumber
        bodyContent.prefHeightProperty().set(150 + Constants.INSETS);
        //CHECKSTYLE.ON: MagicNumber
    }

    @Override
    protected void initUnitLabel() {
        String unitLabel = Constants.UNKNOWN_ID;
        try {
            unitLabel = this.temperatureControllerRemote.getData().getLabel();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
        setUnitLabelString(unitLabel);
    }

    @Override
    public DALRemoteService getDALRemoteService() {
        return temperatureControllerRemote;
    }

    @Override
    void removeObserver() {
        this.temperatureControllerRemote.removeObserver(this);
    }

    @Override
    public void update(final Observable observable, final Object temperatureController) throws java.lang.Exception {
        Platform.runLater(() -> {
            actualTemperature =
                    ((TemperatureControllerType.TemperatureController) temperatureController).getActualTemperature();
            targetTemperature =
                    ((TemperatureControllerType.TemperatureController) temperatureController).getTargetTemperature();
            setLabelValues();
        });

    }
}
