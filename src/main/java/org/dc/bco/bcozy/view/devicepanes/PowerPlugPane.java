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

import javafx.scene.layout.GridPane;
import org.dc.bco.dal.remote.unit.DALRemoteService;
import org.dc.bco.dal.remote.unit.PowerPlugRemote;
import org.dc.jul.exception.CouldNotPerformException;
import org.dc.jul.exception.printer.ExceptionPrinter;
import org.dc.jul.exception.printer.LogLevel;
import org.dc.jul.pattern.Observable;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import org.controlsfx.control.ToggleSwitch;
import org.dc.bco.bcozy.view.Constants;
import org.dc.bco.bcozy.view.SVGIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.homeautomation.state.PowerStateType.PowerState.State;
import rst.homeautomation.unit.PowerPlugType.PowerPlug;

/**
 * Created by timo on 08.01.16.
 */
public class PowerPlugPane extends UnitPane {
    private static final Logger LOGGER = LoggerFactory.getLogger(PowerPlugPane.class);

    private final PowerPlugRemote powerPlugRemote;
    private final SVGIcon powerPlugIcon;
    private final SVGIcon powerStatusIcon;
    private final GridPane iconPane;
    private final ToggleSwitch toggleSwitch;
    private final BorderPane headContent;

    /**
     * Constructor for the PowerPlugPane.
     * @param powerPlugRemote powerPlugRemote
     */
    public PowerPlugPane(final DALRemoteService powerPlugRemote) {
        this.powerPlugRemote = (PowerPlugRemote) powerPlugRemote;

        toggleSwitch = new ToggleSwitch();
        headContent = new BorderPane();
        powerPlugIcon = new SVGIcon(FontAwesomeIcon.PLUG, Constants.EXTRA_SMALL_ICON, true);
        powerStatusIcon = new SVGIcon(FontAwesomeIcon.BOLT, Constants.EXTRA_EXTRA_SMALL_ICON, false);
        iconPane = new GridPane();

        try {
            super.setUnitLabel(this.powerPlugRemote.getData().getLabel());
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
            super.setUnitLabel("UnknownID");
        }

        initTitle();
        initContent();
        createWidgetPane(headContent);

        try {
            initEffectAndSwitch();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }

        this.powerPlugRemote.addObserver(this);
    }

    private void initEffectAndSwitch() throws CouldNotPerformException {
        if (powerPlugRemote.getPower().getValue().equals(State.ON)) {
            powerStatusIcon.setForegroundIconColorAnimated(Constants.LIGHTBULB_COLOR);

            if (!toggleSwitch.isSelected()) {
                toggleSwitch.setSelected(true);
            }
        } else if (powerPlugRemote.getPower().getValue().equals(State.OFF)) {
            powerStatusIcon.setForegroundIconColorAnimated(Color.TRANSPARENT);

            if (toggleSwitch.isSelected()) {
                toggleSwitch.setSelected(false);
            }
        }
    }

    /**
     * Method creates the header content of the widgetPane.
     */
    @Override
    protected void initTitle() {
        powerStatusIcon.setForegroundIconColorAnimated(Color.TRANSPARENT);
        toggleSwitch.setOnMouseClicked(event -> {
            if (toggleSwitch.isSelected()) {
                try {
                    powerPlugRemote.setPower(State.ON);
                } catch (CouldNotPerformException e) {
                    ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                }
            } else {
                try {
                    powerPlugRemote.setPower(State.OFF);
                } catch (CouldNotPerformException e) {
                    ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                }
            }
        });

        iconPane.add(powerPlugIcon, 1, 0, 3, 2);
        iconPane.add(powerStatusIcon, 0, 0);

        headContent.setLeft(iconPane);
        headContent.setCenter(new Label(super.getUnitLabel()));
        headContent.setRight(toggleSwitch);
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
        return powerPlugRemote;
    }

    @Override
    void removeObserver() {
        this.powerPlugRemote.removeObserver(this);
    }

    @Override
    public void update(final Observable observable, final Object powerPlug) throws java.lang.Exception {
        Platform.runLater(() -> {
            if (((PowerPlug) powerPlug).getPowerState().getValue().equals(State.ON)) {
                powerStatusIcon.setForegroundIconColorAnimated(Constants.LIGHTBULB_COLOR);
                if (!toggleSwitch.isSelected()) {
                    toggleSwitch.setSelected(true);
                }
            } else {
                powerStatusIcon.setForegroundIconColorAnimated(Color.TRANSPARENT);
                if (toggleSwitch.isSelected()) {
                    toggleSwitch.setSelected(false);
                }
            }
        });
    }
}
