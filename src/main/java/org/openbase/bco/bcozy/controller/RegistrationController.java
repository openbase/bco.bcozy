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
import javafx.util.StringConverter;
import org.controlsfx.control.CheckComboBox;
import org.openbase.bco.bcozy.model.SessionManagerFacade;
import org.openbase.bco.bcozy.model.SessionManagerFacadeImpl;
import org.openbase.bco.bcozy.util.Groups;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.ObserverButton;

import java.util.List;

import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.VerificationFailedException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.unit.UnitConfigType.UnitConfig;
import rst.domotic.unit.user.UserConfigType;

/**
 * User registration.
 *
 * @author vdasilva
 */
public class RegistrationController {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

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
    private CheckComboBox<UnitConfig> usergroupField;
    @FXML
    private JFXCheckBox isAdmin;
    @FXML
    private JFXCheckBox isOccupant;
    @FXML
    private ObserverButton registerBtn;

    public void initialize() {
        ObservableList<UnitConfig> groups = Groups.getGroups();
        groups.addListener((ListChangeListener.Change<? extends UnitConfig> c)
                -> setGroups(groups)
        );


        registerBtn.getStyleClass().clear();
        registerBtn.getStyleClass().add("transparent-button");
        registerBtn.setApplyOnNewText(String::toUpperCase);
        usergroupField.setConverter(Groups.stringConverter(groups));
        usergroupField.prefWidthProperty().bind(root.widthProperty());
    }

    private void setGroups(ObservableList<UnitConfig> groups) {
        Platform.runLater(() -> usergroupField.getItems().setAll(groups));
        System.out.println("Groups size: " + groups.size());

        for (UnitConfig group : groups) {
            System.out.println("Group: " + group.getLabel() + "ID: " + group.getId());

        }
    }

    @FXML
    private void register() throws InterruptedException {
        resetHints();

        if (!sessionManager.isAdmin()) {
            return;
        }

        try {
            sessionManager.verifyUserName(username.getText());
        } catch (VerificationFailedException ex) {
            username.getStyleClass().add("text-field-wrong");
            return;
        }

        try {
            sessionManager.verifyPasswords(passwordField.getText(), repeatPasswordField.getText());
        } catch (VerificationFailedException ex) {
            passwordField.getStyleClass().add("password-field-wrong");
            repeatPasswordField.getStyleClass().add("password-field-wrong");
            return;
        }

        try {
            sessionManager.verifyPhoneNumber(phone.getText());
        } catch (VerificationFailedException ex) {
            phone.getStyleClass().add("text-field-wrong");
            return;
        }

        try {
            sessionManager.verifyMailAddress(mail.getText());
        } catch (VerificationFailedException ex) {
            mail.getStyleClass().add("text-field-wrong");
            return;
        }

        List<UnitConfig> groups = usergroupField.getCheckModel().getCheckedItems();

        UserConfigType.UserConfig.Builder userConfigBuilder = UserConfigType.UserConfig.newBuilder();

        UserConfigType.UserConfig user = userConfigBuilder
                .setUserName(username.getText())
                .setFirstName(firstname.getText())
                .setLastName(lastname.getText())
                .setEmail(mail.getText())
                .setMobilePhoneNumber(phone.getText())
                .setOccupant(isOccupant.isSelected())
                .build();


        try {
            sessionManager.registerUser(user,
                    passwordField.getText(),
                    isAdmin.isSelected(),
                    groups);
            resetFields();
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, logger);
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
        public void registerUser(final UserConfigType.UserConfig user, final String plainPassword, final boolean asAdmin, final List<UnitConfig> groups) throws CouldNotPerformException {
            StringConverter<UnitConfig> converter = Groups.stringConverter(groups);
            System.out.print("username = [" + user.getUserName() + "], plainPassword = [" + plainPassword + "], " +
                    "" + "asAdmin = [" + asAdmin + "], groups = [");
            groups.forEach(g -> System.out.print(converter.toString(g)));
            System.out.println("]");
        }

        @Override
        public void verifyUserName(final String username) throws VerificationFailedException {
            if (username.isEmpty()) {
                throw new VerificationFailedException("user name is empty!");
            }
        }

        @Override
        public void verifyPasswords(final String text, final String text1) throws VerificationFailedException {
            if (text.isEmpty() && text.equals(text1)) {
                throw new VerificationFailedException("repeated password does not match!");
            }
        }

        @Override
        public void verifyPhoneNumber(final String phoneNumber) throws VerificationFailedException {
        }

        @Override
        public void verifyMailAddress(final String mailAddress) throws VerificationFailedException {
        }
    }
}
