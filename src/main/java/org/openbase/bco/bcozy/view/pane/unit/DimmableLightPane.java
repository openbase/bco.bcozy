package org.openbase.bco.bcozy.view.pane.unit;

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
import com.jfoenix.controls.JFXSlider;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.scene.paint.Color;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.openbase.jul.schedule.RecurrenceEventFilter;
import rst.domotic.unit.dal.DimmableLightDataType.DimmableLightData;
import java.util.concurrent.Future;
import javafx.scene.layout.Pane;
import org.openbase.bco.dal.remote.unit.DimmableLightRemote;
import rst.domotic.state.BrightnessStateType.BrightnessState;
import rst.domotic.state.PowerStateType.PowerState;

/**
 * Created by agatting on 03.12.15.
 */
public class DimmableLightPane extends AbstractUnitPane<DimmableLightRemote, DimmableLightData> {

    private JFXSlider brightnessSlider;

    private final RecurrenceEventFilter<Double> recurrenceEventFilterHSV = new RecurrenceEventFilter<Double>(Constants.RECURRENCE_EVENT_FILTER_MILLI_TIMEOUT) {

        @Override
        public void relay() {
            try {
                getUnitRemote().setBrightnessState(BrightnessState.newBuilder().setBrightness(brightnessSlider.getValue()).build());
            } catch (CouldNotPerformException ex) {
                ExceptionPrinter.printHistory("Could not send color update!", ex, LOGGER);
            }
        }
    };

    /**
     * Constructor for the Dimmable Light Pane.
     *
     */
    public DimmableLightPane() {
        super(DimmableLightRemote.class, true);
        this.setIcon(MaterialDesignIcon.LIGHTBULB_OUTLINE, MaterialDesignIcon.LIGHTBULB);
    }

    @Override
    protected void initBodyContent(Pane bodyPane) throws CouldNotPerformException {
        brightnessSlider = new JFXSlider();
        brightnessSlider.valueProperty().addListener((observable) -> {
            if (isHover()) {
                recurrenceEventFilterHSV.trigger(brightnessSlider.getValue());
            }
        });
        bodyPane.getChildren().add(brightnessSlider);
    }

    @Override
    public void updateDynamicContent() {
        super.updateDynamicContent();

        // detect power state
        PowerState.State state = PowerState.State.UNKNOWN;
        try {
            state = getUnitRemote().getData().getPowerState().getValue();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.DEBUG);
        }

        // detect color
        double brightness;
        try {
            brightness = getData().getBrightnessState().getBrightness();
        } catch (CouldNotPerformException e) {
            brightness = 100d;
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.DEBUG);
        }

        if (brightnessSlider != null && !isHover()) {
            brightnessSlider.setValue(brightness);
        }
        switch (state) {
            case OFF:
                getIcon().setBackgroundIconColor(Constants.LIGHTBULB_OFF_COLOR);
                setInfoText("lightOff");
                setPrimaryActivationWithoutNotification(Boolean.FALSE);
                break;
            case ON:
                getIcon().setBackgroundIconColor(Constants.LIGHTBULB_OFF_COLOR.interpolate(Color.CORNSILK, brightness/100d));
                setInfoText("lightOn");
                setPrimaryActivationWithoutNotification(Boolean.TRUE);
                break;
            default:
                setInfoText("unknown");
                break;
        }
    }

    @Override
    protected Future applyPrimaryActivationUpdate(final boolean activation) throws CouldNotPerformException {
        return (activation) ? getUnitRemote().setPowerState(PowerState.State.ON) : getUnitRemote().setPowerState(PowerState.State.OFF);
    }
}
