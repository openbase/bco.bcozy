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

    private final Map<GlyphIcons, EventHandler<ActionEvent>> eventHandler = new HashMap<>();

    private GlyphIcons parent;

    public FloatingPopUp(final Pos position) {
        super(Constants.INSETS);

        this.setMaxSize(Constants.MIDDLE_ICON, Double.MAX_VALUE);
        this.setAlignment(position);
        viewSwitcherPopUp = new VBox(Constants.INSETS);
        viewSwitcherPopUp.setAlignment(Pos.CENTER);

        StackPane.setAlignment(this, position);
        this.translateYProperty().set(-Constants.INSETS);
    }
    
    public FloatingPopUp(final Pos position, final GlyphIcons... icons) {
        this(position);
    }

    public final void addParentElement(final GlyphIcons icon, final Runnable handler) {
        this.addParentElement(icon, (event) -> handler.run());
    }

    public final void addParentElement(final GlyphIcons icon, final EventHandler<ActionEvent> value) {
        parent = icon;
        eventHandler.put(icon, value);

        showParent();
        addChildren();

        setViewSwitchingButtonsVisible(visible);
    }

    public final void addElement(final GlyphIcons icon, final Runnable handler) {
        this.addElement(icon, (event) -> handler.run());
    }

    public final void addElement(final GlyphIcons icon, final EventHandler<ActionEvent> value) {
        eventHandler.put(icon, value);

        addChildren();
    }

    public void clickOnChild(final GlyphIcons clicked) {
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
                    if (Objects.nonNull(handler)) {
                        handler.handle(event);
                    }
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

    private void setViewSwitchingButtonsVisible(final boolean visible) {
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
