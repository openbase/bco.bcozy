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
import org.openbase.bco.bcozy.model.LanguageSelection;
import org.openbase.bco.bcozy.util.MultiLabelSynchronizer;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.dal.remote.layer.unit.Units;
import org.openbase.bco.dal.remote.layer.unit.user.UserRemote;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.EnumNotSupportedException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.iface.Shutdownable;
import org.openbase.jul.visual.javafx.JFXConstants;
import org.openbase.jul.visual.javafx.geometry.svg.SVGGlyphIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openbase.type.language.LabelType;
import org.openbase.type.domotic.unit.UnitConfigType.UnitConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hoestreich
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class UserPane extends BorderPane implements Shutdownable {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserPane.class);
    private final GridPane userIconPane;
    private SVGGlyphIcon userIcon;
    private SVGGlyphIcon atHomeIcon;
    private Label userNameLabel;
    private VBox userStatePane;
    private MultiLabelSynchronizer multiLabelSynchronizer;
    private UserRemote user;

    public UserPane() {
        userIcon = new SVGGlyphIcon(MaterialIcon.PERSON, JFXConstants.ICON_SIZE_MIDDLE, false);
        atHomeIcon = new SVGGlyphIcon(MaterialIcon.SEARCH, JFXConstants.ICON_SIZE_EXTRA_SMALL, true);
        multiLabelSynchronizer = new MultiLabelSynchronizer();
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
        userStatePane = new VBox();
        userStatePane.setAlignment(Pos.CENTER);


        final VBox nameAndStateLayout = new VBox(Constants.INSETS / 2);
        nameAndStateLayout.setAlignment(Pos.CENTER);
        nameAndStateLayout.getChildren().addAll(nameAndGuestLayout, userStatePane);
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

            updateUserTransitState();
            userNameLabel.setText(user.getName());
            updateUserState();
            updateBounds();
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory("Could not update dynamic components!", ex, LOGGER);
        }
    }

    private void updateUserState() {
        List<LabelType.Label> activityLabelList = new ArrayList<>();
        try {
            for (final String activityId : user.getActivityMultiState().getActivityIdList()) {
                activityLabelList.add(Registries.getActivityRegistry().getActivityConfigById(activityId).getLabel());
            }

            // generate backup label
            if (activityLabelList.isEmpty()) {
                switch (user.getUserTransitState().getValue()) {
                    case UNKNOWN:
                        break;
                    default:
                        activityLabelList.add(LanguageSelection.buildLabel(user.getUserTransitState().getValue().name()));
                        break;
                }
            }

            // update synchronizer
            multiLabelSynchronizer.removeAll();
            for (final LabelType.Label label : activityLabelList) {
                multiLabelSynchronizer.register(label);
            }

            // update userStatePane
            userStatePane.getChildren().clear();
            userStatePane.getChildren().addAll(multiLabelSynchronizer.getLabels());
        } catch (final Exception ex) {
            ExceptionPrinter.printHistory("Could not update user state!", ex, LOGGER);
        }
    }

    /**
     * Visualize the presence state of the user.
     */
    private void updateUserTransitState() {
        try {
            switch (user.getPresenceState().getValue()) {
                case PRESENT:
                    userIconPane.getChildren().remove(atHomeIcon);
                    atHomeIcon = new SVGGlyphIcon(MaterialIcon.HOME, JFXConstants.ICON_SIZE_EXTRA_SMALL, true);
                    userIconPane.add(atHomeIcon, 4, 4, 1, 1);
                    userIcon.setForegroundIconColor(Color.DODGERBLUE);
                    setManaged(true);
                    setVisible(true);
                    break;
                case ABSENT:
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
                    ExceptionPrinter.printHistory(new EnumNotSupportedException(user.getUserTransitState().getValue(), this), LOGGER);
            }
        } catch (final NotAvailableException ex) {
            ExceptionPrinter.printHistory("Could not update user presence state!", ex, LOGGER);
        }
    }

    @Override
    public void shutdown() {
    }
}
