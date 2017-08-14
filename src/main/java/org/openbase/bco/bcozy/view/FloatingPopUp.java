package org.openbase.bco.bcozy.view;

import de.jensd.fx.glyphs.GlyphIcons;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author vdasilva
 */
public class FloatingPopUp extends VBox {

    private FloatingButton popUpParent;

    private final VBox viewSwitcherPopUp;

    private boolean visible = false;

    Map<GlyphIcons, EventHandler<ActionEvent>> eventHandler = new HashMap<>();

    GlyphIcons parent;


    public FloatingPopUp(Pos position) {
        super(Constants.INSETS);

        this.setMaxSize(Constants.MIDDLE_ICON, Double.MAX_VALUE);
        this.setAlignment(position);
        viewSwitcherPopUp = new VBox(Constants.INSETS);
        viewSwitcherPopUp.setAlignment(Pos.CENTER);

        StackPane.setAlignment(this, position);
        this.translateYProperty().set(-Constants.INSETS);
    }

    @Deprecated
    public FloatingPopUp(GlyphIcons parent, GlyphIcons topChild, GlyphIcons bottomChild, Pos position) {
        this(position);

        addParentElement(parent, (Runnable) null);
        addElement(topChild, (Runnable)null);
        addElement(bottomChild, (Runnable)null);

    }

    public void addParentElement(GlyphIcons icon, Runnable handler) {
        this.addParentElement(icon, (event) -> handler.run());
    }

    public void addParentElement(GlyphIcons icon, EventHandler<ActionEvent> value) {
        parent = icon;
        eventHandler.put(icon, value);

        showParent();
        addChildren();

        setViewSwitchingButtonsVisible(visible);
    }

    public void addElement(GlyphIcons icon, Runnable handler) {
        this.addElement(icon, (event) -> handler.run());
    }


    public void addElement(GlyphIcons icon, EventHandler<ActionEvent> value) {
        eventHandler.put(icon, value);

        addChildren();
    }

    public void clickOnChild(GlyphIcons clicked) {
        parent = clicked;

        showParent();
        addChildren();
        switchingButtonsVisible();
    }

    private void addChildren() {
        viewSwitcherPopUp.getChildren().clear();
        for (Map.Entry<GlyphIcons, EventHandler<ActionEvent>> glyphIconsEventHandlerEntry : eventHandler.entrySet()) {
            GlyphIcons icon = glyphIconsEventHandlerEntry.getKey();
            EventHandler<ActionEvent> handler = glyphIconsEventHandlerEntry.getValue();

            if (icon != parent) {
                FloatingButton element = new FloatingButton(new SVGIcon(icon, Constants.SMALL_ICON, true));
                viewSwitcherPopUp.getChildren().addAll(element);
                element.setOnAction(event -> {
                    clickOnChild(icon);
                    if (Objects.nonNull(handler))
                        handler.handle(event);
                });
            }
        }
    }

    private void showParent() {
        popUpParent = new FloatingButton(new SVGIcon(parent, Constants.MIDDLE_ICON, true));
        popUpParent.setOnAction(event -> this.switchingButtonsVisible());
    }


    public void switchingButtonsVisible() {
        visible = !visible;
        setViewSwitchingButtonsVisible(visible);
    }

    private void setViewSwitchingButtonsVisible(boolean visible) {
        if (visible) {
            this.getChildren().clear();
//            this.getChildren().addAll(viewSwitcherPopUp, popUpParent);
            this.getChildren().addAll(popUpParent, viewSwitcherPopUp);
        } else {
            this.getChildren().clear();
            this.getChildren().addAll(popUpParent);
        }
    }
}
