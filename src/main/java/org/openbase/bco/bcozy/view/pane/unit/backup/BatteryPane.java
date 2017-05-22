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
import org.openbase.bco.dal.remote.unit.BatteryRemote;
import rst.domotic.unit.dal.BatteryDataType.BatteryData;

/**
 * Created by tmichalski on 13.01.16.
 */
public class BatteryPane extends AbstractUnitPane<BatteryRemote, BatteryData> {

//    private static final Logger LOGGER = LoggerFactory.getLogger(BatteryPane.class);
//
//    private final SVGIcon batteryIcon;
//    private final Text batteryStatus;
//    private final BorderPane headContent;
//
//    /**
//     * Constructor for the BatteryPane.
//     *
//     * @param batteryRemote batteryRemote
//     */
    public BatteryPane() {
        super(BatteryRemote.class, false);
//        headContent = new BorderPane();
//        batteryIcon = new SVGIcon(MaterialDesignIcon.BATTERY, MaterialDesignIcon.BATTERY_OUTLINE, Constants.SMALL_ICON);
//        batteryStatus = new Text();
//
//        initTitle();
//        initBodyContent();
//        createWidgetPane(headContent, false);
//        initEffect();
//        tooltip.textProperty().bind(labelText.textProperty());
    }
//
//    private void initEffect() {
//        double batteryLevel = 0.0;
//        State batteryState = State.UNKNOWN;
//
//        try {
//            batteryLevel = batteryRemote.getBatteryState().getLevel();
//            batteryState = batteryRemote.getBatteryState().getValue();
//        } catch (CouldNotPerformException e) {
//            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
//        }
//        setStatusBatteryIcon(batteryLevel);
//        setBatteryStateColorAndIcon(batteryState);
//    }
//
//    private void setBatteryStateColorAndIcon(final State batteryState) {
//        switch (batteryState) {
//            case UNKNOWN:
//                batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_UNKNOWN);
//                batteryIcon.setBackgroundIconColorAnimated(Color.BLACK);
//                labelText.setIdentifier("unknown");
//                break;
//            case OK:
//                batteryIcon.setBackgroundIconColorAnimated(Color.GREEN);
//                labelText.setIdentifier("okay");
//                break;
//            case CRITICAL:
//                batteryIcon.setBackgroundIconColorAnimated(Color.RED);
//                labelText.setIdentifier("critical");
//                break;
//            case INSUFFICIENT:
//                batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_ALERT);
//                batteryIcon.setBackgroundIconColorAnimated(Color.RED);
//                labelText.setIdentifier("insufficient");
//                break;
//            default:
//                break;
//        }
//    }
//
//    private void setStatusBatteryIcon(final double batteryLevel) {
//        batteryStatus.setText((int) batteryLevel + "%");
//        //CHECKSTYLE.OFF: MagicNumber
//        //CHECKSTYLE.OFF: EmptyBlock
//        if (batteryLevel > 95) {
//            batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY);
//        } else if (batteryLevel > 85) {
//            batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_90);
//        } else if (batteryLevel > 75) {
//            batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_80);
//        } else if (batteryLevel > 55) {
//            batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_60);
//        } else if (batteryLevel > 35) {
//            batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_40);
//        } else if (batteryLevel > 25) {
//            batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_30);
//        } else if (batteryLevel > 15) {
//            batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_20);
//        } else {
//            batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_OUTLINE);
//        }
//        //CHECKSTYLE.ON: MagicNumber
//        //CHECKSTYLE.ON: EmptyBlock
//    }
//
//    @Override
//    protected void initTitle() {
//        batteryStatus.getStyleClass().add(Constants.ICONS_CSS_STRING);
//
//        batteryIcon.setBackgroundIconColorAnimated(Color.TRANSPARENT);
//
//        iconPane.add(batteryIcon, 0, 0);
//        iconPane.add(batteryStatus, 1, 0);
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
//    public void update(final Observable observable, final Object battery) throws java.lang.Exception {
//        Platform.runLater(() -> {
//            final double batteryLevel = ((BatteryData) battery).getBatteryState().getLevel();
//            setStatusBatteryIcon(batteryLevel);
//
//            final State batteryState
//                    = ((BatteryData) battery).getBatteryState().getValue();
//            setBatteryStateColorAndIcon(batteryState);
//        });
//    }
}
