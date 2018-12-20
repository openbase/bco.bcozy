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

import java.util.concurrent.TimeUnit;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.pattern.Factory;
import org.openbase.type.domotic.unit.UnitConfigType;
import org.openbase.type.domotic.unit.UnitTemplateType;
import org.openbase.type.com.ScopeType;
import org.openbase.jul.exception.InstantiationException;

/**
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public interface UnitPaneFactory extends Factory<AbstractUnitPane, UnitConfigType.UnitConfig> {

    /**
     * Creates and initializes an unit pane out of the given unit configuration.
     *
     * @param config the unit configuration which defines the remote type and is used for the remote initialization.
     * @return the new created unit pane.
     * @throws CouldNotPerformException is thrown if any other error occurs during buildup.
     * @throws InterruptedException
     */
    public AbstractUnitPane newInitializedInstance(final UnitConfigType.UnitConfig config) throws CouldNotPerformException, InterruptedException;

    /**
     * Creates an unit pane out of the given unit configuration.
     *
     * @param config the unit configuration which defines the remote type.
     * @return the new created unit pane.
     * @throws InstantiationException
     */
    @Override
    public AbstractUnitPane newInstance(final UnitConfigType.UnitConfig config) throws InstantiationException;

    /**
     * Creates an unit pane out of the given unit class.
     *
     * @param <R> the unit pane class type.
     * @param unitRemoteClass the unit class which defines the remote type.
     * @return the new created unit pane.
     * @throws InstantiationException
     */
    public <R extends AbstractUnitPane> R newInstance(final Class<R> unitRemoteClass) throws InstantiationException;

    /**
     * Creates an unit pane out of the given unit id.
     *
     * @param unitId the unit id which defines the remote type.
     * @param timeout the timeout for the unit registry lookup.
     * @param timeUnit the time unit of the timeout.
     * @return the new created unit pane.
     * @throws InstantiationException is thrown if the unit could not be instantiated with the given information.
     * @throws InterruptedException is thrown if the thread was externally interrupted.
     */
    public AbstractUnitPane newInstance(String unitId, long timeout, final TimeUnit timeUnit) throws InstantiationException, CouldNotPerformException, InterruptedException;

    /**
     * Creates and initializes an unit pane out of the given unit id.
     *
     * @param unitId the unit id which is used for the remote initialization.
     * @param timeout the timeout for the unit registry lookup.
     * @param timeUnit the time unit of the timeout.
     * @return the new created and initialized unit pane.
     * @throws InitializationException is thrown if the unit could not be initialized with the given information.
     * @throws InstantiationException is thrown if the unit could not be instantiated with the given information.
     * @throws CouldNotPerformException is thrown if any other error occurs during buildup.
     * @throws InterruptedException is thrown if the thread was externally interrupted.
     */
    public AbstractUnitPane newInitializedInstance(final String unitId, long timeout, final TimeUnit timeUnit) throws InitializationException, InstantiationException, CouldNotPerformException, InterruptedException;

    /**
     * Creates an unit pane out of the given unit configuration.
     *
     * @param type the unit type which is used for the remote initialization.
     * @return the new created and initialized unit pane.
     * @throws InstantiationException is thrown if the unit could not be instantiated with the given information.
     */
    public AbstractUnitPane newInstance(final UnitTemplateType.UnitTemplate.UnitType type) throws InstantiationException;

}
