package org.openbase.bco.bcozy.controller;

import com.jfoenix.controls.JFXCheckBox;
import com.sun.javafx.tk.Toolkit;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.controlsfx.control.CheckComboBox;
import org.openbase.bco.authentication.lib.SessionManager;
import org.openbase.bco.bcozy.model.LanguageSelection;
import org.openbase.bco.bcozy.model.SessionManagerFacade;
import org.openbase.bco.bcozy.model.SessionManagerFacadeImpl;
import org.openbase.bco.bcozy.model.UserData;
import org.openbase.bco.bcozy.util.AuthorizationGroups;
import org.openbase.bco.bcozy.view.InfoPane;
import org.openbase.bco.bcozy.view.ObserverButton;
import org.openbase.bco.bcozy.view.ObserverLabel;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.ExceptionProcessor;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.VerificationFailedException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.unit.UnitConfigType.UnitConfig;
import rst.domotic.unit.user.UserConfigType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

/**
 * User registration.
 *
 * @author vdasilva
 */
public class UserManagementController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserManagementController.class);


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
    private ComboBox<UserData> chooseUserBox;
    @FXML
    private JFXCheckBox isAdmin;
    @FXML
    private JFXCheckBox isOccupant;
    @FXML
    private ObserverButton saveBtn;
    @FXML
    public ObserverButton deleteButton;
    @FXML
    private ObserverLabel usernameEmptyLabel;
    @FXML
    private ObserverLabel firstnameEmptyLabel;
    @FXML
    private ObserverLabel lastnameEmptyLabel;
    @FXML
    private ObserverLabel mailEmptyLabel;


    private UserData selectedUser;

    public void initialize() {
        ObservableList<UnitConfig> groups = AuthorizationGroups.getAuthorizationGroups();
        groups.addListener((ListChangeListener.Change<? extends UnitConfig> c)
                -> setGroups(groups)
        );

        chooseUserBox.setConverter(new StringConverter<UserData>() {
            @Override
            public String toString(UserData object) {
                if (object.isUnsaved()) {
                    return "neuer Nutzer";//TODO localized
                }
                return object.getUserName();
            }

            @Override
            public UserData fromString(String string) {
                return chooseUserBox.getItems().stream()
                        .filter(userData -> !userData.isUnsaved())//filter new user
                        .filter(userData -> userData.getUserName().equals(string))//find user with username
                        .findFirst()//select user
                        .orElse(new UserData());//or null (=new user)
            }
        });

        chooseUserBox.valueProperty().addListener((observable, oldValue, newValue) -> userSelected(newValue));


        saveBtn.setApplyOnNewText(String::toUpperCase);
        deleteButton.setApplyOnNewText(String::toUpperCase);

        usergroupField.setConverter(AuthorizationGroups.stringConverter(groups));

        usergroupField.prefWidthProperty().bind(root.widthProperty());

        usernameEmptyLabel.getStyleClass().remove("label");
        firstnameEmptyLabel.getStyleClass().remove("label");
        lastnameEmptyLabel.getStyleClass().remove("label");
        mailEmptyLabel.getStyleClass().remove("label");

        chooseUserBox.getSelectionModel().select(0);


        try {
            Registries.getUserRegistry().addDataObserver((source, data) -> fillUserList());
        } catch (NotAvailableException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        SessionManager.getInstance().addLoginObserver((source, data) -> fillUserList());
    }


    private void fillUserList() {
        if (Platform.isFxApplicationThread()) {
            fillUserListInternal();
        } else {
            Platform.runLater(this::fillUserListInternal);
        }
    }

    private void fillUserListInternal() {
        Toolkit.getToolkit().checkFxUserThread();

        UserData userData = selectedUser;

        chooseUserBox.getItems().clear();

        chooseUserBox.getItems().add(new UserData());//new User
        try {
            if (Registries.getUserRegistry().isDataAvailable()) {
                List<UnitConfig> users = Registries.getUserRegistry().getUserConfigs();

                for (UnitConfig user : users) {
                    chooseUserBox.getItems().add(new UserData(user));
                }
            }
        } catch (CouldNotPerformException | ExecutionException | TimeoutException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        chooseUserBox.getSelectionModel().select(userData);
    }

    private void userSelected(UserData selectedUser) {
        if (Platform.isFxApplicationThread()) {
            userSelectedInternal(selectedUser);
        } else {
            Platform.runLater(() -> userSelectedInternal(selectedUser));
        }
    }

    private void userSelectedInternal(UserData selectedUser) {
        Toolkit.getToolkit().checkFxUserThread();

        unbindFields();
        resetFields();
        resetHints();

        if (selectedUser == null) {
            saveBtn.setDisable(true);
            deleteButton.setDisable(true);
            return;
        }
        this.selectedUser = selectedUser;

        bindProperty(username, UserData::userNameProperty);
        bindProperty(firstname, UserData::firstnameProperty);
        bindProperty(lastname, UserData::lastNameProperty);
        bindProperty(mail, UserData::mailProperty);
        bindProperty(phone, UserData::phoneProperty);

        isOccupant.selectedProperty().bindBidirectional(selectedUser.occupantProperty());
        isAdmin.selectedProperty().bindBidirectional(selectedUser.adminProperty());


        for (UnitConfig unitConfig : usergroupField.getItems()) {
            for (UnitConfig userGroup : selectedUser.getGroups()) {
                if (unitConfig.getId().equals(userGroup.getId())) {
                    usergroupField.getCheckModel().check(unitConfig);
                }
            }
        }


        if (selectedUser.isUnsaved()) {
            saveBtn.setIdentifier("register");
            deleteButton.setDisable(true);
            saveBtn.setDisable(false);
        } else {
            saveBtn.setIdentifier("save");
            deleteButton.setDisable(false);
            saveBtn.setDisable(false);

        }
    }

    private void unbindFields() {
        if (selectedUser == null) {
            return;
        }
        unbindProperty(username, UserData::userNameProperty);
        unbindProperty(firstname, UserData::firstnameProperty);
        unbindProperty(lastname, UserData::lastNameProperty);
        unbindProperty(mail, UserData::mailProperty);
        unbindProperty(phone, UserData::phoneProperty);
        isOccupant.selectedProperty().unbindBidirectional(selectedUser.occupantProperty());
        isAdmin.selectedProperty().unbindBidirectional(selectedUser.adminProperty());

    }

    private void bindProperty(TextInputControl field, Function<UserData, StringProperty> propertySupplier) {
        field.textProperty().bindBidirectional(propertySupplier.apply(selectedUser));
    }

    private void unbindProperty(TextInputControl field, Function<UserData, StringProperty> propertySupplier) {
        field.textProperty().unbindBidirectional(propertySupplier.apply(selectedUser));
    }


    private void setGroups(ObservableList<UnitConfig> groups) {
        Platform.runLater(() -> usergroupField.getItems().setAll(groups));
    }


    @FXML
    private void save() throws InterruptedException {
        if (selectedUser.isUnsaved()) {
            registerUser();
        } else {
            saveUser();
        }
    }

    private void registerUser() throws InterruptedException  {
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

        selectedUser.getUserConfig();

        UserConfigType.UserConfig user = selectedUser.getUserConfig();

        try {
            sessionManager.registerUser(user,
                    passwordField.getText(),
                    isAdmin.isSelected(),
                    groups);
            resetFields();
            showSuccessMessage();
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER);
            showErrorMessage(ex);
        }
    }

    private void saveUser() throws InterruptedException {

        try {
            List<UnitConfig> groups = new ArrayList<>(usergroupField.getCheckModel().getCheckedItems());

            UnitConfig unitConfig = Registries.getUserRegistry()
                    .getUserConfigById(selectedUser.getUserId())
                    .toBuilder()
                    .setUserConfig(selectedUser.getUserConfig())
                    .build();

            Registries.getUserRegistry().updateUserConfig(unitConfig);

            for (UnitConfig config : selectedUser.getGroups()) {
                AuthorizationGroups.tryRemoveFromGroup(config, selectedUser.getUserId());
            }
            for (UnitConfig config : groups) {
                AuthorizationGroups.tryAddToGroup(config, selectedUser.getUserId());
            }




            showSuccessMessage();
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER);
            showErrorMessage(ex);
        }
    }


    @FXML
    private void delete(ActionEvent actionEvent) {
        new Alert(Alert.AlertType.CONFIRMATION, "Wirklich löschen"/*TODO*/)
                .showAndWait()
                .filter(response -> response == ButtonType.OK)
                .ifPresent(response -> {
                    try {
                        deleteUser();
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                });

    }

    private void deleteUser() throws InterruptedException {
        //TODO

        try {
            Registries.getUserRegistry().removeUserConfig(Registries.getUnitRegistry().getUnitConfigById(selectedUser
                    .getUserId()));
            showSuccessMessage("deleteSuccess");
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER);
            showErrorMessage(ex);
        }

        userSelected(null);
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
        showSuccessMessage("saveSuccess");
    }

    private void showSuccessMessage(String message) {
        InfoPane.info(message)
                .backgroundColor(Color.GREEN)
                .hideAfter(Duration.seconds(5));
    }

    private void showErrorMessage() {
        InfoPane.info("saveError")
                .backgroundColor(Color.RED)
                .hideAfter(Duration.seconds(5));
    }

    private void showErrorMessage(Exception ex) {
        String message = LanguageSelection.getLocalized("saveErrorWithMessage", ExceptionProcessor.getInitialCauseMessage(ex));

        InfoPane.info(message)
                .backgroundColor(Color.RED)
                .hideAfter(Duration.seconds(5));
    }


    public Pane getRoot() {
        return root;
    }


}
