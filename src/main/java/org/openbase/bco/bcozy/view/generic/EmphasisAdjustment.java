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
package org.openbase.bco.bcozy.view.generic;

import com.jfoenix.controls.JFXSlider;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.GridPane;
import org.openbase.bco.bcozy.view.ObserverLabel;
import org.openbase.jul.visual.javafx.iface.DynamicPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.state.EmphasisStateType.EmphasisState;

/**
 *
 * @author <a href="mailto:tmichalski@techfak.uni-bielefeld.de">Timo Michalski</a>
 */
public class EmphasisAdjustment extends GridPane implements DynamicPane {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final DoubleProperty comfortProperty;
    private final DoubleProperty energyProperty;
    private final DoubleProperty securityProperty;

    private JFXSlider comfortSlider;
    private JFXSlider energySlider;
    private JFXSlider securitySlider;

    public EmphasisAdjustment() {
        this.comfortProperty = new SimpleDoubleProperty(0.0);
        this.energyProperty = new SimpleDoubleProperty(0.0);
        this.securityProperty = new SimpleDoubleProperty(0.0);
    }

    @Override
    public void updateDynamicContent() {
        comfortProperty.set(comfortSlider.getValue());
        energyProperty.set(energySlider.getValue());
        securityProperty.set(securitySlider.getValue());
    }

    @Override
    public void initContent() {
        comfortSlider = new JFXSlider(0.0, 100.0, 0.0);
        energySlider = new JFXSlider(0.0, 100.0, 0.0);
        securitySlider = new JFXSlider(0.0, 100.0, 0.0);

        ObserverLabel comfortLabel = new ObserverLabel("comfort");
        ObserverLabel energyLabel = new ObserverLabel("energy");
        ObserverLabel securityLabel = new ObserverLabel("security");

        comfortSlider.valueProperty().addListener((observable) -> {
            if (isHover()) {
                comfortProperty.set(comfortSlider.getValue());
            }
        });
        energySlider.valueProperty().addListener((observable) -> {
            if (isHover()) {
                energyProperty.set(energySlider.getValue());
            }
        });
        securitySlider.valueProperty().addListener((observable) -> {
            if (isHover()) {
                securityProperty.set(securitySlider.getValue());
            }
        });

        setConstraints(comfortLabel, 0, 0);
        setConstraints(energyLabel, 0, 1);
        setConstraints(securityLabel, 0, 2);
        setConstraints(comfortSlider, 1, 0);
        setConstraints(energySlider, 1, 1);
        setConstraints(securitySlider, 1, 2);
        getChildren().addAll(comfortSlider, energySlider, securitySlider, comfortLabel, energyLabel, securityLabel);
    }

    public DoubleProperty getComfortProperty() {
        return comfortProperty;
    }

    public DoubleProperty getEnergyProperty() {
        return energyProperty;
    }

    public DoubleProperty getSecurityProperty() {
        return securityProperty;
    }

    public EmphasisState getCurrentEmphasisState() {
        return EmphasisState.newBuilder().setComfort(comfortSlider.getValue()).setEnergy(energySlider.getValue()).setSecurity(securitySlider.getValue()).build();
    }

    public void setSelectedEmphasis(EmphasisState emphasisState) {
        if (!isHover()) {
            comfortSlider.setValue(emphasisState.getComfort());
            energySlider.setValue(emphasisState.getEnergy());
            securitySlider.setValue(emphasisState.getSecurity());
        }
    }

}
