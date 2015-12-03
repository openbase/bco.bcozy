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

import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.controlsfx.control.PlusMinusSlider;
import org.dc.bco.bcozy.model.ShutterInstance;
import org.dc.bco.bcozy.view.Constants;

/**
 * Created by hoestreich on 11/19/15.
 */
public class ShutterPane extends VBox {

    /**
     * Constructor for a ShutterPane.
     * @param shutterInstance the shutterInstance which should be controlled / observed with this pane
     */
    public ShutterPane(final ShutterInstance shutterInstance) {
        //final UnitPane devicePane = new UnitPane(shutterInstance.getShutterName(), initContent(shutterInstance));
        //devicePane.setFillWidth(true);
        this.setFillWidth(true);
        //this.getChildren().add(devicePane);
    }

    /**
     * Method for gui generation to keep the constructor smaller.
     * @param shutterInstance the shutter instance for which a gui should be created
     * @return
     */
    private BorderPane initContent(final ShutterInstance shutterInstance) {
        final BorderPane borderPane = new BorderPane();
        final BorderPane verticalLayout = new BorderPane();
        //TODO: Implement Icon class which adapts its size depending on the window size (responsive)
        final Label actualValue = new Label("Aktueller Wert: " + Double.toString(shutterInstance.getOpeningRatio()));
        final Image imageIcon = new Image(getClass().getResourceAsStream("/icons/shutter.png"));
        final ImageView imageViewIcon = new ImageView(imageIcon);
        imageViewIcon.setFitHeight(Constants.MIDDLEICON);
        imageViewIcon.setFitWidth(Constants.MIDDLEICON);

        final PlusMinusSlider control = new PlusMinusSlider();
        control.setOrientation(Orientation.VERTICAL);
        verticalLayout.setTop(imageViewIcon);
        verticalLayout.setBottom(actualValue);

        borderPane.setLeft(verticalLayout);
        borderPane.setRight(control);
        return borderPane;
    }
}
