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
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Created by agatting on 24.11.15.
 */
public class TitledPaneContainer extends VBox {

    /**
     * Constructor for the TitledPaneContainer.
     */
    public TitledPaneContainer() {
        //This is only for test purpose!!
        final UnitPaneContainer unitPaneContainer = new UnitPaneContainer("AmbientLight");
        this.getChildren().add(unitPaneContainer);
    }

    /**
     * Takes a List of DALRemoteServices and a unitType and creates a new UnitPaneContainer.
     * @param unitType unitType
     * @param dalRemoteServiceList dalRemoteServiceList
     */
    public void createAndAddNewTitledPane(final String unitType, final List<DALRemoteService> dalRemoteServiceList) {
        final UnitPaneContainer unitPaneContainer = new UnitPaneContainer(unitType);
        unitPaneContainer.createAndAddNewUnitPanes(dalRemoteServiceList);
        this.getChildren().add(unitPaneContainer);
    }
}
