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
import org.openbase.type.domotic.state.EmphasisStateType.EmphasisState;

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
    private final DoubleProperty economyProperty;
    private final DoubleProperty securityProperty;

    private JFXSlider comfortSlider;
    private JFXSlider economySlider;
    private JFXSlider securitySlider;

    public EmphasisAdjustment() {
        this(true, true, true);
    }

    public EmphasisAdjustment(boolean comfort, boolean energy, boolean security) {
        this.comfort = comfort;
        this.energy = energy;
        this.security = security;

        this.comfortProperty = new SimpleDoubleProperty(0.0);
        this.economyProperty = new SimpleDoubleProperty(0.0);
        this.securityProperty = new SimpleDoubleProperty(0.0);
    }

    @Override
    public void updateDynamicContent() {
        if (comfortSlider != null && comfort) {
            comfortProperty.set(comfortSlider.getValue());
        }
        if (economySlider != null && energy) {
            economyProperty.set(economySlider.getValue());
        }
        if (securitySlider != null && security) {
            securityProperty.set(securitySlider.getValue());
        }
    }

    @Override
    public void initContent() {
        int counter = 0;

        if (comfort) {
            comfortSlider = new JFXSlider(0.0, 100.0, 0.0);
            comfortSlider.getStyleClass().clear();
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
            economySlider = new JFXSlider(0.0, 100.0, 0.0);
            economySlider.getStyleClass().clear();
            ObserverLabel energyLabel = new ObserverLabel("economy");
            economySlider.valueProperty().addListener((observable) -> {
                if (isHover()) {
                    economyProperty.set(economySlider.getValue());
                }
            });
            setConstraints(energyLabel, 0, counter);
            setConstraints(economySlider, 1, counter);
            getChildren().addAll(economySlider, energyLabel);
            counter++;
        }
        if (security) {
            securitySlider = new JFXSlider(0.0, 100.0, 0.0);
            securitySlider.getStyleClass().clear();
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

    public DoubleProperty getEconomyProperty() {
        return economyProperty;
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
            emphasisStateBuilder.setEconomy(economySlider.getValue());
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
                economySlider.setValue(emphasisState.getEconomy());
            }
            if (security) {
                securitySlider.setValue(emphasisState.getSecurity());
            }
        }
    }

}
