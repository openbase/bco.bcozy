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
package org.openbase.bco.bcozy.view.devicepanes;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.SVGIcon;
import org.openbase.jul.extension.rsb.com.AbstractIdentifiableRemote;
import org.openbase.bco.dal.remote.unit.PowerSwitchRemote;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.openbase.jul.pattern.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.state.PowerStateType;
import rst.domotic.state.PowerStateType.PowerState.State;
import rst.domotic.unit.dal.PowerSwitchDataType.PowerSwitchData;

/**
 * Created by tmichalski on 08.01.16.
 */
public class PowerSwitchPane extends UnitPane {
    private static final Logger LOGGER = LoggerFactory.getLogger(PowerSwitchPane.class);

    private final PowerSwitchRemote powerSwitchRemote;
    private final SVGIcon powerPlugIcon;
    private final SVGIcon powerStatusIcon;
    private final SVGIcon unknownForegroundIcon;
    private final SVGIcon unknownBackgroundIcon;
    private final BorderPane headContent;

    /**
     * Constructor for the PowerPlugPane.
     * @param powerPlugRemote powerPlugRemote
     */
    public PowerSwitchPane(final AbstractIdentifiableRemote powerPlugRemote) {
        this.powerSwitchRemote = (PowerSwitchRemote) powerPlugRemote;

        headContent = new BorderPane();
        powerPlugIcon = new SVGIcon(FontAwesomeIcon.PLUG, Constants.SMALL_ICON, true);
        powerStatusIcon = new SVGIcon(FontAwesomeIcon.BOLT, Constants.EXTRA_SMALL_ICON, false);
        unknownBackgroundIcon = new SVGIcon(MaterialDesignIcon.CHECKBOX_BLANK_CIRCLE, Constants.SMALL_ICON - 2, false);
        unknownForegroundIcon = new SVGIcon(MaterialDesignIcon.HELP_CIRCLE, Constants.SMALL_ICON, false);

        initUnitLabel();
        initTitle();
        initContent();
        createWidgetPane(headContent, true);
        initEffectAndSwitch();
        tooltip.textProperty().bind(observerText.textProperty());

        this.powerSwitchRemote.addDataObserver(this);
    }

    private void initEffectAndSwitch() {
        State powerState = State.OFF;

        try {
            powerState = powerSwitchRemote.getPowerState().getValue();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
        setPowerStateSwitchAndIcon(powerState);
    }

    private void setPowerStateSwitchAndIcon(final State powerState) {
        iconPane.getChildren().clear();

        if (powerState.equals(State.ON)) {
            powerStatusIcon.setForegroundIconColor(Color.YELLOW, Color.BLACK, Constants.THIN_STROKE);
            iconPane.add(powerPlugIcon, 1, 0, 3, 2);
            observerText.setIdentifier("powerOn");

            if (!toggleSwitch.isSelected()) {
                toggleSwitch.setSelected(true);
            }
        } else if (powerState.equals(State.OFF)) {
            powerStatusIcon.setForegroundIconColor(Color.TRANSPARENT);
            iconPane.add(powerPlugIcon, 1, 0, 3, 2);
            observerText.setIdentifier("powerOff");

            if (toggleSwitch.isSelected()) {
                toggleSwitch.setSelected(false);
            }
        } else {
            powerStatusIcon.setForegroundIconColor(Color.TRANSPARENT);
            iconPane.add(unknownBackgroundIcon, 1, 0, 3, 2);
            iconPane.add(unknownForegroundIcon, 1, 0, 3, 2);
            observerText.setIdentifier("unknown");
        }
        iconPane.add(powerStatusIcon, 0, 0);
    }

    private void sendStateToRemote(final State state) {
        try {
            powerSwitchRemote.setPowerState(state);
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
            setWidgetPaneDisable(true);
        }
    }

    @Override
    protected void initTitle() {
        oneClick.addListener((observable, oldValue, newValue) -> new Thread(new Task() {
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
            unitLabel = this.powerSwitchRemote.getData().getLabel();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
        setUnitLabelString(unitLabel);
    }

    @Override
    public AbstractIdentifiableRemote getDALRemoteService() {
        return powerSwitchRemote;
    }

    @Override
    void removeObserver() {
        this.powerSwitchRemote.removeObserver(this);
    }

    @Override
    public void update(final Observable observable, final Object powerPlug) throws java.lang.Exception {
        Platform.runLater(() -> {
            final State powerState = ((PowerSwitchData) powerPlug).getPowerState().getValue();
            setPowerStateSwitchAndIcon(powerState);
        });
    }
}
