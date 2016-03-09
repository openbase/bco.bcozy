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

import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.dc.bco.bcozy.view.Constants;
import org.dc.bco.bcozy.view.ObserverLabel;
import org.dc.bco.bcozy.view.SVGIcon;

/**
 * Created by hoestreich on 1/28/16.
 */
public class UserPane extends BorderPane {

    private SVGIcon userIcon;
    private SVGIcon atHomeIcon;
    private Label userNameLabel;
    private ObserverLabel userStateLabel;

    /**
     * Constructor for UserPane.
     * @param userName userName.
     * @param guest guest.
     * @param userState userState.
     * @param atHome atHome.
     */
    public UserPane(final String userName, final boolean guest, final String userState, final boolean atHome) {
        init(userName, guest, userState, atHome);
    }

    private void init(final String userName, final boolean guest, final String userState, final boolean atHome) {

        userIcon = new SVGIcon(MaterialIcon.PERSON, Constants.MIDDLE_ICON, false);
        setAtHome(atHome);

        final GridPane userIconPane = new GridPane();
        userIconPane.setVgap(Constants.INSETS);
        userIconPane.setHgap(Constants.INSETS);
        //CHECKSTYLE.OFF: MagicNumbers
        userIconPane.add(userIcon, 0, 0, 5, 5);
        userIconPane.add(atHomeIcon, 4, 4, 1, 1);
        //CHECKSTYLE.ON: MagicNumbers

        userNameLabel = new Label(userName);
        userNameLabel.getStyleClass().add(Constants.BOLD_LABEL);
        final ObserverLabel guestLabel = new ObserverLabel("guest");
        guestLabel.getStyleClass().add(Constants.BOLD_LABEL);
        if (guest) {
            guestLabel.setVisible(true);
        } else {
            guestLabel.setVisible(false);
        }

        final HBox nameAndGuestLayout = new HBox(Constants.INSETS);
        nameAndGuestLayout.getChildren().addAll(userNameLabel, guestLabel);
        nameAndGuestLayout.setAlignment(Pos.CENTER);
        userStateLabel = new ObserverLabel(userState);


        final VBox nameAndStateLayout = new VBox(Constants.INSETS / 2);
        nameAndStateLayout.setAlignment(Pos.CENTER);
        nameAndStateLayout.getChildren().addAll(nameAndGuestLayout, userStateLabel);

        this.setLeft(userIconPane);
        this.setCenter(nameAndStateLayout);
    }

    /**
     * Sets the at home value of the user.
     * @param atHome true if at home, false if on the way
     */
    public void setAtHome(final boolean atHome) {
        if (atHome) {
            atHomeIcon = new SVGIcon(MaterialIcon.HOME, Constants.EXTRA_SMALL_ICON, true);
            userIcon.setForegroundIconColor(Color.DODGERBLUE);
        } else {
            atHomeIcon = new SVGIcon(MaterialIcon.DIRECTIONS_WALK, Constants.EXTRA_SMALL_ICON, true);
            userIcon.setForegroundIconColor(Color.LIGHTGRAY);
        }
    }

    /**
     * Setter for the userState Label.
     * @param newUserState must be a string identifier from language properties
     */
    public void setUserState(final String newUserState) {
        userStateLabel.setIdentifier(newUserState);
    }

    /**
     * Setter for the user name Label.
     * @param newUserName a string value for the label text
     */
    public void setUserName(final String newUserName) {
        userNameLabel.setText(newUserName);
    }
}
