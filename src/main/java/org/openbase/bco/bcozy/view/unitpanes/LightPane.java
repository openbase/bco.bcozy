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
package org.openbase.bco.bcozy.view.unitpanes;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.SVGIcon;
import org.openbase.bco.dal.remote.unit.UnitRemote;
import org.openbase.bco.dal.remote.unit.LightRemote;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.openbase.jul.pattern.Observable;
import org.openbase.jul.schedule.GlobalCachedExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.state.PowerStateType;
import rst.domotic.state.PowerStateType.PowerState.State;
import rst.domotic.unit.dal.LightDataType.LightData;

/**
 * Created by tmichalski on 08.01.16.
 */
public class LightPane extends AbstractUnitPane {
    private static final Logger LOGGER = LoggerFactory.getLogger(LightPane.class);

    private final SVGIcon unknownForegroundIcon;
    private final SVGIcon unknownBackgroundIcon;
    private final LightRemote lightRemote;
    private final SVGIcon lightBulbIcon;
    private final BorderPane headContent;

    /**
     * Constructor for the LightPane.
     * @param lightRemote lightRemote
     */
    public LightPane(final UnitRemote lightRemote) {
        this.lightRemote = (LightRemote) lightRemote;

        lightBulbIcon =
                new SVGIcon(MaterialDesignIcon.LIGHTBULB, MaterialDesignIcon.LIGHTBULB_OUTLINE, Constants.SMALL_ICON);
        unknownBackgroundIcon = new SVGIcon(MaterialDesignIcon.CHECKBOX_BLANK_CIRCLE, Constants.SMALL_ICON - 2, false);
        unknownForegroundIcon = new SVGIcon(MaterialDesignIcon.HELP_CIRCLE, Constants.SMALL_ICON, false);
        headContent = new BorderPane();

        initUnitLabel();
        initTitle();
        initContent();
        createWidgetPane(headContent, true);
        initEffectAndSwitch();
        tooltip.textProperty().bind(observerText.textProperty());

        addObserverAndInitDisableState(this.lightRemote);
    }

    private void initEffectAndSwitch() {
        State powerState = State.OFF;

        try {
            powerState = lightRemote.getPowerState().getValue();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
        setPowerStateSwitchAndIcon(powerState);
    }

    private void setPowerStateSwitchAndIcon(final State powerState) {
        iconPane.getChildren().clear();

        switch (powerState) {
            case ON:
                iconPane.add(lightBulbIcon, 0, 0);
                lightBulbIcon.setBackgroundIconColorAnimated(Constants.LIGHTBULB_COLOR);
                observerText.setIdentifier("lightOn");
                if (!toggleSwitch.isSelected()) {
                    toggleSwitch.setSelected(true);
                }   break;
            case OFF:
                iconPane.add(lightBulbIcon, 0, 0);
                lightBulbIcon.setBackgroundIconColorAnimated(Color.TRANSPARENT);
                observerText.setIdentifier("lightOff");
                if (toggleSwitch.isSelected()) {
                    toggleSwitch.setSelected(false);
                }   break;
            default:
                iconPane.add(unknownBackgroundIcon, 0, 0);
                iconPane.add(unknownForegroundIcon, 0, 0);
                observerText.setIdentifier("unknown");
                break;
        }
    }

    private void sendStateToRemote(final State state) {
        try {
            lightRemote.setPowerState(state).get(Constants.OPERATION_SERVICE_MILLI_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException | CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER, LogLevel.ERROR);
        }
    }

    @Override
    protected void initTitle() {
        oneClick.addListener((observable, oldValue, newValue) -> GlobalCachedExecutorService.submit(new Task() {
            @Override
            protected Object call() {
                if (toggleSwitch.isSelected()) {
                    sendStateToRemote(PowerStateType.PowerState.State.OFF);
                } else {
                    sendStateToRemote(PowerStateType.PowerState.State.ON);
                }
                return null;
            }
        }));

        toggleSwitch.setOnMouseClicked(event -> GlobalCachedExecutorService.submit(new Task() {
            @Override
            protected Object call() {
                if (toggleSwitch.isSelected()) {
                    sendStateToRemote(PowerStateType.PowerState.State.ON);
                } else {
                    sendStateToRemote(PowerStateType.PowerState.State.OFF);
                }
                return null;
            }
        }));

        unknownForegroundIcon.setForegroundIconColor(Color.BLUE);
        unknownBackgroundIcon.setForegroundIconColor(Color.WHITE);
        lightBulbIcon.setBackgroundIconColorAnimated(Color.TRANSPARENT);

        headContent.setCenter(getUnitLabel());
        headContent.setAlignment(getUnitLabel(), Pos.CENTER_LEFT);
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
    public UnitRemote getDALRemoteService() {
        return lightRemote;
    }

    @Override
    void removeObserver() {
        this.lightRemote.removeObserver(this);
    }

    @Override
    public void update(final Observable observable, final Object light) throws java.lang.Exception {
        Platform.runLater(() -> {
            final State powerState = ((LightData) light).getPowerState().getValue();
            setPowerStateSwitchAndIcon(powerState);
        });
    }
}
