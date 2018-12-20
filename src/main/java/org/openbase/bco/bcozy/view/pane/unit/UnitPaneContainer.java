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
package org.openbase.bco.bcozy.view.pane.unit;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.openbase.bco.bcozy.view.mainmenupanes.ObserverTitledPane;
import org.openbase.bco.dal.lib.layer.unit.UnitRemote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openbase.type.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;

import java.util.List;
import org.openbase.jps.core.JPService;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;

/**
 * @author tmichalksi
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
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
        this.vBox.getStyleClass().add("observer-titled-pane");
        this.getStyleClass().add("observer-titled-pane");
        this.setContent(vBox);
    }

    /**
     * Takes a List of UnitRemote and creates new UnitPanes for each.
     *
     * @param unitType unitType
     * @param dalRemoteServiceList dalRemoteServiceList
     * @throws java.lang.InterruptedException
     */
    public void createAndAddNewUnitPanes(final UnitType unitType, final List<UnitRemote> dalRemoteServiceList) throws InterruptedException {
        this.setExpanded(false);

        for (final UnitRemote<?> remote : dalRemoteServiceList) {
            try {
                vBox.getChildren().add(UnitPaneFactoryImpl.getInstance().newInitializedInstance(remote.getConfig()));
            } catch (CouldNotPerformException ex) {
                if (JPService.verboseMode()) {
                    ExceptionPrinter.printHistory("UnitType[" + unitType + "] is not supported yet!", ex, LOGGER, LogLevel.WARN);
                } else {
                    ExceptionPrinter.printHistory("UnitType[" + unitType + "] is not supported yet!", ex, LOGGER, LogLevel.DEBUG);
                }
            }
        }
    }

    /**
     * Deletes and clears all UnitPanes.
     */
    public void clearUnitPaneContainer() {
        for (final Node node : vBox.getChildren()) {
            ((AbstractUnitPane) node).shutdown();
        }

        this.getChildren().clear();
    }

    /**
     * Method returns the amount of containing unit panes.
     *
     * @return the amount of unit panes as integer.
     */
    public int size() {
        return vBox.getChildren().size();
    }

    /**
     * Method checks if the container contains any unit panes.
     *
     * @return returns true if the container dose not contain any unit panes.
     */
    public boolean isEmpty() {
        return vBox.getChildren().isEmpty();
    }
}
