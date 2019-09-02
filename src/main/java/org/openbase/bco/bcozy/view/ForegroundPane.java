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

import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.BoundingBox;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.openbase.bco.bcozy.controller.CenterPaneController;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.iface.DefaultInitializable;

/**
 * @author hoestreich
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class ForegroundPane extends BorderPane implements DefaultInitializable {

    private static ForegroundPane instance;

    private final MainMenu mainMenu;
    private final UnitMenu unitMenu;
    private final CenterPane centerPane;
    private final MenuHeader menuHeader;
    private final InfoPane infoFooter;

    /**
     * Constructor for the ForegroundPane.
     *
     * @param height Height of the application window
     * @param width Width of the application window
     */
    public ForegroundPane(final double height, final double width) throws InstantiationException {
        try {
            this.mainMenu = new MainMenu(height - 150, 300);
            this.unitMenu = new UnitMenu(height - 150, 300);
            this.unitMenu.getFullscreen().setOnAction(event -> setMaximizeAction());
            this.unitMenu.getSettingsBtn().setOnAction(event -> toggleSettings());
            this.menuHeader = new MenuHeader(30, width);
            this.infoFooter = new InfoPane(20, width);
            this.centerPane = new CenterPane(height - 150, this);

            //this.setTop(this.menuHeader);
            this.setLeft(this.mainMenu);
            this.setRight(this.unitMenu);
            this.setBottom(this.infoFooter);
            this.setCenter(this.centerPane);
            this.setTop(this.menuHeader);
            this.setPickOnBounds(false);

            instance = this;
        } catch (CouldNotPerformException ex) {
            throw new InstantiationException(this, ex);
        }
    }

    public static ForegroundPane getInstance() throws NotAvailableException {
        if(instance == null) {
            throw new NotAvailableException("ForegroundPane");
        }
        return instance;
    }

    @Override
    public void init() throws InterruptedException {
        try {
            mainMenu.init();
        } catch (CouldNotPerformException ex) {
            new InitializationException(this, ex);
        }
    }

    /**
     * Getter for the main menu (on the left).
     *
     * @return MainMenu (VBox)
     */
    public MainMenu getMainMenu() {
        return mainMenu;
    }

    /**
     * Getter for the context menu (bottom or right).
     *
     * @return ContextMenu Instance (VBox)
     */
    public UnitMenu getUnitMenu() {
        return unitMenu;
    }

    /**
     * Getter for the center pane.
     *
     * @return CenterPane
     */
    public CenterPane getCenterPane() {
        return centerPane;
    }

    /**
     * Getter for the info footer (bottom).
     *
     * @return InfoPane (HBox)
     */
    public InfoPane getInfoFooter() {
        return infoFooter;
    }

    /**
     * Method to provide a bounding box within which the location should be drawn.
     *
     * @return a bounding box with the values.
     */
    public BoundingBox getBoundingBox() {
        return new BoundingBox(this.mainMenu.getLayoutBounds().getMaxX(),
                this.menuHeader.getLayoutBounds().getMaxY(),
                this.centerPane.getWidth(),
                this.centerPane.getHeight());
    }

    /**
     * Method to provide the width property of the bounding box within which the location should be drawn.
     *
     * @return a property of the width from the bounding box.
     */
    public ReadOnlyDoubleProperty getMainMenuWidthProperty() {
        return this.mainMenu.widthProperty();
    }


    private void setMaximizeAction() {
        final Stage stage = (Stage) unitMenu.getScene().getWindow();
        if (stage.isFullScreen()) {
            unitMenu.getFullscreen().changeIcon(MaterialIcon.FULLSCREEN);
            stage.setFullScreen(false);
        } else {
            unitMenu.getFullscreen().changeIcon(MaterialIcon.FULLSCREEN_EXIT);
            stage.setFullScreen(true);
        }
    }

    private void toggleSettings() {
        if (centerPane.getChildren().contains(centerPane.getSettingsMenu())) {
            centerPane.getChildren().remove(centerPane.getSettingsMenu());
        } else {
            centerPane.getChildren().add(0, centerPane.getSettingsMenu());
            centerPane.getSettingsMenu().toFront();
        }
    }
}
