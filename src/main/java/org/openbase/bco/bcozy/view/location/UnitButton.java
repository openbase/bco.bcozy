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

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import org.openbase.bco.bcozy.view.SVGIcon;
import org.openbase.bco.bcozy.view.generic.WidgetPane.DisplayMode;
import org.openbase.bco.bcozy.view.pane.unit.AbstractUnitPane;
import org.openbase.bco.bcozy.view.pane.unit.UnitPaneFactoryImpl;
import org.openbase.jul.exception.CouldNotPerformException;
import rst.domotic.unit.UnitConfigType.UnitConfig;
import rst.domotic.unit.UnitTemplateType;

/**
 *
 */
public class UnitButton extends Pane {

    private final double DEFAULT_ICON_SIZE = 8.0;

    /**
     * Creates a button with suitable unit symbol
     *
     * @param config config of the remote unit
     */
    // LocationRemote unit = Units.getUnit("locaction unit id", true, Units.LOCATION);
    //   unit.getUnits(UnitTemplateType.UnitTemplate.UnitType.UNKNOWN, true, unitRemoteClass);
    public UnitButton(UnitConfig config)  {
           
        try {                        
            AbstractUnitPane content;
            System.out.println(Platform.isFxApplicationThread());
            content = UnitPaneFactoryImpl.getInstance().newInitializedInstance(config);
            content.setDisplayMode(DisplayMode.ICON_ONLY);
            this.getChildren().add(content);
        } catch (CouldNotPerformException | InterruptedException ex) {
            Logger.getLogger(UnitButton.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void setUnitPane(AbstractUnitPane content) {
        this.getChildren().clear();
        this.getChildren().add(content);
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
