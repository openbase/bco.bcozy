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

import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import org.dc.bco.bcozy.view.Constants;
import org.dc.bco.bcozy.view.ObserverLabel;
import org.dc.bco.bcozy.view.SVGIcon;

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

        statusIcon = new SVGIcon(MaterialIcon.TUNE, Constants.SMALL_ICON, true);

        final TitledPane settingsPane = new TitledPane();
        settingsPane.getStyleClass().add("settings-pane");
        //final HBox settingsHeader = new HBox();
        settingsIcon = new SVGIcon(MaterialIcon.TUNE, Constants.EXTRA_SMALL_ICON, true);
        settingsLbl = new ObserverLabel("settings", settingsIcon);
        //settingsHeader.getChildren().addAll(settingsIcon, settingsLbl);

        //final VBox verticalLayout = new VBox();
        final BorderPane verticalLayout = new BorderPane();

        //TODO: Implement Property implementation
        //JPService.getProperty(JPLanguage.class).getValue();

        availableLanguages = FXCollections.observableArrayList("English", "Deutsch");
        languageChoice = new ChoiceBox<>(availableLanguages);
        //CHECKSTYLE.OFF: MagicNumber
        languageChoice.setPrefWidth(250);

        availableThemes = FXCollections.observableArrayList(
                languageBundle.getString(Constants.LIGHT_THEME_CSS_NAME),
                languageBundle.getString(Constants.DARK_THEME_CSS_NAME));
        themeChoice = new ChoiceBox<>(availableThemes);
        themeChoice.setPrefWidth(250);
        //CHECKSTYLE.ON: MagicNumber

        //verticalLayout.setFillWidth(true);
        //verticalLayout.getChildren().addAll(languageChoice, themeChoice);
        verticalLayout.setTop(languageChoice);
        verticalLayout.setBottom(themeChoice);

        settingsPane.setGraphic(settingsLbl);
        settingsPane.setContent(verticalLayout);

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
    public ObserverLabel getSettingsLbl() {
        return settingsLbl;
    }

    @Override
    public Node getStatusIcon() {
        return statusIcon;
    }
}
