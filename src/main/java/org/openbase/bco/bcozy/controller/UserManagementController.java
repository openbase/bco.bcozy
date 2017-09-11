package org.openbase.bco.bcozy.controller;

import com.jfoenix.controls.JFXCheckBox;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.controlsfx.control.CheckComboBox;
import org.openbase.bco.authentication.lib.SessionManager;
import org.openbase.bco.bcozy.model.SessionManagerFacade;
import org.openbase.bco.bcozy.model.SessionManagerFacadeImpl;
import org.openbase.bco.bcozy.model.UserData;
import org.openbase.bco.bcozy.util.AuthorizationGroups;
import org.openbase.bco.bcozy.view.InfoPane;
import org.openbase.bco.bcozy.view.ObserverButton;
import org.openbase.bco.bcozy.view.ObserverLabel;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.VerificationFailedException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.unit.UnitConfigType;
import rst.domotic.unit.UnitConfigType.UnitConfig;
import rst.domotic.unit.user.UserConfigType;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
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
    private ObserverButton registerBtn;
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


        ObservableList<UserData> userDataList = FXCollections.observableArrayList();

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
                return userDataList.stream()
                        .filter(userData -> !userData.isUnsaved())//filter new user
                        .filter(userData -> userData.getUserName().equals(string))//find user with username
                        .findFirst()//select user
                        .orElse(new UserData());//or null (=new user)
            }
        });
        chooseUserBox.getItems().add(new UserData());//new User

        chooseUserBox.valueProperty().addListener((observable, oldValue, newValue) ->
                userSelected(newValue));
        try {
//            chooseUserBox.getItems().add(new UserData("test-id"));//FIXME

            if (Registries.getUserRegistry().isDataAvailable()) {
                List<UnitConfig> users = Registries.getUserRegistry().getUserConfigs();


                for (UnitConfig user : users) {
                    userDataList.add(new UserData(user));
                }
            }
            chooseUserBox.getItems().addAll(userDataList);
        } catch (CouldNotPerformException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }


        registerBtn.getStyleClass().clear();
        registerBtn.getStyleClass().add("transparent-button");
        registerBtn.setApplyOnNewText(String::toUpperCase);
        usergroupField.setConverter(AuthorizationGroups.stringConverter(groups));
        usergroupField.prefWidthProperty().bind(root.widthProperty());

        usernameEmptyLabel.getStyleClass().remove("label");
        firstnameEmptyLabel.getStyleClass().remove("label");
        lastnameEmptyLabel.getStyleClass().remove("label");
        mailEmptyLabel.getStyleClass().remove("label");
    }

    private void userSelected(UserData selectedUser) {
        unbindFields();
        if (selectedUser == null) {
            return;
        }
        this.selectedUser = selectedUser;

        bindProperty(username, UserData::userNameProperty);
        bindProperty(firstname, UserData::firstnameProperty);
        bindProperty(lastname, UserData::lastNameProperty);
        bindProperty(mail, UserData::mailProperty);
        bindProperty(phone, UserData::phoneProperty);

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
            ExceptionPrinter.printHistory(ex, LOGGER);
            showErrorMessage();
        }
    }

    private void updateOccupant(Boolean isOccupant) {
        try {
            UserConfigType.UserConfig userConfig = Registries.getUserRegistry()
                    .getUserConfigById(SessionManager.getInstance().getUserId()).getUserConfig().toBuilder()
                    .setOccupant(isOccupant).build();

            UnitConfigType.UnitConfig unitConfig = Registries.getUserRegistry()
                    .getUserConfigById(SessionManager.getInstance().getUserId()).toBuilder()
                    .setUserConfig(userConfig).build();

            Future<UnitConfig> saved = Registries.getUserRegistry().updateUserConfig(unitConfig);

            boolean savedValue = saved.get(5, TimeUnit.SECONDS).getUserConfig().getOccupant();

            if (savedValue == isOccupant) {
                showSuccessMessage();

                return;
            }

        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        } catch (CouldNotPerformException | ExecutionException | TimeoutException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER);
        }

        showErrorMessage();
    }

    //TODO:
    private void initOccupant() throws InterruptedException, CouldNotPerformException {
        /*isOccupantField.setSelected(Registries.getUserRegistry().getUserConfigById(SessionManager.getInstance()
                .getUserId()).getUserConfig().getOccupant());

        isOccupantField.selectedProperty().addListener((observable, oldValue, newValue) -> {
            updateOccupant(newValue);

        });*/
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
