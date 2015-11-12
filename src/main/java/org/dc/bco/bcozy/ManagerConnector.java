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
package org.dc.bco.bcozy;

import de.citec.dal.remote.unit.AmbientLightRemote;
import de.citec.jul.exception.CouldNotPerformException;
import de.citec.jul.exception.printer.ExceptionPrinter;
import de.citec.jul.exception.printer.LogLevel;
import de.citec.jul.extension.rst.processing.MetaConfigVariableProvider;
import de.citec.lm.remote.LocationRegistryRemote;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import org.dc.bco.bcozy.view.ForegroundPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.homeautomation.unit.UnitConfigType;
import rst.spatial.LocationConfigType;

import java.awt.*;
import java.util.List;

/**
 * The ManagerConnector.
 */
public class ManagerConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(BCozy.class);

    private final ForegroundPane foregroundPane;

    /**
     * The Constructor.
     * @param foregroundPane ForegroundPane
     */
    public ManagerConnector(final ForegroundPane foregroundPane) {
        this.foregroundPane = foregroundPane;

        this.foregroundPane.getMainMenu().addMainButtonEventHandler(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent event) {
                try {
                    locationRegistryExample();
                } catch (InterruptedException e) {
                    ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                }
            }
        });
    }

    /**
     * Example for the Location Registry.
     * @throws InterruptedException InterruptedException
     */
    public void locationRegistryExample() throws InterruptedException {
        try {
            final LocationRegistryRemote locationRegistryRemote = new LocationRegistryRemote();

            locationRegistryRemote.init();
            locationRegistryRemote.activate();
            final List<LocationConfigType.LocationConfig> list = locationRegistryRemote.getLocationConfigs();
            for (final LocationConfigType.LocationConfig locationConfig : list) {
                LOGGER.info(locationConfig.getLabel());
                final MetaConfigVariableProvider metaConfigVariableProvider =
                        new MetaConfigVariableProvider("locationConfig", locationConfig.getMetaConfig());
                try {
                    LOGGER.info("Found test entry: " + metaConfigVariableProvider.getValue("TEST_ENTRY"));
                } catch (CouldNotPerformException e) {
                    LOGGER.info("No test entry found");
                }
            }

            //Example: How to control a light:
            final LocationConfigType.LocationConfig controlConfig =
                    locationRegistryRemote.getLocationConfigById("Control");


            final List<UnitConfigType.UnitConfig> ambientLight  =
                    locationRegistryRemote.getUnitConfigsByLabel("TestUnit_0", controlConfig.getId());

            final AmbientLightRemote ambientLightRemote = new AmbientLightRemote();
            ambientLightRemote.init(ambientLight.get(0));
            ambientLightRemote.activate();

            ambientLightRemote.setColor(Color.blue);
            LOGGER.info("INFO: " + ambientLight.get(0).getId());

            ambientLightRemote.shutdown();
            locationRegistryRemote.shutdown();

        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
    }
}
