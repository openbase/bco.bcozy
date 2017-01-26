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

import javafx.scene.control.Label;
import org.openbase.bco.dal.remote.unit.AbstractUnitRemote;
import org.openbase.bco.dal.remote.unit.UnitRemote;
import org.openbase.jul.pattern.Observer;
import org.openbase.jul.pattern.Remote;

/**
 * Created by tmichalski on 03.12.15.
 */
public abstract class AbstractUnitPane extends WidgetPane implements Observer {

    private final Label unitLabel;
    private String unitLabelString;

    /**
     * Constructor for the UnitPane.
     */
    public AbstractUnitPane() {
        this.unitLabel = new Label();
        //TODO: Set css styling for unitlabel
    }

    /**
     * Sets the object as dataObserver of remote and adds connetionStateObserver to disable/enable pane.
     * @param remote UnitRemote
     */
    protected void addObserverAndInitDisableState(final AbstractUnitRemote remote) {
        remote.addDataObserver(this);
        remote.addConnectionStateObserver((source, data) -> {
            if (data.equals(Remote.ConnectionState.CONNECTED) && this.isDisabled()) {
                setWidgetPaneDisable(false);
            } else if (!this.isDisabled()) {
                setWidgetPaneDisable(true);
            }
        });

        if (!remote.isConnected()) {
            setWidgetPaneDisable(true);
        }
    }

    /**
     * Getter for unitLabelString.
     * @return unitLabelString
     */
    public String getUnitLabelString() {
        return unitLabelString;
    }

    /**
     * Setter for unitLabelString.
     * @param unitLabelString unitLabelString
     */
    public void setUnitLabelString(final String unitLabelString) {
        this.unitLabelString = unitLabelString;
        this.unitLabel.setText(unitLabelString);
    }

    /**
     * Getter for unitLabel.
     * @return unitLabel
     */
    public Label getUnitLabel() {
        return unitLabel;
    }

    /**
     * Returns the UnitRemote.
     * @return UnitRemote
     */
    abstract UnitRemote getDALRemoteService();

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
