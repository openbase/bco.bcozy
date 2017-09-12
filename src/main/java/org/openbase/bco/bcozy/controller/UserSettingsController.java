/*
 * ==================================================================
 * This file is part of org.openbase.bco.bcozy.
 *
 * org.openbase.bco.bcozy is free software: you can redistribute it and modify
 * it under the terms of the GNU General Public License (Version 3)
 * as published by the Free Software Foundation.
 *
 * org.openbase.bco.bcozy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
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
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.CustomTextField;
import org.openbase.bco.authentication.lib.SessionManager;
import org.openbase.bco.bcozy.model.LanguageSelection;
import org.openbase.bco.bcozy.util.Language;
import org.openbase.bco.bcozy.util.Languages;
import org.openbase.bco.bcozy.view.*;
import org.openbase.bco.registry.remote.Registries;
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
    private ChoiceBox<Language> languageChoice;
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
    private ObservableList<Language> availableLanguages;
    private final ForegroundPane foregroundPane;

    public UserSettingsController(ForegroundPane foregroundPane) {
        this.foregroundPane = foregroundPane;
    }


    public void initialize() {

        SessionManager.getInstance().addLoginObserver((o, b) -> {
            LOGGER.warn("isLoggedIn is " + b);
            if (b != null) {
                onLogin();
            }
        });

        initEditableFields(changeUsername, changeFirstname, changeLastname, changeMail, changePhone);

        initializeLanguages();

        final ResourceBundle languageBundle = ResourceBundle
                .getBundle(Constants.LANGUAGE_RESOURCE_BUNDLE, Locale.getDefault());
        availableThemes = FXCollections.observableArrayList(
                languageBundle.getString(Constants.LIGHT_THEME_CSS_NAME),
                languageBundle.getString(Constants.DARK_THEME_CSS_NAME));
        themeChoice.setItems(availableThemes);

        savePassword.setApplyOnNewText(String::toUpperCase);
        changePasswordPane.setExpanded(false);


    }

    /**
     * Initializes the ChoiceBox, adds Listeners sets the current Language.
     */
    private void initializeLanguages() {
        availableLanguages = FXCollections.observableList(Languages.getInstance().get());
        languageChoice.setItems(availableLanguages);
        languageChoice.setConverter(new StringConverter<Language>() {
            @Override
            public String toString(Language object) {
                return object.getName();
            }

            @Override
            public Language fromString(String string) {
                return Languages.getInstance().get(string);
            }
        });
        languageChoice.getSelectionModel().select(Languages.getInstance().get(Locale.getDefault()));
        languageChoice.getSelectionModel().selectedItemProperty().addListener((observable, oldLocale, newLocale)
                -> {
            if (newLocale != null) {
                LanguageSelection.getInstance().setSelectedLocale(newLocale.getLocale());
            }
        });

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
            ExceptionPrinter.printHistory(ex, LOGGER);
        }

        showErrorMessage();
    }

    private void onLogin() throws InterruptedException {
        LOGGER.warn("UserID is " + SessionManager.getInstance().getUserId());

        try {
            initUserName();
            initFirstName();
            initLastName();
            initMail();
            initPhone();
            initOccupant();

        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER);
        }
    }

    private void initOccupant() throws InterruptedException, CouldNotPerformException {
        isOccupantField.setSelected(Registries.getUserRegistry().getUserConfigById(SessionManager.getInstance()
                .getUserId()).getUserConfig().getOccupant());

        isOccupantField.selectedProperty().addListener((observable, oldValue, newValue) -> {
            updateOccupant(newValue);

        });
    }

    private void initPhone() throws InterruptedException, CouldNotPerformException {
        changePhone.setText(Registries.getUserRegistry().getUserConfigById(SessionManager.getInstance().getUserId
                ()).getUserConfig().getMobilePhoneNumber());
    }

    private void initMail() throws InterruptedException, CouldNotPerformException {
        changeMail.setText(Registries.getUserRegistry().getUserConfigById(SessionManager.getInstance().getUserId
                ()).getUserConfig().getEmail());
    }

    private void initUserName() throws InterruptedException, CouldNotPerformException {
        changeUsername.setText(Registries.getUserRegistry().getUserConfigById(SessionManager.getInstance().getUserId
                ()).getUserConfig().getUserName());
    }

    private void initFirstName() throws InterruptedException, CouldNotPerformException {
        changeFirstname.setText(Registries.getUserRegistry().getUserConfigById(SessionManager.getInstance()
                .getUserId
                        ()).getUserConfig().getFirstName());
    }

    private void initLastName() throws InterruptedException, CouldNotPerformException {
        changeLastname.setText(Registries.getUserRegistry().getUserConfigById(SessionManager.getInstance().getUserId
                ()).getUserConfig().getLastName());
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
            ExceptionPrinter.printHistory(ex, LOGGER);
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
            InfoPane.info("oldPasswordWrong").backgroundColor(Color.RED).hideAfter(Duration.seconds(5));
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER);
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
    public ChoiceBox<Language> getLanguageChoice() {
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
    public ObservableList<Language> getAvailableLanguages() {
        return availableLanguages;
    }

    public VBox getRoot() {
        return root;
    }
}
