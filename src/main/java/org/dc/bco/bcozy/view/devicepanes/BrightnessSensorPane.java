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
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.dc.bco.dal.remote.unit.BrightnessSensorRemote;
import org.dc.bco.dal.remote.unit.DALRemoteService;
import org.dc.jul.exception.CouldNotPerformException;
import org.dc.jul.exception.printer.ExceptionPrinter;
import org.dc.jul.exception.printer.LogLevel;
import org.dc.jul.pattern.Observable;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import org.dc.bco.bcozy.view.Constants;
import org.dc.bco.bcozy.view.SVGIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.homeautomation.unit.BrightnessSensorType.BrightnessSensor;

/**
 * Created by timo on 15.01.16.
 */
public class BrightnessSensorPane extends UnitPane {
    private static final Logger LOGGER = LoggerFactory.getLogger(BatteryPane.class);

    private final BrightnessSensorRemote brightnessSensorRemote;
    private final SVGIcon brightnessIcon;
    private final Text brightnessStatus;
    private final GridPane iconPane;
    private final BorderPane headContent;

    /**
     * Constructor for the BatteryPane.
     * @param brightnessSensorRemote brightnessSensorRemote
     */
    public BrightnessSensorPane(final DALRemoteService brightnessSensorRemote) {
        this.brightnessSensorRemote = (BrightnessSensorRemote) brightnessSensorRemote;

        headContent = new BorderPane();
        brightnessIcon = new SVGIcon(FontAwesomeIcon.CIRCLE, FontAwesomeIcon.CIRCLE_THIN,
                Constants.SMALL_ICON);
        brightnessStatus = new Text();
        iconPane = new GridPane();

        try {
            super.setUnitLabel(this.brightnessSensorRemote.getLatestValue().getLabel());
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
            super.setUnitLabel("UnknownID");
        }

        initTitle();
        initContent();
        createWidgetPane(headContent);

        try {
            initEffect();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }

        this.brightnessSensorRemote.addObserver(this);
    }

    private void initEffect() throws CouldNotPerformException {
        final double brightnessLevel = brightnessSensorRemote.getBrightness();

        this.brightnessIcon.setBackgroundIconColorAnimated(
                new Color(brightnessLevel, brightnessLevel, brightnessLevel, 1));

        this.brightnessStatus.setText((int) brightnessLevel + ""); //NOPMD //TODO: add measure
    }

    /**
     * Method creates the header content of the widgetPane.
     */

    @Override
    protected void initTitle() {
        brightnessIcon.setBackgroundIconColorAnimated(Color.TRANSPARENT);

        iconPane.add(brightnessIcon, 0, 0);
        iconPane.add(brightnessStatus, 1, 0);

        headContent.setLeft(iconPane);
        headContent.setCenter(new Label(super.getUnitLabel()));
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

            this.brightnessIcon.setBackgroundIconColorAnimated(
                    new Color(brightnessLevel, brightnessLevel, brightnessLevel, 1));

            this.brightnessStatus.setText((int) brightnessLevel + ""); //NOPMD //TODO: add measure
        });
    }
}
