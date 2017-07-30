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

import com.google.protobuf.GeneratedMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import org.openbase.bco.bcozy.view.location.UnitButton;
import org.openbase.bco.dal.lib.layer.unit.UnitRemote;
import org.openbase.jul.exception.NotAvailableException;

/**
 *
 * @author lili
 */
public class UnitSymbolsPane extends Pane {

    
    /**
     * Application logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UnitSymbolsPane.class);

    private final Map<String, UnitButton> locationUnitsMap;
    private final Map<String, Map<String, UnitButton>> unitsPerLocationMap;
    
    public final SimpleStringProperty selectedLocationId;

    public UnitSymbolsPane() {
        super();
        locationUnitsMap = new HashMap<>();
        unitsPerLocationMap = new HashMap<>();
        selectedLocationId = new SimpleStringProperty(Constants.DUMMY_ROOM_NAME);
        
        selectedLocationId.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                updateUnitsPane();
            }
            
        });
    }
    
    /*public void addUnit(final UnitConfig unitConfig, final Point2D position) {  //TODO hand over Position or ready coordinates? where to calculate?
               
        UnitButton newButton = new UnitButton(unitConfig);
        newButton.setTranslateX(position.getY());  //Attention: swap correct? according to location pane 
        newButton.setTranslateY(position.getX());
        locationUnitsMap.put(unitConfig.getId(), newButton);
    }
    
    public void addRoomControlUnit(final UnitConfig unitConfig, final Point2D position, final String locationId) {
        UnitButton newButton = new UnitButton(unitConfig);
        newButton.setTranslateX(position.getY());  //Attention: swap correct? according to location pane 
        newButton.setTranslateY(position.getX());
        
        if(!unitsPerLocationMap.containsKey(locationId)) {
            unitsPerLocationMap.put(locationId, new HashMap<>());
        }
        unitsPerLocationMap.get(locationId).put(unitConfig.getId(), newButton);
    }*/
    
      public void addRoomUnit(UnitRemote<? extends GeneratedMessage> u, Point2D position) {
        UnitButton newButton;
        try {
            newButton = new UnitButton(u);
       
            newButton.setTranslateX(position.getY());  //Attention: swap correct? according to location pane 
            newButton.setTranslateY(position.getX());
            locationUnitsMap.put(u.getConfig().getId(), newButton);
         } catch (NotAvailableException ex) {
            java.util.logging.Logger.getLogger(UnitSymbolsPane.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
      
       public void addUnit(UnitRemote<? extends GeneratedMessage> u, Point2D position, String locationId) {
        UnitButton newButton;
        try {
            newButton = new UnitButton(u);
       
            newButton.setTranslateX(position.getY());  //Attention: swap correct? according to location pane 
            newButton.setTranslateY(position.getX());
            
            if(unitsPerLocationMap.containsKey(locationId)) {
                unitsPerLocationMap.get(locationId).put(u.getConfig().getId(), newButton);
            } else {
                Map<String, UnitButton> units = new HashMap<>();
                units.put(u.getConfig().getId(), newButton);
                unitsPerLocationMap.put(locationId, units);
            }            
         } catch (NotAvailableException ex) {
            java.util.logging.Logger.getLogger(UnitSymbolsPane.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    public void clearUnits() {
        locationUnitsMap.forEach((unitId, button) 
            -> {this.getChildren().remove(button);}
        );
        unitsPerLocationMap.forEach((locationId, entry) -> entry.forEach((unitId, button) 
                 -> {this.getChildren().remove(button);}
        ));
    }

    public void updateUnitsPane(){
        this.getChildren().clear();
        
        locationUnitsMap.forEach((unitId, button) 
            -> {if(!unitId.equals(selectedLocationId.getValue())) {this.getChildren().add(button);}}
        );
    
        if(unitsPerLocationMap.get(selectedLocationId.getValue()) != null) {
            unitsPerLocationMap.get(selectedLocationId.getValue()).forEach((unitId, button)
                -> {this.getChildren().add(button);}
            );
        }
    }

  

}