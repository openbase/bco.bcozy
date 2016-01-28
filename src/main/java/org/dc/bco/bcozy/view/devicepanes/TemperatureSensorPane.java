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

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.weathericons.WeatherIcon;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.dc.bco.bcozy.view.Constants;
import org.dc.bco.bcozy.view.SVGIcon;
import org.dc.bco.dal.remote.unit.DALRemoteService;
import org.dc.bco.dal.remote.unit.TemperatureSensorRemote;
import org.dc.jul.exception.CouldNotPerformException;
import org.dc.jul.exception.printer.ExceptionPrinter;
import org.dc.jul.exception.printer.LogLevel;
import org.dc.jul.pattern.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.homeautomation.state.AlarmStateType.AlarmState.State;
import rst.homeautomation.unit.TemperatureSensorType.TemperatureSensor;

/**
 * Created by tmichalski on 17.01.16.
 */
public class TemperatureSensorPane extends UnitPane {

    private static final Logger LOGGER = LoggerFactory.getLogger(RollerShutterPane.class);

    private final TemperatureSensorRemote temperatureSensorRemote;
    private final BorderPane headContent;
    private final SVGIcon thermometerIconBackground;
    private final SVGIcon thermometerIconForeground;
    private final SVGIcon alarmIcon;
    private final Text temperatureStatus;
    private final GridPane iconPane;

    /**
     * Constructor for TemperatureSensorPane.
     * @param temperatureSensorRemote DALRemoteService
     */
    public TemperatureSensorPane(final DALRemoteService temperatureSensorRemote) {
        this.temperatureSensorRemote = (TemperatureSensorRemote) temperatureSensorRemote;

        thermometerIconBackground = new SVGIcon(WeatherIcon.THERMOMETER_EXTERIOR, Constants.SMALL_ICON, true);
        thermometerIconForeground = new SVGIcon(WeatherIcon.THERMOMETER_INTERNAL, Constants.SMALL_ICON, false);
        alarmIcon = new SVGIcon(FontAwesomeIcon.EXCLAMATION_TRIANGLE, Constants.SMALL_ICON, false);
        temperatureStatus = new Text();
        iconPane = new GridPane();

        headContent = new BorderPane();

        initUnitLabel();
        initTitle();
        initContent();
        initEffect();

        createWidgetPane(headContent);

        this.temperatureSensorRemote.addObserver(this);
    }

    private void initEffect() {
        double temperature = Double.NEGATIVE_INFINITY;
        State alarmState = State.UNKNOWN;
        try {
            temperature = temperatureSensorRemote.getTemperature();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }

        setEffectTemperature(temperature);

        try {
            alarmState = temperatureSensorRemote.getTemperatureAlarmState().getValue();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
        setAlarmStateIcon(alarmState);
    }

    private void setAlarmStateIcon(final State alarmState) {
        if (alarmState.equals(State.ALARM)) {
            alarmIcon.setColor(Color.RED, Color.BLACK, Constants.NORMAL_STROKE);
        } else if (alarmState.equals(State.UNKNOWN)) {
            alarmIcon.setColor(Color.YELLOW, Color.BLACK, Constants.NORMAL_STROKE);
        } else {
            alarmIcon.setColor(Color.TRANSPARENT);
        }
    }

    private void setEffectTemperature(final double temperature) {
        if (temperature == Double.NEGATIVE_INFINITY) {
            temperatureStatus.setText("??°C");
        } else {
            temperatureStatus.setText((int) temperature + "°C");
            if (temperature <= Constants.TEMPERATUR_FADING_MINIMUM) {
                thermometerIconForeground.setForegroundIconColorAnimated(Color.BLUE);
            } else if (temperature < Constants.TEMPERATUR_FADING_MAXIMUM) {
                final double redChannel = (temperature - Constants.TEMPERATUR_FADING_MINIMUM)
                        / (Constants.TEMPERATUR_FADING_MAXIMUM - Constants.TEMPERATUR_FADING_MINIMUM);
                final double blueChannel = 1 - ((temperature - Constants.TEMPERATUR_FADING_MINIMUM)
                        / (Constants.TEMPERATUR_FADING_MAXIMUM - Constants.TEMPERATUR_FADING_MINIMUM));
                thermometerIconForeground.setForegroundIconColorAnimated(new Color(redChannel, 0.0, blueChannel, 1.0));
            } else {
                thermometerIconForeground.setForegroundIconColorAnimated(Color.RED);
            }
        }

    }

    @Override
    protected void initTitle() {
        thermometerIconBackground.setColor(Color.BLACK);
        thermometerIconForeground.setColor(Color.RED);
        alarmIcon.setColor(Color.TRANSPARENT);

        iconPane.add(thermometerIconBackground, 0, 0);
        iconPane.add(thermometerIconForeground, 0, 0);
        iconPane.add(temperatureStatus, 1, 0);
        iconPane.setHgap(Constants.INSETS);

        headContent.setLeft(iconPane);
        headContent.setCenter(getUnitLabel());
        headContent.setAlignment(getUnitLabel(), Pos.CENTER_LEFT);
        headContent.setRight(alarmIcon);
        headContent.prefHeightProperty().set(Constants.SMALL_ICON + Constants.INSETS);
    }

    @Override
    protected void initContent() {
        //No body content.
    }

    @Override
    protected void initUnitLabel() {
        String unitLabel = Constants.UNKNOWN_ID;
        try {
            unitLabel = this.temperatureSensorRemote.getData().getLabel();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
        setUnitLabelString(unitLabel);
    }

    @Override
    public DALRemoteService getDALRemoteService() {
        return temperatureSensorRemote;
    }

    @Override
    void removeObserver() {
        this.temperatureSensorRemote.removeObserver(this);
    }

    @Override
    public void update(final Observable observable, final Object temperatureSensor) throws java.lang.Exception {
        Platform.runLater(() -> {
            final double temperature = ((TemperatureSensor) temperatureSensor).getTemperature();
            setEffectTemperature(temperature);

            final State alarmState =
                    ((TemperatureSensor) temperatureSensor).getTemperatureAlarmState().getValue();
            setAlarmStateIcon(alarmState);
        });

    }
}
