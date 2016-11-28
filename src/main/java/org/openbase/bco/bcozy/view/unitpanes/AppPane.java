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
package org.openbase.bco.bcozy.view.unitpanes;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.SVGIcon;
import org.openbase.bco.dal.remote.unit.app.AppRemote;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.openbase.jul.extension.rsb.com.AbstractIdentifiableRemote;
import org.openbase.jul.pattern.Observable;
import org.openbase.jul.schedule.GlobalExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.unit.agent.AgentDataType;
import rst.domotic.state.ActivationStateType.ActivationState;
import rst.domotic.state.ActivationStateType.ActivationState.State;

/**
 * Created by agatting on 12.04.16.
 */
public class AppPane extends AbstractUnitPane {

    private static final Logger LOGGER = LoggerFactory.getLogger(TamperDetectorPane.class);

    private final SVGIcon appIcon;
    private final SVGIcon unknownForegroundIcon;
    private final SVGIcon unknownBackgroundIcon;
    private final AppRemote appRemote;
    private final BorderPane headContent;

    /**
     * Constructor for the AppPane.
     *
     * @param appRemote appRemote
     */
    public AppPane(final AbstractIdentifiableRemote appRemote) {
        this.appRemote = (AppRemote) appRemote;

        headContent = new BorderPane();
        appIcon = new SVGIcon(MaterialDesignIcon.POWER, Constants.SMALL_ICON, false);
        unknownBackgroundIcon = new SVGIcon(MaterialDesignIcon.CHECKBOX_BLANK_CIRCLE, Constants.SMALL_ICON - 2, false);
        unknownForegroundIcon = new SVGIcon(MaterialDesignIcon.HELP_CIRCLE, Constants.SMALL_ICON, false);

        initUnitLabel();
        initTitle();
        initContent();
        createWidgetPane(headContent, true);
        initEffect();
        tooltip.textProperty().bind(observerText.textProperty());

        this.appRemote.addDataObserver(this);
    }

    private void initEffect() {
        State state = State.UNKNOWN;

        try {
            state = appRemote.getData().getActivationState().getValue();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
        setAppIconAndText(state);
    }

    private void setAppIconAndText(final State state) {
        iconPane.getChildren().clear();

        switch (state) {
            case ACTIVE:
                appIcon.setForegroundIconColor(Color.GREEN);
                iconPane.add(appIcon, 0, 0);
                observerText.setIdentifier("active");
                if (!toggleSwitch.isSelected()) {
                    toggleSwitch.setSelected(true);
                }   break;
            case DEACTIVE:
                appIcon.changeForegroundIcon(MaterialDesignIcon.POWER);
                iconPane.add(appIcon, 0, 0);
                observerText.setIdentifier("inactive");
                if (toggleSwitch.isSelected()) {
                    toggleSwitch.setSelected(false);
                }   break;
            default:
                iconPane.add(unknownBackgroundIcon, 0, 0);
                iconPane.add(unknownForegroundIcon, 0, 0);
                observerText.setIdentifier("unknown");
                break;
        }
    }

    private void sendStateToRemote(final State state) {
        try {
            appRemote.setActivationState(ActivationState.newBuilder().setValue(state).build()).get(Constants.OPERATION_SERVICE_MILLI_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException | CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER, LogLevel.ERROR);
            setWidgetPaneDisable(true);
        }
    }

    @Override
    protected void initTitle() {
        oneClick.addListener((observable, oldValue, newValue) -> GlobalExecutionService.submit(new Task() {
            @Override
            protected Object call() {
                if (toggleSwitch.isSelected()) {
                    sendStateToRemote(ActivationState.State.DEACTIVE);
                } else {
                    sendStateToRemote(ActivationState.State.ACTIVE);
                }
                return null;
            }
        }));

        toggleSwitch.setOnMouseClicked(event -> GlobalExecutionService.submit(new Task() {
            @Override
            protected Object call() {
                if (toggleSwitch.isSelected()) {
                    sendStateToRemote(ActivationState.State.ACTIVE);
                } else {
                    sendStateToRemote(ActivationState.State.DEACTIVE);
                }
                return null;
            }
        }));

        unknownForegroundIcon.setForegroundIconColor(Color.BLUE);
        unknownBackgroundIcon.setForegroundIconColor(Color.WHITE);

        headContent.setCenter(getUnitLabel());
        headContent.setAlignment(getUnitLabel(), Pos.CENTER_LEFT);
        headContent.prefHeightProperty().set(appIcon.getHeight() + Constants.INSETS);
    }

    @Override
    protected void initContent() {
        //No body content.
    }

    @Override
    protected void initUnitLabel() {
        String unitLabel = Constants.UNKNOWN_ID;
        try {
            unitLabel = this.appRemote.getData().getLabel();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
        setUnitLabelString(unitLabel);
    }

    @Override
    public AbstractIdentifiableRemote getDALRemoteService() {
        return appRemote;
    }

    @Override
    void removeObserver() {
        this.appRemote.removeObserver(this);
    }

    @Override
    public void update(final Observable observable, final Object agent) throws java.lang.Exception {
        Platform.runLater(() -> {
            final State state = ((AgentDataType.AgentData) agent).getActivationState().getValue();
            setAppIconAndText(state);
        });
    }
}
