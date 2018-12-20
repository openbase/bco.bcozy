/**
 * ==================================================================
 *
 * This file is part of org.openbase.bco.bcozy.
 *
 * org.openbase.bco.bcozy is free software: you can redistribute it and modify it
 * under the terms of the GNU General Public License (Version 3) as published by
 * the Free Software Foundation.
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
package org.openbase.bco.bcozy.view.pane.unit.backup;

import org.openbase.bco.bcozy.view.pane.unit.AbstractUnitPane;
import org.openbase.bco.dal.remote.layer.unit.DimmableLightRemote;
import org.openbase.type.domotic.unit.dal.DimmableLightDataType.DimmableLightData;

/**
 * Created by agatting on 12.01.16.
 */
public class DimmableLightPane extends AbstractUnitPane<DimmableLightRemote, DimmableLightData> {

//    private static final Logger LOGGER = LoggerFactory.getLogger(DimmableLightPane.class);
//
//    private final SVGIcon unknownForegroundIcon;
//    private final SVGIcon unknownBackgroundIcon;
//    private final ProgressBar progressBar;
//    private final BorderPane headContent;
//    private final SVGIcon lightBulbIcon;
//    private final StackPane stackPane;
//    private final VBox bodyContent;
//    private final Slider slider;
//
//    private RecurrenceEventFilter recurrenceEventFilter = new RecurrenceEventFilter(Constants.RECURRENCE_EVENT_FILTER_MILLI_TIMEOUT) {
//        @Override
//        public void relay() {
//            sendBrightnessStateToRemote();
//        }
//    };
//
//    private final EventHandler<MouseEvent> sendBrightness = event -> GlobalCachedExecutorService.submit(new Task() {
//        @Override
//        protected Object call() {
//            recurrenceEventFilter.trigger();
//            return null;
//        }
//    });
//
//    /**
//     * Constructor for the DimmerPane.
//     *
//     */
    public DimmableLightPane() {
        super(DimmableLightRemote.class, true);
//
//        lightBulbIcon = new SVGIcon(MaterialDesignIcon.LIGHTBULB, MaterialDesignIcon.LIGHTBULB_OUTLINE, JFXConstants.ICON_SIZE_SMALL);
//        unknownBackgroundIcon = new SVGIcon(MaterialDesignIcon.CHECKBOX_BLANK_CIRCLE, JFXConstants.ICON_SIZE_SMALL - 2, false);
//        unknownForegroundIcon = new SVGIcon(MaterialDesignIcon.HELP_CIRCLE, JFXConstants.ICON_SIZE_SMALL, false);
//        progressBar = new ProgressBar();
//        headContent = new BorderPane();
//        stackPane = new StackPane();
//        bodyContent = new VBox();
//        slider = new Slider();
//
//        initTitle();
//        initBodyContent();
//        initWidgetPane(headContent, bodyContent, true);
//        initEffectAndSwitch();
//        tooltip.textProperty().bind(labelText.textProperty());
    }
//
//    private void initEffectAndSwitch() {
//        State powerState = State.OFF;
//        double brightness = 0.0;
//
//        try {
//            powerState = dimmableLightRemote.getPowerState().getValue();
//            brightness = dimmableLightRemote.getBrightnessState().getBrightness() / Constants.ONE_HUNDRED;
//        } catch (CouldNotPerformException ex) {
//            ExceptionPrinter.printHistory(ex, LOGGER, LogLevel.ERROR);
//        }
//        setEffectColorAndSlider(powerState, brightness);
//    }
//
//    private void setEffectColorAndSlider(final State powerState, final double brightness) {
//        iconPane.getChildren().clear();
//
//        if (powerState.equals(State.ON)) {
//            iconPane.add(lightBulbIcon, 0, 0);
//
//            final Color color = Color.hsb(Constants.LIGHTBULB_COLOR.getHue(),
//                    Constants.LIGHTBULB_COLOR.getSaturation(), brightness, Constants.LIGHTBULB_COLOR.getOpacity());
//            lightBulbIcon.setBackgroundIconColorAnimated(color);
//            progressBar.setProgress(brightness);
//            slider.setValue(brightness * slider.getMax());
//
//            labelText.setIdentifier("lightOn");
//
//            if (!toggleSwitch.isSelected()) {
//                toggleSwitch.setSelected(true);
//            }
//
//        } else if (powerState.equals(State.OFF)) {
//            iconPane.add(lightBulbIcon, 0, 0);
//
//            lightBulbIcon.setBackgroundIconColorAnimated(Color.TRANSPARENT);
//            progressBar.setProgress(0);
//            slider.setValue(0);
//
//            labelText.setIdentifier("lightOff");
//
//            if (toggleSwitch.isSelected()) {
//                toggleSwitch.setSelected(false);
//            }
//        } else {
//            iconPane.add(unknownBackgroundIcon, 0, 0);
//            iconPane.add(unknownForegroundIcon, 0, 0);
//            labelText.setIdentifier("unknown");
//        }
//    }
//
//    private void sendStateToRemote(final State state) {
//        try {
//            dimmableLightRemote.setPowerState(state).get(Constants.OPERATION_SERVICE_MILLI_TIMEOUT, TimeUnit.MILLISECONDS);
//        } catch (InterruptedException | ExecutionException | TimeoutException | CouldNotPerformException ex) {
//            ExceptionPrinter.printHistory(ex, LOGGER, LogLevel.ERROR);
//        }
//    }
//
//    private void sendBrightnessStateToRemote() {
//        try {
//            dimmableLightRemote.setBrightnessState(BrightnessState.newBuilder().setBrightness(slider.getValue()).build()).get(Constants.OPERATION_SERVICE_MILLI_TIMEOUT, TimeUnit.MILLISECONDS);
//        } catch (InterruptedException | ExecutionException | TimeoutException | CouldNotPerformException ex) {
//            ExceptionPrinter.printHistory(ex, LOGGER, LogLevel.ERROR);
//        }
//    }
//
//    private void initTitle() {
//        lightBulbIcon.setBackgroundIconColorAnimated(Color.TRANSPARENT);
//
//        isClickedProperty.addListener((observable, oldValue, newValue) -> GlobalCachedExecutorService.submit(new Task() {
//            @Override
//            protected Object call() {
//                if (toggleSwitch.isSelected()) {
//                    sendStateToRemote(PowerState.State.OFF);
//                } else {
//                    sendStateToRemote(PowerState.State.ON);
//                }
//                return null;
//            }
//        }));
//
//        toggleSwitch.setOnMouseClicked(event -> GlobalCachedExecutorService.submit(new Task() {
//            @Override
//            protected Object call() {
//                if (toggleSwitch.isSelected()) {
//                    sendStateToRemote(PowerState.State.ON);
//                } else {
//                    sendStateToRemote(PowerState.State.OFF);
//                }
//                return null;
//            }
//        }));
//
//        unknownForegroundIcon.setForegroundIconColor(Color.BLUE);
//        unknownBackgroundIcon.setForegroundIconColor(Color.WHITE);
//
//        headContent.setCenter(getUnitLabel());
//        headContent.setAlignment(getUnitLabel(), Pos.CENTER_LEFT);
//        headContent.prefHeightProperty().set(lightBulbIcon.getSize() + Constants.INSETS);
//    }
//
//    @Override
//    protected void initBodyContent() {
//        //CHECKSTYLE.OFF: MagicNumber
//        final double sliderWidth = 200;
//
//        slider.setPrefHeight(25);
//        slider.setMinHeight(25);
//        //CHECKSTYLE.ON: MagicNumber
//        slider.setMin(0);
//        slider.setMax(Constants.ONE_HUNDRED);
//        slider.setMinWidth(sliderWidth);
//        slider.setMaxWidth(sliderWidth);
//
//        slider.setOnMouseDragged(sendBrightness);
//        slider.setOnMouseClicked(sendBrightness);
//
//        progressBar.setMinWidth(sliderWidth);
//        progressBar.setMaxWidth(sliderWidth);
//
//        stackPane.getStyleClass().clear();
//        stackPane.getStyleClass().add("dimmer-body");
//        stackPane.getChildren().addAll(progressBar, slider);
//
//        bodyContent.getChildren().add(stackPane);
//        bodyContent.prefHeightProperty().set(slider.getPrefHeight() + Constants.INSETS);
//    }
//
//
//    @Override
//    public void update(final Observable observable, final Object dimmer) throws java.lang.Exception {
//        Platform.runLater(() -> {
//            final State powerState = ((DimmableLightData) dimmer).getPowerState().getValue();
//            final double brightness = ((DimmableLightData) dimmer).getBrightnessState().getBrightness() / Constants.ONE_HUNDRED;
//            setEffectColorAndSlider(powerState, brightness);
//        });
//    }
}
