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

package org.dc.bco.bcozy.view.devicepanes;

import javafx.geometry.Insets;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import org.dc.bco.bcozy.view.Constants;


/**
 * Created by agatting on 24.11.15.
 */
public class TitledPanes extends VBox {

    /**
     * Constructor for the TitledPanes.
     * @param unitTypesName All names of the unit types (inclusive number of unit types)
     */
    public TitledPanes(final String[] unitTypesName) {

        //TODO vbox needless...
        final VBox vBox = new VBox(Constants.INSETS);

        //TODO give singleTitlePane a suitable unit list
        for (int i = 0; i < unitTypesName.length; i++) {
            final TitledPane titledPane = singleTitledPane(unitTypesName[i]);
            vBox.getChildren().add(titledPane);
        }
        this.getChildren().add(vBox);
    }


    private TitledPane singleTitledPane(final String unitTypeName) {

        final TitledPane titledPane;
        final TilePane tilePane;

        tilePane = new TilePane();
        tilePane.setPrefColumns(1);
        tilePane.setPadding(new Insets(0, 0, 0, 0));

        //final ShutterPane shutterPane = new ShutterPane(new ShutterInstance("Shutter Living", 50.0));
        //final ShutterPane shutterPane1 = new ShutterPane(new ShutterInstance("Shutter Kitchen", 0.0));
        //final ShutterPane shutterPane2 = new ShutterPane(new ShutterInstance("Shutter Sports", 100.0));
        //final ShutterPane shutterPane3 = new ShutterPane(new ShutterInstance("Shutter Control", 100.0));
        final LightBulbPane lightBulbPane = new LightBulbPane();

        //tilePane.getChildren().addAll(shutterPane, shutterPane1, shutterPane2, shutterPane3, widgetPaneElement);
        tilePane.getChildren().add(lightBulbPane);

        titledPane = new TitledPane();
        titledPane.setText(unitTypeName);
        titledPane.setContent(tilePane);

        return titledPane;
    }
}
