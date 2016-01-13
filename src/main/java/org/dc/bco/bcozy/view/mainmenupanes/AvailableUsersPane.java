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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.HiddenSidesPane;
import org.dc.bco.bcozy.view.Constants;
import org.dc.bco.bcozy.view.SVGIcon;

import java.util.Locale;
import java.util.ResourceBundle;


/**
 * Created by hoestreich on 12/15/15.
 */
public class AvailableUsersPane extends PaneElement {

    private final ObservableList<AvailableUserRow> availableUsersList;

    /**
     * Constructor for the AvailableUsersPane.
     */
    public AvailableUsersPane() {
        final ResourceBundle languageBundle = ResourceBundle
                .getBundle(Constants.LANGUAGE_RESOURCE_BUNDLE, Locale.getDefault());

        availableUsersList = FXCollections.observableArrayList();
        availableUsersList.addAll(new AvailableUserRow("Andi"), new AvailableUserRow("Julian"),
                new AvailableUserRow("Timo"));

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

        final VBox verticalLayout = new VBox();
        verticalLayout.setFillWidth(true);

        final Label availableUsersLbl = new Label(languageBundle.getString("availableUsers"));
        availableUsersLbl.getStyleClass().clear();
        availableUsersLbl.getStyleClass().add("small-label");
        availableUsersLbl.getStyleClass().add("padding-bottom");
        verticalLayout.getChildren().add(availableUsersLbl);

        for (final AvailableUserRow user : availableUsersList) {
            verticalLayout.getChildren().add(user);
        }
        verticalScrollPane.setContent(verticalLayout);
        verticalScrollPane.setFitToWidth(true);
        this.getChildren().addAll(hiddenSidesPane);
    }

    private class AvailableUserRow extends BorderPane {
        AvailableUserRow(final String userName) {
            // TODO: uncomment when the time comes...
//            final ImageView userIcon = ImageViewProvider
//                    .createImageView("/icons/users.png", Constants.EXTRA_SMALL_ICON);
//            final ImageView deleteUserIcon = ImageViewProvider
//                    .createImageView("/icons/delete_user.png", Constants.EXTRA_SMALL_ICON);
//            final ImageView logoutUserIcon = ImageViewProvider
//                    .createImageView("/icons/logout.png", Constants.EXTRA_SMALL_ICON);
//            final Button deleteUserBtn = new Button("", deleteUserIcon);
//            final Button logoutUserBtn = new Button("", logoutUserIcon);
//            final Label userLabel = new Label(userName, userIcon);
//            BorderPane.setAlignment(userLabel, Pos.CENTER_LEFT);
//            final HBox rightAlign = new HBox();
//            rightAlign.getChildren().addAll(deleteUserBtn, logoutUserBtn);
//            this.setLeft(userLabel);
//            this.setRight(rightAlign);
//            this.getStyleClass().add("list-element");
        }
    }

    @Override
    public Node getStatusIcon() {
        return new SVGIcon(MaterialDesignIcon.ACCOUNT_CIRCLE, Constants.MIDDLE_ICON, true);
    }
}
