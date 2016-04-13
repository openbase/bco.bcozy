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
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.dc.bco.bcozy.view.Constants;
import org.dc.bco.bcozy.view.SVGIcon;
import org.dc.bco.dal.remote.unit.BatteryRemote;
import org.dc.jul.extension.rsb.com.AbstractIdentifiableRemote;
import org.dc.jul.exception.CouldNotPerformException;
import org.dc.jul.exception.printer.ExceptionPrinter;
import org.dc.jul.exception.printer.LogLevel;
import org.dc.jul.pattern.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.homeautomation.state.BatteryStateType.BatteryState.State;
import rst.homeautomation.unit.BatteryType.Battery;

/**
 * Created by tmichalski on 13.01.16.
 */
public class BatteryPane extends UnitPane {
    private static final Logger LOGGER = LoggerFactory.getLogger(BatteryPane.class);

    private final BatteryRemote batteryRemote;
    private final SVGIcon batteryIcon;
    private final Text batteryStatus;
    private final BorderPane headContent;

    /**
     * Constructor for the BatteryPane.
     * @param batteryRemote batteryRemote
     */
    public BatteryPane(final AbstractIdentifiableRemote batteryRemote) {
        this.batteryRemote = (BatteryRemote) batteryRemote;

        headContent = new BorderPane();
        batteryIcon = new SVGIcon(MaterialDesignIcon.BATTERY, MaterialDesignIcon.BATTERY_OUTLINE,
                Constants.SMALL_ICON);
        batteryStatus = new Text();

        initUnitLabel();
        initTitle();
        initContent();
        createWidgetPane(headContent, false);
        initEffect();
        tooltip.textProperty().bind(observerText.textProperty());

        this.batteryRemote.addObserver(this);
    }

    private void initEffect() {
        double batteryLevel = 0.0;
        State batteryState = State.UNKNOWN;

        try {
            batteryLevel = batteryRemote.getBattery().getLevel();
            batteryState = batteryRemote.getBattery().getValue();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
        setStatusBatteryIcon(batteryLevel);
        setBatteryStateColorAndIcon(batteryState);
    }

    private void setBatteryStateColorAndIcon(final State batteryState) {
        switch (batteryState) {
            case UNKNOWN:
                batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_UNKNOWN);
                batteryIcon.setBackgroundIconColorAnimated(Color.BLACK);
                observerText.setIdentifier("unknown");
                break;
            case OK:
                batteryIcon.setBackgroundIconColorAnimated(Color.GREEN);
                observerText.setIdentifier("okay");
                break;
            case CRITICAL:
                batteryIcon.setBackgroundIconColorAnimated(Color.RED);
                observerText.setIdentifier("critical");
                break;
            case INSUFFICIENT:
                batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_ALERT);
                batteryIcon.setBackgroundIconColorAnimated(Color.RED);
                observerText.setIdentifier("insufficient");
                break;
            default:
                break;
        }
    }

    private void setStatusBatteryIcon(final double batteryLevel) {
        batteryStatus.setText((int) batteryLevel + "%");
        //CHECKSTYLE.OFF: MagicNumber
        //CHECKSTYLE.OFF: EmptyBlock
        if (batteryLevel > 95) {
            batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY);
        } else if (batteryLevel > 85) {
            batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_90);
        } else if (batteryLevel > 75) {
            batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_80);
        }  else if (batteryLevel > 55) {
            batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_60);
        }  else if (batteryLevel > 35) {
            batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_40);
        } else if (batteryLevel > 25) {
            batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_30);
        } else if (batteryLevel > 15) {
            batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_20);
        }  else {
            batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_OUTLINE);
        }
        //CHECKSTYLE.ON: MagicNumber
        //CHECKSTYLE.ON: EmptyBlock
    }

    @Override
    protected void initTitle() {
        batteryStatus.getStyleClass().add(Constants.ICONS_CSS_STRING);

        batteryIcon.setBackgroundIconColorAnimated(Color.TRANSPARENT);

        iconPane.add(batteryIcon, 0, 0);
        iconPane.add(batteryStatus, 1, 0);

        headContent.setCenter(getUnitLabel());
        headContent.setAlignment(getUnitLabel(), Pos.CENTER_LEFT);
        headContent.prefHeightProperty().set(iconPane.getHeight() + Constants.INSETS);
    }

    @Override
    protected void initContent() {
        //No body content.
    }

    @Override
    protected void initUnitLabel() {
        String unitLabel = Constants.UNKNOWN_ID;
        try {
            unitLabel = this.batteryRemote.getData().getLabel();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
        setUnitLabelString(unitLabel);
    }

    @Override
    public AbstractIdentifiableRemote getDALRemoteService() {
        return batteryRemote;
    }

    @Override
    void removeObserver() {
        this.batteryRemote.removeObserver(this);
    }

    @Override
    public void update(final Observable observable, final Object battery) throws java.lang.Exception {
        Platform.runLater(() -> {
            final double batteryLevel = ((Battery) battery).getBatteryState().getLevel();
            setStatusBatteryIcon(batteryLevel);

            final State batteryState =
                    ((Battery) battery).getBatteryState().getValue();
            setBatteryStateColorAndIcon(batteryState);
        });
    }
}
