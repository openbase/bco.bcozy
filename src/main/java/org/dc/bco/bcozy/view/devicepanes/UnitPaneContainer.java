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

import de.citec.dal.remote.unit.DALRemoteService;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import rst.homeautomation.unit.UnitTemplateType.UnitTemplate.UnitType;

import java.util.Iterator;
import java.util.List;

/**
 * Created by timo on 03.12.15.
 */
public class UnitPaneContainer extends TitledPane {

    private final VBox vBox;

    /**
     * Constructor for the UnitPaneContainer.
     * @param unitTypeName unitTypeName
     */
    public UnitPaneContainer(final String unitTypeName) {
        this.setText(unitTypeName);
        this.setPadding(new Insets(0, 0, 0, 0));

        vBox = new VBox();
        vBox.setPadding(new Insets(0, 0, 0, 0));
        this.setContent(vBox);
    }

    /**
     * Takes a List of DALRemoteService and creates new UnitPanes for each.
     * @param unitType unitType
     * @param dalRemoteServiceList dalRemoteServiceList
     */
    public void createAndAddNewUnitPanes(final UnitType unitType, final List<DALRemoteService> dalRemoteServiceList) {
        this.setExpanded(false);

        if (unitType.equals(UnitType.AMBIENT_LIGHT)) {
            final Iterator<DALRemoteService> dalRemoteServiceIterator = dalRemoteServiceList.iterator();
            while (dalRemoteServiceIterator.hasNext()) {
                final DALRemoteService dalRemoteService = dalRemoteServiceIterator.next();
                final AmbientLightPane ambientLightPane = new AmbientLightPane(dalRemoteService);

                vBox.getChildren().add(ambientLightPane);
            }
        } else if (unitType.equals(UnitType.LIGHT)) {
            final Iterator<DALRemoteService> dalRemoteServiceIterator = dalRemoteServiceList.iterator();
            while (dalRemoteServiceIterator.hasNext()) {
                final DALRemoteService dalRemoteService = dalRemoteServiceIterator.next();
                final LightPane lightPane = new LightPane(dalRemoteService);

                vBox.getChildren().add(lightPane);
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
