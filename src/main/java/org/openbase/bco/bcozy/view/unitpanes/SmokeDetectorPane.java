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
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.SVGIcon;
import org.openbase.bco.dal.remote.unit.SmokeDetectorRemote;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.openbase.jul.extension.rsb.com.AbstractIdentifiableRemote;
import org.openbase.jul.pattern.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.state.AlarmStateType.AlarmState.State;
import rst.domotic.state.SmokeStateType.SmokeState;
import rst.domotic.unit.dal.SmokeDetectorDataType.SmokeDetectorData;

/**
 * Created by agatting on 11.04.16.
 */
public class SmokeDetectorPane extends AbstractUnitPane {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmokeDetectorPane.class);

    private final SmokeDetectorRemote remote;
    private final SVGIcon smokeDetectorIconFire;
    private final SVGIcon smokeDetectorIconNoFire;
    private final SVGIcon smokeDetectorIconFireFade;
    private final SVGIcon unknownForegroundIcon;
    private final SVGIcon unknownBackgroundIcon;
    private final BorderPane headContent;
    private final FadeTransition flashAnimation;

    /**
     * Constructor for the TamperSwitchPane.
     *
     * @param smokeDetectorRemote smokeDetectorRemote.
     */
    public SmokeDetectorPane(final AbstractIdentifiableRemote smokeDetectorRemote) {
        this.remote = (SmokeDetectorRemote) smokeDetectorRemote;

        headContent = new BorderPane();
        smokeDetectorIconFire = new SVGIcon(MaterialDesignIcon.FIRE, Constants.SMALL_ICON, false);
        smokeDetectorIconNoFire = new SVGIcon(MaterialDesignIcon.CHECKBOX_MARKED_CIRCLE, Constants.SMALL_ICON, false);
        smokeDetectorIconFireFade = new SVGIcon(MaterialDesignIcon.FIRE, Constants.SMALL_ICON, false);
        unknownBackgroundIcon = new SVGIcon(MaterialDesignIcon.CHECKBOX_BLANK_CIRCLE, Constants.SMALL_ICON - 2, false);
        unknownForegroundIcon = new SVGIcon(MaterialDesignIcon.HELP_CIRCLE, Constants.SMALL_ICON, false);

        flashAnimation = new FadeTransition(Duration.millis(Constants.SMOKE_DETECTOR_FADE_DURATION));
        flashAnimation.setNode(smokeDetectorIconFireFade);
        flashAnimation.setFromValue(0.0);
        flashAnimation.setToValue(1.0);
        flashAnimation.setCycleCount(Timeline.INDEFINITE);
        flashAnimation.setAutoReverse(true);

        initUnitLabel();
        initTitle();
        initContent();
        createWidgetPane(headContent, false);
        initEffect();
        tooltip.textProperty().bind(observerText.textProperty());

        this.remote.addDataObserver(this);
    }

    private void initEffect() {
        State alarmState = State.UNKNOWN;
        SmokeState.State smokeState = SmokeState.State.UNKNOWN;

        try {
            alarmState = remote.getSmokeAlarmState().getValue();
            smokeState = remote.getSmokeState().getValue();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
        setSmokeDetectorIconAndText(alarmState, smokeState);
    }

    private void setSmokeDetectorIconAndText(final State alarmState, final SmokeState.State smokeState) {
        iconPane.getChildren().clear();

        if (smokeState == SmokeState.State.SMOKE) {
            smokeDetectorIconFire.setForegroundIconColor(Color.color(0, 0, 0, 1.0));
            iconPane.add(smokeDetectorIconFire, 0, 0);
            observerText.setIdentifier("smoke");
        } else if (smokeState == SmokeState.State.SOME_SMOKE) {
            smokeDetectorIconFire.setForegroundIconColor(Color.color(0, 0, 0, Constants.HALF_TRANSPARENT));
            iconPane.add(smokeDetectorIconFire, 0, 0);
            observerText.setIdentifier("someSmoke");
        } else if (smokeState == SmokeState.State.NO_SMOKE) {
            iconPane.add(smokeDetectorIconNoFire, 0, 0);
            observerText.setIdentifier("noSmoke");
        } else if (smokeState == SmokeState.State.UNKNOWN || alarmState == State.UNKNOWN) {
            iconPane.add(unknownBackgroundIcon, 0, 0);
            iconPane.add(unknownForegroundIcon, 0, 0);
            observerText.setIdentifier("unknown");
        }

        if (alarmState == State.ALARM && smokeState != SmokeState.State.UNKNOWN) {
            if (smokeState == SmokeState.State.SMOKE || smokeState == SmokeState.State.SOME_SMOKE) {
                smokeDetectorIconFireFade.changeForegroundIcon(MaterialDesignIcon.FIRE);
            } else if (smokeState == SmokeState.State.NO_SMOKE) {
                smokeDetectorIconFireFade.changeForegroundIcon(MaterialDesignIcon.CHECKBOX_MARKED_CIRCLE);
                observerText.setIdentifier("alarm");
            }
            iconPane.add(smokeDetectorIconFireFade, 0, 0);

            flashAnimation.play();
        } else {
            flashAnimation.stop();
        }
    }

    @Override
    protected void initTitle() {
        unknownForegroundIcon.setForegroundIconColor(Color.BLUE);
        unknownBackgroundIcon.setForegroundIconColor(Color.WHITE);

        smokeDetectorIconFireFade.setForegroundIconColor(Color.RED);

        headContent.setCenter(getUnitLabel());
        headContent.setAlignment(getUnitLabel(), Pos.CENTER_LEFT);
        headContent.prefHeightProperty().set(smokeDetectorIconFire.getSize() + Constants.INSETS);
    }

    @Override
    protected void initContent() {
        //No body content.
    }

    @Override
    protected void initUnitLabel() {
        String unitLabel = Constants.UNKNOWN_ID;
        try {
            unitLabel = remote.getLocationConfig().getLabel() + " " + remote.getData().getLabel();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
        setUnitLabelString(unitLabel);
    }

    @Override
    public AbstractIdentifiableRemote getDALRemoteService() {
        return remote;
    }

    @Override
    void removeObserver() {
        this.remote.removeObserver(this);
    }

    @Override
    public void update(final Observable observable, final Object smokeDetector) throws java.lang.Exception {
        Platform.runLater(() -> {
            final State alarmState = ((SmokeDetectorData) smokeDetector).getSmokeAlarmState().getValue();
            final SmokeState.State smokeState
                    = ((SmokeDetectorData) smokeDetector).getSmokeState().getValue();

            setSmokeDetectorIconAndText(alarmState, smokeState);
        });
    }
}
