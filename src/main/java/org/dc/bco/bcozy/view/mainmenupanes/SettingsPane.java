/**
 * ==================================================================
 *
 * This file is part of org.dc.bco.bcozy.
 *
 * org.dc.bco.bcozy is free software: you can redistribute it and modify
 * it under the terms of the GNU General Public License (Version 3)
 * as published by the Free Software Foundation.
 *
 * org.dc.bco.bcozy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with org.dc.bco.bcozy. If not, see <http://www.gnu.org/licenses/>.
 * ==================================================================
 */
package org.dc.bco.bcozy.view.mainmenupanes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.dc.bco.bcozy.view.Constants;
import org.dc.bco.bcozy.view.ImageViewProvider;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by hoestreich on 12/16/15.
 */
public class SettingsPane extends PaneElement {

    private final ChoiceBox<String> themeChoice;
    private final ChoiceBox<String> languageChoice;
    private final ObservableList<String> availableThemes;
    private final ObservableList<String> availableLanguages;
    private final Label settingsLbl;

    /**
     * Constructor for the SettingsPane.
     */
    public SettingsPane() {
        final ResourceBundle languageBundle = ResourceBundle
                .getBundle(Constants.LANGUAGE_RESOURCE_BUNDLE, Locale.getDefault());

        final TitledPane settingsPane = new TitledPane();
        final HBox settingsHeader = new HBox();
        final ImageView settingsIcon = ImageViewProvider
                .createImageView("/icons/adjustment.png", Constants.EXTRA_SMALL_ICON);
        settingsLbl = new Label(languageBundle.getString("settings"));
        settingsHeader.getChildren().addAll(settingsIcon, settingsLbl);

        final VBox verticalLayout = new VBox();
        availableLanguages = FXCollections.observableArrayList("English", "Deutsch");
        languageChoice = new ChoiceBox<>(availableLanguages);

        availableThemes = FXCollections.observableArrayList(
                languageBundle.getString(Constants.LIGHT_THEME_CSS_NAME), languageBundle.getString(
                        Constants.DARK_THEME_CSS_NAME));
        themeChoice = new ChoiceBox<>(availableThemes);
        //themeChoice.setValue(availableThemes.get(0));

        verticalLayout.setFillWidth(true);
        verticalLayout.getChildren().addAll(languageChoice, themeChoice);

        settingsPane.setGraphic(settingsHeader);
        settingsPane.setContent(verticalLayout);
        //settingsPane.setExpanded(false);

        this.getChildren().addAll(settingsPane);

    }

    /**
     * Getter for the themeChoice ChoiceBox.
     * @return instance of the themeChoice
     */
    public ChoiceBox<String> getThemeChoice() {
        return themeChoice;
    }

    /**
     * Getter for the languageChoice ChoiceBox.
     * @return instance of the languageChoice
     */
    public ChoiceBox<String> getLanguageChoice() {
        return languageChoice;
    }

    /**
     * Getter for the availableThemes List.
     * @return instance of the availableThemes
     */
    public ObservableList<String> getAvailableThemes() {
        return availableThemes;
    }
    /**
     * Getter for the availableLanguages List.
     * @return instance of the availableLanguages
     */
    public ObservableList<String> getAvailableLanguages() {
        return availableLanguages;
    }

    /**
     * Getter for the settingsLbl.
     * @return instance of the settingsLbl
     */
    public Label getSettingsLbl() {
        return settingsLbl;
    }
}
