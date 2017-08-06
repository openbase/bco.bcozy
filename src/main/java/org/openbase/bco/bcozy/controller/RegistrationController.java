package org.openbase.bco.bcozy.controller;

import com.jfoenix.controls.JFXCheckBox;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.controlsfx.control.CheckComboBox;
import org.openbase.bco.bcozy.model.SessionManagerFacade;
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

    private SessionManagerFacade sessionManager = new SessionManagerFacadeFake();//new SessionManagerFacadeImpl();

    @FXML
    private Pane root;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField repeatPasswordField;
    @FXML
    private TextField username;
    @FXML
    private CheckComboBox<UnitConfigType.UnitConfig> usergroupField;
    @FXML
    private JFXCheckBox isAdmin;


    public void initialize() {
        ObservableList<UnitConfigType.UnitConfig> groups = Groups.getGroups();
        groups.addListener((ListChangeListener.Change<? extends UnitConfigType.UnitConfig> c)
                -> usergroupField.getItems().setAll(groups));

        usergroupField.setConverter(Groups.stringConverter(groups));
        usergroupField.prefWidthProperty().bind(root.widthProperty());
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

    public Pane getRoot() {
        return root;
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
