/**
 * ==================================================================
 * <p>
 * This file is part of org.openbase.bco.bcozy.
 * <p>
 * org.openbase.bco.bcozy is free software: you can redistribute it and modify
 * it under the terms of the GNU General Public License (Version 3)
 * as published by the Free Software Foundation.
 * <p>
 * org.openbase.bco.bcozy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with org.openbase.bco.bcozy. If not, see <http://www.gnu.org/licenses/>.
 * ==================================================================
 */
package org.openbase.bco.bcozy.controller;

import com.jfoenix.controls.JFXCheckBox;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import org.controlsfx.control.textfield.CustomTextField;
import org.openbase.bco.authentication.lib.SessionManager;
import org.openbase.bco.bcozy.view.*;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.bco.registry.user.lib.UserRegistry;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.RejectedException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.unit.UnitConfigType;
import rst.domotic.unit.user.UserConfigType;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author vdasilva
 */
public class UserSettingsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserSettingsController.class);


    @FXML
    private CustomTextField changeUsername;
    @FXML
    private CustomTextField changeFirstname;
    @FXML
    private CustomTextField changeLastname;
    @FXML
    private CustomTextField changeMail;
    @FXML
    private CustomTextField changePhone;

    @FXML
    private VBox root;

    @FXML
    private ChoiceBox<String> themeChoice;
    @FXML
    private ChoiceBox<String> languageChoice;
    @FXML
    private JFXCheckBox isOccupantField;
    @FXML
    private TitledPane changePasswordPane;
    @FXML
    private PasswordField oldPassword;
    @FXML
    private PasswordField newPassword;
    @FXML
    private PasswordField repeatedPassword;
    @FXML
    private ObserverButton savePassword;

    private ObservableList<String> availableThemes;
    private ObservableList<String> availableLanguages;
    private final ForegroundPane foregroundPane;

    public UserSettingsController(ForegroundPane foregroundPane) {
        this.foregroundPane = foregroundPane;
    }


    public void initialize() {

        SessionManager.getInstance().getLoginObervable().addObserver((o, b) -> {
            LOGGER.warn("isLoggedIn is " + b);
            if (b) {
                onLogin();
            }
        });

        initEditableFields(changeUsername, changeFirstname, changeLastname, changeMail, changePhone);


        final ResourceBundle languageBundle = ResourceBundle
                .getBundle(Constants.LANGUAGE_RESOURCE_BUNDLE, Locale.getDefault());

        //TODO: Implement Property implementation

        availableLanguages = FXCollections.observableArrayList("English", "Deutsch");
        languageChoice.setItems(availableLanguages);


        availableThemes = FXCollections.observableArrayList(
                languageBundle.getString(Constants.LIGHT_THEME_CSS_NAME),
                languageBundle.getString(Constants.DARK_THEME_CSS_NAME));
        themeChoice.setItems(availableThemes);

        savePassword.setApplyOnNewText(String::toUpperCase);
        changePasswordPane.setExpanded(false);


    }

    private void updateOccupant(Boolean isOccupant) {
        try {
            UserConfigType.UserConfig userConfig = Registries.getUserRegistry()
                    .getUserConfigById(SessionManager.getInstance().getUserId()).getUserConfig().toBuilder()
                    .setOccupant(isOccupant).build();

            UnitConfigType.UnitConfig unitConfig = Registries.getUserRegistry()
                    .getUserConfigById(SessionManager.getInstance().getUserId()).toBuilder()
                    .setUserConfig(userConfig).build();

            Future<UnitConfigType.UnitConfig> saved = Registries.getUserRegistry().updateUserConfig(unitConfig);

            boolean savedValue = saved.get(5, TimeUnit.SECONDS).getUserConfig().getOccupant();

            if (savedValue == isOccupant) {
                showSuccessMessage();

                return;
            }

        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        } catch (CouldNotPerformException | ExecutionException | TimeoutException ex) {
            ex.printStackTrace();
        }

        showErrorMessage();
    }

    private void onLogin() throws InterruptedException {
        LOGGER.warn("UserID is " + SessionManager.getInstance().getUserId());

        initUserName();
        initFirstName();
        initLastName();
        initMail();
        initPhone();
        initOccupant();

    }

    private void initOccupant() throws InterruptedException {
        try {
            isOccupantField.setSelected(Registries.getUserRegistry().getUserConfigById(SessionManager.getInstance()
                    .getUserId()).getUserConfig().getOccupant());

            isOccupantField.selectedProperty().addListener((observable, oldValue, newValue) -> {
                updateOccupant(newValue);

            });
        } catch (CouldNotPerformException e) {
            e.printStackTrace();
        }

    }

    private void initPhone() throws InterruptedException {
        try {
            changePhone.setText(Registries.getUserRegistry().getUserConfigById(SessionManager.getInstance().getUserId
                    ()).getUserConfig().getMobilePhoneNumber());
        } catch (CouldNotPerformException e) {
            e.printStackTrace();
        }
    }

    private void initMail() throws InterruptedException {
        try {
            changeMail.setText(Registries.getUserRegistry().getUserConfigById(SessionManager.getInstance().getUserId
                    ()).getUserConfig().getEmail());
        } catch (CouldNotPerformException e) {
            e.printStackTrace();
        }
    }

    private void initUserName() throws InterruptedException {
        try {
            changeUsername.setText(Registries.getUserRegistry().getUserConfigById(SessionManager.getInstance().getUserId
                    ()).getUserConfig().getUserName());
        } catch (CouldNotPerformException e) {
            e.printStackTrace();
        }
    }

    private void initFirstName() throws InterruptedException {
        try {
            changeFirstname.setText(Registries.getUserRegistry().getUserConfigById(SessionManager.getInstance()
                    .getUserId
                            ()).getUserConfig().getFirstName());
        } catch (CouldNotPerformException e) {
            e.printStackTrace();
        }
    }

    private void initLastName() throws InterruptedException {
        try {
            changeLastname.setText(Registries.getUserRegistry().getUserConfigById(SessionManager.getInstance().getUserId
                    ()).getUserConfig().getLastName());
        } catch (CouldNotPerformException e) {
            e.printStackTrace();
        }
    }

    private void initEditableFields(CustomTextField... fields) {
        for (CustomTextField field : fields) {

            Button editButton = new Button("");
            editButton.setGraphic(new SVGIcon(FontAwesomeIcon.PENCIL, Constants.EXTRA_SMALL_ICON, true));

            editButton.setOnAction((a) -> {

                if (field.isEditable()) {
                    try {
                        saveUserSettings();
                        field.setEditable(false);
                        editButton.setGraphic(new SVGIcon(FontAwesomeIcon.PENCIL, Constants.EXTRA_SMALL_ICON, true));
                    } catch (InterruptedException ex) {
                        ExceptionPrinter.printHistory("Could not save user settings!", ex, LOGGER);
                        Thread.currentThread().interrupt();
                    }

                } else {

                    field.setEditable(true);
                    editButton.setGraphic(new SVGIcon(FontAwesomeIcon.FLOPPY_ALT, Constants.EXTRA_SMALL_ICON, true));
                }


            });

            field.setEditable(false);
            field.setRight(editButton);
        }
    }


    private void saveUserSettings() throws InterruptedException {

        try {

            UnitConfigType.UnitConfig.Builder newUserConfig = Registries.getUserRegistry().getUserConfigById
                    (SessionManager.getInstance().getUserId()).toBuilder();

//        UserConfigType.UserConfig.newBuilder().setOccupant()

            newUserConfig.getUserConfigBuilder()
                    .setUserName(changeUsername.getText())
                    .setFirstName(changeFirstname.getText())
                    .setLastName(changeLastname.getText())
                    .setEmail(changeMail.getText())
                    .setMobilePhoneNumber(changePhone.getText())
                    .setOccupant(isOccupantField.isSelected());

            Registries.getUserRegistry().updateUserConfig(newUserConfig.build());
            showSuccessMessage();
        } catch (CouldNotPerformException ex) {
            showErrorMessage();
            ex.printStackTrace();
        }


    }

    @FXML
    private void saveNewPassword() throws InterruptedException {

        if (!verifyNewPassword()) {
            InfoPane.warn("passwordsNotEqual").hideAfter(Duration.seconds(5));
            clearPasswordFields();

            return;
        }

        try {
            SessionManager.getInstance().changeCredentials(SessionManager.getInstance().getUserId(), oldPassword.getText(), newPassword.getText());
            showSuccessMessage();

        } catch (RejectedException rex) {
            rex.printStackTrace();
            InfoPane.info("oldPasswordWrong").backgroundColor(Color.RED).hideAfter(Duration.seconds(5));

        } catch (CouldNotPerformException ex) {
            ex.printStackTrace();
        }

        clearPasswordFields();

    }

    private void clearPasswordFields() {
        oldPassword.clear();
        newPassword.clear();
        repeatedPassword.clear();
    }

    private boolean verifyNewPassword() {
        return newPassword.getText().equals(repeatedPassword.getText());
    }

    private boolean verifyOldPassword() throws InterruptedException {
        try {
            return Registries.getUserRegistry().getUserConfigById(SessionManager.getInstance().getUserId()).getUserConfig()
                    .getPassword().toStringUtf8().equals(oldPassword.getText());
        } catch (CouldNotPerformException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void showSuccessMessage() {
        InfoPane.info("saveSuccess")
                .backgroundColor(Color.GREEN)
                .hideAfter(Duration.seconds(5));
    }

    private void showErrorMessage() {
        InfoPane.info("saveError")
                .backgroundColor(Color.RED)
                .hideAfter(Duration.seconds(5));    }

    /**
     * Getter for the themeChoice ChoiceBox.
     *
     * @return instance of the themeChoice
     */
    public ChoiceBox<String> getThemeChoice() {
        return themeChoice;
    }

    /**
     * Getter for the languageChoice ChoiceBox.
     *
     * @return instance of the languageChoice
     */
    public ChoiceBox<String> getLanguageChoice() {
        return languageChoice;
    }

    /**
     * Getter for the availableThemes List.
     *
     * @return instance of the availableThemes
     */
    public ObservableList<String> getAvailableThemes() {
        return availableThemes;
    }

    /**
     * Getter for the availableLanguages List.
     *
     * @return instance of the availableLanguages
     */
    public ObservableList<String> getAvailableLanguages() {
        return availableLanguages;
    }

    public VBox getRoot() {
        return root;
    }
}
