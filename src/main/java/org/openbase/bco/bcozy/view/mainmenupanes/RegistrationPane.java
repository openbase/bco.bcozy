package org.openbase.bco.bcozy.view.mainmenupanes;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.control.CheckComboBox;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.ObserverButton;
import org.openbase.bco.bcozy.view.ObserverLabel;
import org.openbase.bco.bcozy.view.SVGIcon;

/**
 * User registration.
 *
 * @author vdasilva
 */
public class RegistrationPane extends PaneElement {
    private final ObserverButton registrationBtn;
    private final PasswordField passwordField;
    private final PasswordField repeatPasswordField;
    private final TextField username;
    private final VBox registrationLayout;
    private final ObserverLabel usernameLbl;
    private final ObserverLabel pwLbl;
    private final ObserverLabel repeatPwLbl;
    private final CheckComboBox usergroupField;
    private final ObserverLabel usergroupLbl;


    public RegistrationPane() {
        registrationBtn = new ObserverButton("register");
        registrationBtn.getStyleClass().clear();
        registrationBtn.getStyleClass().add("transparent-button");

        passwordField = new PasswordField();
        repeatPasswordField = new PasswordField();

        username = new TextField();
        usernameLbl = new ObserverLabel("username");
        usernameLbl.getStyleClass().clear();
        usernameLbl.getStyleClass().add("small-label");
        usernameLbl.setAlignment(Pos.BOTTOM_LEFT);

        usergroupLbl = new ObserverLabel("usergroups");
        usergroupLbl.getStyleClass().clear();
        usergroupLbl.getStyleClass().add("small-label");
        usergroupLbl.setAlignment(Pos.BOTTOM_LEFT);

        ObservableList<String> options = FXCollections.observableArrayList("Group1", "Group2", "Group3", "Group4");

        usergroupField = new CheckComboBox(options);
        usergroupField.prefWidthProperty().bind(this.widthProperty());

        pwLbl = new ObserverLabel("password");
        pwLbl.getStyleClass().clear();
        pwLbl.getStyleClass().add("small-label");
        pwLbl.setAlignment(Pos.BOTTOM_LEFT);

        repeatPwLbl = new ObserverLabel("repeatPassword");
        repeatPwLbl.getStyleClass().clear();
        repeatPwLbl.getStyleClass().add("small-label");
        repeatPwLbl.setAlignment(Pos.BOTTOM_LEFT);


        final HBox rightAlignRegistrationButton = new HBox(registrationBtn);
        rightAlignRegistrationButton.setAlignment(Pos.CENTER_RIGHT);


        registrationLayout = new VBox(Constants.INSETS);
        registrationLayout.getChildren().addAll(usernameLbl, username, usergroupLbl, usergroupField, pwLbl, passwordField, repeatPwLbl, repeatPasswordField, rightAlignRegistrationButton);

        this.getChildren().addAll(registrationLayout);

    }

    @Override
    public Node getStatusIcon() {

        return new SVGIcon(FontAwesomeIcon.USER_PLUS, Constants.SMALL_ICON, true);
    }
}
