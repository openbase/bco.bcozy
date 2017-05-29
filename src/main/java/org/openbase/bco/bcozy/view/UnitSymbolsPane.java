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
package org.openbase.bco.bcozy.view;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import org.openbase.bco.bcozy.view.location.UnitButton;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.NotAvailableException;
import rst.domotic.unit.UnitConfigType;

/**
 *
 * @author lili
 */
public class UnitSymbolsPane extends Pane{
	
	private BackgroundPane background;
	
	public UnitSymbolsPane(BackgroundPane bg) {
		this.background = bg;
		this.addUnit(new SVGIcon(FontAwesomeIcon.LIGHTBULB_ALT, 30.0, true), null, new Point2D(20,20));
		
	}
	
	public void drawUnitSymbols() throws NotAvailableException {
		
		/*for(TilePolygon x: background.getTileMap().values()) {
			x.getUnitRemote().getConfig().getPlacementConfig().getPosition();
		}
	
		for (final Map.Entry<UnitTemplateType.UnitTemplate.UnitType, List<UnitRemote>> nextEntry : Units.getUnit(locationUnitConfig.getId(), false, Units.LOCATION).getUnitMap().entrySet()) {
			if (nextEntry.getValue().isEmpty()) {
				continue;
			}
			AbstractUnitPane blubs = UnitPaneFactoryImpl.getInstance().newInstance(nextEntry.getKey());

			this.addUnit(blubs.getIcon(), null, new Point2D(5, 5));
		}*/
		
		
	}
	
	public void addUnit(final SVGIcon svgIcon, final EventHandler<ActionEvent> onActionHandler,
            final Point2D position) {
        final UnitButton unitButton = new UnitButton(svgIcon, onActionHandler);
        unitButton.setTranslateX(position.getX());
        unitButton.setTranslateY(position.getY());
        this.getChildren().add(unitButton);
    }
	
	 /**
     * Adds a room to the location Pane and use the controls to add a mouse event handler.
     *
     * If a room with the same id already exists, it will be overwritten.
     *
     * @param locationUnitConfig the configuration of the location to add.
     * @throws org.openbase.jul.exception.CouldNotPerformException
     * @throws java.lang.InterruptedException
     */
    public void addLocation(final UnitConfigType.UnitConfig locationUnitConfig) throws CouldNotPerformException, InterruptedException {
       /* try {
            
           
		for (final Map.Entry<UnitTemplateType.UnitTemplate.UnitType, List<UnitRemote>> nextEntry : Units.getUnit(locationUnitConfig.getId(), false, Units.LOCATION).getUnitMap().entrySet()) {
                if (nextEntry.getValue().isEmpty()) {
                    continue;
                }
                AbstractUnitPane blubs = UnitPaneFactoryImpl.getInstance().newInstance(nextEntry.getKey());
				
				this.addUnit(blubs.getIcon(), null, new Point2D(5,5));
            }
        } catch (CouldNotPerformException ex) {
            throw new CouldNotPerformException("Could not add location!", ex);
        }*/
    }	
}
