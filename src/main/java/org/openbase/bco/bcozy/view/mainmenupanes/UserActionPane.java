package org.openbase.bco.bcozy.view.mainmenupanes;

import com.sun.tools.internal.jxc.ap.Const;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.SVGIcon;

/**
 * Contains tabs for user login and registration.
 *
 * @author vdasilva
 */
public class UserActionPane extends PaneElement {

    private static final String REGISTRATION_HEADLINE = "Registrierung";
    private static final String LOGIN_HEADLINE = "Login";
    LoginPane loginPane;
    RegistrationPane registrationPane;


    public UserActionPane(LoginPane lp, RegistrationPane rp) {
        loginPane = lp;
        loginPane.setState(LoginPane.State.LOGINACTIVE);
        registrationPane = rp;

        TabPane userActionPane = new TabPane();
        Tab loginTab = new Tab();
        loginTab.setGraphic(new SVGIcon(MaterialDesignIcon.LOGIN, Constants.EXTRA_SMALL_ICON, true));
        loginTab.setContent(loginPane);


        Tab registerTab = new Tab();
        registerTab.setGraphic(new SVGIcon(MaterialDesignIcon.ACCOUNT_PLUS, Constants.EXTRA_SMALL_ICON, true));
        registerTab.setContent(registrationPane);

        userActionPane.getTabs().addAll(loginTab, registerTab);
        userActionPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        this.getChildren().addAll(userActionPane);
    }

    @Override
    public Node getStatusIcon() {
        VBox statusIcons = new VBox(loginPane.getStatusIcon()/*, registrationPane.getStatusIcon()*/);
        statusIcons.setAlignment(Pos.CENTER);
        statusIcons.setSpacing(20.0);
        return statusIcons;
    }
}
