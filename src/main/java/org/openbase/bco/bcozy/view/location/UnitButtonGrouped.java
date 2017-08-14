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
import de.jensd.fx.glyphs.GlyphIcons;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.apache.bcel.classfile.Constant;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.SVGIcon;
import org.openbase.bco.bcozy.view.generic.WidgetPane.DisplayMode;
import org.openbase.bco.bcozy.view.pane.unit.AbstractUnitPane;
import org.openbase.bco.bcozy.view.pane.unit.UnitPaneFactoryImpl;
import org.openbase.bco.dal.lib.layer.unit.UnitRemote;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.NotAvailableException;
import rst.domotic.unit.UnitTemplateType;

/**
 *
 */
public class UnitButtonGrouped extends Pane {

    private final FlowPane groupingPane;
    private final StackPane stackPane;
    private final Text unitCount;
    private final GridPane iconPane;
    private String locationId;
    boolean expanded;

    Rectangle clipRectangle1;
    Rectangle clipRectangle2;
    
    public UnitButtonGrouped() {
        locationId = new String();
        expanded = false;
        groupingPane = new FlowPane();
        groupingPane.setPrefWrapLength(70);
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

        
        clipRectangle1 = new Rectangle(Constants.SMALL_ICON,Constants.SMALL_ICON);
        this.setClip(clipRectangle1); 
        
        final EventHandler<MouseEvent> mouseEventHandler = (MouseEvent event) -> {
            event.consume();
            if (!expanded) {
                expand();
                expanded = true;
            } 
        };
        
        final EventHandler<MouseEvent> mouseExitedHandler = (MouseEvent event) -> {
            event.consume();
            if (expanded) {
                shrink();
                expanded = false;
            } 
        };
        stackPane.setOnMouseClicked(mouseEventHandler);
        stackPane.setOnMouseExited(mouseExitedHandler);
        
    }

    public void addUnit(UnitRemote<? extends GeneratedMessage> unit) {

        try {
            AbstractUnitPane content;
            content = UnitPaneFactoryImpl.getInstance().newInitializedInstance(unit.getConfig());
            content.setDisplayMode(DisplayMode.ICON_ONLY);
            
            if (groupingPane.getChildren().isEmpty()) {
                SVGIcon icon = content.getIconSymbol();
                iconPane.getChildren().add(icon);
                this.locationId = unit.getConfig().getPlacementConfig().getLocationId();
            }
            content.setVisible(false);
            content.setBackground(new Background(new BackgroundFill(new Color(0.25, 0.25, 0.25, 1.0), CornerRadii.EMPTY, Insets.EMPTY)));
            this.groupingPane.getChildren().add(content);
            
        } catch (CouldNotPerformException | InterruptedException ex) {
            Logger.getLogger(UnitButtonGrouped.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void expand() {
        iconPane.setVisible(false);
        this.groupingPane.getChildren().forEach((node)
        -> {node.setVisible(true);});
        clipRectangle1.setWidth(groupingPane.getWidth());
        clipRectangle1.setHeight(groupingPane.getHeight());
    }

    public void shrink() {
        this.groupingPane.getChildren().forEach((node)
        -> {node.setVisible(false);});
        iconPane.setVisible(true);
        clipRectangle1.setWidth(Constants.SMALL_ICON);
        clipRectangle1.setHeight(Constants.SMALL_ICON);
    }
    
    public String getLocationId() {
        return this.locationId;
    }
}
