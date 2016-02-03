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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
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

    private final DimmerRemote dimmerRemote;
    private final SVGIcon lightBulbIcon;
    private final ToggleSwitch toggleSwitch;
    private final BorderPane headContent;
    private final VBox bodyContent;
    private final Slider slider;
    private final ProgressBar progressBar;
    private final StackPane stackPane;

    /**
     * Constructor for the DimmerPane.
     * @param dimmerRemote dimmerRemote.
     */
    public DimmerPane(final AbstractIdentifiableRemote dimmerRemote) {
        this.dimmerRemote = (DimmerRemote) dimmerRemote;

        toggleSwitch = new ToggleSwitch();
        progressBar = new ProgressBar();
        slider = new Slider();
        lightBulbIcon =
                new SVGIcon(MaterialDesignIcon.LIGHTBULB, MaterialDesignIcon.LIGHTBULB_OUTLINE, Constants.SMALL_ICON);
        headContent = new BorderPane();
        bodyContent = new VBox();
        stackPane = new StackPane();

        initUnitLabel();
        initTitle();
        initContent();
        createWidgetPane(headContent, bodyContent);

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
        if (powerState.equals(State.ON)) {
            final Color color = Color.hsb(Constants.LIGHTBULB_COLOR.getHue(),
                    Constants.LIGHTBULB_COLOR.getSaturation(), brightness, Constants.LIGHTBULB_COLOR.getOpacity());
            lightBulbIcon.setBackgroundIconColorAnimated(color);
            slider.setValue(brightness * slider.getMax());
            progressBar.setProgress(brightness);

            if (!toggleSwitch.isSelected()) {
                toggleSwitch.setSelected(true);
            }
        } else {
            lightBulbIcon.setBackgroundIconColorAnimated(Color.TRANSPARENT);
            slider.setValue(0);
            progressBar.setProgress(0);

            if (toggleSwitch.isSelected()) {
                toggleSwitch.setSelected(false);
            }
        }
    }

    /**
     * Method creates the header content of the widgetPane.
     */
    @Override
    protected void initTitle() {
        lightBulbIcon.setBackgroundIconColorAnimated(Color.TRANSPARENT);
        toggleSwitch.setOnMouseClicked(event -> {
            new Thread(new Task() {
                @Override
                protected Object call() throws java.lang.Exception {
                    if (toggleSwitch.isSelected()) {
                        try {
                            dimmerRemote.setPower(PowerStateType.PowerState.State.ON);
                        } catch (CouldNotPerformException e) {
                            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                        }
                    } else {
                        try {
                            dimmerRemote.setPower(PowerStateType.PowerState.State.OFF);
                        } catch (CouldNotPerformException e) {
                            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                        }
                    }
                    return null;
                }
            }).start();
        });

        headContent.setLeft(lightBulbIcon);
        headContent.setCenter(getUnitLabel());
        headContent.setAlignment(getUnitLabel(), Pos.CENTER_LEFT);
        headContent.setRight(toggleSwitch);
        headContent.prefHeightProperty().set(lightBulbIcon.getSize() + Constants.INSETS);
    }

    /**
     * Method creates the body content of the widgetPane.
     */
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

        progressBar.setMinWidth(sliderWidth);
        progressBar.setMaxWidth(sliderWidth);

        final EventHandler<MouseEvent> sendingBrightness = event -> new Thread(new Task() {
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

        slider.setOnMouseDragged(sendingBrightness);
        slider.setOnMouseClicked(sendingBrightness);

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
