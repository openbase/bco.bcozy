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
package org.openbase.bco.bcozy.view.pane.unit;

import de.jensd.fx.glyphs.weathericons.WeatherIcon;
import javafx.scene.paint.Color;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.dal.remote.layer.unit.TemperatureSensorRemote;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import rst.domotic.state.TemperatureStateType;
import rst.domotic.unit.dal.TemperatureSensorDataType.TemperatureSensorData;

/**
 * Created by tmichalski on 17.01.16.
 */
public class TemperatureSensorPane extends AbstractUnitPane<TemperatureSensorRemote, TemperatureSensorData> {

    /**
     * Constructor for TemperatureSensorPane.
     */
    public TemperatureSensorPane() {
        super(TemperatureSensorRemote.class, false);
        getIcon().setBackgroundIcon(WeatherIcon.THERMOMETER_EXTERIOR);
        getIcon().setForegroundIcon(WeatherIcon.THERMOMETER_INTERNAL);

//        thermometerIconBackground = new SVGIcon(WeatherIcon.THERMOMETER_EXTERIOR,
//                JFXConstants.ICON_SIZE_SMALL * Constants.WEATHER_ICONS_SCALE, true);
//        thermometerIconForeground = new SVGIcon(WeatherIcon.THERMOMETER_INTERNAL,
//                JFXConstants.ICON_SIZE_SMALL * Constants.WEATHER_ICONS_SCALE, false);
//        alarmIcon = new SVGIcon(FontAwesomeIcon.EXCLAMATION_TRIANGLE, JFXConstants.ICON_SIZE_SMALL, false);
//        unknownBackgroundIcon = new SVGIcon(MaterialDesignIcon.CHECKBOX_BLANK_CIRCLE, JFXConstants.ICON_SIZE_SMALL - 2, false);
//        unknownForegroundIcon = new SVGIcon(MaterialDesignIcon.HELP_CIRCLE, JFXConstants.ICON_SIZE_SMALL, false);
    }

    @Override
    public void updateDynamicContent() {
        super.updateDynamicContent();

        TemperatureStateType.TemperatureState state;

        try {
            state = getUnitRemote().getData().getTemperatureState();

            if (state.getTemperature() == Double.NEGATIVE_INFINITY) {

            } else {
//                temperatureStatus.setText((int) state.getTemperature() + Constants.CELSIUS);
                if (state.getTemperature() <= Constants.TEMPERATUR_FADING_MINIMUM) {
                    getIcon().setForegroundIconColorAnimated(Color.BLUE, 1);
                } else if (state.getTemperature() < Constants.TEMPERATUR_FADING_MAXIMUM) {
                    final double redChannel = (state.getTemperature() - Constants.TEMPERATUR_FADING_MINIMUM)
                            / (Constants.TEMPERATUR_FADING_MAXIMUM - Constants.TEMPERATUR_FADING_MINIMUM);
                    final double blueChannel = 1 - ((state.getTemperature() - Constants.TEMPERATUR_FADING_MINIMUM)
                            / (Constants.TEMPERATUR_FADING_MAXIMUM - Constants.TEMPERATUR_FADING_MINIMUM));
                    getIcon().setForegroundIconColor(new Color(redChannel, 0.0, blueChannel, 1.0));
                } else {
                    getIcon().setForegroundIconColorAnimated(Color.RED, 1);
                }
            }

        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER, LogLevel.DEBUG);
        }

//        // update alarm state
//                iconPaneAlarm.getChildren().clear();
//
//        switch (alarmState) {
//            case ALARM:
//                iconPaneAlarm.add(alarmIcon, 0, 0);
//                alarmIcon.setForegroundIconColor(Color.RED, Color.BLACK, Constants.NORMAL_STROKE);
//                break;
//            case NO_ALARM:
//                iconPaneAlarm.add(alarmIcon, 0, 0);
//                alarmIcon.setForegroundIconColor(Color.TRANSPARENT);
//                break;
//            default:
//                iconPaneAlarm.add(unknownBackgroundIcon, 0, 0);
//                iconPaneAlarm.add(unknownForegroundIcon, 0, 0);
//                break;
//        }
    }
//
//    private void initEffect() {
//        double temperature = Double.NEGATIVE_INFINITY;
//        State alarmState = State.UNKNOWN;
//        try {
//            temperature = temperatureSensorRemote.getTemperatureState().getTemperature();
//        } catch (CouldNotPerformException ex) {
//            ExceptionPrinter.printHistory(ex, LOGGER, LogLevel.ERROR);
//        }
//
//        setEffectTemperature(temperature);
//
//        try {
//            alarmState = temperatureSensorRemote.getTemperatureAlarmState().getValue();
//        } catch (CouldNotPerformException ex) {
//            ExceptionPrinter.printHistory(ex, LOGGER, LogLevel.ERROR);
//        }
//        setAlarmStateIcon(alarmState);
//    }
//
//    private void setAlarmStateIcon(final State alarmState) {

//    }
//
//    @Override
//    protected void initTitle() {
//        unknownForegroundIcon.setForegroundIconColor(Color.BLUE);
//        unknownBackgroundIcon.setForegroundIconColor(Color.WHITE);
//        thermometerIconBackground.setForegroundIconColor(Color.BLACK);
//        thermometerIconForeground.setForegroundIconColor(Color.RED);
//
//        iconPane.add(thermometerIconBackground, 0, 0);
//        iconPane.add(thermometerIconForeground, 0, 0);
//        iconPane.add(temperatureStatus, 1, 0);
//        iconPane.setHgap(Constants.INSETS);
//
//        headContent.setLeft(iconPane);
//        headContent.setCenter(getUnitLabel());
//        headContent.setAlignment(getUnitLabel(), Pos.CENTER_LEFT);
//        headContent.setRight(iconPaneAlarm);
//        headContent.prefHeightProperty().set(thermometerIconBackground.getSize() + Constants.INSETS);
//    }
//
//    @Override
//    public void update(final Observable observable, final Object temperatureSensor) throws java.lang.Exception {
//        Platform.runLater(() -> {
//            final double temperature = ((TemperatureSensorData) temperatureSensor).getTemperatureState().getTemperature();
//            setEffectTemperature(temperature);
//
//            final State alarmState  = ((TemperatureSensorData) temperatureSensor).getTemperatureAlarmState().getValue();
//            setAlarmStateIcon(alarmState);
//        });
//    }
}
