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
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.dc.bco.bcozy.view.Constants;
import org.dc.bco.bcozy.view.SVGIcon;
import org.dc.bco.dal.remote.unit.BrightnessSensorRemote;
import org.dc.jul.extension.rsb.com.AbstractIdentifiableRemote;
import org.dc.jul.exception.CouldNotPerformException;
import org.dc.jul.exception.printer.ExceptionPrinter;
import org.dc.jul.exception.printer.LogLevel;
import org.dc.jul.pattern.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.homeautomation.unit.BrightnessSensorType.BrightnessSensor;

/**
 * Created by tmichalski on 15.01.16.
 */
public class BrightnessSensorPane extends UnitPane {
    private static final Logger LOGGER = LoggerFactory.getLogger(BatteryPane.class);

    private final BrightnessSensorRemote brightnessSensorRemote;
    private final SVGIcon brightnessIcon;
    private final Text brightnessStatus;
    private final BorderPane headContent;

    /**
     * Constructor for the BrightnessSensorPane.
     * @param brightnessSensorRemote brightnessSensorRemote
     */
    public BrightnessSensorPane(final AbstractIdentifiableRemote brightnessSensorRemote) {
        this.brightnessSensorRemote = (BrightnessSensorRemote) brightnessSensorRemote;

        headContent = new BorderPane();
        brightnessIcon = new SVGIcon(MaterialDesignIcon.CHECKBOX_BLANK_CIRCLE,
                MaterialDesignIcon.CHECKBOX_BLANK_CIRCLE_OUTLINE, Constants.SMALL_ICON);
        brightnessStatus = new Text();

        initUnitLabel();
        initTitle();
        initContent();
        createWidgetPane(headContent, false);
        initEffect();
        tooltip.textProperty().bind(observerText.textProperty());

        this.brightnessSensorRemote.addObserver(this);
    }

    private void initEffect() {
        double brightnessLevel = 0;

        try {
            brightnessLevel = brightnessSensorRemote.getBrightness();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
        setBrightnessLevelTextAndIcon(brightnessLevel);
    }

    private void setBrightnessLevelTextAndIcon(final double brightnessLevel) {
        if (brightnessLevel <= Constants.BRIGHTNESS_MAXIMUM) {
            //root function to get a not linear representation of the brightness (icon color).
            final double rootFunction = Math.sqrt(brightnessLevel / Constants.BRIGHTNESS_MAXIMUM);
            this.brightnessIcon.setBackgroundIconColorAnimated(new Color(rootFunction, rootFunction, 0, 1));
        } else {
            this.brightnessIcon.setBackgroundIconColorAnimated(new Color(1, 1, 1, 1));
        }
        observerText.setIdentifier("brightness");

        this.brightnessStatus.setText((int) brightnessLevel + "lx");
    }

    @Override
    protected void initTitle() {
        brightnessStatus.getStyleClass().add(Constants.ICONS_CSS_STRING);
        brightnessIcon.setBackgroundIconColorAnimated(Color.TRANSPARENT);

        iconPane.add(brightnessIcon, 0, 0);
        iconPane.add(brightnessStatus, 1, 0);
        iconPane.setHgap(Constants.INSETS);

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
            unitLabel = this.brightnessSensorRemote.getData().getLabel();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
        setUnitLabelString(unitLabel);
    }

    @Override
    public AbstractIdentifiableRemote getDALRemoteService() {
        return brightnessSensorRemote;
    }

    @Override
    void removeObserver() {
        this.brightnessSensorRemote.removeObserver(this);
    }

    @Override
    public void update(final Observable observable, final Object brightnessSensor) throws java.lang.Exception {
        Platform.runLater(() -> {
            final double brightnessLevel = ((BrightnessSensor) brightnessSensor).getBrightness();
            setBrightnessLevelTextAndIcon(brightnessLevel);
        });
    }
}
