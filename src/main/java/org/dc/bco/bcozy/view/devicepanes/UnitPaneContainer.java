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

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.dc.bco.bcozy.view.mainmenupanes.ObserverTitledPane;
import org.dc.jul.extension.rsb.com.AbstractIdentifiableRemote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.homeautomation.unit.UnitTemplateType.UnitTemplate.UnitType;

import java.util.List;

/**
 * Created by tmichalksi on 03.12.15.
 */
public class UnitPaneContainer extends ObserverTitledPane {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnitPaneContainer.class);

    private final VBox vBox;

    /**
     * Constructor for the UnitPaneContainer.
     * @param unitTypeName unitTypeName
     */
    public UnitPaneContainer(final String unitTypeName) {
        super(unitTypeName);

        this.vBox = new VBox();
        //CHECKSTYLE.OFF: MultipleStringLiterals
        this.vBox.getStyleClass().add("observer-titled-pane");
        this.getStyleClass().add("observer-titled-pane");
        //CHECKSTYLE.ON: MultipleStringLiterals
        this.setContent(vBox);
    }

    /**
     * Takes a List of AbstractIdentifiableRemote and creates new UnitPanes for each.
     * @param unitType unitType
     * @param dalRemoteServiceList dalRemoteServiceList
     * @return true if unitType is handled, otherwise false
     */
    public boolean createAndAddNewUnitPanes(final UnitType unitType, //NOPMD
                                         final List<AbstractIdentifiableRemote> dalRemoteServiceList) {
        this.setExpanded(false);

        if (unitType.equals(UnitType.AGENT)) {
            for (final AbstractIdentifiableRemote dalRemoteService : dalRemoteServiceList) {
                final AgentPane agentPane = new AgentPane(dalRemoteService);

                vBox.getChildren().add(agentPane);
            }
        } else if (unitType.equals(UnitType.AMBIENT_LIGHT)) {
            for (final AbstractIdentifiableRemote dalRemoteService : dalRemoteServiceList) {
                final AmbientLightPane ambientLightPane = new AmbientLightPane(dalRemoteService);

                vBox.getChildren().add(ambientLightPane);
            }
        } else if (unitType.equals(UnitType.APP)) {
            for (final AbstractIdentifiableRemote dalRemoteService : dalRemoteServiceList) {
                final AppPane appPane = new AppPane(dalRemoteService);

                vBox.getChildren().add(appPane);
            }
        } else if (unitType.equals(UnitType.BATTERY)) {
            for (final AbstractIdentifiableRemote dalRemoteService : dalRemoteServiceList) {
                final BatteryPane batteryPanePane = new BatteryPane(dalRemoteService);

                vBox.getChildren().add(batteryPanePane);
            }
        } else if (unitType.equals(UnitType.BRIGHTNESS_SENSOR)) {
            for (final AbstractIdentifiableRemote dalRemoteService : dalRemoteServiceList) {
                final BrightnessSensorPane brightnessSensorPane = new BrightnessSensorPane(dalRemoteService);

                vBox.getChildren().add(brightnessSensorPane);
            }
        } else if (unitType.equals(UnitType.DIMMER)) {
            for (final AbstractIdentifiableRemote dalRemoteService : dalRemoteServiceList) {
                final DimmerPane dimmerPane = new DimmerPane(dalRemoteService);

                vBox.getChildren().add(dimmerPane);
            }
        } else if (unitType.equals(UnitType.LIGHT)) {
            for (final AbstractIdentifiableRemote dalRemoteService : dalRemoteServiceList) {
                final LightPane lightPane = new LightPane(dalRemoteService);

                vBox.getChildren().add(lightPane);
            }
        } else if (unitType.equals(UnitType.MOTION_SENSOR)) {
            for (final AbstractIdentifiableRemote dalRemoteService : dalRemoteServiceList) {
                final MotionSensorPane motionSensorPane = new MotionSensorPane(dalRemoteService);

                vBox.getChildren().add(motionSensorPane);
            }
        } else if (unitType.equals(UnitType.POWER_CONSUMPTION_SENSOR)) {
            for (final AbstractIdentifiableRemote dalRemoteService : dalRemoteServiceList) {
                final PowerConsumptionSensorPane powerConsumptionSensorPane
                        = new PowerConsumptionSensorPane(dalRemoteService);

                vBox.getChildren().add(powerConsumptionSensorPane);
            }
        } else if (unitType.equals(UnitType.POWER_PLUG)) {
            for (final AbstractIdentifiableRemote dalRemoteService : dalRemoteServiceList) {
                final PowerPlugPane powerPlugPane = new PowerPlugPane(dalRemoteService);

                vBox.getChildren().add(powerPlugPane);
            }
        } else if (unitType.equals(UnitType.REED_SWITCH)) {
            for (final AbstractIdentifiableRemote dalRemoteService : dalRemoteServiceList) {
                final ReedSwitchPane reedSwitchPane = new ReedSwitchPane(dalRemoteService);

                vBox.getChildren().add(reedSwitchPane);
            }
        } else if (unitType.equals(UnitType.ROLLERSHUTTER)) {
            for (final AbstractIdentifiableRemote dalRemoteService : dalRemoteServiceList) {
                final RollerShutterPane rollerShutterPane = new RollerShutterPane(dalRemoteService);

                vBox.getChildren().add(rollerShutterPane);
            }
        } else if (unitType.equals(UnitType.SCENE)) {
            for (final AbstractIdentifiableRemote dalRemoteService : dalRemoteServiceList) {
                final ScenePane scenePane = new ScenePane(dalRemoteService);

                vBox.getChildren().add(scenePane);
            }
        } else if (unitType.equals(UnitType.SMOKE_DETECTOR)) {
            for (final AbstractIdentifiableRemote dalRemoteService : dalRemoteServiceList) {
                final SmokeDetectorPane smokeDetectorPane = new SmokeDetectorPane(dalRemoteService);

                vBox.getChildren().add(smokeDetectorPane);
            }
        } else if (unitType.equals(UnitType.TAMPER_SWITCH)) {
            for (final AbstractIdentifiableRemote dalRemoteService : dalRemoteServiceList) {
                final TamperSwitchPane tamperSwitchPane = new TamperSwitchPane(dalRemoteService);

                vBox.getChildren().add(tamperSwitchPane);
            }
        } else if (unitType.equals(UnitType.TEMPERATURE_CONTROLLER)) {
            for (final AbstractIdentifiableRemote dalRemoteService : dalRemoteServiceList) {
                final TemperatureControllerPane temperatureControllerPane
                        = new TemperatureControllerPane(dalRemoteService);

                vBox.getChildren().add(temperatureControllerPane);
            }
        } else if (unitType.equals(UnitType.TEMPERATURE_SENSOR)) {
            for (final AbstractIdentifiableRemote dalRemoteService : dalRemoteServiceList) {
                final TemperatureSensorPane temperatureSensorPane = new TemperatureSensorPane(dalRemoteService);

                vBox.getChildren().add(temperatureSensorPane);
            }
        } else {
            LOGGER.info("INFO: Unit Type is not supported yet: ".concat(unitType.toString()));
            return false;
        }
        return true;
    }

    /**
     * Deletes and clears all UnitPanes.
     */
    public void clearUnitPaneContainer() {
        for (final Node node : vBox.getChildren()) {
            ((UnitPane) node).removeObserver();
        }

        this.getChildren().clear();
    }
}
