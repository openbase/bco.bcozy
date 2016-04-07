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

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.controlsfx.control.ToggleSwitch;
import org.dc.bco.bcozy.view.Constants;
import org.dc.bco.bcozy.view.SVGIcon;
import org.dc.jul.extension.rsb.com.AbstractIdentifiableRemote;
import org.dc.bco.dal.remote.unit.DimmerRemote;
import org.dc.jul.exception.CouldNotPerformException;
import org.dc.jul.exception.printer.ExceptionPrinter;
import org.dc.jul.exception.printer.LogLevel;
import org.dc.jul.pattern.Observable;
import org.dc.jul.schedule.RecurrenceEventFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.homeautomation.state.PowerStateType;
import rst.homeautomation.state.PowerStateType.PowerState.State;
import rst.homeautomation.unit.DimmerType;

/**
 * Created by agatting on 12.01.16.
 */
public class DimmerPane extends UnitPane {
    private static final Logger LOGGER = LoggerFactory.getLogger(DimmerPane.class);

    private RecurrenceEventFilter recurrenceEventFilter;
    private final SVGIcon unknownForegroundIcon;
    private final SVGIcon unknownBackgroundIcon;
    private final DimmerRemote dimmerRemote;
    private final ToggleSwitch toggleSwitch;
    private final ProgressBar progressBar;
    private final BorderPane headContent;
    private final SVGIcon lightBulbIcon;
    private final StackPane stackPane;
    private final GridPane iconPane;
    private final VBox bodyContent;
    private final Tooltip tooltip;
    private final Slider slider;

    private final EventHandler<MouseEvent> sendBrightness = event -> new Thread(new Task() {
        @Override
        protected Object call() {
            try {
                dimmerRemote.setDim(slider.getValue());
            } catch (CouldNotPerformException e) {
                ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
            }
            return null;
        }
    }).start();

    /**
     * Constructor for the DimmerPane.
     * @param dimmerRemote dimmerRemote.
     */
    public DimmerPane(final AbstractIdentifiableRemote dimmerRemote) {
        this.dimmerRemote = (DimmerRemote) dimmerRemote;

        lightBulbIcon =
                new SVGIcon(MaterialDesignIcon.LIGHTBULB, MaterialDesignIcon.LIGHTBULB_OUTLINE, Constants.SMALL_ICON);
        unknownBackgroundIcon = new SVGIcon(MaterialDesignIcon.CHECKBOX_BLANK_CIRCLE, Constants.SMALL_ICON - 2, false);
        unknownForegroundIcon = new SVGIcon(MaterialDesignIcon.HELP_CIRCLE, Constants.SMALL_ICON, false);
        toggleSwitch = new ToggleSwitch();
        progressBar = new ProgressBar();
        headContent = new BorderPane();
        stackPane = new StackPane();
        iconPane = new GridPane();
        bodyContent = new VBox();
        tooltip = new Tooltip();
        slider = new Slider();

        initUnitLabel();
        initTitle();
        initContent();
        createWidgetPane(headContent, bodyContent, true);
        initEffectAndSwitch();

        this.dimmerRemote.addObserver(this);
    }

