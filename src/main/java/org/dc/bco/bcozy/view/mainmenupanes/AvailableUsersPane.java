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
package org.dc.bco.bcozy.view.mainmenupanes;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.HiddenSidesPane;
import org.dc.bco.bcozy.view.Constants;
import org.dc.bco.bcozy.view.SVGIcon;

/**
 * Created by hoestreich on 12/15/15.
 */
public class AvailableUsersPane extends PaneElement {

    private final BorderPane statusIcon;
    /**
     * Constructor for the AvailableUsersPane.
     */
    public AvailableUsersPane() {

        statusIcon = new BorderPane(new SVGIcon(MaterialDesignIcon.ACCOUNT_CIRCLE, Constants.MIDDLE_ICON, true));

        final ScrollPane verticalScrollPane = new ScrollPane();
        verticalScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        verticalScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        final ScrollBar scrollBar = new ScrollBar();
        scrollBar.setOrientation(Orientation.VERTICAL);
        final HiddenSidesPane hiddenSidesPane = new HiddenSidesPane();
        hiddenSidesPane.setContent(verticalScrollPane);
        hiddenSidesPane.setRight(scrollBar);
        //hiddenSidesPane.setTriggerDistance(Constants.TRIGGER_DISTANCE);

        scrollBar.maxProperty().bind(verticalScrollPane.vmaxProperty());
        scrollBar.minProperty().bind(verticalScrollPane.vminProperty());
//
//        AdvancedHorizontalSlider advancedHorizontalSlider = new AdvancedHorizontalSlider(10, 30);
        final VBox userPanes = new VBox(Constants.INSETS);
        final UserPane userMarian = new UserPane("Marian", false, "userStateCooking", true);
        final UserPane userTamino = new UserPane("Tamino", true, "userStateWatchingTV", true);
        final UserPane userAndi = new UserPane("Andi", false, "userStateNotAvailable", false);
        final UserPane userJulian = new UserPane("Julian", false, "userStateSleeping", true);
        userPanes.getChildren().addAll(userMarian, userTamino, userAndi, userJulian);

        verticalScrollPane.setContent(userPanes);
        verticalScrollPane.setFitToWidth(true);
        this.getChildren().addAll(hiddenSidesPane);
    }


    @Override
    public Node getStatusIcon() {
        return statusIcon;
    }
}
