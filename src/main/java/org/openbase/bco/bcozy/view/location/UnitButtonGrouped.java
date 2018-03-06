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
package org.openbase.bco.bcozy.view.location;

import com.google.protobuf.GeneratedMessage;
import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.jul.visual.javafx.JFXConstants;
import org.openbase.jul.visual.javafx.geometry.svg.SVGGlyphIcon;
import org.openbase.bco.bcozy.view.generic.WidgetPane.DisplayMode;
import org.openbase.bco.bcozy.view.pane.unit.AbstractUnitPane;
import org.openbase.bco.bcozy.view.pane.unit.UnitPaneFactoryImpl;
import org.openbase.bco.dal.lib.layer.unit.UnitRemote;
import org.openbase.jul.exception.CouldNotPerformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Button that groups several UnitButtons that have the same position.
 * It displays the correct symbol and a small number that indicates how many buttons have been grouped.
 * If it is clicked, it displays AbstractUnitPanes in a clipped form for every unit of this button. So every
 * button can be controlled from within the location plan.
 */
public class UnitButtonGrouped extends Pane {

    protected final Logger LOGGER = LoggerFactory.getLogger(UnitButtonGrouped.class);

    private final FlowPane groupingPane;
    private final StackPane stackPane;
    private final Text unitCount;
    private final GridPane iconPane;
    private String locationId;
    private boolean expanded;
    private Rectangle clipRectangle1;

    /**
     * Constructor for the grouped button.
     */
    public UnitButtonGrouped() {
        locationId = new String();
        expanded = false;
        groupingPane = new FlowPane();
        groupingPane.setPrefWrapLength(2 * (JFXConstants.ICON_SIZE_SMALL + (2 * Constants.INSETS)));
        iconPane = new GridPane();
        stackPane = new StackPane();
        unitCount = new Text("0");
        unitCount.setTextAlignment(TextAlignment.LEFT);
        unitCount.setStyle("-fx-font: 10pt Tahoma; -fx-fill: black; -fx-stroke: white; -fx-stroke-width: 0.2px;");
        unitCount.textProperty().bind(Bindings.size(groupingPane.getChildren()).asString());

        iconPane.getChildren().add(unitCount);
        stackPane.getChildren().add(iconPane);
        stackPane.getChildren().add(groupingPane);
        this.getChildren().add(stackPane);

        clipRectangle1 = new Rectangle(JFXConstants.ICON_SIZE_SMALL, JFXConstants.ICON_SIZE_SMALL);
        this.setClip(clipRectangle1);

        groupingPane.layoutBoundsProperty().addListener((ov, oldValue, newValue) -> {
            clipRectangle1.setWidth(newValue.getWidth());
            clipRectangle1.setHeight(newValue.getHeight());
        });

        final EventHandler<MouseEvent> mouseEventHandler = (event) -> {
            event.consume();
            if (!expanded) {
                expand();
                expanded = true;
            }
        };

        final EventHandler<MouseEvent> mouseExitedHandler = (event) -> {
            event.consume();
            if (expanded) {
                shrink();
                expanded = false;
            }
        };
        stackPane.setOnMouseClicked(mouseEventHandler);
        stackPane.setOnMouseExited(mouseExitedHandler);

        this.getStyleClass().clear();
        this.getStyleClass().addAll("units-button");
    }

    /**
     * Adds a UnitRemote to the list of this button's units. If it is the first unit after construction,
     * the correct icon is added to this button.
     *
     * @param unit UnitRemote that is supposed to be controlled by this grouped button.
     * @throws InterruptedException
     * @throws CouldNotPerformException
     */
    public void addUnit(final UnitRemote<? extends GeneratedMessage> unit) throws InterruptedException, CouldNotPerformException {

        try {
            AbstractUnitPane content;
            content = UnitPaneFactoryImpl.getInstance().newInitializedInstance(unit.getConfig());
            content.setDisplayMode(DisplayMode.ICON_ONLY);

            if (groupingPane.getChildren().isEmpty()) {
                SVGGlyphIcon icon = content.getIconSymbol();
                iconPane.getChildren().add(icon);
                this.locationId = unit.getConfig().getPlacementConfig().getLocationId();
            }
            content.setVisible(false);
            content.getStyleClass().add("units-button");
            content.setStyle("-fx-background-color: rgb(64.0, 64.0, 64.0)");
            this.groupingPane.getChildren().add(content);
        } catch (CouldNotPerformException ex) {
            throw new CouldNotPerformException("Could not create grouped unit button for config " + this, ex);
        }
    }

    private void expand() {
        iconPane.setVisible(false);
        this.groupingPane.getChildren().forEach((node)
            -> {
            node.setVisible(true);
        });
        clipRectangle1.setWidth(groupingPane.getWidth());
        clipRectangle1.setHeight(groupingPane.getHeight());
    }

    private void shrink() {
        this.groupingPane.getChildren().forEach((node)
            -> {
            node.setVisible(false);
        });
        iconPane.setVisible(true);
        clipRectangle1.setWidth(JFXConstants.ICON_SIZE_SMALL);
        clipRectangle1.setHeight(JFXConstants.ICON_SIZE_SMALL);
    }

    /**
     * Convenience method to get the location the underlying unit belongs to.
     *
     * @return LocationId of the unit's location
     */
    public String getLocationId() {
        return locationId;
    }
}
