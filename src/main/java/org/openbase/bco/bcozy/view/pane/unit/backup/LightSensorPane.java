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
import org.openbase.bco.dal.remote.unit.LightSensorRemote;
import rst.domotic.unit.dal.LightSensorDataType;

/**
 * Created by tmichalski on 15.01.16.
 */
public class LightSensorPane extends AbstractUnitPane<LightSensorRemote, LightSensorDataType.LightSensorData> {

//    private static final Logger LOGGER = LoggerFactory.getLogger(BatteryPane.class);
//
//    private final SVGIcon brightnessIcon;
//    private final Text brightnessStatus;
//    private final BorderPane headContent;
//
//    /**
//     * Constructor for the LightSensorPane.
//     */
    public LightSensorPane() {
        super(LightSensorRemote.class, true);
//
//        headContent = new BorderPane();
//        brightnessIcon = new SVGIcon(MaterialDesignIcon.CHECKBOX_BLANK_CIRCLE, MaterialDesignIcon.CHECKBOX_BLANK_CIRCLE_OUTLINE, Constants.SMALL_ICON);
//        brightnessStatus = new Text();
//
//        initTitle();
//        initBodyContent();
//        createWidgetPane(headContent, false);
//        initEffect();
//        tooltip.textProperty().bind(labelText.textProperty());
    }
//
//    private void initEffect() {
//        double brightnessLevel = 0;
//
//        try {
//            brightnessLevel = lightSensorremote.getBrightnessState().getBrightness();
//        } catch (CouldNotPerformException e) {
//            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
//        }
//        setBrightnessLevelTextAndIcon(brightnessLevel);
//    }
//
//    private void setBrightnessLevelTextAndIcon(final double brightnessLevel) {
//        if (brightnessLevel <= Constants.BRIGHTNESS_MAXIMUM) {
//            //root function to get a not linear representation of the brightness (icon color).
//            final double rootFunction = Math.sqrt(brightnessLevel / Constants.BRIGHTNESS_MAXIMUM);
//            this.brightnessIcon.setBackgroundIconColorAnimated(new Color(rootFunction, rootFunction, 0, 1));
//        } else {
//            this.brightnessIcon.setBackgroundIconColorAnimated(new Color(1, 1, 1, 1));
//        }
//        labelText.setIdentifier("brightness");
//
//        this.brightnessStatus.setText((int) brightnessLevel + "lx");
//    }
//
//    @Override
//    protected void initTitle() {
//        brightnessStatus.getStyleClass().add(Constants.ICONS_CSS_STRING);
//        brightnessIcon.setBackgroundIconColorAnimated(Color.TRANSPARENT);
//
//        iconPane.add(brightnessIcon, 0, 0);
//        iconPane.add(brightnessStatus, 1, 0);
//        iconPane.setHgap(Constants.INSETS);
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
//    public void update(final Observable observable, final Object brightnessSensor) throws java.lang.Exception {
//        Platform.runLater(() -> {
//            final double brightnessLevel
//                    = ((BrightnessSensorData) brightnessSensor).getBrightnessState().getBrightness();
//            setBrightnessLevelTextAndIcon(brightnessLevel);
//        });
//    }
}
