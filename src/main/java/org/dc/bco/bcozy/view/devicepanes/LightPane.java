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
import javafx.geometry.Pos;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import org.controlsfx.control.ToggleSwitch;
import org.dc.bco.bcozy.view.Constants;
import org.dc.bco.bcozy.view.SVGIcon;
import org.dc.jul.extension.rsb.com.AbstractIdentifiableRemote;
import org.dc.bco.dal.remote.unit.LightRemote;
import org.dc.jul.exception.CouldNotPerformException;
import org.dc.jul.exception.printer.ExceptionPrinter;
import org.dc.jul.exception.printer.LogLevel;
import org.dc.jul.pattern.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.homeautomation.state.PowerStateType;
import rst.homeautomation.state.PowerStateType.PowerState.State;
import rst.homeautomation.unit.LightType.Light;

/**
 * Created by timo on 08.01.16.
 */
public class LightPane extends UnitPane {
    private static final Logger LOGGER = LoggerFactory.getLogger(LightPane.class);

    private final LightRemote lightRemote;
    private final SVGIcon lightBulbIcon;
    private final ToggleSwitch toggleSwitch;
    private final BorderPane headContent;
    private final Tooltip tooltip;

    /**
     * Constructor for the LightPane.
     * @param lightRemote lightRemote
     */
    public LightPane(final AbstractIdentifiableRemote lightRemote) {
        this.lightRemote = (LightRemote) lightRemote;

        toggleSwitch = new ToggleSwitch();
        lightBulbIcon =
                new SVGIcon(MaterialDesignIcon.LIGHTBULB, MaterialDesignIcon.LIGHTBULB_OUTLINE, Constants.SMALL_ICON);
        headContent = new BorderPane();
        tooltip = new Tooltip();

        initUnitLabel();
        initTitle();
        initContent();
        createWidgetPane(headContent);

        initEffectAndSwitch();

        this.lightRemote.addObserver(this);
    }

    private void initEffectAndSwitch() {
        State powerState = State.OFF;

        try {
            powerState = lightRemote.getPower().getValue();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
        setPowerStateSwitchAndIcon(powerState);
    }

    private void setPowerStateSwitchAndIcon(final State powerState) {
        if (powerState.equals(State.ON)) {
            lightBulbIcon.setBackgroundIconColorAnimated(Constants.LIGHTBULB_COLOR);
            tooltip.setText(Constants.LIGHT_ON);

            if (!toggleSwitch.isSelected()) {
                toggleSwitch.setSelected(true);
            }
        } else if (powerState.equals(State.OFF)) {
            lightBulbIcon.setBackgroundIconColorAnimated(Color.TRANSPARENT);
            tooltip.setText(Constants.LIGHT_OFF);

            if (toggleSwitch.isSelected()) {
                toggleSwitch.setSelected(false);
            }
        } else {
            tooltip.setText(Constants.UNKNOWN);
        }
    }

    @Override
    protected void initTitle() {
        toggleSwitch.setMouseTransparent(true);
        this.setOnMouseClicked(event -> {
            new Thread(new Task() {
                @Override
                protected Object call() throws java.lang.Exception {
                    toggleSwitch.setSelected(!toggleSwitch.isSelected());
                    if (toggleSwitch.isSelected()) {
                        try {
                            lightRemote.setPower(PowerStateType.PowerState.State.ON);
                        } catch (CouldNotPerformException e) {
                            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                        }
                    } else {
                        try {
                            lightRemote.setPower(PowerStateType.PowerState.State.OFF);
                        } catch (CouldNotPerformException e) {
                            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                        }
                    }
                    return null;
                }
            }).start();
        });

        lightBulbIcon.setBackgroundIconColorAnimated(Color.TRANSPARENT);

        headContent.setLeft(lightBulbIcon);
        headContent.setCenter(getUnitLabel());
        headContent.setAlignment(getUnitLabel(), Pos.CENTER_LEFT);
        headContent.setRight(toggleSwitch);
        //Padding values are not available here
        headContent.prefHeightProperty().set(lightBulbIcon.getSize() + Constants.INSETS);
    }

    @Override
    protected void initContent() {
        //No body content.
    }

    @Override
    protected void initUnitLabel() {
        String unitLabel = Constants.UNKNOWN_ID;
        try {
            unitLabel = this.lightRemote.getData().getLabel();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
        setUnitLabelString(unitLabel);
    }

    @Override
    public AbstractIdentifiableRemote getDALRemoteService() {
        return lightRemote;
    }

    @Override
    void removeObserver() {
        this.lightRemote.removeObserver(this);
    }

    @Override
    public void update(final Observable observable, final Object light) throws java.lang.Exception {
        Platform.runLater(() -> {
            final State powerState = ((Light) light).getPowerState().getValue();
            setPowerStateSwitchAndIcon(powerState);
        });
    }
}
