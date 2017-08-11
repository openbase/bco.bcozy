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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.layout.Pane;
import org.openbase.bco.bcozy.view.generic.WidgetPane.DisplayMode;
import org.openbase.bco.bcozy.view.pane.unit.AbstractUnitPane;
import org.openbase.bco.bcozy.view.pane.unit.UnitPaneFactoryImpl;
import org.openbase.bco.dal.lib.layer.unit.UnitRemote;
import org.openbase.jul.exception.CouldNotPerformException;

/**
 *
 */
public class UnitButton extends Pane {

    private final double DEFAULT_ICON_SIZE = 8.0;
    UnitRemote<? extends GeneratedMessage> unitRemote;

    /**
     * Creates a button with suitable unit symbol
     *
     * @param config config of the remote unit
     */
    // LocationRemote unit = Units.getUnit("locaction unit id", true, Units.LOCATION);
    //   unit.getUnits(UnitTemplateType.UnitTemplate.UnitType.UNKNOWN, true, unitRemoteClass);
    /*public UnitButton(UnitConfig config)  {
           
        try {                        
            AbstractUnitPane content;
            content = UnitPaneFactoryImpl.getInstance().newInitializedInstance(config);
            content.setDisplayMode(DisplayMode.ICON_ONLY);
            this.getChildren().add(content);
        } catch (CouldNotPerformException | InterruptedException ex) {
            Logger.getLogger(UnitButton.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }*/

    public UnitButton(UnitRemote<? extends GeneratedMessage> u) {
        try {                                 
            AbstractUnitPane content;
            content = UnitPaneFactoryImpl.getInstance().newInitializedInstance(u.getConfig());
            content.setDisplayMode(DisplayMode.ICON_ONLY); 
            unitRemote = content.getUnitRemote();
            this.getChildren().add(content);
        } catch (CouldNotPerformException | InterruptedException ex) {
            Logger.getLogger(UnitButton.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public UnitRemote<? extends GeneratedMessage> getUnitRemote() {
        return this.unitRemote;
    }
    

    
   /* 

    final ContextMenu cm = new ContextMenu();
            MenuItem cmItem1 = new MenuItem("Toggle power state");
            try {
                UnitPaneFactoryImpl.getInstance().newInstance(UnitPaneFactoryImpl.loadUnitPaneClass(config.getType()));
                CustomMenuItem cmItem2 = new CustomMenuItem();
                cm.getItems().add(cmItem2);
            } catch (CouldNotPerformException ex) {
                Logger.getLogger(UnitButton.class.getName()).log(Level.SEVERE, null, ex);
            }
            cmItem1.setOnAction((ActionEvent e) -> {
                //
            });
            cm.getItems().add(cmItem1);
            this.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
                if (e.getButton() == MouseButton.SECONDARY) {
                    cm.show(this.getParent(), e.getScreenX(), e.getScreenY());
                }
            });
 
    */
}