    private void initEffectAndSwitch() {
        State powerState = State.OFF;
        double brightness = 0.0;

        try {
            powerState = dimmerRemote.getPower().getValue();
            brightness = dimmerRemote.getDim() / Constants.ONE_HUNDRED;
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
        setEffectColorAndSlider(powerState, brightness);
    }

    private void setEffectColorAndSlider(final State powerState, final double brightness) {
        iconPane.getChildren().clear();

        if (powerState.equals(State.ON)) {
            iconPane.add(lightBulbIcon, 0, 0);

            final Color color = Color.hsb(Constants.LIGHTBULB_COLOR.getHue(),
                    Constants.LIGHTBULB_COLOR.getSaturation(), brightness, Constants.LIGHTBULB_COLOR.getOpacity());
            lightBulbIcon.setBackgroundIconColorAnimated(color);
            progressBar.setProgress(brightness);
            slider.setValue(brightness * slider.getMax());

            tooltip.setText(Constants.LIGHT_ON);

            if (!toggleSwitch.isSelected()) {
                toggleSwitch.setSelected(true);
            }

        } else if (powerState.equals(State.OFF)) {
            iconPane.add(lightBulbIcon, 0, 0);

            lightBulbIcon.setBackgroundIconColorAnimated(Color.TRANSPARENT);
            progressBar.setProgress(0);
            slider.setValue(0);

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

    private void sendStateToRemote(final State state) {
        try {
            dimmerRemote.setPower(state);
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
            setWidgetPaneDisable(true);
        }
    }

    @Override
    protected void initTitle() {
        lightBulbIcon.setBackgroundIconColorAnimated(Color.TRANSPARENT);

        getOneClick().addListener((observable, oldValue, newValue) -> new Thread(new Task() {
            @Override
            protected Object call() {
                if (toggleSwitch.isSelected()) {
                    sendStateToRemote(PowerStateType.PowerState.State.OFF);
                } else {
                    sendStateToRemote(PowerStateType.PowerState.State.ON);
                }
                return null;
            }
        }).start());

        toggleSwitch.setOnMouseClicked(event -> new Thread(new Task() {
            @Override
            protected Object call() {
                if (toggleSwitch.isSelected()) {
                    sendStateToRemote(PowerStateType.PowerState.State.ON);
                } else {
                    sendStateToRemote(PowerStateType.PowerState.State.OFF);
                }
                return null;
            }
        }).start());

        unknownForegroundIcon.setForegroundIconColor(Color.BLUE);
        unknownBackgroundIcon.setForegroundIconColor(Color.WHITE);

        headContent.setLeft(iconPane);
        headContent.setCenter(getUnitLabel());
        headContent.setAlignment(getUnitLabel(), Pos.CENTER_LEFT);
        headContent.setRight(toggleSwitch);
        headContent.prefHeightProperty().set(lightBulbIcon.getSize() + Constants.INSETS);
    }

    @Override
    protected void initContent() {
        //CHECKSTYLE.OFF: MagicNumber
        final double sliderWidth = 200;

        slider.setPrefHeight(25);
        slider.setMinHeight(25);
        //CHECKSTYLE.ON: MagicNumber
        slider.setMin(0);
        slider.setMax(Constants.ONE_HUNDRED);
        slider.setMinWidth(sliderWidth);
        slider.setMaxWidth(sliderWidth);

        this.recurrenceEventFilter =  new RecurrenceEventFilter(Constants.FILTER_TIME) {
            @Override
            public void relay() {
                slider.setOnMouseDragged(sendBrightness);
                slider.setOnMouseClicked(sendBrightness);
            }
        };
        recurrenceEventFilter.trigger();

        progressBar.setMinWidth(sliderWidth);
        progressBar.setMaxWidth(sliderWidth);

        stackPane.getStyleClass().clear();
        stackPane.getStyleClass().add("dimmer-body");
        stackPane.getChildren().addAll(progressBar, slider);

        bodyContent.getChildren().add(stackPane);
        bodyContent.prefHeightProperty().set(slider.getPrefHeight() + Constants.INSETS);
    }

    @Override
    protected void initUnitLabel() {
        String unitLabel = Constants.UNKNOWN_ID;
        try {
            unitLabel = this.dimmerRemote.getData().getLabel();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
        setUnitLabelString(unitLabel);
    }

    @Override
    public AbstractIdentifiableRemote getDALRemoteService() {
        return dimmerRemote;
    }

    @Override
    void removeObserver() {
        this.dimmerRemote.removeObserver(this);
    }

    @Override
    public void update(final Observable observable, final Object dimmer) throws java.lang.Exception {
        Platform.runLater(() -> {
            final State powerState = ((DimmerType.Dimmer) dimmer).getPowerState().getValue();
            final double brightness = ((DimmerType.Dimmer) dimmer).getValue() / Constants.ONE_HUNDRED;
            setEffectColorAndSlider(powerState, brightness);
        });
    }
}
