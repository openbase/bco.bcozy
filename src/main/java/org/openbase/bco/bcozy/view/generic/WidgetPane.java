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
package org.openbase.bco.bcozy.view.generic;

import de.jensd.fx.glyphs.GlyphIcons;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.controlsfx.control.ToggleSwitch;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.ObserverText;
import org.openbase.bco.bcozy.view.SVGIcon;
import org.openbase.jul.schedule.GlobalCachedExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author agatting
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class WidgetPane extends VBox implements DynamicPane {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    /**
     * Text for tooltip dependent on selected language.
     */
    private final ObserverText infoText = new ObserverText("DUMMY");

    /**
     * ToggleSwitch Button on right side of the header Pane. Is visible via activation at
     * constructor (oneClickActivatable).
     */
    private final ToggleSwitch toggleSwitch = new ToggleSwitch();

    /**
     * GridPane as Area on the left side of the header Pane (Icon, tooltip, ...).
     */
    private final GridPane iconPane = new GridPane();

    /**
     * Tooltip for Icon description (current state).
     */
    private final Tooltip tooltip = new Tooltip("");

    private final SVGIcon mainIcon;

    private final Label widgetLabel;

    /**
     * Head content pane to visualize the main functionally.
     */
    final BorderPane headPane;

    protected final BooleanProperty primaryActivationProperty;
    protected final BooleanProperty secondaryActivationProperty;

    /**
     * defines if this widget can be activated e.g. by mouse click.
     */
    private final boolean activateable;

    /**
     * Constructor for the widget pane.
     *
     * @param activateable defines if this widget should be activateable e.g. via mouse click.
     */
    public WidgetPane(final boolean activateable) {
        this.activateable = activateable;
        this.getStyleClass().add("widget-pane");
        this.headPane = new BorderPane();
        this.primaryActivationProperty = new SimpleBooleanProperty();
        this.secondaryActivationProperty = new SimpleBooleanProperty();
        this.mainIcon = new SVGIcon(MaterialDesignIcon.VECTOR_CIRCLE, Constants.SMALL_ICON, false);
        this.widgetLabel = new Label("?");
        this.initContent();
        this.updateDynamicContent();
    }

    private void initContent() {
        toggleSwitch.setMouseTransparent(true);

        Tooltip.install(iconPane, tooltip);

        initHeadPane();

        if (headPane.getCenter() != null) {
            BorderPane.setMargin(headPane.getCenter(), new Insets(0.0, 0.0, 0.0, Constants.INSETS));
        }
        if (headPane.getRight() != null) {
            BorderPane.setMargin(headPane.getRight(), new Insets(0.0, Constants.INSETS, 0.0, 0.0));
        }
        this.getChildren().add(headPane);

        this.minHeightProperty().bind(headPane.prefHeightProperty());
        this.maxHeightProperty().bind(headPane.prefHeightProperty());

        if (activateable) {
            toggleSwitch.setOnMouseClicked(event -> GlobalCachedExecutorService.submit(() -> {
                toggleActivation();
                return null;
            }));
            headPane.setOnMouseClicked(event -> {
                switch (event.getClickCount()) {
                    case 1:
                        toggleActivation();
                        break;
                    case 2:
                        toggleSecondFunction();
                        break;
                }
            });
            toggleSwitch.selectedProperty().bindBidirectional(primaryActivationProperty);
        }
    }

    public void initHeadPane() {
        headPane.getStyleClass().clear();
        headPane.getStyleClass().add("head-pane");
        iconPane.add(mainIcon, 0, 0);

        headPane.setLeft(iconPane);
        headPane.setAlignment(iconPane, Pos.CENTER);

        headPane.setCenter(widgetLabel);
        headPane.setAlignment(widgetLabel, Pos.CENTER_LEFT);

        if (activateable) {

            // workaround to center the toggle switch, default value -1 aligns the switch not in the pane center which looks bad.
            toggleSwitch.maxHeightProperty().set(1);

            headPane.setRight(toggleSwitch);
            headPane.setAlignment(toggleSwitch, Pos.CENTER);
        }
    }

    public void setIcon(final GlyphIcons foregroundIcon, final Color foregroundIconColor, final GlyphIcons backgroundIcon, final Color backgroundIconColor) {
        mainIcon.setForegroundIcon(foregroundIcon, foregroundIconColor);
        mainIcon.setBackgroundIcon(backgroundIcon, backgroundIconColor);
    }

    public void setIcon(final GlyphIcons foregroundIcon, final GlyphIcons backgroundIcon) {
        mainIcon.setForegroundIcon(foregroundIcon);
        mainIcon.setBackgroundIcon(backgroundIcon);
    }

    public void setForegroundIcon(final GlyphIcons icon) {
        mainIcon.setForegroundIcon(icon);
    }

    public void setBackgroundIcon(final GlyphIcons icon) {
        mainIcon.setBackgroundIcon(icon);
    }

    public void setForegroundIcon(final GlyphIcons icon, final Color color) {
        mainIcon.setForegroundIcon(icon, color);
    }

    public void setBackgroundIcon(final GlyphIcons icon, final Color color) {
        mainIcon.setBackgroundIcon(icon, color);
    }

    public void setLabel(final String label) {
        widgetLabel.setText(label);
    }

    public SVGIcon getIcon() {
        return mainIcon;
    }

    @Override
    public void updateDynamicContent() {

    }

    public BooleanProperty primaryActivationProperty() {
        return primaryActivationProperty;
    }

    public BooleanProperty secondaryActivationProperty() {
        return secondaryActivationProperty;
    }

    public void toggleActivation() {
        primaryActivationProperty.set(!primaryActivationProperty.getValue());
    }

    public void toggleSecondFunction() {
        secondaryActivationProperty.set(!secondaryActivationProperty.getValue());
    }

    public void setInfoText(final String identifier) {
        infoText.setIdentifier(identifier);
    }
}
