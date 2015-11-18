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
package org.dc.bco.bcozy;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import org.dc.bco.bcozy.view.ForegroundPane;

/**
 * Created by hoestreich on 11/18/15.
 */
public class MenuController {

    /**
     * Constructor for the Menu Controller.
     * @param foregroundPane The pane where all menus are nested.
     */
    public MenuController(final ForegroundPane foregroundPane) {
        foregroundPane.getMenuHeader().getMainMenuBtn().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent event) {
                if (foregroundPane.getMainMenu().isVisible()) {
                    foregroundPane.getMainMenu().hideMainMenu();
                } else {
                    foregroundPane.getMainMenu().showMainMenu();
                }
            }
        });
    }
}
