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
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.openbase.jul.extension.rsb.com.AbstractIdentifiableRemote;
import rst.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;

import java.util.List;

/**
 * Created by agatting on 24.11.15.
 */
public class TitledPaneContainer extends VBox {

    /**
     * Takes a List of DALRemoteServices and a unitType and creates a new UnitPaneContainer.
     * @param unitType unitType
     * @param dalRemoteServiceList dalRemoteServiceList
     */
    public void createAndAddNewTitledPane(final UnitType unitType,
                                          final List<AbstractIdentifiableRemote> dalRemoteServiceList) {
        final UnitPaneContainer unitPaneContainer = new UnitPaneContainer(unitType.toString());
        if (unitPaneContainer.createAndAddNewUnitPanes(unitType, dalRemoteServiceList)) {
            this.getChildren().add(unitPaneContainer);
        }
    }

    /**
     * Deletes and clears all UnitPaneContainer.
     */
    public void clearTitledPane() {
        for (final Node node : this.getChildren()) {
            ((UnitPaneContainer) node).clearUnitPaneContainer();
        }

        this.getChildren().clear();
    }

    /**
     * Workaround Method to cope with the missing 15 pixel.
     */
    public void addDummyPane() {
        //CHECKSTYLE.OFF: MagicNumber
        this.getChildren().add(new Rectangle(15.0, 15.0, Color.TRANSPARENT));
        //CHECKSTYLE.ON: MagicNumber
    }
}
