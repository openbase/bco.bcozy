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

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.CustomTextField;
import org.openbase.bco.authentication.lib.SessionManager;
import org.openbase.bco.bcozy.model.LanguageSelection;
import org.openbase.bco.bcozy.model.UserData;
import org.openbase.bco.bcozy.util.Language;
import org.openbase.bco.bcozy.util.Languages;
import org.openbase.bco.bcozy.util.ThemeManager;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.InfoPane;
import org.openbase.jul.visual.javafx.JFXConstants;
import org.openbase.jul.visual.javafx.geometry.svg.SVGGlyphIcon;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.unit.UnitConfigType;
import rst.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * @author vdasilva
 */
public class UserSettingsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserSettingsController.class);
    public Pane changePassword;
    public PasswordChangeController changePasswordController;


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
    private TitledPane changePasswordPane;


    private ObservableList<String> availableThemes;
    private ObservableList<Language> availableLanguages;


    @FXML
    public void initialize() {

        SessionManager.getInstance().addLoginObserver((o, b) -> {
            LOGGER.warn("isLoggedIn is " + b);
            if (b != null) {
                onLogin();
            }
        });

        initEditableFields(changeUsername, changeFirstname, changeLastname, changeMail, changePhone);
        initializeLanguages();

        // load theme list
        final ResourceBundle languageBundle = ResourceBundle.getBundle(Constants.LANGUAGE_RESOURCE_BUNDLE, Locale.getDefault());
        availableThemes = FXCollections.observableArrayList();
        for (String theme : ThemeManager.getInstance().getThemeList()) {
            availableThemes.add(languageBundle.getString(theme));
        }

        themeChoice.setItems(availableThemes);
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
        languageChoice.getSelectionModel().selectedItemProperty().addListener((observable, oldLocale, newLocale) -> {
            if (newLocale != null) {
                LanguageSelection.getInstance().setSelectedLocale(newLocale.getLocale());
            }
        });

    }

    private void onLogin() throws InterruptedException {
        LOGGER.warn("UserID is " + SessionManager.getInstance().getUserId());

        try {
            UserData userData = UserData.currentUser();

            changeUsername.textProperty().bindBidirectional(userData.userNameProperty());
            changeFirstname.textProperty().bindBidirectional(userData.firstnameProperty());
            changeLastname.textProperty().bindBidirectional(userData.lastNameProperty());
            changeMail.textProperty().bindBidirectional(userData.mailProperty());
            changePhone.textProperty().bindBidirectional(userData.phoneProperty());

        } catch (CouldNotPerformException | ExecutionException | TimeoutException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER);
        }
    }

    private void initEditableFields(CustomTextField... fields) {
        for (CustomTextField field : fields) {

            Button editButton = new Button("");
            editButton.setGraphic(new SVGGlyphIcon(FontAwesomeIcon.PENCIL, JFXConstants.ICON_SIZE_EXTRA_SMALL, true));

            editButton.setOnAction((a) -> {

                if (field.isEditable()) {
                    try {
                        saveUserSettings();
                        field.setEditable(false);
                        editButton.setGraphic(new SVGGlyphIcon(FontAwesomeIcon.PENCIL, JFXConstants.ICON_SIZE_EXTRA_SMALL, true));
                    } catch (InterruptedException ex) {
                        ExceptionPrinter.printHistory("Could not save user settings!", ex, LOGGER);
                        Thread.currentThread().interrupt();
                    }

                } else {

                    field.setEditable(true);
                    editButton.setGraphic(new SVGGlyphIcon(FontAwesomeIcon.FLOPPY_ALT, JFXConstants.ICON_SIZE_EXTRA_SMALL, true));
                }


            });

            field.setEditable(false);
            field.setRight(editButton);
        }
    }


    private void saveUserSettings() throws InterruptedException {

        try {

            UnitConfigType.UnitConfig.Builder newUserConfig = Registries.getUnitRegistry().getUnitConfigById(SessionManager.getInstance().getUserId(), UnitType.USER).toBuilder();

            newUserConfig.getUserConfigBuilder()
                    .setUserName(changeUsername.getText())
                    .setFirstName(changeFirstname.getText())
                    .setLastName(changeLastname.getText())
                    .setEmail(changeMail.getText())
                    .setMobilePhoneNumber(changePhone.getText());

            Registries.getUnitRegistry().updateUnitConfig(newUserConfig.build());
            showSuccessMessage();
        } catch (CouldNotPerformException ex) {
            showErrorMessage();
            ExceptionPrinter.printHistory(ex, LOGGER);
        }
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
