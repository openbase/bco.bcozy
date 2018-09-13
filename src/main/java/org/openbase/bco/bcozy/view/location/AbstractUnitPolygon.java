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
package org.openbase.bco.bcozy.view.location;

import com.google.protobuf.GeneratedMessage;
import org.apache.http.cookie.SM;
import org.openbase.bco.dal.lib.layer.unit.UnitRemote;
import org.openbase.bco.dal.remote.unit.Units;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.iface.Manageable;
import org.openbase.jul.pattern.Observable;
import org.openbase.jul.pattern.Observer;
import org.openbase.jul.pattern.provider.DataProvider;
import rst.domotic.unit.UnitConfigType.UnitConfig;

/**
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public abstract class AbstractUnitPolygon<M extends GeneratedMessage, UR extends UnitRemote<M>> extends AbstractPolygon implements Manageable<UnitConfig> {

    private UR unitRemote;
    private UnitDataObserver dataObserver;
    private boolean active, init;

    public AbstractUnitPolygon(final double... points) throws InstantiationException {
        super(points);
        this.dataObserver = new UnitDataObserver();
    }

    @Override
    public void init(final UnitConfig unitConfig) throws InitializationException, InterruptedException {
        try {
            init = true;
            this.unitRemote = (UR) Units.getUnit(unitConfig, false);
        } catch (final CouldNotPerformException ex) {
            throw new InitializationException(this, ex);
        }
    }

//    public void init(final String unitID) throws InitializationException, InterruptedException {
//        try {
//            init = true;
//            this.unitRemote = (UR) Units.getUnit(unitID, false);
//        } catch (final CouldNotPerformException ex) {
//            throw new InitializationException(this, ex);
//        }
//    }

    @Override
    public void activate() throws CouldNotPerformException, InterruptedException {
        active = true;
        unitRemote.addDataObserver(dataObserver);
        if(unitRemote.isDataAvailable()) {
            applyDataUpdate(unitRemote.getData());
        }
    }

    @Override
    public void deactivate() throws CouldNotPerformException, InterruptedException {
        active = false;
        unitRemote.removeDataObserver(dataObserver);
    }

    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Method returns the internal unit remote.
     *
     * @return
     * @throws NotAvailableException
     */
    public UR getUnitRemote() throws NotAvailableException {
        if (unitRemote == null) {
            throw new NotAvailableException("UnitRemote");
        }
        return unitRemote;
    }

    /**
     * Returns the id of the internal unit.
     */
    public String getUnitId() throws NotAvailableException {
        return getUnitRemote().getId();
    }

    /**
     * Method return the label of the unit.
     *
     * @return the label as a String
     */
    public String getLabel() throws NotAvailableException {
        return unitRemote.getLabel();
    }

    public abstract void applyDataUpdate(final M unitData);

    public class UnitDataObserver implements Observer<DataProvider<M>, M> {

        @Override
        public void update(DataProvider<M> source, M data) {
            applyDataUpdate(data);
        }
    }
}
