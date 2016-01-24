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

import org.dc.bco.bcozy.view.mainmenupanes.ObserverTitledPane;
import org.dc.bco.dal.remote.unit.DALRemoteService;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import rst.homeautomation.unit.UnitTemplateType.UnitTemplate.UnitType;

import java.util.Iterator;
import java.util.List;

/**
 * Created by timo on 03.12.15.
 */
public class UnitPaneContainer extends ObserverTitledPane {

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
     * Takes a List of DALRemoteService and creates new UnitPanes for each.
     * @param unitType unitType
     * @param dalRemoteServiceList dalRemoteServiceList
     */
    public void createAndAddNewUnitPanes(final UnitType unitType, //NOPMD
                                         final List<DALRemoteService> dalRemoteServiceList) {
        this.setExpanded(false);

        if (unitType.equals(UnitType.AMBIENT_LIGHT)) {
            final Iterator<DALRemoteService> dalRemoteServiceIterator = dalRemoteServiceList.iterator();
            while (dalRemoteServiceIterator.hasNext()) {
                final DALRemoteService dalRemoteService = dalRemoteServiceIterator.next();
                final AmbientLightPane ambientLightPane = new AmbientLightPane(dalRemoteService);

                vBox.getChildren().add(ambientLightPane);
            }
        } else if (unitType.equals(UnitType.BATTERY)) {
            final Iterator<DALRemoteService> dalRemoteServiceIterator = dalRemoteServiceList.iterator();
            while (dalRemoteServiceIterator.hasNext()) {
                final DALRemoteService dalRemoteService = dalRemoteServiceIterator.next();
                final BatteryPane batteryPanePane = new BatteryPane(dalRemoteService);

                vBox.getChildren().add(batteryPanePane);
            }
        } else if (unitType.equals(UnitType.BRIGHTNESS_SENSOR)) {
            final Iterator<DALRemoteService> dalRemoteServiceIterator = dalRemoteServiceList.iterator();
            while (dalRemoteServiceIterator.hasNext()) {
                final DALRemoteService dalRemoteService = dalRemoteServiceIterator.next();
                final BrightnessSensorPane brightnessSensorPane = new BrightnessSensorPane(dalRemoteService);

                vBox.getChildren().add(brightnessSensorPane);
            }
        } else if (unitType.equals(UnitType.DIMMER)) {
            final Iterator<DALRemoteService> dalRemoteServiceIterator = dalRemoteServiceList.iterator();
            while (dalRemoteServiceIterator.hasNext()) {
                final DALRemoteService dalRemoteService = dalRemoteServiceIterator.next();
                final DimmerPane dimmerPane = new DimmerPane(dalRemoteService);

                vBox.getChildren().add(dimmerPane);
            }
        } else if (unitType.equals(UnitType.LIGHT)) {
            final Iterator<DALRemoteService> dalRemoteServiceIterator = dalRemoteServiceList.iterator();
            while (dalRemoteServiceIterator.hasNext()) {
                final DALRemoteService dalRemoteService = dalRemoteServiceIterator.next();
                final LightPane lightPane = new LightPane(dalRemoteService);

                vBox.getChildren().add(lightPane);
            }
        } else if (unitType.equals(UnitType.MOTION_SENSOR)) {
            final Iterator<DALRemoteService> dalRemoteServiceIterator = dalRemoteServiceList.iterator();
            while (dalRemoteServiceIterator.hasNext()) {
                final DALRemoteService dalRemoteService = dalRemoteServiceIterator.next();
                final MotionSensorPane motionSensorPane = new MotionSensorPane(dalRemoteService);

                vBox.getChildren().add(motionSensorPane);
            }
        } else if (unitType.equals(UnitType.POWER_PLUG)) {
            final Iterator<DALRemoteService> dalRemoteServiceIterator = dalRemoteServiceList.iterator();
            while (dalRemoteServiceIterator.hasNext()) {
                final DALRemoteService dalRemoteService = dalRemoteServiceIterator.next();
                final PowerPlugPane powerPlugPane = new PowerPlugPane(dalRemoteService);

                vBox.getChildren().add(powerPlugPane);
            }
        } else if (unitType.equals(UnitType.ROLLERSHUTTER)) {
            final Iterator<DALRemoteService> dalRemoteServiceIterator = dalRemoteServiceList.iterator();
            while (dalRemoteServiceIterator.hasNext()) {
                final DALRemoteService dalRemoteService = dalRemoteServiceIterator.next();
                final RollershutterPane rollershutterPane = new RollershutterPane(dalRemoteService);

                vBox.getChildren().add(rollershutterPane);
            }
        } else if (unitType.equals(UnitType.TEMPERATURE_CONTROLLER)) {
            final Iterator<DALRemoteService> dalRemoteServiceIterator = dalRemoteServiceList.iterator();
            while (dalRemoteServiceIterator.hasNext()) {
                final DALRemoteService dalRemoteService = dalRemoteServiceIterator.next();
                final TemperatureControllerPane temperatureControllerPane
                        = new TemperatureControllerPane(dalRemoteService);

                vBox.getChildren().add(temperatureControllerPane);
            }
        } else if (unitType.equals(UnitType.TEMPERATURE_SENSOR)) {
            final Iterator<DALRemoteService> dalRemoteServiceIterator = dalRemoteServiceList.iterator();
            while (dalRemoteServiceIterator.hasNext()) {
                final DALRemoteService dalRemoteService = dalRemoteServiceIterator.next();
                final TemperatureSensorPane temperatureSensorPane = new TemperatureSensorPane(dalRemoteService);

                vBox.getChildren().add(temperatureSensorPane);
            }
        }
    }

    /**
     * Deletes and clears all UnitPanes.
     */
    public void clearUnitPaneContainer() {
        final Iterator<Node> childrenIterator = vBox.getChildren().iterator();

        while (childrenIterator.hasNext()) {
            final UnitPane currentUnitPane = (UnitPane) childrenIterator.next();
            currentUnitPane.removeObserver();
        }

        this.getChildren().clear();
    }
}
