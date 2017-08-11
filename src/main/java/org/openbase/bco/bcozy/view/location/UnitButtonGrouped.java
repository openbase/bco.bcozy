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
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.SVGIcon;
import org.openbase.bco.bcozy.view.generic.WidgetPane.DisplayMode;
import org.openbase.bco.bcozy.view.pane.unit.AbstractUnitPane;
import org.openbase.bco.bcozy.view.pane.unit.UnitPaneFactoryImpl;
import org.openbase.bco.dal.lib.layer.unit.UnitRemote;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.NotAvailableException;

/**
 *
 */
public class UnitButtonGrouped extends Pane {

    // private ObservableList<AbstractUnitPane> unitButtons;
    private SimpleListProperty<AbstractUnitPane> unitButtons;
    private final GridPane groupingPane;
    private final StackPane stackPane;
    private final Label unitCount;
    private final GridPane iconPane;
    boolean expanded;

    public UnitButtonGrouped() {
        
        expanded = false;
        groupingPane = new GridPane();
        iconPane = new GridPane();
        stackPane = new StackPane();
        unitCount = new Label("0");
        unitCount.setTextAlignment(TextAlignment.LEFT);
        unitCount.setFont(new Font(12));
        unitButtons = new SimpleListProperty(FXCollections.<AbstractUnitPane>observableArrayList());
        unitCount.textProperty().bind(unitButtons.sizeProperty().asString());
        
        iconPane.getChildren().add(unitCount);
       
        stackPane.getChildren().add(iconPane);
        stackPane.getChildren().add(groupingPane);
        this.getChildren().add(stackPane);
        
        Rectangle clipRectangle1 = new Rectangle(100,100);
        this.setClip(clipRectangle1);
        groupingPane.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
                clipRectangle1.setWidth(newValue.getWidth());
                clipRectangle1.setHeight(newValue.getHeight());
            });
        this.setBackground(new Background(new BackgroundFill(Color.GREY, CornerRadii.EMPTY, Insets.EMPTY)));
        
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
                System.out.println("out focus");
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
            if (unitButtons.isEmpty()) {
                SVGIcon icon = content.getIcon();
                //SVGIcon icon =new SVGIcon(MaterialDesignIcon.VECTOR_CIRCLE, Constants.SMALL_ICON, false);            
                iconPane.getChildren().add(icon);
            }
            unitButtons.add(content);
        } catch (CouldNotPerformException | InterruptedException ex) {
            Logger.getLogger(UnitButtonGrouped.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void expand() {
        stackPane.getChildren().remove(iconPane);
        unitButtons.forEach((button)
            -> {
            groupingPane.getChildren().add(button);
        });
       // this.stackPane.getChildren().remove(unitCount);
      //  this.getChildren().add(stackPane);
    }

    public void shrink() {
        this.groupingPane.getChildren().clear();
        stackPane.getChildren().add(iconPane);
    }
    
    //TODO refactor
      public UnitRemote<? extends GeneratedMessage> getUnitRemote() {
        try {
            return this.unitButtons.get(0).getUnitRemote();
        } catch (NotAvailableException ex) {
            Logger.getLogger(UnitButtonGrouped.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /*  count.setValue(count.getValue() + 1);

    final ContextMenu cm = new ContextMenu();
            MenuItem cmItem1 = new MenuItem("Toggle power state");
            try {
                UnitPaneFactoryImpl.getInstance().newInstance(UnitPaneFactoryImpl.loadUnitPaneClass(config.getType()));
                CustomMenuItem cmItem2 = new CustomMenuItem();
                cm.getItems().add(cmItem2);
            } catch (CouldNotPerformException ex) {
                Logger.getLogger(UnitButton.class.getName()).log(Level.SEVERE, null, ex);
            }
            cmItem1.setOnAction((ActionEvent e) -> {
                //
            });
            cm.getItems().add(cmItem1);
            this.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
                if (e.getButton() == MouseButton.SECONDARY) {
                    cm.show(this.getParent(), e.getScreenX(), e.getScreenY());
                }
            });
 
     */
}
