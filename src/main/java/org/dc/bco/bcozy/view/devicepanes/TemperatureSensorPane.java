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
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.weathericons.WeatherIcon;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.dc.bco.bcozy.view.Constants;
import org.dc.bco.bcozy.view.SVGIcon;
import org.dc.jul.extension.rsb.com.AbstractIdentifiableRemote;
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
    private final SVGIcon unknownForegroundIcon;
    private final SVGIcon unknownBackgroundIcon;
    private final SVGIcon alarmIcon;
    private final Text temperatureStatus;
    private final GridPane iconPaneAlarm;

    /**
     * Constructor for TemperatureSensorPane.
     * @param temperatureSensorRemote AbstractIdentifiableRemote
     */
    public TemperatureSensorPane(final AbstractIdentifiableRemote temperatureSensorRemote) {
        this.temperatureSensorRemote = (TemperatureSensorRemote) temperatureSensorRemote;

        thermometerIconBackground = new SVGIcon(WeatherIcon.THERMOMETER_EXTERIOR,
                Constants.SMALL_ICON * Constants.WEATHER_ICONS_SCALE, true);
        thermometerIconForeground = new SVGIcon(WeatherIcon.THERMOMETER_INTERNAL,
                Constants.SMALL_ICON * Constants.WEATHER_ICONS_SCALE, false);
        alarmIcon = new SVGIcon(FontAwesomeIcon.EXCLAMATION_TRIANGLE, Constants.SMALL_ICON, false);
        unknownBackgroundIcon = new SVGIcon(MaterialDesignIcon.CHECKBOX_BLANK_CIRCLE, Constants.SMALL_ICON - 2, false);
        unknownForegroundIcon = new SVGIcon(MaterialDesignIcon.HELP_CIRCLE, Constants.SMALL_ICON, false);

        headContent = new BorderPane();
        temperatureStatus = new Text();
        iconPaneAlarm = new GridPane();

        initUnitLabel();
        initTitle();
        initContent();
        initEffect();
        createWidgetPane(headContent, false);
        tooltip.textProperty().bind(observerText.textProperty());

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
        iconPaneAlarm.getChildren().clear();

        if (alarmState.equals(State.ALARM)) {
            iconPaneAlarm.add(alarmIcon, 0, 0);
            alarmIcon.setForegroundIconColor(Color.RED, Color.BLACK, Constants.NORMAL_STROKE);
            observerText.setIdentifier("alarm");
        } else if (alarmState.equals(State.NO_ALARM)) {
            iconPaneAlarm.add(alarmIcon, 0, 0);
            alarmIcon.setForegroundIconColor(Color.TRANSPARENT);
            observerText.setIdentifier("noAlarm");
        } else {
            iconPaneAlarm.add(unknownBackgroundIcon, 0, 0);
            iconPaneAlarm.add(unknownForegroundIcon, 0, 0);
            observerText.setIdentifier("unknown");
        }
    }

    private void setEffectTemperature(final double temperature) {
        if (temperature == Double.NEGATIVE_INFINITY) {
            temperatureStatus.setText("??" + Constants.CELSIUS);
        } else {
            temperatureStatus.setText((int) temperature + Constants.CELSIUS);
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
        unknownForegroundIcon.setForegroundIconColor(Color.BLUE);
        unknownBackgroundIcon.setForegroundIconColor(Color.WHITE);
        thermometerIconBackground.setForegroundIconColor(Color.BLACK);
        thermometerIconForeground.setForegroundIconColor(Color.RED);

        iconPane.add(thermometerIconBackground, 0, 0);
        iconPane.add(thermometerIconForeground, 0, 0);
        iconPane.add(temperatureStatus, 1, 0);
        iconPane.setHgap(Constants.INSETS);

        headContent.setLeft(iconPane);
        headContent.setCenter(getUnitLabel());
        headContent.setAlignment(getUnitLabel(), Pos.CENTER_LEFT);
        headContent.setRight(iconPaneAlarm);
        headContent.prefHeightProperty().set(thermometerIconBackground.getSize() + Constants.INSETS);
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
    public AbstractIdentifiableRemote getDALRemoteService() {
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
