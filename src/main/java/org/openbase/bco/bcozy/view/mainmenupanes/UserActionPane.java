package org.openbase.bco.bcozy.view.mainmenupanes;

import de.jensd.fx.glyphs.GlyphIcons;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.*;
import org.openbase.bco.bcozy.controller.RegistrationController;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.SVGIcon;

/**
 * Contains tabs for user login and registration.
 *
 * @author vdasilva
 */
public class UserActionPane extends PaneElement {

    private final LoginPane loginPane;
    //private final RegistrationController registrationController;
    private final PseudoClass tabpaneContentHidden;
    private final TabPane userActionPane;

    private State state;


    public UserActionPane(LoginPane lp, RegistrationController rp) {
        super(true);
        loginPane = lp;
        loginPane.setState(LoginPane.State.LOGINACTIVE);
        //registrationController = rp;
        BorderPane root = new BorderPane();
        userActionPane = new TabPane();
        TabPaneSelectionModelImpl selectionModel = new TabPaneSelectionModelImpl(userActionPane);
        userActionPane.setSelectionModel(selectionModel);

        selectionModel.selectedItemProperty().addListener((ov, oldTab, newTab) -> setState(State.OPEN));
        selectionModel.setClickOnSelectedTabListener((tab) -> toggleState(tab));

        HBox hbox = new HBox();

        // Anchor the controls
        AnchorPane anchor = new AnchorPane();

        anchor.getChildren().addAll(userActionPane, hbox);
        anchor.getStyleClass().addAll("bg-white");
        AnchorPane.setTopAnchor(hbox, 3.0);
        AnchorPane.setRightAnchor(hbox, 5.0);
        AnchorPane.setTopAnchor(userActionPane, 1.0);
        AnchorPane.setRightAnchor(userActionPane, 10.0);
        AnchorPane.setLeftAnchor(userActionPane, 10.0);
        AnchorPane.setBottomAnchor(userActionPane, 10.0);


        Tab loginTab = createTab(this.loginPane, MaterialDesignIcon.LOGIN);
        loginTab.getContent().maxHeight(100);

        //Tab registerTab = createTab(this.registrationController, MaterialDesignIcon.ACCOUNT_PLUS);


        userActionPane.getTabs().addAll(loginTab/*, registerTab*/);
        userActionPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);


        tabpaneContentHidden = PseudoClass.getPseudoClass("tabcontenthidden");
        root.setCenter(anchor);

        this.setState(State.CLOSED);
        this.getChildren().addAll(root);

    }

    /**
     * Creates a new Tab.
     *
     * @param pane the tab-content
     * @param icon the tab-icon
     * @return the new tab
     */
    private Tab createTab(Pane pane, GlyphIcons icon) {
        Tab tab = new Tab();
        SVGIcon svgIcon = new SVGIcon(icon, Constants.EXTRA_SMALL_ICON, true);
        tab.setGraphic(svgIcon);
        tab.setContent(pane);
        pane.getStyleClass().add("tab-content-area");
        tab.getStyleClass().add("tab");
        return tab;
    }


    @Override
    public Node getStatusIcon() {
        VBox statusIcons = new VBox(loginPane.getStatusIcon()/*, registrationPane.getStatusIcon()*/);
        statusIcons.setAlignment(Pos.CENTER);
        statusIcons.setSpacing(20.0);
        return statusIcons;
    }

    public void toggleState(Tab clickedTab) {

        if (clickedTab.isDisabled()) {
            this.setState(State.CLOSED);
            return;
        }

        if (state == State.OPEN) {
            this.setState(State.CLOSED);
        } else {
            this.setState(State.OPEN);
        }
    }

    public void setState(State state) {
        this.state = state;
        userActionPane.pseudoClassStateChanged(tabpaneContentHidden, state.isActive());

        this.requestLayout();
    }

    public enum State {
        OPEN(false, new SVGIcon(FontAwesomeIcon.CARET_UP, Constants.EXTRA_SMALL_ICON, true)),
        CLOSED(true, new SVGIcon(FontAwesomeIcon.CARET_DOWN, Constants.EXTRA_SMALL_ICON, true));

        private final boolean active;
        private final SVGIcon icon;

        State(boolean active, SVGIcon icon) {
            this.active = active;
            this.icon = icon;
        }

        public boolean isActive() {
            return active;
        }

        public SVGIcon getIcon() {
            return icon;
        }
    }
}
