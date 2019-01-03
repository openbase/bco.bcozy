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
import org.openbase.bco.registry.lib.util.UnitConfigProcessor;
import org.openbase.bco.registry.unit.remote.CachedUnitRegistryRemote;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.EnumNotSupportedException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.processing.StringProcessor;
import org.openbase.type.domotic.unit.UnitConfigType.UnitConfig;
import org.openbase.type.domotic.unit.UnitTemplateType;

/**
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class UnitPaneFactoryImpl implements UnitPaneFactory {

    private static UnitPaneFactory instance;

    private UnitPaneFactoryImpl() {
    }

    /**
     * Method returns a new singelton instance of the unit factory.
     *
     * @return
     */
    public synchronized static UnitPaneFactory getInstance() {
        if (instance == null) {
            instance = new UnitPaneFactoryImpl();
        }
        return instance;
    }

    /**
     * Method resolves the unit pane class of the given unit config.
     *
     * @param config the unit config to detect the unit class.
     * @return the unit pane class is returned.
     * @throws CouldNotPerformException is thrown if something went wrong during class loading.
     */
    public static Class<? extends AbstractUnitPane> loadUnitPaneClass(final UnitConfig config) throws CouldNotPerformException {
        return loadUnitPaneClass(config.getUnitType());
    }

    /**
     * Method resolves the unit pane class of the given unit type.
     *
     * @param unitType the unit type to detect the unit class.
     * @return the unit pane class is returned.
     * @throws CouldNotPerformException is thrown if something went wrong during class loading.
     */
    public static Class<? extends AbstractUnitPane> loadUnitPaneClass(final UnitTemplateType.UnitTemplate.UnitType unitType) throws CouldNotPerformException {
        try {
            String remoteClassName = null;
            // check unit type and load related class.
            if (UnitConfigProcessor.isBaseUnit(unitType)) {
                remoteClassName = AbstractUnitPane.class.getPackage().getName() + "." + unitType.name().toLowerCase().replaceAll("_", "") + "." + StringProcessor.transformUpperCaseToCamelCase(unitType.name()) + "Pane";
            } else if (UnitConfigProcessor.isDalUnit(unitType)) {
                remoteClassName = AbstractUnitPane.class.getPackage().getName() + "." + StringProcessor.transformUpperCaseToCamelCase(unitType.name()) + "Pane";
            } else {
                throw new EnumNotSupportedException(unitType, UnitPaneFactoryImpl.class);
            }
            return (Class<? extends AbstractUnitPane>) UnitPaneFactoryImpl.class.getClassLoader().loadClass(remoteClassName);
        } catch (CouldNotPerformException | ClassNotFoundException ex) {
            throw new CouldNotPerformException("Could not detect unit pane class for UnitType[" + unitType.name() + "]!", ex);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param config {@inheritDoc}
     * @return {@inheritDoc}
     * @throws InstantiationException {@inheritDoc}
     */
    @Override
    public AbstractUnitPane newInstance(final UnitConfig config) throws InstantiationException {
        try {
            return newInstance(loadUnitPaneClass(config));
        } catch (CouldNotPerformException ex) {
            throw new InstantiationException("Could not create unit pane!", ex);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param unitPaneClass {@inheritDoc}
     * @return {@inheritDoc}
     * @throws InstantiationException {@inheritDoc}
     */
    @Override
    public <R extends AbstractUnitPane> R newInstance(final Class<R> unitPaneClass) throws InstantiationException {
        try {
            return unitPaneClass.newInstance();
        } catch (java.lang.InstantiationException | IllegalAccessException ex) {
            throw new org.openbase.jul.exception.InstantiationException("Could not instantiate unit pane out of Class[" + unitPaneClass.getName() + "]", ex);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param type {@inheritDoc}
     * @return {@inheritDoc}
     * @throws InstantiationException {@inheritDoc}
     */
    @Override
    public AbstractUnitPane newInstance(final UnitTemplateType.UnitTemplate.UnitType type) throws InstantiationException {
        try {
            return newInstance(loadUnitPaneClass(type));
        } catch (CouldNotPerformException ex) {
            throw new InstantiationException("Could not create unit pane!", ex);
        }

    }

    /**
     * {@inheritDoc}
     *
     * @param unitId {@inheritDoc}
     * @return {@inheritDoc}
     * @throws InstantiationException {@inheritDoc}
     */
    @Override
    public AbstractUnitPane newInstance(String unitId, long timeout, TimeUnit timeUnit) throws CouldNotPerformException, InterruptedException {
        CachedUnitRegistryRemote.waitForData(timeout, timeUnit);
        return newInstance(CachedUnitRegistryRemote.getRegistry().getUnitConfigById(unitId));
    }

    /**
     * {@inheritDoc}
     *
     * @param config {@inheritDoc}
     * @return {@inheritDoc}
     * @throws InitializationException {@inheritDoc}
     * @throws InstantiationException {@inheritDoc}
     * @throws InterruptedException {@inheritDoc}
     */
    @Override
    public AbstractUnitPane newInitializedInstance(final UnitConfig config) throws InitializationException, InstantiationException, InterruptedException {
        AbstractUnitPane unitPane = newInstance(config);
        unitPane.init(config);
        return unitPane;
    }

    /**
     * {@inheritDoc}
     *
     * @param unitId {@inheritDoc}
     * @param timeout {@inheritDoc}
     * @param timeUnit {@inheritDoc}
     * @return {@inheritDoc}
     * @throws InitializationException {@inheritDoc}
     * @throws InstantiationException {@inheritDoc}
     * @throws CouldNotPerformException {@inheritDoc}
     * @throws InterruptedException {@inheritDoc}
     */
    @Override
    public AbstractUnitPane newInitializedInstance(final String unitId, long timeout, TimeUnit timeUnit) throws InitializationException, InstantiationException, CouldNotPerformException, InterruptedException {
        CachedUnitRegistryRemote.waitForData(timeout, timeUnit);
        return newInitializedInstance(CachedUnitRegistryRemote.getRegistry().getUnitConfigById(unitId));
    }
}
