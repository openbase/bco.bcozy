/**
 * ==================================================================
 *
 * This file is part of org.openbase.bco.bcozy.
 *
 * org.openbase.bco.bcozy is free software: you can redistribute it and modify
 * it under the terms of the GNU General Public License (Version 3) as published
 * by the Free Software Foundation.
 *
 * org.openbase.bco.bcozy is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * org.openbase.bco.bcozy. If not, see <http://www.gnu.org/licenses/>.
 * ==================================================================
 */
package org.openbase.bco.bcozy.view.unitpanes;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.SVGIcon;
import org.openbase.bco.dal.remote.unit.UnitRemote;
import org.openbase.bco.dal.remote.unit.TemperatureControllerRemote;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.openbase.jul.pattern.Observable;
import org.openbase.jul.schedule.RecurrenceEventFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.unit.dal.TemperatureControllerDataType.TemperatureControllerData;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.openbase.jul.schedule.GlobalCachedExecutorService;
import rst.domotic.state.TemperatureStateType.TemperatureState;

/**
 * Created by agatting on 17.01.16.
 */
public class TemperatureControllerPane extends AbstractUnitPane {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemperatureControllerPane.class);

    private final TemperatureControllerRemote temperatureControllerRemote;
    private final SVGIcon temperatureControllerIcon;
    private final BorderPane headContent;
    private final VBox bodyContent;
    private final Slider slider;
    private final VBox vBox;
    private final Button actual;
    private final Button target;
    private final DecimalFormat decimalFormat;
    private double actualTemperature;
    private double targetTemperature;

    private RecurrenceEventFilter recurrenceEventFilter = new RecurrenceEventFilter(Constants.RECURRENCE_EVENT_FILTER_MILLI_TIMEOUT) {
        @Override
        public void relay() {
            sendTemperatureStateToRemote();
        }
    };

    private final EventHandler<MouseEvent> sendingTargetTemperature = event -> GlobalCachedExecutorService.submit(new Task() {
        @Override
        protected Object call() {
            recurrenceEventFilter.trigger();
            return null;
        }
    });

    /**
     * Constructor for a TemperatureControllerPane.
     *
     * @param temperatureControllerRemote temperatureControllerRemote
     */
    public TemperatureControllerPane(final UnitRemote temperatureControllerRemote) {
        this.temperatureControllerRemote = (TemperatureControllerRemote) temperatureControllerRemote;

        temperatureControllerIcon = new SVGIcon(MaterialDesignIcon.RADIATOR, Constants.SMALL_ICON, true);
        decimalFormat = new DecimalFormat("#.#");
        headContent = new BorderPane();
        bodyContent = new VBox();
        slider = new Slider();
        vBox = new VBox();
        actual = new Button();
        target = new Button();

        initUnitLabel();
        initTitle();
        initContent();
        createWidgetPane(headContent, bodyContent, false);
        initEffectSlider();
        tooltip.textProperty().bind(observerText.textProperty());

        addObserverAndInitDisableState(this.temperatureControllerRemote);
    }

    private void initEffectSlider() {
        actualTemperature = slider.getMin();
        targetTemperature = slider.getMin();
        slider.setValue(0.0);

        try {
            actualTemperature = temperatureControllerRemote.getTemperatureState().getTemperature();
            targetTemperature = temperatureControllerRemote.getTargetTemperatureState().getTemperature();
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
        target.setText(decimalFormat.format(targetTemperature) + Constants.CELSIUS);
    }

    private void sendTemperatureStateToRemote() {
        try {
            temperatureControllerRemote.setTargetTemperatureState(TemperatureState.newBuilder().setTemperature(slider.getValue()).build()).get(Constants.OPERATION_SERVICE_MILLI_TIMEOUT, TimeUnit.MILLISECONDS);

            final StackPane track = (StackPane) slider.lookup(".track");
            target.setTranslateX(track.getLayoutX());
        } catch (InterruptedException | ExecutionException | TimeoutException | CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER, LogLevel.ERROR);
        }
    }

    @Override
    protected void initTitle() {
        observerText.setIdentifier("heating");
        iconPane.add(temperatureControllerIcon, 0, 0);
        headContent.setCenter(getUnitLabel());
        headContent.setAlignment(getUnitLabel(), Pos.CENTER_LEFT);
        headContent.prefHeightProperty().set(temperatureControllerIcon.getSize() + Constants.INSETS);
    }

    @Override
    protected void initContent() {
        final double sliderWidth = 200;

        slider.setPrefHeight(25);
        slider.setMinHeight(25);
        slider.setMin(10);
        slider.setMax(35);
        slider.setMinWidth(sliderWidth);
        slider.setMaxWidth(sliderWidth);
        slider.getStyleClass().add("temperature-slider");
        actual.getStyleClass().addAll("temperature-slider-pane-top");
        target.getStyleClass().addAll("temperature-slider-pane-bottom");
        //CHECKSTYLE.ON: MagicNumber

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
    public UnitRemote getDALRemoteService() {
        return temperatureControllerRemote;
    }

    @Override
    void removeObserver() {
        this.temperatureControllerRemote.removeObserver(this);
    }

    @Override
    public void update(final Observable observable, final Object temperatureController) throws java.lang.Exception {
        Platform.runLater(() -> {
            actualTemperature
                    = ((TemperatureControllerData) temperatureController).getTemperatureState().getTemperature();
            targetTemperature
                    = ((TemperatureControllerData) temperatureController).getTargetTemperatureState().getTemperature();
            setLabelValues();
        });
    }
}
