package org.openbase.bco.bcozy.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import org.openbase.bco.bcozy.BCozy;
import org.openbase.bco.bcozy.model.LanguageSelection;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.mainmenupanes.SettingsPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author vdasilva
 */
public class SettingsController {
    /**
     * Application Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MainMenuController.class);

    @FXML
    public Tab settingsTab;

    private SettingsPane settings;


    /**
     * Default Controller necessary for loading fxml files.
     */
    public SettingsController() {

    }

    public void init() {

    }

    public void addSettingsTab() {
        settingsTab.setContent(settings);
    }


    private void chooseTheme() {
        final ResourceBundle languageBundle = ResourceBundle
                .getBundle(Constants.LANGUAGE_RESOURCE_BUNDLE, Locale.getDefault());

        settings.getThemeChoice().getSelectionModel().selectedIndexProperty()
                .addListener(new ChangeListener<Number>() {

                    @Override
                    public void changed(final ObservableValue<? extends Number> observableValue, final Number number,
                                        final Number number2) {
                        if (settings.getAvailableThemes().get(number2.intValue())
                                .equals(languageBundle.getString(Constants.LIGHT_THEME_CSS_NAME))) {
                            BCozy.changeTheme(Constants.LIGHT_THEME_CSS);
                        } else if (settings.getAvailableThemes().get(number2.intValue())
                                .equals(languageBundle.getString(Constants.DARK_THEME_CSS_NAME))) {
                            BCozy.changeTheme(Constants.DARK_THEME_CSS);
                        }
                    }
                });
    }

    private void chooseLanguage() {
        settings.getLanguageChoice().getSelectionModel().selectedIndexProperty()
                .addListener(new ChangeListener<Number>() {

                    @Override
                    public void changed(final ObservableValue<? extends Number> observableValue, final Number number,
                                        final Number number2) {
                        if (settings.getAvailableLanguages().get(number2.intValue()).equals("English")) {
                            LanguageSelection.getInstance().setSelectedLocale(new Locale("en", "US"));
                        } else if (settings.getAvailableLanguages().get(number2.intValue()).equals("Deutsch")) {
                            LanguageSelection.getInstance().setSelectedLocale(new Locale("de", "DE"));
                        }
                    }
                });
    }

    public void setSettingsPane(SettingsPane settingsPane) {
        this.settings = settingsPane;

        settings.getThemeChoice().setOnAction(event -> chooseTheme());
        settings.getLanguageChoice().setOnAction(event -> chooseLanguage());
        //Necessary to ensure that the first change is not missed by the ChangeListener
        settings.getThemeChoice().getSelectionModel().select(0);
        settings.getLanguageChoice().getSelectionModel().select(0);
    }
}
