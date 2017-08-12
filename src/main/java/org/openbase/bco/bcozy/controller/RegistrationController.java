package org.openbase.bco.bcozy.controller;

import com.jfoenix.controls.JFXCheckBox;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.controlsfx.control.CheckComboBox;
import org.openbase.bco.authentication.lib.SessionManager;
import org.openbase.bco.bcozy.model.SessionManagerFacade;
import org.openbase.bco.bcozy.model.SessionManagerFacadeImpl;
import org.openbase.bco.bcozy.util.Groups;
import org.openbase.bco.bcozy.view.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.unit.UnitConfigType;

import java.util.List;

/**
 * User registration.
 *
 * @author vdasilva
 */
public class RegistrationController {

    private SessionManagerFacade sessionManager = new SessionManagerFacadeImpl(); //new SessionManagerFacadeFake();

    @FXML
    private Pane root;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField repeatPasswordField;
    @FXML
    private TextField username;
    @FXML
    private TextField firstname;
    @FXML
    private TextField lastname;
    @FXML
    private TextField mail;
    @FXML
    private TextField phone;
    @FXML
    private CheckComboBox<UnitConfigType.UnitConfig> usergroupField;
    @FXML
    private JFXCheckBox isAdmin;
    @FXML
    private Button registerBtn;

    public void initialize() {
        ObservableList<UnitConfigType.UnitConfig> groups = Groups.getGroups();
        groups.addListener((ListChangeListener.Change<? extends UnitConfigType.UnitConfig> c)
                -> setGroups(groups)
        );

        registerBtn.getStyleClass().clear();
        registerBtn.getStyleClass().add("transparent-button");
        registerBtn.setText(registerBtn.getText().toUpperCase());
        usergroupField.setConverter(Groups.stringConverter(groups));
        usergroupField.prefWidthProperty().bind(root.widthProperty());
    }

    private void setGroups(ObservableList<UnitConfigType.UnitConfig> groups) {
        Platform.runLater(() -> usergroupField.getItems().setAll(groups));
    }

    @FXML
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

        if (!sessionManager.phoneIsValid(phone.getText())) {
            phone.getStyleClass().add("text-field-wrong");

            return;
        }

        if (!sessionManager.mailIsValid(mail.getText())) {
            mail.getStyleClass().add("text-field-wrong");

            return;
        }

        List<UnitConfigType.UnitConfig> groups = usergroupField.getCheckModel().getCheckedItems();

        boolean registered = sessionManager.registerUser(
                new SessionManagerFacade.NewUser(username.getText(),
                        firstname.getText(), lastname.getText(), mail.getText(), phone.getText()), //TODO: Fields
                passwordField.getText(), isAdmin.isSelected(), groups);
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
        firstname.setText("");
        lastname.setText("");
        mail.setText("");
        phone.setText("");
        passwordField.setText("");
        repeatPasswordField.setText("");
        isAdmin.setSelected(false);
        usergroupField.getCheckModel().clearChecks();
    }

    public Pane getRoot() {
        return root;
    }

    class SessionManagerFacadeFake implements SessionManagerFacade {

        @Override
        public boolean isAdmin() {
            return true;
        }

        @Override
        public boolean registerUser(NewUser user, String plainPassword, boolean asAdmin,
                                    List<UnitConfigType.UnitConfig> groups) {

            StringConverter<UnitConfigType.UnitConfig> converter = Groups.stringConverter(groups);

            System.out.print("username = [" + user.getUsername() + "], plainPassword = [" + plainPassword + "], " +
                    "asAdmin = ["
                    + asAdmin + "], groups = [");
            groups.forEach(g -> System.out.print(converter.toString(g)));
            System.out.println("]");


            return true;
        }

        @Override
        public boolean userNameAvailable(String username) {
            return !username.isEmpty();
        }

        @Override
        public boolean passwordsValid(String text, String text1) {
            return !text.isEmpty() && text.equals(text1);
        }

        @Override
        public boolean phoneIsValid(String phoneNumber) {
            return true;
        }

        @Override
        public boolean mailIsValid(String mailAdress) {
            return true;
        }
    }
}
