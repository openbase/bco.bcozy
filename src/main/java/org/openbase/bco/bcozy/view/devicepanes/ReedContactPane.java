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
package org.openbase.bco.bcozy.view.devicepanes;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.SVGIcon;
import org.openbase.jul.extension.rsb.com.AbstractIdentifiableRemote;
import org.openbase.bco.dal.remote.unit.ReedContactRemote;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.openbase.jul.pattern.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.state.ContactStateType.ContactState.State;
import rst.domotic.unit.dal.ReedContactDataType.ReedContactData;

/**
 * Created by agatting on 27.01.16.
 */
public class ReedContactPane extends UnitPane {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReedContactPane.class);

    private final ReedContactRemote reedContactRemote;
    private final SVGIcon unknownForegroundIcon;
    private final SVGIcon unknownBackgroundIcon;
    private final SVGIcon reedSwitchIcon;
    private final BorderPane headContent;

    /**
     * Constructor for the ReedSwitchPane.
     * @param reedSwitchRemote reedSwitchRemote
     */
    public ReedContactPane(final AbstractIdentifiableRemote reedSwitchRemote) {
        this.reedContactRemote = (ReedContactRemote) reedSwitchRemote;

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

        this.reedContactRemote.addDataObserver(this);
    }

    private void initEffectAndText() {
        State reedSwitchState = State.UNKNOWN;

        try {
            reedSwitchState = reedContactRemote.getContactState().getValue();
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
            unitLabel = this.reedContactRemote.getData().getLabel();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
        setUnitLabelString(unitLabel);
    }

    @Override
    public AbstractIdentifiableRemote getDALRemoteService() {
        return reedContactRemote;
    }

    @Override
    void removeObserver() {
        this.reedContactRemote.removeObserver(this);
    }

    @Override
    public void update(final Observable observable, final Object reedSwitch) throws java.lang.Exception {
        Platform.runLater(() -> {
            final State reedSwitchState = ((ReedContactData) reedSwitch).getContactState().getValue();

            setReedSwitchIconAndTooltip(reedSwitchState);
        });
    }
}
