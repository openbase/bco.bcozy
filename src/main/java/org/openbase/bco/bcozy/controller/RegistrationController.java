package org.openbase.bco.bcozy.controller;

import com.jfoenix.controls.JFXCheckBox;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.controlsfx.control.CheckComboBox;
import org.openbase.bco.bcozy.model.SessionManagerFacade;
import org.openbase.bco.bcozy.model.SessionManagerFacadeImpl;
import org.openbase.bco.bcozy.util.Groups;
import org.openbase.bco.bcozy.view.InfoPane;
import org.openbase.bco.bcozy.view.ObserverButton;
import org.openbase.bco.bcozy.view.ObserverLabel;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.VerificationFailedException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.unit.UnitConfigType.UnitConfig;
import rst.domotic.unit.user.UserConfigType;

import java.util.List;

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
    @FXML
    private ObserverLabel usernameEmptyLabel;
    @FXML
    private ObserverLabel firstnameEmptyLabel;
    @FXML
    private ObserverLabel lastnameEmptyLabel;
    @FXML
    private ObserverLabel mailEmptyLabel;


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

        usernameEmptyLabel.getStyleClass().remove("label");
        firstnameEmptyLabel.getStyleClass().remove("label");
        lastnameEmptyLabel.getStyleClass().remove("label");
        mailEmptyLabel.getStyleClass().remove("label");
    }

    private void setGroups(ObservableList<UnitConfig> groups) {
        Platform.runLater(() -> usergroupField.getItems().setAll(groups));
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
            usernameEmptyLabel.setVisible(true);
            return;
        }

        if (firstname.getText().equals("")) {
            firstname.getStyleClass().add("text-field-wrong");
            firstnameEmptyLabel.setVisible(true);
            return;
        }

        if (lastname.getText().equals("")) {
            lastname.getStyleClass().add("text-field-wrong");
            lastnameEmptyLabel.setVisible(true);
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
            mailEmptyLabel.setVisible(true);
            return;
        }

        List<UnitConfig> groups = usergroupField.getCheckModel().getCheckedItems();

        UserConfigType.UserConfig user = UserConfigType.UserConfig.newBuilder()
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
            showSuccessMessage();
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, logger);
            showErrorMessage();
        }
    }

    private void resetHints() {
        username.getStyleClass().removeAll("text-field-wrong");
        firstname.getStyleClass().removeAll("text-field-wrong");
        lastname.getStyleClass().removeAll("text-field-wrong");
        passwordField.getStyleClass().removeAll("password-field-wrong");
        repeatPasswordField.getStyleClass().removeAll("password-field-wrong");
        usernameEmptyLabel.setVisible(false);
        firstnameEmptyLabel.setVisible(false);
        lastnameEmptyLabel.setVisible(false);
        mailEmptyLabel.setVisible(false);
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

    private void showSuccessMessage() {
        InfoPane.info("saveSuccess")
                .backgroundColor(Color.GREEN)
                .hideAfter(Duration.seconds(5));
    }

    private void showErrorMessage() {
        InfoPane.info("saveError")
                .backgroundColor(Color.RED)
                .hideAfter(Duration.seconds(5));
    }


    public Pane getRoot() {
        return root;
    }
}
