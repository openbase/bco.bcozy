package org.openbase.bco.bcozy.view.mainmenupanes;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.SVGIcon;

/**
 * Contains tabs for user login and registration.
 *
 * @author vdasilva
 */
public class UserActionPane extends PaneElement {

    LoginPane loginPane;
    RegistrationPane registrationPane;
    PseudoClass tabpaneContentHidden;
    TabPane userActionPane;
    State state;
    SVGIcon minBtnIcon;
    SVGIcon maxBtnIcon;
    Button toggleBtn;


    enum State {
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


    public UserActionPane(LoginPane lp, RegistrationPane rp) {
        super(true);
        loginPane = lp;
        loginPane.setState(LoginPane.State.LOGINACTIVE);
        registrationPane = rp;
        BorderPane root = new BorderPane();
        userActionPane = new TabPane();
        userActionPane.getSelectionModel().selectedItemProperty().addListener(
                (ov, oldTab, newTab) -> setState(State.OPEN));


        toggleBtn = new Button();
        toggleBtn.setOnAction(e -> toggleState());
        toggleBtn.getStyleClass().add("tab-button");


        HBox hbox = new HBox();
        hbox.getChildren().addAll(toggleBtn);

        // Anchor the controls
        AnchorPane anchor = new AnchorPane();
        anchor.getChildren().addAll(userActionPane, hbox);
        anchor.getStyleClass().addAll("bg-white");
        AnchorPane.setTopAnchor(hbox, 3.0);
        AnchorPane.setRightAnchor(hbox, 5.0);
        AnchorPane.setTopAnchor(userActionPane, -2.0);
        AnchorPane.setRightAnchor(userActionPane, 10.0);
        AnchorPane.setLeftAnchor(userActionPane, 10.0);
        AnchorPane.setBottomAnchor(userActionPane, 10.0);



        Tab loginTab = new Tab();
        loginTab.setGraphic(new SVGIcon(MaterialDesignIcon.LOGIN, Constants.EXTRA_SMALL_ICON, true));
        loginTab.setContent(loginPane);
        loginPane.getStyleClass().add("tab-content-area");
        loginTab.getStyleClass().addAll("tab");


        Tab registerTab = new Tab();
        registerTab.setGraphic(new SVGIcon(MaterialDesignIcon.ACCOUNT_PLUS, Constants.EXTRA_SMALL_ICON, true));
        registerTab.setContent(registrationPane);
        registrationPane.getStyleClass().addAll("tab-content-area");
        registerTab.getStyleClass().add("tab");


        userActionPane.getTabs().addAll(loginTab, registerTab);
        userActionPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);


        tabpaneContentHidden = PseudoClass.getPseudoClass("tabcontenthidden");
        root.setCenter(anchor);

        setState(State.CLOSED);

        this.getChildren().addAll(root);
    }


    @Override
    public Node getStatusIcon() {
        VBox statusIcons = new VBox(loginPane.getStatusIcon()/*, registrationPane.getStatusIcon()*/);
        statusIcons.setAlignment(Pos.CENTER);
        statusIcons.setSpacing(20.0);
        return statusIcons;
    }

    private void toggleState() {
        if (state == State.OPEN) {
            this.setState(State.CLOSED);

        } else {
            this.setState(State.OPEN);
        }

    }

    public void setState(State state) {
        this.state = state;
        userActionPane.pseudoClassStateChanged(tabpaneContentHidden, state.isActive());
        toggleBtn.setGraphic(state.getIcon());
    }
}
