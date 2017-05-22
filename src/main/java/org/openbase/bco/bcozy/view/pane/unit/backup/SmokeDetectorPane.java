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
import org.openbase.bco.dal.remote.unit.SmokeDetectorRemote;
import rst.domotic.unit.dal.SmokeDetectorDataType.SmokeDetectorData;

/**
 * Created by agatting on 11.04.16.
 */
public class SmokeDetectorPane extends AbstractUnitPane<SmokeDetectorRemote, SmokeDetectorData> {

//    private static final Logger LOGGER = LoggerFactory.getLogger(SmokeDetectorPane.class);
//
//    private final SmokeDetectorRemote smokeDetectorRemote;
//    private final SVGIcon smokeDetectorIconFire;
//    private final SVGIcon smokeDetectorIconNoFire;
//    private final SVGIcon smokeDetectorIconFireFade;
//    private final SVGIcon unknownForegroundIcon;
//    private final SVGIcon unknownBackgroundIcon;
//    private final BorderPane headContent;
//    private final FadeTransition flashAnimation;
//
//    /**
//     * Constructor for the TamperSwitchPane.
//     *
//     * @param smokeDetectorRemote smokeDetectorRemote.
//     */
    public SmokeDetectorPane() {
        super(SmokeDetectorRemote.class, false);
//
//        headContent = new BorderPane();
//        smokeDetectorIconFire = new SVGIcon(MaterialDesignIcon.FIRE, Constants.SMALL_ICON, false);
//        smokeDetectorIconNoFire = new SVGIcon(MaterialDesignIcon.CHECKBOX_MARKED_CIRCLE, Constants.SMALL_ICON, false);
//        smokeDetectorIconFireFade = new SVGIcon(MaterialDesignIcon.FIRE, Constants.SMALL_ICON, false);
//        unknownBackgroundIcon = new SVGIcon(MaterialDesignIcon.CHECKBOX_BLANK_CIRCLE, Constants.SMALL_ICON - 2, false);
//        unknownForegroundIcon = new SVGIcon(MaterialDesignIcon.HELP_CIRCLE, Constants.SMALL_ICON, false);
//
//        flashAnimation = new FadeTransition(Duration.millis(Constants.SMOKE_DETECTOR_FADE_DURATION));
//        flashAnimation.setNode(smokeDetectorIconFireFade);
//        flashAnimation.setFromValue(0.0);
//        flashAnimation.setToValue(1.0);
//        flashAnimation.setCycleCount(Timeline.INDEFINITE);
//        flashAnimation.setAutoReverse(true);
//
//        initTitle();
//        initBodyContent();
//        createWidgetPane(headContent, false);
//        initEffect();
//        tooltip.textProperty().bind(labelText.textProperty());
    }
//
//    private void initEffect() {
//        State alarmState = State.UNKNOWN;
//        SmokeState.State smokeState = SmokeState.State.UNKNOWN;
//
//        try {
//            alarmState = smokeDetectorRemote.getSmokeAlarmState().getValue();
//            smokeState = smokeDetectorRemote.getSmokeState().getValue();
//        } catch (CouldNotPerformException e) {
//            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
//        }
//        setSmokeDetectorIconAndText(alarmState, smokeState);
//    }
//
//    private void setSmokeDetectorIconAndText(final State alarmState, final SmokeState.State smokeState) {
//        iconPane.getChildren().clear();
//
//        if (smokeState == SmokeState.State.SMOKE) {
//            smokeDetectorIconFire.setForegroundIconColor(Color.color(0, 0, 0, 1.0));
//            iconPane.add(smokeDetectorIconFire, 0, 0);
//            labelText.setIdentifier("smoke");
//        } else if (smokeState == SmokeState.State.SOME_SMOKE) {
//            smokeDetectorIconFire.setForegroundIconColor(Color.color(0, 0, 0, Constants.HALF_TRANSPARENT));
//            iconPane.add(smokeDetectorIconFire, 0, 0);
//            labelText.setIdentifier("someSmoke");
//        } else if (smokeState == SmokeState.State.NO_SMOKE) {
//            iconPane.add(smokeDetectorIconNoFire, 0, 0);
//            labelText.setIdentifier("noSmoke");
//        } else if (smokeState == SmokeState.State.UNKNOWN || alarmState == State.UNKNOWN) {
//            iconPane.add(unknownBackgroundIcon, 0, 0);
//            iconPane.add(unknownForegroundIcon, 0, 0);
//            labelText.setIdentifier("unknown");
//        }
//
//        if (alarmState == State.ALARM && smokeState != SmokeState.State.UNKNOWN) {
//            if (smokeState == SmokeState.State.SMOKE || smokeState == SmokeState.State.SOME_SMOKE) {
//                smokeDetectorIconFireFade.changeForegroundIcon(MaterialDesignIcon.FIRE);
//            } else if (smokeState == SmokeState.State.NO_SMOKE) {
//                smokeDetectorIconFireFade.changeForegroundIcon(MaterialDesignIcon.CHECKBOX_MARKED_CIRCLE);
//                labelText.setIdentifier("alarm");
//            }
//            iconPane.add(smokeDetectorIconFireFade, 0, 0);
//
//            flashAnimation.play();
//        } else {
//            flashAnimation.stop();
//        }
//    }
//
//    @Override
//    protected void initTitle() {
//        unknownForegroundIcon.setForegroundIconColor(Color.BLUE);
//        unknownBackgroundIcon.setForegroundIconColor(Color.WHITE);
//
//        smokeDetectorIconFireFade.setForegroundIconColor(Color.RED);
//
//        headContent.setCenter(getUnitLabel());
//        headContent.setAlignment(getUnitLabel(), Pos.CENTER_LEFT);
//        headContent.prefHeightProperty().set(smokeDetectorIconFire.getSize() + Constants.INSETS);
//    }
//
//    @Override
//    protected void initBodyContent() {
//        //No body content.
//    }
//
//    @Override
//    public void update(final Observable observable, final Object smokeDetector) throws java.lang.Exception {
//        Platform.runLater(() -> {
//            final State alarmState = ((SmokeDetectorData) smokeDetector).getSmokeAlarmState().getValue();
//            final SmokeState.State smokeState
//                    = ((SmokeDetectorData) smokeDetector).getSmokeState().getValue();
//
//            setSmokeDetectorIconAndText(alarmState, smokeState);
//        });
//    }
}
