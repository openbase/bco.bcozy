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

import de.jensd.fx.glyphs.GlyphIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import org.openbase.bco.bcozy.view.SVGIcon;
import org.openbase.bco.bcozy.view.pane.unit.UnitPaneFactoryImpl;
import org.openbase.bco.dal.remote.unit.Units;
import org.openbase.bco.dal.remote.unit.location.LocationRemote;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import rst.domotic.unit.UnitConfigType.UnitConfig;
import rst.domotic.unit.UnitTemplateType;

/**
 *
 */
public class UnitButton extends Button {

    private final double DEFAULT_ICON_SIZE = 8.0;

    private final double centerX;
    private final double centerY;

    /**
     * Creates a button with suitable unit symbol
     *
     * @param config config of the remote unit
     */
    // LocationRemote unit = Units.getUnit("locaction unit id", true, Units.LOCATION);
    //   unit.getUnits(UnitTemplateType.UnitTemplate.UnitType.UNKNOWN, true, unitRemoteClass);
    public UnitButton(UnitConfig config) {

        this.setGraphic(getSymbolForType(config.getType()));
        this.setOnAction(null);  //TODO
        this.centerX = (super.getLayoutBounds().getMaxX() + super.getLayoutBounds().getMinX()) / 2; //TODO was macht dieses?
        this.centerY = (super.getLayoutBounds().getMaxY() + super.getLayoutBounds().getMinY()) / 2;
        this.setScaleX(this.scaleXProperty().getValue() / 2);
        this.setScaleY(this.scaleYProperty().getValue() / 2);   //TODO ändern!
    }

    private SVGIcon getSymbolForType(UnitTemplateType.UnitTemplate.UnitType type) {
        try {
            //TODO
            return UnitPaneFactoryImpl.getInstance().newInstance(UnitPaneFactoryImpl.loadUnitPaneClass(type)).getIcon();
        } catch (CouldNotPerformException ex) {
            Logger.getLogger(UnitButton.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new SVGIcon(FontAwesomeIcon.ELLIPSIS_H, DEFAULT_ICON_SIZE, true);
    }

    public double getCenterX() {
        return centerX;
    }

    public double getCenterY() {
        return centerY;
    }
}
