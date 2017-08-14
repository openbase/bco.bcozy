/**
 * ==================================================================
 *
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
package org.openbase.bco.bcozy.view.mainmenupanes;

import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.ObserverLabel;
import org.openbase.bco.bcozy.view.SVGIcon;

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
    private final ObserverLabel settingsLbl;
    private final SVGIcon settingsIcon;
    private final SVGIcon statusIcon;


    /**
     * Constructor for the SettingsPane.
     */
    public SettingsPane() {
        final ResourceBundle languageBundle = ResourceBundle
                .getBundle(Constants.LANGUAGE_RESOURCE_BUNDLE, Locale.getDefault());
        final VBox verticalLayout = new VBox();


        statusIcon = new SVGIcon(MaterialIcon.TUNE, Constants.SMALL_ICON, true);

        this.getStyleClass().add("settings-pane");


        settingsIcon = new SVGIcon(MaterialIcon.TUNE, Constants.EXTRA_SMALL_ICON, true);
        settingsLbl = new ObserverLabel("settings", settingsIcon);


        //TODO: Implement Property implementation

        availableLanguages = FXCollections.observableArrayList("English", "Deutsch");
        languageChoice = new ChoiceBox<>(availableLanguages);
        languageChoice.prefWidthProperty().bind(this.widthProperty());

        availableThemes = FXCollections.observableArrayList(
                languageBundle.getString(Constants.LIGHT_THEME_CSS_NAME),
                languageBundle.getString(Constants.DARK_THEME_CSS_NAME));
        themeChoice = new ChoiceBox<>(availableThemes);
        themeChoice.prefWidthProperty().bind(this.widthProperty());

        verticalLayout.getChildren().addAll(languageChoice, themeChoice);
        verticalLayout.setSpacing(10.0);

        this.getChildren().addAll(verticalLayout);
        this.setFillWidth(true);

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

    /**
     * Getter for the settingsLbl.
     *
     * @return instance of the settingsLbl
     */
    public ObserverLabel getSettingsLbl() {
        return settingsLbl;
    }

    @Override
    public Node getStatusIcon() {
        return statusIcon;
    }
}
