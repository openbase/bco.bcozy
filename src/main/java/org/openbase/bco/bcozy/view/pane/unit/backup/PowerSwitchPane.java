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
package org.openbase.bco.bcozy.view.pane.unit.backup;

import org.openbase.bco.bcozy.view.pane.unit.AbstractUnitPane;
import org.openbase.bco.dal.remote.unit.PowerSwitchRemote;
import rst.domotic.unit.dal.PowerSwitchDataType.PowerSwitchData;

/**
 * Created by tmichalski on 08.01.16.
 */
public class PowerSwitchPane extends AbstractUnitPane<PowerSwitchRemote, PowerSwitchData> {
//    private static final Logger LOGGER = LoggerFactory.getLogger(PowerSwitchPane.class);
//
//    private final PowerSwitchRemote powerSwitchRemote;
//    private final SVGIcon powerPlugIcon;
//    private final SVGIcon powerStatusIcon;
//    private final SVGIcon unknownForegroundIcon;
//    private final SVGIcon unknownBackgroundIcon;
//    private final BorderPane headContent;
//
//    /**
//     * Constructor for the PowerPlugPane.
//     * @param powerPlugRemote powerPlugRemote
//     */
    public PowerSwitchPane() {
        super(PowerSwitchRemote.class, true);
//        headContent = new BorderPane();
//        powerPlugIcon = new SVGIcon(FontAwesomeIcon.PLUG, Constants.SMALL_ICON, true);
//        powerStatusIcon = new SVGIcon(FontAwesomeIcon.BOLT, Constants.EXTRA_SMALL_ICON, false);
//        unknownBackgroundIcon = new SVGIcon(MaterialDesignIcon.CHECKBOX_BLANK_CIRCLE, Constants.SMALL_ICON - 2, false);
//        unknownForegroundIcon = new SVGIcon(MaterialDesignIcon.HELP_CIRCLE, Constants.SMALL_ICON, false);
//
//        initTitle();
//        initBodyContent();
//        createWidgetPane(headContent, true);
//        initEffectAndSwitch();
//        tooltip.textProperty().bind(labelText.textProperty());
//
//        addObserverAndInitDisableState(this.powerSwitchRemote);
    }
//
//    private void initEffectAndSwitch() {
//        State powerState = State.OFF;
//
//        try {
//            powerState = powerSwitchRemote.getPowerState().getValue();
//        } catch (CouldNotPerformException e) {
//            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
//        }
//        setPowerStateSwitchAndIcon(powerState);
//    }
//
//    private void setPowerStateSwitchAndIcon(final State powerState) {
//        iconPane.getChildren().clear();
//
//        switch (powerState) {
//            case ON:
//                powerStatusIcon.setForegroundIconColor(Color.YELLOW, Color.BLACK, Constants.THIN_STROKE);
//                iconPane.add(powerPlugIcon, 1, 0, 3, 2);
//                labelText.setIdentifier("powerOn");
//                if (!toggleSwitch.isSelected()) {
//                    toggleSwitch.setSelected(true);
//                }   break;
//            case OFF:
//                powerStatusIcon.setForegroundIconColor(Color.TRANSPARENT);
//                iconPane.add(powerPlugIcon, 1, 0, 3, 2);
//                labelText.setIdentifier("powerOff");
//                if (toggleSwitch.isSelected()) {
//                    toggleSwitch.setSelected(false);
//                }   break;
//            default:
//                powerStatusIcon.setForegroundIconColor(Color.TRANSPARENT);
//                iconPane.add(unknownBackgroundIcon, 1, 0, 3, 2);
//                iconPane.add(unknownForegroundIcon, 1, 0, 3, 2);
//                labelText.setIdentifier("unknown");
//                break;
//        }
//        iconPane.add(powerStatusIcon, 0, 0);
//    }
//
//    private void sendStateToRemote(final State state) {
//        try {
//            powerSwitchRemote.setPowerState(state).get(Constants.OPERATION_SERVICE_MILLI_TIMEOUT, TimeUnit.MILLISECONDS);
//        } catch (InterruptedException | ExecutionException | TimeoutException | CouldNotPerformException ex) {
//            ExceptionPrinter.printHistory(ex, LOGGER, LogLevel.ERROR);
//        }
//    }
//
//    @Override
//    protected void initTitle() {
//        isClickedProperty.addListener((observable, oldValue, newValue) -> GlobalCachedExecutorService.submit(new Task() {
//            @Override
//            protected Object call() {
//                if (toggleSwitch.isSelected()) {
//                    sendStateToRemote(PowerStateType.PowerState.State.OFF);
//                } else {
//                    sendStateToRemote(PowerStateType.PowerState.State.ON);
//                }
//                return null;
//            }
//        }));
//
//        toggleSwitch.setOnMouseClicked(event -> GlobalCachedExecutorService.submit(new Task() {
//            @Override
//            protected Object call() {
//                if (toggleSwitch.isSelected()) {
//                    sendStateToRemote(PowerStateType.PowerState.State.ON);
//                } else {
//                    sendStateToRemote(PowerStateType.PowerState.State.OFF);
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
//        headContent.prefHeightProperty().set(iconPane.getHeight() + Constants.INSETS);
//    }
//
//    @Override
//    protected void initBodyContent() {
//        //No body content.
//    }
//
//    @Override
//    public void update(final Observable observable, final Object powerPlug) throws java.lang.Exception {
//        Platform.runLater(() -> {
//            final State powerState = ((PowerSwitchData) powerPlug).getPowerState().getValue();
//            setPowerStateSwitchAndIcon(powerState);
//        });
//    }
}
