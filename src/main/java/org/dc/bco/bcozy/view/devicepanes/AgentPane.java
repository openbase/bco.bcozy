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
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import org.dc.bco.bcozy.view.Constants;
import org.dc.bco.bcozy.view.SVGIcon;
import org.dc.bco.manager.agent.remote.AgentRemote;
import org.dc.jul.exception.CouldNotPerformException;
import org.dc.jul.exception.printer.ExceptionPrinter;
import org.dc.jul.exception.printer.LogLevel;
import org.dc.jul.extension.rsb.com.AbstractIdentifiableRemote;
import org.dc.jul.pattern.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.homeautomation.state.ActivationStateType.ActivationState;
import rst.homeautomation.state.ActivationStateType.ActivationState.State;
import rst.homeautomation.control.agent.AgentDataType.AgentData;

/**
 * Created by agatting on 12.04.16.
 */
public class AgentPane extends UnitPane {
    private static final Logger LOGGER = LoggerFactory.getLogger(TamperSwitchPane.class);

    private final SVGIcon agentIcon;
    private final SVGIcon unknownForegroundIcon;
    private final SVGIcon unknownBackgroundIcon;
    private final AgentRemote agentRemote;
    private final BorderPane headContent;

    /**
     * Constructor for the AgentPane.
     * @param agentRemote agentRemote
     */
    public AgentPane(final AbstractIdentifiableRemote agentRemote) {
        this.agentRemote = (AgentRemote) agentRemote;

        headContent = new BorderPane();
        agentIcon = new SVGIcon(MaterialDesignIcon.POWER, Constants.SMALL_ICON, false);
        unknownBackgroundIcon = new SVGIcon(MaterialDesignIcon.CHECKBOX_BLANK_CIRCLE, Constants.SMALL_ICON - 2, false);
        unknownForegroundIcon = new SVGIcon(MaterialDesignIcon.HELP_CIRCLE, Constants.SMALL_ICON, false);

        initUnitLabel();
        initTitle();
        initContent();
        createWidgetPane(headContent, true);
        initEffect();
        tooltip.textProperty().bind(observerText.textProperty());

        this.agentRemote.addObserver(this);
    }

    private void initEffect() {
        State state = State.UNKNOWN;

        try {
            state = agentRemote.getData().getActivationState().getValue();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
        setAgentIconAndText(state);
    }

    private void setAgentIconAndText(final State state) {
        iconPane.getChildren().clear();

        if (state.equals(State.ACTIVE)) {
            agentIcon.setForegroundIconColor(Color.GREEN);
            iconPane.add(agentIcon, 0, 0);
            observerText.setIdentifier("active");

            if (!toggleSwitch.isSelected()) {
                toggleSwitch.setSelected(true);
            }
        } else if (state.equals(State.DEACTIVE)) {
            agentIcon.changeForegroundIcon(MaterialDesignIcon.POWER);
            iconPane.add(agentIcon, 0, 0);
            observerText.setIdentifier("inactive");

            if (toggleSwitch.isSelected()) {
                toggleSwitch.setSelected(false);
            }
        } else {
            iconPane.add(unknownBackgroundIcon, 0, 0);
            iconPane.add(unknownForegroundIcon, 0, 0);
            observerText.setIdentifier("unknown");
        }
    }

    private void sendStateToRemote(final State state) {
        try {
            agentRemote.setActivationState(ActivationState.newBuilder().setValue(state).build());
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
            setWidgetPaneDisable(true);
        }
    }

    @Override
    protected void initTitle() {
        oneClick.addListener((observable, oldValue, newValue) -> new Thread(new Task() {
            @Override
            protected Object call() {
                if (toggleSwitch.isSelected()) {
                    sendStateToRemote(ActivationState.State.DEACTIVE);
                } else {
                    sendStateToRemote(ActivationState.State.ACTIVE);
                }
                return null;
            }
        }).start());

        toggleSwitch.setOnMouseClicked(event -> new Thread(new Task() {
            @Override
            protected Object call() {
                if (toggleSwitch.isSelected()) {
                    sendStateToRemote(ActivationState.State.ACTIVE);
                } else {
                    sendStateToRemote(ActivationState.State.DEACTIVE);
                }
                return null;
            }
        }).start());

        unknownForegroundIcon.setForegroundIconColor(Color.BLUE);
        unknownBackgroundIcon.setForegroundIconColor(Color.WHITE);

        headContent.setCenter(getUnitLabel());
        headContent.setAlignment(getUnitLabel(), Pos.CENTER_LEFT);
        headContent.prefHeightProperty().set(agentIcon.getHeight() + Constants.INSETS);
    }

    @Override
    protected void initContent() {
        //No body content.
    }

    @Override
    protected void initUnitLabel() {
        String unitLabel = Constants.UNKNOWN_ID;
        try {
            unitLabel = this.agentRemote.getData().getLabel();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
        setUnitLabelString(unitLabel);
    }

    @Override
    public AbstractIdentifiableRemote getDALRemoteService() {
        return agentRemote;
    }

    @Override
    void removeObserver() {
        this.agentRemote.removeObserver(this);
    }

    @Override
    public void update(final Observable observable, final Object agent) throws java.lang.Exception {
        Platform.runLater(() -> {
            final State state = ((AgentData) agent).getActivationState().getValue();
            setAgentIconAndText(state);
        });
    }
}
