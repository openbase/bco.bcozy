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
package org.openbase.bco.bcozy.view.mainmenupanes;

import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.ObserverLabel;
import org.openbase.bco.bcozy.view.SVGIcon;
import org.openbase.bco.dal.remote.unit.Units;
import org.openbase.bco.dal.remote.unit.user.UserRemote;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.EnumNotSupportedException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.iface.Shutdownable;
import org.openbase.jul.pattern.Observable;
import org.openbase.jul.pattern.Observer;
import org.openbase.jul.processing.StringProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.state.UserActivityStateType.UserActivityState;
import rst.domotic.unit.UnitConfigType.UnitConfig;
import rst.domotic.unit.user.UserDataType;

/**
 * @author hoestreich
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 *
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class UserPane extends BorderPane implements Shutdownable {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserPane.class);

    private SVGIcon userIcon;
    private SVGIcon atHomeIcon;
    private Label userNameLabel;
    private ObserverLabel userStateLabel;
    private UserRemote user;
    private final GridPane userIconPane;

    public UserPane() {
        userIcon = new SVGIcon(MaterialIcon.PERSON, Constants.MIDDLE_ICON, false);
        atHomeIcon = new SVGIcon(MaterialIcon.SEARCH, Constants.EXTRA_SMALL_ICON, true);
        userIconPane = new GridPane();
        userIconPane.setVgap(Constants.INSETS);
        userIconPane.setHgap(Constants.INSETS);
        //CHECKSTYLE.OFF: MagicNumbers
        userIconPane.add(userIcon, 0, 0, 5, 5);
        userIconPane.add(atHomeIcon, 4, 4, 1, 1);
        //CHECKSTYLE.ON: MagicNumbers

        userNameLabel = new Label();
        userNameLabel.getStyleClass().add(Constants.BOLD_LABEL);

        final HBox nameAndGuestLayout = new HBox(Constants.INSETS);
        nameAndGuestLayout.getChildren().addAll(userNameLabel);
        nameAndGuestLayout.setAlignment(Pos.CENTER);
        userStateLabel = new ObserverLabel("---");

        final VBox nameAndStateLayout = new VBox(Constants.INSETS / 2);
        nameAndStateLayout.setAlignment(Pos.CENTER);
        nameAndStateLayout.getChildren().addAll(nameAndGuestLayout, userStateLabel);

        this.setLeft(userIconPane);
        this.setCenter(nameAndStateLayout);

    }

//    /**
//     * Constructor for UserPane.
//     * @param userName userName.
//     * @param guest guest.
//     * @param userState userState.
//     * @param atHome atHome.
//     */
//    public UserPane(final String userName, final boolean guest, final String userState, final boolean atHome) {
//        init(userName, guest, userState, atHome);
//    }
    public void init(final UnitConfig userUniConfig) throws InitializationException, InterruptedException {
        try {
            user = Units.getUnit(userUniConfig, false, Units.USER);
            user.addDataObserver(new Observer<UserDataType.UserData>() {
                @Override
                public void update(Observable<UserDataType.UserData> source, UserDataType.UserData data) throws Exception {
                    Platform.runLater(() -> {
                        updateDynamicComponents();
                    });
                }
            });
            updateDynamicComponents();
        } catch (CouldNotPerformException ex) {
            throw new InitializationException(this, ex);
        }
    }

    private void updateDynamicComponents() {
        try {
            // filter if no data is available
            if (user == null || !user.isDataAvailable()) {
                return;
            }

            updateUserPresenceState();
            userNameLabel.setText(user.getName());
            updateUserState();
            updateBounds();
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory("Could not update dynamic components!", ex, LOGGER);
        }
    }

    private void updateUserState() {
        String userState = null;
        try {
            if (user.getUserActivityState().getCurrentActivity() != UserActivityState.Activity.UNKNOWN) {
                userState = StringProcessor.transformUpperCaseToCamelCase(user.getUserActivityState().getCurrentActivity().name());
            }

            // generate user state fallback
            if (userState == null) {
                userState = user.getUserPresenceState().getValue().name();
            }

        } catch (final NotAvailableException ex) {
            ExceptionPrinter.printHistory("Could not update user presence state!", ex, LOGGER);
            try {
                if (user.getConfig().getUserConfig().getOccupant()) {
                    userState = "occupant";
                } else {
                    userState = "guest";
                }
            } catch (NotAvailableException ex1) {
                userState = "";
            }
            userStateLabel.setText(userState);
        }
    }

    /**
     * Visualize the presence state of the user.
     */
    private void updateUserPresenceState() {
        try {
            switch (user.getUserPresenceState().getValue()) {
                case AT_HOME:
                case SHORT_AT_HOME:
                case SOON_AWAY:
                    userIconPane.getChildren().remove(atHomeIcon);
                    atHomeIcon = new SVGIcon(MaterialIcon.HOME, Constants.EXTRA_SMALL_ICON, true);
                    userIconPane.add(atHomeIcon, 4, 4, 1, 1);
                    userIcon.setForegroundIconColor(Color.DODGERBLUE);
                    setManaged(true);
                    setVisible(true);
                    break;
                case AWAY:
                case SHORT_AWAY:
                case SOON_AT_HOME:
                    atHomeIcon = new SVGIcon(MaterialIcon.DIRECTIONS_WALK, Constants.EXTRA_SMALL_ICON, true);
                    userIcon.setForegroundIconColor(Color.LIGHTGRAY);

                    // do not display user pane if user is a guest and not present.
                    if (!user.getConfig().getUserConfig().getOccupant()) {
                        setManaged(false);
                        setVisible(false);
                    }
                    break;
                case UNKNOWN:
                    atHomeIcon = new SVGIcon(MaterialIcon.SEARCH, Constants.EXTRA_SMALL_ICON, true);
                    userIcon.setForegroundIconColor(Color.DARKGREY);
                    // do not display user pane if user is a guest and not present.
                    if (!user.getConfig().getUserConfig().getOccupant()) {
                        setManaged(false);
                        setVisible(false);
                    }
                    break;
                default:
                    ExceptionPrinter.printHistory(new EnumNotSupportedException(user.getUserPresenceState().getValue(), this), LOGGER);
            }
        } catch (final NotAvailableException ex) {
            ExceptionPrinter.printHistory("Could not update user presence state!", ex, LOGGER);
        }
    }

    /**
     * Setter for the userState Label.
     *
     * @param newUserState must be a string identifier from language properties
     */
    public void setUserState(final String newUserState) {
        userStateLabel.setIdentifier(newUserState);
    }

    /**
     * Setter for the user name Label.
     *
     * @param newUserName a string value for the label text
     */
    public void setUserName(final String newUserName) {
        userNameLabel.setText(newUserName);
    }

    @Override
    public void shutdown() {
    }
}
