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

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.textfield.CustomTextField;
import org.openbase.bco.authentication.lib.SessionManager;
import org.openbase.bco.bcozy.BCozy;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.ObserverLabel;
import org.openbase.bco.bcozy.view.SVGIcon;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by hoestreich on 12/16/15.
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

    private ObservableList<String> availableThemes;
    private ObservableList<String> availableLanguages;


    /**
     * Constructor for the SettingsPane.
     */
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


    }

    private void onLogin() {
        LOGGER.warn("UserID is " + SessionManager.getInstance().getUserId());

        initUserName();
        initFirstName();
        initLastName();
        initMail();
        initPhone();

    }

    private void initPhone() {
        try {
            changePhone.setText(Registries.getUserRegistry().getUserConfigById(SessionManager.getInstance().getUserId
                    ()).getUserConfig().getMobilePhoneNumber());
        } catch (CouldNotPerformException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initMail() {
        try {
            changeMail.setText(Registries.getUserRegistry().getUserConfigById(SessionManager.getInstance().getUserId
                    ()).getUserConfig().getEmail());
        } catch (CouldNotPerformException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initUserName() {
        try {
            changeUsername.setText(Registries.getUserRegistry().getUserConfigById(SessionManager.getInstance().getUserId
                    ()).getUserConfig().getUserName());
        } catch (CouldNotPerformException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initFirstName() {
        try {
            changeFirstname.setText(Registries.getUserRegistry().getUserConfigById(SessionManager.getInstance().getUserId
                    ()).getUserConfig().getFirstName());
        } catch (CouldNotPerformException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initLastName() {
        try {
            changeLastname.setText(Registries.getUserRegistry().getUserConfigById(SessionManager.getInstance().getUserId
                    ()).getUserConfig().getLastName());
        } catch (CouldNotPerformException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initEditableFields(CustomTextField... fields) {
        for (CustomTextField field : fields) {
            Button editButton = new Button("");
            editButton.setGraphic(new SVGIcon(FontAwesomeIcon.PENCIL, Constants.EXTRA_SMALL_ICON, true));

            editButton.setOnAction((a) -> {

                if (field.isEditable()) {
                    field.setEditable(false);
                    editButton.setGraphic(new SVGIcon(FontAwesomeIcon.PENCIL, Constants.EXTRA_SMALL_ICON, true));


                } else {

                    field.setEditable(true);
                    editButton.setGraphic(new SVGIcon(FontAwesomeIcon.FLOPPY_ALT, Constants.EXTRA_SMALL_ICON, true));
                }


            });

            field.setEditable(false);
            field.setRight(editButton);
        }
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
