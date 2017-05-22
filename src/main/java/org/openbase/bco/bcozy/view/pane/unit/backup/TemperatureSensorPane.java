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
import org.openbase.bco.dal.remote.unit.TemperatureSensorRemote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.unit.dal.TemperatureSensorDataType.TemperatureSensorData;

/**
 * Created by tmichalski on 17.01.16.
 */
public class TemperatureSensorPane extends AbstractUnitPane<TemperatureSensorRemote, TemperatureSensorData> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RollerShutterPane.class);
//
//    private final TemperatureSensorRemote temperatureSensorRemote;
//    private final BorderPane headContent;
//    private final SVGIcon thermometerIconBackground;
//    private final SVGIcon thermometerIconForeground;
//    private final SVGIcon unknownForegroundIcon;
//    private final SVGIcon unknownBackgroundIcon;
//    private final SVGIcon alarmIcon;
//    private final Text temperatureStatus;
//    private final GridPane iconPaneAlarm;
//
//    /**
//     * Constructor for TemperatureSensorPane.
//     *
//     * @param temperatureSensorRemote UnitRemote
//     */

    public TemperatureSensorPane() {
        super(TemperatureSensorRemote.class, false);
//
//        thermometerIconBackground = new SVGIcon(WeatherIcon.THERMOMETER_EXTERIOR,
//                Constants.SMALL_ICON * Constants.WEATHER_ICONS_SCALE, true);
//        thermometerIconForeground = new SVGIcon(WeatherIcon.THERMOMETER_INTERNAL,
//                Constants.SMALL_ICON * Constants.WEATHER_ICONS_SCALE, false);
//        alarmIcon = new SVGIcon(FontAwesomeIcon.EXCLAMATION_TRIANGLE, Constants.SMALL_ICON, false);
//        unknownBackgroundIcon = new SVGIcon(MaterialDesignIcon.CHECKBOX_BLANK_CIRCLE, Constants.SMALL_ICON - 2, false);
//        unknownForegroundIcon = new SVGIcon(MaterialDesignIcon.HELP_CIRCLE, Constants.SMALL_ICON, false);
//
//        headContent = new BorderPane();
//        temperatureStatus = new Text();
//        iconPaneAlarm = new GridPane();
//
//        initTitle();
//        initBodyContent();
//        initEffect();
//        createWidgetPane(headContent, false);
//        tooltip.textProperty().bind(labelText.textProperty());
    }
//
//    private void initEffect() {
//        double temperature = Double.NEGATIVE_INFINITY;
//        State alarmState = State.UNKNOWN;
//        try {
//            temperature = temperatureSensorRemote.getTemperatureState().getTemperature();
//        } catch (CouldNotPerformException e) {
//            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
//        }
//
//        setEffectTemperature(temperature);
//
//        try {
//            alarmState = temperatureSensorRemote.getTemperatureAlarmState().getValue();
//        } catch (CouldNotPerformException e) {
//            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
//        }
//        setAlarmStateIcon(alarmState);
//    }
//
//    private void setAlarmStateIcon(final State alarmState) {
//        iconPaneAlarm.getChildren().clear();
//
//        switch (alarmState) {
//            case ALARM:
//                iconPaneAlarm.add(alarmIcon, 0, 0);
//                alarmIcon.setForegroundIconColor(Color.RED, Color.BLACK, Constants.NORMAL_STROKE);
//                labelText.setIdentifier("alarm");
//                break;
//            case NO_ALARM:
//                iconPaneAlarm.add(alarmIcon, 0, 0);
//                alarmIcon.setForegroundIconColor(Color.TRANSPARENT);
//                labelText.setIdentifier("noAlarm");
//                break;
//            default:
//                iconPaneAlarm.add(unknownBackgroundIcon, 0, 0);
//                iconPaneAlarm.add(unknownForegroundIcon, 0, 0);
//                labelText.setIdentifier("unknown");
//                break;
//        }
//    }
//
//    private void setEffectTemperature(final double temperature) {
//        if (temperature == Double.NEGATIVE_INFINITY) {
//            temperatureStatus.setText("??" + Constants.CELSIUS);
//        } else {
//            temperatureStatus.setText((int) temperature + Constants.CELSIUS);
//            if (temperature <= Constants.TEMPERATUR_FADING_MINIMUM) {
//                thermometerIconForeground.setForegroundIconColorAnimated(Color.BLUE);
//            } else if (temperature < Constants.TEMPERATUR_FADING_MAXIMUM) {
//                final double redChannel = (temperature - Constants.TEMPERATUR_FADING_MINIMUM)
//                        / (Constants.TEMPERATUR_FADING_MAXIMUM - Constants.TEMPERATUR_FADING_MINIMUM);
//                final double blueChannel = 1 - ((temperature - Constants.TEMPERATUR_FADING_MINIMUM)
//                        / (Constants.TEMPERATUR_FADING_MAXIMUM - Constants.TEMPERATUR_FADING_MINIMUM));
//                thermometerIconForeground.setForegroundIconColorAnimated(new Color(redChannel, 0.0, blueChannel, 1.0));
//            } else {
//                thermometerIconForeground.setForegroundIconColorAnimated(Color.RED);
//            }
//        }
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
//    protected void initBodyContent() {
//        //No body content.
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
