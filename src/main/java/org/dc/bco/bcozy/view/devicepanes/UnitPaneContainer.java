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

import de.citec.dal.remote.unit.AmbientLightRemote;
import de.citec.dal.remote.unit.DALRemoteService;
import javafx.geometry.Insets;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

import java.util.Iterator;
import java.util.List;

/**
 * Created by timo on 03.12.15.
 */
public class UnitPaneContainer extends TitledPane {

    private final VBox vBox;

    /**
     * Constructor for the UnitPaneContainer.
     * @param unitType unitType
     */
    public UnitPaneContainer(final String unitType) {
        this.setText(unitType);
        this.setPadding(new Insets(0, 0, 0, 0));

        vBox = new VBox();
        vBox.setPadding(new Insets(0, 0, 0, 0));
        this.setContent(vBox);

        // This is only for test purpose!!
        final AmbientLightPane ambientLightPane = new AmbientLightPane(new AmbientLightRemote());
        final AmbientLightPane ambientLightPane1 = new AmbientLightPane(new AmbientLightRemote());
        final AmbientLightPane ambientLightPane2 = new AmbientLightPane(new AmbientLightRemote());
        vBox.getChildren().addAll(ambientLightPane, ambientLightPane1, ambientLightPane2);
    }

    /**
     * Takes a List of DALRemoteService and creates new UnitPanes for each.
     * @param dalRemoteServiceList dalRemoteServiceList
     */
    public void createAndAddNewUnitPanes(final List<DALRemoteService> dalRemoteServiceList) {
        final Iterator<DALRemoteService> dalRemoteServiceIterator = dalRemoteServiceList.iterator();
        while (dalRemoteServiceIterator.hasNext()) {
            final DALRemoteService dalRemoteService = dalRemoteServiceIterator.next();
            final AmbientLightPane ambientLightPane = new AmbientLightPane(dalRemoteService);

            vBox.getChildren().add(ambientLightPane);
        }
    }
}
