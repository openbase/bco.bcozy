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

import javafx.scene.control.Label;
import org.dc.jul.extension.rsb.com.AbstractIdentifiableRemote;
import org.dc.jul.pattern.Observer;

/**
 * Created by tmichalski on 03.12.15.
 */
public abstract class UnitPane extends WidgetPane implements Observer {

    private final Label unitLabel;
    private String unitLabelString;

    /**
     * Constructor for the UnitPane.
     */
    public UnitPane() {
        this.unitLabel = new Label();
        //TODO: Set css styling for unitlabel
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
     * Returns the AbstractIdentifiableRemote.
     * @return AbstractIdentifiableRemote
     */
    abstract AbstractIdentifiableRemote getDALRemoteService();

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
