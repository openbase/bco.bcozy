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

import org.dc.bco.dal.remote.unit.DALRemoteService;
import org.dc.jul.pattern.Observer;

/**
 * Created by tmichalski on 03.12.15.
 */
public abstract class UnitPane extends WidgetPane implements Observer {

    private String unitLabel;

    /**
     * Getter for unitLabel.
     * @return unitLabel
     */
    public String getUnitLabel() {
        return unitLabel;
    }

    /**
     * Setter for unitLabel.
     * @param unitLabel unitLabel
     */
    public void setUnitLabel(final String unitLabel) {
        this.unitLabel = unitLabel;
    }

    /**
     * Returns the DALRemoteService.
     * @return DALRemoteService
     */
    abstract DALRemoteService getDALRemoteService();

    /**
     * Initialize the TitlePane.
     */
    abstract void initTitle();

    /**
     * Initialize the ContentPane.
     */
    abstract void initContent();

    /**
     * Initialize the UnitLabel.
     */
    abstract void initUnitLabel();

    /**
     * Removes the Observer.
     */
    abstract void removeObserver();
}
