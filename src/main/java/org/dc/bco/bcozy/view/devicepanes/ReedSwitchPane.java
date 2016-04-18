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
import org.dc.bco.dal.remote.unit.ReedSwitchRemote;
import org.dc.jul.exception.CouldNotPerformException;
import org.dc.jul.exception.printer.ExceptionPrinter;
import org.dc.jul.exception.printer.LogLevel;
import org.dc.jul.pattern.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.homeautomation.state.ReedSwitchStateType.ReedSwitchState.State;
import rst.homeautomation.unit.ReedSwitchType;

/**
 * Created by agatting on 27.01.16.
 */
public class ReedSwitchPane extends UnitPane {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReedSwitchPane.class);

    private final ReedSwitchRemote reedSwitchRemote;
    private final SVGIcon unknownForegroundIcon;
    private final SVGIcon unknownBackgroundIcon;
    private final SVGIcon reedSwitchIcon;
    private final BorderPane headContent;

    /**
     * Constructor for the ReedSwitchPane.
     * @param reedSwitchRemote reedSwitchRemote
     */
    public ReedSwitchPane(final AbstractIdentifiableRemote reedSwitchRemote) {
        this.reedSwitchRemote = (ReedSwitchRemote) reedSwitchRemote;

        reedSwitchIcon = new SVGIcon(MaterialIcon.RADIO_BUTTON_CHECKED, Constants.SMALL_ICON, true);
        unknownBackgroundIcon = new SVGIcon(MaterialDesignIcon.CHECKBOX_BLANK_CIRCLE, Constants.SMALL_ICON - 2, false);
        unknownForegroundIcon = new SVGIcon(MaterialDesignIcon.HELP_CIRCLE, Constants.SMALL_ICON, false);
        headContent = new BorderPane();

        initUnitLabel();
        initTitle();
        initContent();
        createWidgetPane(headContent, false);
        initEffectAndText();
        tooltip.textProperty().bind(observerText.textProperty());

        this.reedSwitchRemote.addObserver(this);
    }

    private void initEffectAndText() {
        State reedSwitchState = State.UNKNOWN;

        try {
            reedSwitchState = reedSwitchRemote.getReedSwitch().getValue();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
        setReedSwitchIconAndTooltip(reedSwitchState);
    }

    private void setReedSwitchIconAndTooltip(final State reedSwitchState) {
        iconPane.getChildren().clear();

        if (reedSwitchState == State.CLOSED) {
            reedSwitchIcon.changeForegroundIcon(MaterialIcon.RADIO_BUTTON_CHECKED);
            iconPane.add(reedSwitchIcon, 0, 0);
            observerText.setIdentifier("closed");
        } else if (reedSwitchState == State.OPEN) {
            reedSwitchIcon.changeForegroundIcon(MaterialIcon.RADIO_BUTTON_UNCHECKED);
            iconPane.add(reedSwitchIcon, 0, 0);
            observerText.setIdentifier("open");
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

        headContent.setCenter(getUnitLabel());
        headContent.setAlignment(getUnitLabel(), Pos.CENTER_LEFT);
        headContent.prefHeightProperty().set(reedSwitchIcon.getSize() + Constants.INSETS);
    }

    @Override
    protected void initContent() {
        //No body content.
    }

    @Override
    protected void initUnitLabel() {
        String unitLabel = Constants.UNKNOWN_ID;
        try {
            unitLabel = this.reedSwitchRemote.getData().getLabel();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
        setUnitLabelString(unitLabel);
    }

    @Override
    public AbstractIdentifiableRemote getDALRemoteService() {
        return reedSwitchRemote;
    }

    @Override
    void removeObserver() {
        this.reedSwitchRemote.removeObserver(this);
    }

    @Override
    public void update(final Observable observable, final Object reedSwitch) throws java.lang.Exception {
        Platform.runLater(() -> {
            final State reedSwitchState = ((ReedSwitchType.ReedSwitch) reedSwitch).getReedSwitchState().getValue();

            setReedSwitchIconAndTooltip(reedSwitchState);
        });
    }
}
