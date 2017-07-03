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
 * along with org.openbase.bco.bcozy. If not, see
 * <http://www.gnu.org/licenses/>.
 * ==================================================================
 */
package org.openbase.bco.bcozy.view;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import org.openbase.bco.bcozy.view.location.UnitButton;
import rst.domotic.unit.UnitConfigType.UnitConfig;

/**
 *
 * @author lili
 */
public class UnitSymbolsPane extends Pane {

    
    /**
     * Application logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UnitSymbolsPane.class);

    private final Map<String, UnitButton> unitsMap;
    private final Map<String, Map<String, UnitButton>> roomUnitsMap;

    public UnitSymbolsPane() {
        super();
        unitsMap = new HashMap<>();
        roomUnitsMap = new HashMap<>();
    }
    
    public void addUnit(final UnitConfig unitConfig, final Point2D position) {  //TODO hand over Position or ready coordinates? where to calculate?
               
        UnitButton newButton = new UnitButton(unitConfig);
        newButton.setTranslateX(position.getY());  //Attention: swap correct? according to location pane 
        newButton.setTranslateY(position.getX());
        unitsMap.put(unitConfig.getId(), newButton);
    }
    
    public void addRoomControlUnit(final UnitConfig unitConfig, final Point2D position, final String locationId) {
        UnitButton newButton = new UnitButton(unitConfig);
        newButton.setTranslateX(position.getY());  //Attention: swap correct? according to location pane 
        newButton.setTranslateY(position.getX());
        
        if(!roomUnitsMap.containsKey(locationId)) {
            roomUnitsMap.put(locationId, new HashMap<>());
        }
        roomUnitsMap.get(locationId).put(unitConfig.getId(), newButton);
    }
    

    public void clearUnits() {
        unitsMap.forEach((unitId, button) 
            -> {this.getChildren().remove(button);}
        );
    }

    public void updateUnitsPane(){
        this.getChildren().clear();

        unitsMap.forEach((unitId, button) 
            -> {this.getChildren().add(button);}
        );
    }

}