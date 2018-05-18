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
import org.openbase.jul.visual.javafx.JFXConstants;
import org.openbase.jul.visual.javafx.geometry.svg.SVGGlyphIcon;
import org.openbase.bco.dal.remote.unit.Units;
import org.openbase.bco.dal.remote.unit.user.UserRemote;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.EnumNotSupportedException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.iface.Shutdownable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.unit.UnitConfigType.UnitConfig;

/**
 * @author hoestreich
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class UserPane extends BorderPane implements Shutdownable {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserPane.class);

    private SVGGlyphIcon userIcon;
    private SVGGlyphIcon atHomeIcon;
    private Label userNameLabel;
    private ObserverLabel userStateLabel;
    private UserRemote user;
    private final GridPane userIconPane;

    public UserPane() {
        userIcon = new SVGGlyphIcon(MaterialIcon.PERSON, JFXConstants.ICON_SIZE_MIDDLE, false);
        atHomeIcon = new SVGGlyphIcon(MaterialIcon.SEARCH, JFXConstants.ICON_SIZE_EXTRA_SMALL, true);
        userIconPane = new GridPane();
        userIconPane.setVgap(Constants.INSETS);
        userIconPane.setHgap(Constants.INSETS);

        userIconPane.add(userIcon, 0, 0, 5, 5);
        userIconPane.add(atHomeIcon, 4, 4, 1, 1);

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

    public void init(final UnitConfig userUniConfig) throws InitializationException, InterruptedException {
        try {
            user = Units.getUnit(userUniConfig, false, Units.USER);
            user.addDataObserver((source, data) -> Platform.runLater(() -> {
                updateDynamicComponents();
            }));
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
            try {
                if (!user.getActivityState().hasActivityId() || user.getActivityState().getActivityId().isEmpty()) {
                    throw new NotAvailableException("Activity");
                }
                userState = Registries.getActivityRegistry().getActivityConfigById(user.getActivityState().getActivityId()).getLabel();
            } catch (CouldNotPerformException ex) {
                // generate user state fallback
                userState = user.getUserPresenceState().getValue().name();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                return;
            }
            userStateLabel.setIdentifier(userState);
        } catch (final Exception ex) {
            ExceptionPrinter.printHistory("Could not update user presence state!", ex, LOGGER);
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
                    atHomeIcon = new SVGGlyphIcon(MaterialIcon.HOME, JFXConstants.ICON_SIZE_EXTRA_SMALL, true);
                    userIconPane.add(atHomeIcon, 4, 4, 1, 1);
                    userIcon.setForegroundIconColor(Color.DODGERBLUE);
                    setManaged(true);
                    setVisible(true);
                    break;
                case AWAY:
                case SHORT_AWAY:
                case SOON_AT_HOME:
                    atHomeIcon = new SVGGlyphIcon(MaterialIcon.DIRECTIONS_WALK, JFXConstants.ICON_SIZE_EXTRA_SMALL, true);
                    userIcon.setForegroundIconColor(Color.LIGHTGRAY);

                    // do not display user pane if user is a guest and not present.
                    if (!user.getConfig().getUserConfig().getOccupant()) {
                        setManaged(false);
                        setVisible(false);
                    }
                    break;
                case UNKNOWN:
                    atHomeIcon = new SVGGlyphIcon(MaterialIcon.SEARCH, JFXConstants.ICON_SIZE_EXTRA_SMALL, true);
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

    @Override
    public void shutdown() {
    }
}
