/**
 * ==================================================================
 * <p>
 * This file is part of org.openbase.bco.bcozy.
 * <p>
 * org.openbase.bco.bcozy is free software: you can redistribute it and modify
 * it under the terms of the GNU General Public License (Version 3)
 * as published by the Free Software Foundation.
 * <p>
 * org.openbase.bco.bcozy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with org.openbase.bco.bcozy. If not, see <http://www.gnu.org/licenses/>.
 * ==================================================================
 */
package org.openbase.bco.bcozy.view;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import org.openbase.bco.bcozy.view.mainmenupanes.SettingsPane;

/**
 * Created by hoestreich on 11/26/15.
 */
public class CenterPane extends StackPane {

    SettingsMenu settingsMenu;

    /**
     * Constructor for the center pane.
     */
    public CenterPane() {

        settingsMenu = new SettingsMenu();

        FloatingPopUp viewModes = new FloatingPopUp(Pos.BOTTOM_RIGHT);
        viewModes.addParentElement(MaterialIcon.SETTINGS, (Runnable) null); //TODO: Add EventHandler when needed
        viewModes.addElement(MaterialDesignIcon.THERMOMETER_LINES, (Runnable) null);//TODO: Add EventHandler when needed
        viewModes.addElement(MaterialIcon.VISIBILITY, (Runnable) null);//TODO: Add EventHandler when needed

        FloatingPopUp settingsModes = new FloatingPopUp(Pos.TOP_RIGHT);
        settingsModes.addParentElement(MaterialIcon.MORE_VERT, this::hideSettings);
        settingsModes.addElement(MaterialIcon.FULLSCREEN, (Runnable) null);
        settingsModes.addElement(FontAwesomeIcon.COGS, this::showSettings);


        // Styling components with CSS
        this.getStyleClass().addAll("padding-small");


        this.setPickOnBounds(false);

        this.getChildren().addAll(viewModes, settingsModes);

    }


    private void showSettings() {
        this.getChildren().add(0, settingsMenu);
    }

    private void hideSettings() {
        this.getChildren().remove(settingsMenu);
    }


    public SettingsPane getSettingsPane() {
        return settingsMenu.getSettingsPane();
    }
}
