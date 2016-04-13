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
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import org.controlsfx.control.ToggleSwitch;
import org.dc.bco.bcozy.view.Constants;
import org.dc.bco.bcozy.view.SVGIcon;
import org.dc.bco.manager.scene.remote.SceneRemote;
import org.dc.jul.exception.CouldNotPerformException;
import org.dc.jul.exception.printer.ExceptionPrinter;
import org.dc.jul.exception.printer.LogLevel;
import org.dc.jul.extension.rsb.com.AbstractIdentifiableRemote;
import org.dc.jul.pattern.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.homeautomation.control.agent.AgentDataType;
import rst.homeautomation.state.ActivationStateType;

/**
 * Created by agatting on 12.04.16.
 */
public class ScenePane extends UnitPane {
    private static final Logger LOGGER = LoggerFactory.getLogger(TamperSwitchPane.class);

    private final SVGIcon sceneIcon;
    private final SVGIcon unknownForegroundIcon;
    private final SVGIcon unknownBackgroundIcon;
    private final SceneRemote sceneRemote;
    private final GridPane iconPane;
    private final BorderPane headContent;
    private final Tooltip tooltip;
    private final ToggleSwitch toggleSwitch;

    /**
     * Constructor for the ScenePane.
     * @param sceneRemote sceneRemote
     */
    public ScenePane(final AbstractIdentifiableRemote sceneRemote) {
        this.sceneRemote = (SceneRemote) sceneRemote;

        headContent = new BorderPane();
        sceneIcon = new SVGIcon(MaterialDesignIcon.POWER, Constants.SMALL_ICON, false);
        unknownBackgroundIcon = new SVGIcon(MaterialDesignIcon.CHECKBOX_BLANK_CIRCLE, Constants.SMALL_ICON - 2, false);
        unknownForegroundIcon = new SVGIcon(MaterialDesignIcon.HELP_CIRCLE, Constants.SMALL_ICON, false);
        iconPane = new GridPane();
        tooltip = new Tooltip();
        toggleSwitch = new ToggleSwitch();

        initUnitLabel();
        initTitle();
        initContent();
        createWidgetPane(headContent, true);

        initEffect();

        this.sceneRemote.addObserver(this);
    }

    private void initEffect() {
        ActivationStateType.ActivationState.State state = ActivationStateType.ActivationState.State.UNKNOWN;

        try {
            state = sceneRemote.getData().getActivationState().getValue();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
        setSceneIconAndText(state);
    }

    private void setSceneIconAndText(final ActivationStateType.ActivationState.State state) {
        iconPane.getChildren().clear();

        if (state.equals(ActivationStateType.ActivationState.State.ACTIVE)) {
            sceneIcon.setForegroundIconColor(Color.GREEN);
            iconPane.add(sceneIcon, 0, 0);
            tooltip.setText(Constants.ACTIVE);

            if (!toggleSwitch.isSelected()) {
                toggleSwitch.setSelected(true);
            }
        } else if (state.equals(ActivationStateType.ActivationState.State.DEACTIVE)) {
            sceneIcon.changeForegroundIcon(MaterialDesignIcon.POWER);
            iconPane.add(sceneIcon, 0, 0);
            tooltip.setText(Constants.DISABLED);

            if (toggleSwitch.isSelected()) {
                toggleSwitch.setSelected(false);
            }
        } else {
            iconPane.add(unknownBackgroundIcon, 0, 0);
            iconPane.add(unknownForegroundIcon, 0, 0);
            tooltip.setText(Constants.UNKNOWN);
        }
        Tooltip.install(iconPane, tooltip);
    }

    private void sendStateToRemote(final ActivationStateType.ActivationState.State state) {
        try {
            sceneRemote.setActivationState(ActivationStateType.ActivationState.newBuilder().setValue(state).build());
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
            setWidgetPaneDisable(true);
        }
    }

    @Override
    protected void initTitle() {
        toggleSwitch.setMouseTransparent(true);

        getOneClick().addListener((observable, oldValue, newValue) -> new Thread(new Task() {
            @Override
            protected Object call() {
                if (toggleSwitch.isSelected()) {
                    sendStateToRemote(ActivationStateType.ActivationState.State.DEACTIVE);
                } else {
                    sendStateToRemote(ActivationStateType.ActivationState.State.ACTIVE);
                }
                return null;
            }
        }).start());

        toggleSwitch.setOnMouseClicked(event -> new Thread(new Task() {
            @Override
            protected Object call() {
                if (toggleSwitch.isSelected()) {
                    sendStateToRemote(ActivationStateType.ActivationState.State.ACTIVE);
                } else {
                    sendStateToRemote(ActivationStateType.ActivationState.State.DEACTIVE);
                }
                return null;
            }
        }).start());

        unknownForegroundIcon.setForegroundIconColor(Color.BLUE);
        unknownBackgroundIcon.setForegroundIconColor(Color.WHITE);

        headContent.setLeft(iconPane);
        headContent.setCenter(getUnitLabel());
        headContent.setAlignment(getUnitLabel(), Pos.CENTER_LEFT);
        headContent.setRight(toggleSwitch);
        headContent.prefHeightProperty().set(sceneIcon.getHeight() + Constants.INSETS);
    }

    @Override
    protected void initContent() {
        //No body content.
    }

    @Override
    protected void initUnitLabel() {
        String unitLabel = Constants.UNKNOWN_ID;
        try {
            unitLabel = this.sceneRemote.getData().getLabel();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
        setUnitLabelString(unitLabel);
    }

    @Override
    public AbstractIdentifiableRemote getDALRemoteService() {
        return sceneRemote;
    }

    @Override
    void removeObserver() {
        this.sceneRemote.removeObserver(this);
    }

    @Override
    public void update(final Observable observable, final Object agent) throws java.lang.Exception {
        Platform.runLater(() -> {
            final ActivationStateType.ActivationState.State state =
                    ((AgentDataType.AgentData) agent).getActivationState().getValue();
            setSceneIconAndText(state);
        });
    }
}
