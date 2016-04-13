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
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import org.dc.bco.bcozy.view.Constants;
import org.dc.bco.bcozy.view.SVGIcon;
import org.dc.jul.extension.rsb.com.AbstractIdentifiableRemote;
import org.dc.bco.dal.remote.unit.MotionSensorRemote;
import org.dc.jul.exception.CouldNotPerformException;
import org.dc.jul.exception.printer.ExceptionPrinter;
import org.dc.jul.exception.printer.LogLevel;
import org.dc.jul.pattern.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.homeautomation.state.MotionStateType.MotionState.State;
import rst.homeautomation.unit.MotionSensorType.MotionSensor;

/**
 * Created by tmichalski on 15.01.16.
 */
public class MotionSensorPane extends UnitPane {
    private static final Logger LOGGER = LoggerFactory.getLogger(BatteryPane.class);

    private final MotionSensorRemote motionSensorRemote;
    private final SVGIcon unknownForegroundIcon;
    private final SVGIcon unknownBackgroundIcon;
    private final SVGIcon backgroundIcon;
    private final SVGIcon motionIcon;
    private final BorderPane headContent;


    /**
     * Constructor for the BatteryPane.
     * @param brightnessSensorRemote motionSensorRemote
     */
    public MotionSensorPane(final AbstractIdentifiableRemote brightnessSensorRemote) {
        this.motionSensorRemote = (MotionSensorRemote) brightnessSensorRemote;

        headContent = new BorderPane();
        unknownBackgroundIcon = new SVGIcon(MaterialDesignIcon.CHECKBOX_BLANK_CIRCLE, Constants.SMALL_ICON - 2, false);
        unknownForegroundIcon = new SVGIcon(MaterialDesignIcon.HELP_CIRCLE, Constants.SMALL_ICON, false);
        motionIcon = new SVGIcon(MaterialIcon.BLUR_ON, MaterialIcon.PANORAMA_FISH_EYE, Constants.SMALL_ICON);
        backgroundIcon = new SVGIcon(MaterialIcon.LENS, Constants.SMALL_ICON, false);

        initUnitLabel();
        initTitle();
        initContent();
        createWidgetPane(headContent, false);
        initEffect();
        tooltip.textProperty().bind(observerText.textProperty());

        this.motionSensorRemote.addObserver(this);
    }

    private void initEffect() {
        State motionState = State.UNKNOWN;

        try {
            motionState = motionSensorRemote.getMotion().getValue();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
        setMotionStateIconAndTooltip(motionState);
    }

    private void setMotionStateIconAndTooltip(final State motionState) {
        iconPane.getChildren().clear();

        if (motionState.equals(State.MOVEMENT)) {
            motionIcon.setBackgroundIconColorAnimated(Color.WHITE);
            iconPane.add(backgroundIcon, 0, 0);
            iconPane.add(motionIcon, 0, 0);
            observerText.setIdentifier("movement");
        } else if (motionState.equals(State.NO_MOVEMENT)) {
            motionIcon.setBackgroundIconColorAnimated(Color.TRANSPARENT);
            iconPane.add(backgroundIcon, 0, 0);
            iconPane.add(motionIcon, 0, 0);
            observerText.setIdentifier("noMovement");
        } else {
            iconPane.add(unknownBackgroundIcon, 0, 0);
            iconPane.add(unknownForegroundIcon, 0, 0);
            observerText.setIdentifier("unknown");
        }
    }

    @Override
    protected void initTitle() {
        unknownForegroundIcon.setForegroundIconColor(Color.BLUE);
        unknownBackgroundIcon.setForegroundIconColor(Color.WHITE);
        backgroundIcon.setForegroundIconColor(Color.BLACK);

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
            unitLabel = this.motionSensorRemote.getData().getLabel();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
        setUnitLabelString(unitLabel);
    }

    @Override
    public AbstractIdentifiableRemote getDALRemoteService() {
        return motionSensorRemote;
    }

    @Override
    void removeObserver() {
        this.motionSensorRemote.removeObserver(this);
    }

    @Override
    public void update(final Observable observable, final Object motionSensor) throws java.lang.Exception {
        Platform.runLater(() -> {
            final State motionState = ((MotionSensor) motionSensor).getMotionState().getValue();
            setMotionStateIconAndTooltip(motionState);
        });
    }
}
