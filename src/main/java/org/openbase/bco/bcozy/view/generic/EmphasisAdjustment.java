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

    private final boolean comfort;
    private final boolean energy;
    private final boolean security;

    private final DoubleProperty comfortProperty;
    private final DoubleProperty energyProperty;
    private final DoubleProperty securityProperty;

    private JFXSlider comfortSlider;
    private JFXSlider energySlider;
    private JFXSlider securitySlider;

    public EmphasisAdjustment() {
        this(true, true, true);
    }

    public EmphasisAdjustment(boolean comfort, boolean energy, boolean security) {
        this.comfort = comfort;
        this.energy = energy;
        this.security = security;

        this.comfortProperty = new SimpleDoubleProperty(0.0);
        this.energyProperty = new SimpleDoubleProperty(0.0);
        this.securityProperty = new SimpleDoubleProperty(0.0);
    }

    @Override
    public void updateDynamicContent() {
        if (comfort) {
            comfortProperty.set(comfortSlider.getValue());
        }
        if (energy) {
            energyProperty.set(energySlider.getValue());
        }
        if (security) {
            securityProperty.set(securitySlider.getValue());
        }
    }

    @Override
    public void initContent() {
        int counter = 0;

        if (comfort) {
            comfortSlider = new JFXSlider(0.0, 100.0, 0.0);
            ObserverLabel comfortLabel = new ObserverLabel("comfort");
            comfortSlider.valueProperty().addListener((observable) -> {
                if (isHover()) {
                    comfortProperty.set(comfortSlider.getValue());
                }
            });
            setConstraints(comfortLabel, 0, 0);
            setConstraints(comfortSlider, 1, 0);
            getChildren().addAll(comfortSlider, comfortLabel);
            counter++;
        }
        if (energy) {
            energySlider = new JFXSlider(0.0, 100.0, 0.0);
            ObserverLabel energyLabel = new ObserverLabel("energy");
            energySlider.valueProperty().addListener((observable) -> {
                if (isHover()) {
                    energyProperty.set(energySlider.getValue());
                }
            });
            setConstraints(energyLabel, 0, counter);
            setConstraints(energySlider, 1, counter);
            getChildren().addAll(energySlider, energyLabel);
            counter++;
        }
        if (security) {
            securitySlider = new JFXSlider(0.0, 100.0, 0.0);
            ObserverLabel securityLabel = new ObserverLabel("security");
            securitySlider.valueProperty().addListener((observable) -> {
                if (isHover()) {
                    securityProperty.set(securitySlider.getValue());
                }
            });
            setConstraints(securityLabel, 0, counter);
            setConstraints(securitySlider, 1, counter);
            getChildren().addAll(securitySlider, securityLabel);
        }
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
        EmphasisState.Builder emphasisStateBuilder = EmphasisState.newBuilder();

        if (comfort) {
            emphasisStateBuilder.setComfort(comfortSlider.getValue());
        }
        if (energy) {
            emphasisStateBuilder.setEnergy(energySlider.getValue());
        }
        if (security) {
            emphasisStateBuilder.setSecurity(securitySlider.getValue());
        }
        return emphasisStateBuilder.build();
    }

    public void setSelectedEmphasis(EmphasisState emphasisState) {
        if (!isHover()) {
            if (comfort) {
                comfortSlider.setValue(emphasisState.getComfort());
            }
            if (energy) {
                energySlider.setValue(emphasisState.getEnergy());
            }
            if (security) {
                securitySlider.setValue(emphasisState.getSecurity());
            }
        }
    }

}
