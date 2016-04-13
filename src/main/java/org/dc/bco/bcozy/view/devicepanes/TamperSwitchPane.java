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
import org.dc.bco.bcozy.view.Constants;
import org.dc.bco.bcozy.view.SVGIcon;
import org.dc.bco.dal.remote.unit.TamperSwitchRemote;
import org.dc.jul.exception.CouldNotPerformException;
import org.dc.jul.exception.printer.ExceptionPrinter;
import org.dc.jul.exception.printer.LogLevel;
import org.dc.jul.extension.rsb.com.AbstractIdentifiableRemote;
import org.dc.jul.pattern.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.homeautomation.state.TamperStateType.TamperState.State;
import rst.homeautomation.unit.TamperSwitchType;

/**
 * Created by agatting on 11.04.16.
 */
public class TamperSwitchPane extends UnitPane {
    private static final Logger LOGGER = LoggerFactory.getLogger(TamperSwitchPane.class);

    private final TamperSwitchRemote tamperSwitchRemote;
    private final SVGIcon tamperSwitchIconOk;
    private final SVGIcon tamperSwitchIconManipulation;
    private final SVGIcon unknownForegroundIcon;
    private final SVGIcon unknownBackgroundIcon;
    private final BorderPane headContent;

    /**
     * Constructor for the TamperSwitchPane.
     * @param tamperSwitchRemote tamperSwitchRemote
     */
    public TamperSwitchPane(final AbstractIdentifiableRemote tamperSwitchRemote) {
        this.tamperSwitchRemote = (TamperSwitchRemote) tamperSwitchRemote;

        headContent = new BorderPane();
        tamperSwitchIconOk = new SVGIcon(MaterialDesignIcon.CHECKBOX_MARKED_CIRCLE, Constants.SMALL_ICON, false);
        tamperSwitchIconManipulation = new SVGIcon(MaterialDesignIcon.ALERT_CIRCLE, Constants.SMALL_ICON, false);
        unknownBackgroundIcon = new SVGIcon(MaterialDesignIcon.CHECKBOX_BLANK_CIRCLE, Constants.SMALL_ICON - 2, false);
        unknownForegroundIcon = new SVGIcon(MaterialDesignIcon.HELP_CIRCLE, Constants.SMALL_ICON, false);

        initUnitLabel();
        initTitle();
        initContent();
        createWidgetPane(headContent, false);
        initEffect();
        tooltip.textProperty().bind(observerText.textProperty());

        this.tamperSwitchRemote.addObserver(this);
    }

    private void initEffect() {
        State tamperSwitchState = State.UNKNOWN;

        try {
            tamperSwitchState = tamperSwitchRemote.getTamper().getValue();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
        setTamperSwitchIconAndText(tamperSwitchState);
    }

    private void setTamperSwitchIconAndText(final State tamperSwitchState) {
        iconPane.getChildren().clear();

        if (tamperSwitchState == State.NO_TAMPER) {
            iconPane.add(tamperSwitchIconOk, 0, 0);
            observerText.setIdentifier("noTamper");
        } else if (tamperSwitchState == State.TAMPER) {
            iconPane.add(tamperSwitchIconManipulation, 0, 0);
            observerText.setIdentifier("tamper");
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
        tamperSwitchIconOk.setForegroundIconColor(Color.GREEN);
        tamperSwitchIconManipulation.setForegroundIconColor(Color.RED);

        headContent.setCenter(getUnitLabel());
        headContent.setAlignment(getUnitLabel(), Pos.CENTER_LEFT);
        headContent.prefHeightProperty().set(tamperSwitchIconOk.getSize() + Constants.INSETS);
    }

    @Override
    protected void initContent() {
        //No body content.
    }

    @Override
    protected void initUnitLabel() {
        String unitLabel = Constants.UNKNOWN_ID;
        try {
            unitLabel = this.tamperSwitchRemote.getData().getLabel();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
        setUnitLabelString(unitLabel);
    }

    @Override
    public AbstractIdentifiableRemote getDALRemoteService() {
        return tamperSwitchRemote;
    }

    @Override
    void removeObserver() {
        this.tamperSwitchRemote.removeObserver(this);
    }

    @Override
    public void update(final Observable observable, final Object tamperSwitch) throws java.lang.Exception {
        Platform.runLater(() -> {
            final State tamperSwitchState = ((TamperSwitchType.TamperSwitch) tamperSwitch).getTamperState().getValue();

            setTamperSwitchIconAndText(tamperSwitchState);
        });
    }
}
