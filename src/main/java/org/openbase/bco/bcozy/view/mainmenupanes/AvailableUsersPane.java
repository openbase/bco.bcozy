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

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import java.util.ArrayList;
import java.util.MissingResourceException;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.HiddenSidesPane;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.SVGIcon;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.pattern.Observable;
import org.openbase.jul.pattern.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.registry.UserRegistryDataType.UserRegistryData;
import rst.domotic.unit.UnitConfigType.UnitConfig;

/**
 * Created by hoestreich on 12/15/15.
 *
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class AvailableUsersPane extends PaneElement {

    private static final Logger LOGGER = LoggerFactory.getLogger(AvailableUsersPane.class);

    private final BorderPane statusIcon;
    private final VBox userPanes;

    /**
     * Constructor for the AvailableUsersPane.
     */
    public AvailableUsersPane() {
//        try {
        userPanes = new VBox(Constants.INSETS);
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

        verticalScrollPane.setContent(userPanes);
        verticalScrollPane.setFitToWidth(true);
        this.getChildren().addAll(hiddenSidesPane);
        //        } catch (CouldNotPerformException ex) {
        //            throw new org.openbase.jul.exception.InstantiationException(this, ex);
        //        }
    }

    public void init() throws InitializationException, InterruptedException {
        try {
            Registries.getUserRegistry().addDataObserver(new Observer<UserRegistryData>() {
                @Override
                public void update(Observable<UserRegistryData> source, UserRegistryData data) throws Exception {
                    Platform.runLater(() -> {
                        updateDynamicComponents();
                    });
                }
            });
            Platform.runLater(() -> {
                updateDynamicComponents();
            });
        } catch (CouldNotPerformException ex) {
            throw new InitializationException(this, ex);
        }
    }

    public void updateDynamicComponents() {
        try {
            if (!Registries.getUserRegistry().isDataAvailable()) {
                return;
            }

            new ArrayList<>(userPanes.getChildren()).forEach((userPane) -> {
                ((UserPane) userPane).shutdown();
                userPanes.getChildren().remove(userPane);
            });

            for (final UnitConfig userUniConfig : Registries.getUserRegistry().getUserConfigs()) {
                final UserPane userPane = new UserPane();
                userPane.init(userUniConfig);
                userPanes.getChildren().add(userPane);
            }
        } catch (CouldNotPerformException | MissingResourceException | InterruptedException ex) {
            ExceptionPrinter.printHistory(new CouldNotPerformException(ex), LOGGER);
        }
    }

    @Override
    public Node getStatusIcon() {
        return statusIcon;
    }
}
