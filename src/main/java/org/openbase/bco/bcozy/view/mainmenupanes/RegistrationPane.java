package org.openbase.bco.bcozy.view.mainmenupanes;

import com.jfoenix.controls.JFXCheckBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.controlsfx.control.CheckComboBox;
import org.openbase.bco.authentication.lib.SessionManager;
import org.openbase.bco.bcozy.util.Groups;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.ObserverButton;
import org.openbase.bco.bcozy.view.ObserverLabel;
import org.openbase.jul.exception.CouldNotPerformException;
import rst.domotic.unit.UnitConfigType;

import java.util.List;

/**
 * User registration.
 *
 * @author vdasilva
 */
public class RegistrationPane extends VBox {
    private SessionManagerFacade sessionManager = new SessionManagerFacadeFake();

    private final PasswordField passwordField;
    private final PasswordField repeatPasswordField;
    private final TextField username;
    private final CheckComboBox<UnitConfigType.UnitConfig> usergroupField;
    private final JFXCheckBox isAdmin;

    public RegistrationPane() {
        super(Constants.INSETS);

        ObserverButton registrationBtn = new ObserverButton("register");
        registrationBtn.getStyleClass().clear();
        registrationBtn.getStyleClass().add("transparent-button");
        registrationBtn.setOnAction(e -> register());

        passwordField = new PasswordField();
        repeatPasswordField = new PasswordField();

        username = new TextField();
        ObserverLabel usernameLbl = new ObserverLabel("username");
        usernameLbl.getStyleClass().clear();
        usernameLbl.getStyleClass().add("small-label");
        usernameLbl.setAlignment(Pos.BOTTOM_LEFT);

        ObserverLabel usergroupLbl = new ObserverLabel("usergroups");
        usergroupLbl.getStyleClass().clear();
        usergroupLbl.getStyleClass().add("small-label");
        usergroupLbl.setAlignment(Pos.BOTTOM_LEFT);

        ObservableList<String> options = FXCollections.observableArrayList("Group1", "Group2", "Group3", "Group4");

        ObservableList<UnitConfigType.UnitConfig> groups = Groups.getGroups();

        usergroupField = new CheckComboBox<>(groups);
        usergroupField.setConverter(Groups.stringConverter(groups));
        usergroupField.prefWidthProperty().bind(this.widthProperty());

        ObserverLabel pwLbl = new ObserverLabel("password");
        pwLbl.getStyleClass().clear();
        pwLbl.getStyleClass().add("small-label");
        pwLbl.setAlignment(Pos.BOTTOM_LEFT);

        ObserverLabel repeatPwLbl = new ObserverLabel("repeatPassword");
        repeatPwLbl.getStyleClass().clear();
        repeatPwLbl.getStyleClass().add("small-label");
        repeatPwLbl.setAlignment(Pos.BOTTOM_LEFT);

        ObserverLabel isAdminLabel = new ObserverLabel("admin");
        isAdmin = new JFXCheckBox();

        final HBox isAdminCheckbox = new HBox(isAdmin, isAdminLabel);
        final BorderPane bottomLine = new BorderPane();
        bottomLine.setLeft(isAdminCheckbox);
        bottomLine.setRight(registrationBtn);


        //VBox registrationLayout = new VBox(Constants.INSETS);
        this.getStyleClass().addAll("padding-large");
        this.getChildren().addAll(usernameLbl, username, usergroupLbl, usergroupField, pwLbl,
                passwordField, repeatPwLbl, repeatPasswordField, bottomLine);

        //this.getChildren().addAll(registrationLayout);

    }

    private void register() {
        resetHints();

        if (!sessionManager.isAdmin()) {
            return;
        }

        if (!sessionManager.userNameAvailable(username.getText())) {
            username.getStyleClass().add("text-field-wrong");
            return;
        }

        if (!sessionManager.passwordsValid(passwordField.getText(), repeatPasswordField.getText())) {
            passwordField.getStyleClass().add("password-field-wrong");
            repeatPasswordField.getStyleClass().add("password-field-wrong");
            return;
        }

        List<UnitConfigType.UnitConfig> groups = usergroupField.getCheckModel().getCheckedItems();

        boolean registered = sessionManager.registerUser(
                username.getText(), passwordField.getText(), isAdmin.isSelected(), groups);
        if (registered) {
            resetFields();
        }
    }

    private void resetHints() {
        username.getStyleClass().removeAll("text-field-wrong");
        passwordField.getStyleClass().removeAll("password-field-wrong");
        repeatPasswordField.getStyleClass().removeAll("password-field-wrong");
    }

    private void resetFields() {
        username.setText("");
        passwordField.setText("");
        repeatPasswordField.setText("");
        isAdmin.setSelected(false);
        usergroupField.getCheckModel().clearChecks();
    }

    private interface SessionManagerFacade {

        /**
         * Checks, if the current user is an admin.
         *
         * @return true, if the current user is an admin, false otherwise
         */
        boolean isAdmin();

        boolean registerUser(String username, String plainPassword, boolean asAdmin,
                             List<UnitConfigType.UnitConfig> groups);

        boolean userNameAvailable(String username);

        boolean passwordsValid(String text, String text1);
    }

    class sessionManagerFacadeImpl implements SessionManagerFacade {

        @Override
        public boolean isAdmin() {
            return SessionManager.getInstance().isAdmin();
        }

        @Override
        public boolean registerUser(String username, String plainPassword, boolean asAdmin, List<UnitConfigType
                        .UnitConfig> groups) {
            try {
                SessionManager.getInstance().registerUser(username, plainPassword, isAdmin());
            } catch (CouldNotPerformException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public boolean userNameAvailable(String username) {
            //TODO
            return true;
        }

        @Override
        public boolean passwordsValid(String text, String text1) {
            //TODO
            return true;
        }
    }

    class SessionManagerFacadeFake implements SessionManagerFacade {

        /**
         * Fake.
         *
         * @return
         */
        @Override
        public boolean isAdmin() {
            return true;
        }

        /**
         * @param username
         * @param plainPassword
         * @param asAdmin
         * @return
         */
        @Override
        public boolean registerUser(String username, String plainPassword, boolean asAdmin,
                                    List<UnitConfigType.UnitConfig> groups) {

            StringConverter<UnitConfigType.UnitConfig> converter = Groups.stringConverter(groups);

            System.out.print("username = [" + username + "], plainPassword = [" + plainPassword + "], asAdmin = ["
                    + asAdmin + "], groups = [");
            groups.forEach(g -> System.out.print(converter.toString(g)));
            System.out.println("]");


            return true;
        }

        /**
         * Fake.
         *
         * @return
         */
        @Override
        public boolean userNameAvailable(String username) {
            return !username.isEmpty();
        }

        @Override
        public boolean passwordsValid(String text, String text1) {
            return !text.isEmpty() && text.equals(text1);
        }
    }
}
