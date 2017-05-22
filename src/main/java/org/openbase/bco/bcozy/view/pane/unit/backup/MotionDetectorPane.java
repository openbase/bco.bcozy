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
import org.openbase.bco.dal.remote.unit.MotionDetectorRemote;
import org.openbase.bco.dal.lib.layer.unit.UnitRemote;
import rst.domotic.unit.dal.MotionDetectorDataType.MotionDetectorData;

/**
 * Created by tmichalski on 15.01.16.
 */
public class MotionDetectorPane extends AbstractUnitPane<MotionDetectorRemote, MotionDetectorData> {

//    private static final Logger LOGGER = LoggerFactory.getLogger(BatteryPane.class);
//
//    private final SVGIcon unknownForegroundIcon;
//    private final SVGIcon unknownBackgroundIcon;
//    private final SVGIcon backgroundIcon;
//    private final SVGIcon motionIcon;
//    private final BorderPane headContent;
//
//    /**
//     * Constructor for the BatteryPane.
//     *
//     * @param brightnessSensorRemote motionSensorRemote
//     */
    public MotionDetectorPane(final UnitRemote brightnessSensorRemote) {
        super(MotionDetectorRemote.class, false);
//
//        headContent = new BorderPane();
//        unknownBackgroundIcon = new SVGIcon(MaterialDesignIcon.CHECKBOX_BLANK_CIRCLE, Constants.SMALL_ICON - 2, false);
//        unknownForegroundIcon = new SVGIcon(MaterialDesignIcon.HELP_CIRCLE, Constants.SMALL_ICON, false);
//        motionIcon = new SVGIcon(MaterialIcon.BLUR_ON, MaterialIcon.PANORAMA_FISH_EYE, Constants.SMALL_ICON);
//        backgroundIcon = new SVGIcon(MaterialIcon.LENS, Constants.SMALL_ICON, false);
//
//        initHeadContent();
//        initBodyContent();
//        createWidgetPane(headContent, false);
//        initEffect();
//        tooltip.textProperty().bind(labelText.textProperty());
    }
//
//    private void initEffect() {
//        State motionState = State.UNKNOWN;
//
//        try {
//            motionState = getUnitRemote().getMotionState().getValue();
//        } catch (CouldNotPerformException e) {
//            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
//        }
//        setMotionStateIconAndTooltip(motionState);
//    }
//
//    private void setMotionStateIconAndTooltip(final State motionState) {
//        iconPane.getChildren().clear();
//
//        if (motionState.equals(State.MOTION)) {
//            motionIcon.setBackgroundIconColorAnimated(Color.WHITE);
//            iconPane.add(backgroundIcon, 0, 0);
//            iconPane.add(motionIcon, 0, 0);
//            labelText.setIdentifier("movement");
//        } else if (motionState.equals(State.NO_MOTION)) {
//            motionIcon.setBackgroundIconColorAnimated(Color.TRANSPARENT);
//            iconPane.add(backgroundIcon, 0, 0);
//            iconPane.add(motionIcon, 0, 0);
//            labelText.setIdentifier("noMovement");
//        } else {
//            iconPane.add(unknownBackgroundIcon, 0, 0);
//            iconPane.add(unknownForegroundIcon, 0, 0);
//            labelText.setIdentifier("unknown");
//        }
//    }
//
//    protected void initHeadContent() {
//        unknownForegroundIcon.setForegroundIconColor(Color.BLUE);
//        unknownBackgroundIcon.setForegroundIconColor(Color.WHITE);
//        backgroundIcon.setForegroundIconColor(Color.BLACK);
//
//        headContent.setCenter(getUnitLabel());
//        headContent.setAlignment(getUnitLabel(), Pos.CENTER_LEFT);
//        headContent.prefHeightProperty().set(iconPane.getHeight() + Constants.INSETS);
//    }
//
//    @Override
//    protected void initBodyContent() {
//        //No body content.
//    }
//
//    @Override
//    void applyDataUpdate(MotionDetectorData data) {
//        Platform.runLater(() -> {
//            setMotionStateIconAndTooltip(data.getMotionState().getValue());
//        });
//    }
}
