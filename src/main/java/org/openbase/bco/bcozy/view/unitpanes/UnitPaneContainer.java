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

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.openbase.bco.bcozy.view.mainmenupanes.ObserverTitledPane;
import org.openbase.bco.dal.remote.unit.UnitRemote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;

import java.util.List;

/**
 * Created by tmichalksi on 03.12.15.
 */
public class UnitPaneContainer extends ObserverTitledPane {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnitPaneContainer.class);

    private final VBox vBox;

    /**
     * Constructor for the UnitPaneContainer.
     *
     * @param unitTypeName unitTypeName
     */
    public UnitPaneContainer(final String unitTypeName) {
        super(unitTypeName);

        this.vBox = new VBox();
        //CHECKSTYLE.OFF: MultipleStringLiterals
        this.vBox.getStyleClass().add(java.util.ResourceBundle.getBundle("languages/languages").getString("OBSERVER-TITLED-PANE"));
        this.getStyleClass().add(java.util.ResourceBundle.getBundle("languages/languages").getString("OBSERVER-TITLED-PANE"));
        //CHECKSTYLE.ON: MultipleStringLiterals
        this.setContent(vBox);
    }

    /**
     * Takes a List of UnitRemote and creates new UnitPanes for each.
     *
     * @param unitType unitType
     * @param dalRemoteServiceList dalRemoteServiceList
     * @return true if unitType is handled, otherwise false
     */
    public boolean createAndAddNewUnitPanes(final UnitType unitType, //NOPMD
            final List<UnitRemote> dalRemoteServiceList) {
        this.setExpanded(false);

        switch (unitType) {
            case AGENT:
                for (final UnitRemote dalRemoteService : dalRemoteServiceList) {
                    final AgentPane agentPane = new AgentPane(dalRemoteService);

                    vBox.getChildren().add(agentPane);
                }
                break;
            case COLORABLE_LIGHT:
                for (final UnitRemote dalRemoteService : dalRemoteServiceList) {
                    final ColorableLightPane ambientLightPane = new ColorableLightPane(dalRemoteService);

                    vBox.getChildren().add(ambientLightPane);
                }
                break;
            case APP:
                for (final UnitRemote dalRemoteService : dalRemoteServiceList) {
                    final AppPane appPane = new AppPane(dalRemoteService);

                    vBox.getChildren().add(appPane);
                }
                break;
            case BATTERY:
                for (final UnitRemote dalRemoteService : dalRemoteServiceList) {
                    final BatteryPane batteryPanePane = new BatteryPane(dalRemoteService);

                    vBox.getChildren().add(batteryPanePane);
                }
                break;
            case BRIGHTNESS_SENSOR:
                for (final UnitRemote dalRemoteService : dalRemoteServiceList) {
                    final BrightnessSensorPane brightnessSensorPane = new BrightnessSensorPane(dalRemoteService);

                    vBox.getChildren().add(brightnessSensorPane);
                }
                break;
            case DIMMABLE_LIGHT:
                for (final UnitRemote dalRemoteService : dalRemoteServiceList) {
                    final DimmableLightPane dimmerPane = new DimmableLightPane(dalRemoteService);

                    vBox.getChildren().add(dimmerPane);
                }
                break;
            case LIGHT:
                for (final UnitRemote dalRemoteService : dalRemoteServiceList) {
                    final LightPane lightPane = new LightPane(dalRemoteService);

                    vBox.getChildren().add(lightPane);
                }
                break;
            case MOTION_DETECTOR:
                for (final UnitRemote dalRemoteService : dalRemoteServiceList) {
                    final MotionDetectorPane motionSensorPane = new MotionDetectorPane(dalRemoteService);

                    vBox.getChildren().add(motionSensorPane);
                }
                break;
            case POWER_CONSUMPTION_SENSOR:
                for (final UnitRemote dalRemoteService : dalRemoteServiceList) {
                    final PowerConsumptionSensorPane powerConsumptionSensorPane
                            = new PowerConsumptionSensorPane(dalRemoteService);

                    vBox.getChildren().add(powerConsumptionSensorPane);
                }
                break;
            case POWER_SWITCH:
                for (final UnitRemote dalRemoteService : dalRemoteServiceList) {
                    final PowerSwitchPane powerPlugPane = new PowerSwitchPane(dalRemoteService);

                    vBox.getChildren().add(powerPlugPane);
                }
                break;
            case REED_CONTACT:
                for (final UnitRemote dalRemoteService : dalRemoteServiceList) {
                    final ReedContactPane reedSwitchPane = new ReedContactPane(dalRemoteService);

                    vBox.getChildren().add(reedSwitchPane);
                }
                break;
            case ROLLER_SHUTTER:
                for (final UnitRemote dalRemoteService : dalRemoteServiceList) {
                    final RollerShutterPane rollerShutterPane = new RollerShutterPane(dalRemoteService);

                    vBox.getChildren().add(rollerShutterPane);
                }
                break;
            case SCENE:
                for (final UnitRemote dalRemoteService : dalRemoteServiceList) {
                    final ScenePane scenePane = new ScenePane(dalRemoteService);

                    vBox.getChildren().add(scenePane);
                }
                break;
            case SMOKE_DETECTOR:
                for (final UnitRemote dalRemoteService : dalRemoteServiceList) {
                    final SmokeDetectorPane smokeDetectorPane = new SmokeDetectorPane(dalRemoteService);

                    vBox.getChildren().add(smokeDetectorPane);
                }
                break;
            case TAMPER_DETECTOR:
                for (final UnitRemote dalRemoteService : dalRemoteServiceList) {
                    final TamperDetectorPane tamperSwitchPane = new TamperDetectorPane(dalRemoteService);

                    vBox.getChildren().add(tamperSwitchPane);
                }
                break;
            case TEMPERATURE_CONTROLLER:
                for (final UnitRemote dalRemoteService : dalRemoteServiceList) {
                    final TemperatureControllerPane temperatureControllerPane
                            = new TemperatureControllerPane(dalRemoteService);

                    vBox.getChildren().add(temperatureControllerPane);
                }
                break;
            case TEMPERATURE_SENSOR:
                for (final UnitRemote dalRemoteService : dalRemoteServiceList) {
                    final TemperatureSensorPane temperatureSensorPane = new TemperatureSensorPane(dalRemoteService);

                    vBox.getChildren().add(temperatureSensorPane);
                }
                break;
            default:
                LOGGER.info(java.util.ResourceBundle.getBundle("languages/languages").getString("INFO: UNIT TYPE IS NOT SUPPORTED YET: ").concat(unitType.toString()));
                return false;
        }
        return true;
    }

    /**
     * Deletes and clears all UnitPanes.
     */
    public void clearUnitPaneContainer() {
        for (final Node node : vBox.getChildren()) {
            ((AbstractUnitPane) node).removeObserver();
        }

        this.getChildren().clear();
    }
}
