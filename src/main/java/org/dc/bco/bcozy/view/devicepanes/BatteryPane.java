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
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.dc.bco.dal.remote.unit.DALRemoteService;
import org.dc.bco.dal.remote.unit.BatteryRemote;
import org.dc.jul.exception.CouldNotPerformException;
import org.dc.jul.exception.printer.ExceptionPrinter;
import org.dc.jul.exception.printer.LogLevel;
import org.dc.jul.pattern.Observable;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import org.dc.bco.bcozy.view.Constants;
import org.dc.bco.bcozy.view.SVGIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.homeautomation.unit.BatteryType.Battery;

/**
 * Created by timo on 13.01.16.
 */
public class BatteryPane extends UnitPane {
    private static final Logger LOGGER = LoggerFactory.getLogger(BatteryPane.class);

    private final BatteryRemote batteryRemote;
    private final SVGIcon batteryIcon;
    private final Text batteryStatus;
    private final GridPane iconPane;
    private final BorderPane headContent;

    /**
     * Constructor for the BatteryPane.
     * @param batteryRemote batteryRemote
     */
    public BatteryPane(final DALRemoteService batteryRemote) {
        this.batteryRemote = (BatteryRemote) batteryRemote;

        headContent = new BorderPane();
        batteryIcon = new SVGIcon(MaterialDesignIcon.BATTERY, MaterialDesignIcon.BATTERY_OUTLINE,
                Constants.SMALL_ICON);
        batteryStatus = new Text();
        iconPane = new GridPane();

        try {
            super.setUnitLabel(this.batteryRemote.getData().getLabel());
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
            super.setUnitLabel("UnknownID");
        }

        initTitle();
        initContent();
        createWidgetPane(headContent);

        try {
            initEffect();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }

        this.batteryRemote.addObserver(this);
    }

    private void initEffect() throws CouldNotPerformException {
        final double batteryLevel = batteryRemote.getBattery().getLevel();
        batteryStatus.setText((int) batteryLevel + "%");
        //CHECKSTYLE.OFF: MagicNumber
        //CHECKSTYLE.OFF: EmptyBlock
        if (batteryLevel > 95) {
            batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY);
        } else if (batteryLevel > 85) {
            batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_90);
        } else if (batteryLevel > 75) {
            batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_80);
        } else if (batteryLevel > 65) {
            //batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_70);
        } else if (batteryLevel > 55) {
            batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_60);
        } else if (batteryLevel > 45) {
            //batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_50);
        } else if (batteryLevel > 35) {
            batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_40);
        } else if (batteryLevel > 25) {
            batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_30);
        } else if (batteryLevel > 15) {
            batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_20);
        } else if (batteryLevel > 5) {
            //batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_10);
        } else {
            batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_OUTLINE);
        }
        //CHECKSTYLE.ON: MagicNumber
        //CHECKSTYLE.ON: EmptyBlock

        switch (batteryRemote.getBattery().getValue()) {
            case UNKNOWN:
                batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_UNKNOWN);
                batteryIcon.setBackgroundIconColorAnimated(Color.BLACK);
                break;
            case OK:
                batteryIcon.setBackgroundIconColorAnimated(Color.GREEN);
                break;
            case CRITICAL:
                batteryIcon.setBackgroundIconColorAnimated(Color.RED);
                break;
            case INSUFFICIENT:
                batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_ALERT);
                batteryIcon.setBackgroundIconColorAnimated(Color.RED);
                break;
            default:
                break;
        }
    }

    /**
     * Method creates the header content of the widgetPane.
     */
    @Override
    protected void initTitle() {
        batteryIcon.setBackgroundIconColorAnimated(Color.TRANSPARENT);

        iconPane.add(batteryIcon, 0, 0);
        iconPane.add(batteryStatus, 1, 0);

        headContent.setLeft(iconPane);
        headContent.setCenter(new Label(super.getUnitLabel()));
        //Padding values are not available here
        headContent.prefHeightProperty().set(iconPane.getHeight() + Constants.INSETS);
    }

    /**
     * Method creates the body content of the widgetPane.
     */
    @Override
    protected void initContent() {
        //No body content.
    }

    @Override
    public DALRemoteService getDALRemoteService() {
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
            batteryStatus.setText((int)batteryLevel + "%");
            //CHECKSTYLE.OFF: MagicNumber
            //CHECKSTYLE.OFF: EmptyBlock
            if (batteryLevel > 95) {
                batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY);
            } else if (batteryLevel > 85) {
                batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_90);
            } else if (batteryLevel > 75) {
                batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_80);
            } else if (batteryLevel > 65) {
                //batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_70);
            } else if (batteryLevel > 55) {
                batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_60);
            } else if (batteryLevel > 45) {
                //batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_50);
            } else if (batteryLevel > 35) {
                batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_40);
            } else if (batteryLevel > 25) {
                batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_30);
            } else if (batteryLevel > 15) {
                batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_20);
            } else if (batteryLevel > 5) {
                //batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_10);
            } else {
                batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_OUTLINE);
            }
            //CHECKSTYLE.ON: MagicNumber
            //CHECKSTYLE.ON: EmptyBlock


            batteryIcon.setForegroundIconColorAnimated(Color.BLACK);
            switch (((Battery) battery).getBatteryState().getValue()) {
                case UNKNOWN:
                    batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_UNKNOWN);
                    batteryIcon.setBackgroundIconColorAnimated(Color.YELLOW);
                    break;
                case OK:
                    batteryIcon.setBackgroundIconColorAnimated(Color.GREEN);
                    break;
                case CRITICAL:
                    batteryIcon.setBackgroundIconColorAnimated(Color.RED);
                    break;
                case INSUFFICIENT:
                    batteryIcon.changeBackgroundIcon(MaterialDesignIcon.BATTERY_ALERT);
                    batteryIcon.setBackgroundIconColorAnimated(Color.RED);
                    break;
                default:
                    break;
            }
        });
    }
}
