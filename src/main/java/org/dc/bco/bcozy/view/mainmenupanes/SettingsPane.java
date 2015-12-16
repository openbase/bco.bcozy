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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.dc.bco.bcozy.BCozy;
import org.dc.bco.bcozy.view.Constants;
import org.dc.bco.bcozy.view.ImageViewProvider;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by hoestreich on 12/16/15.
 */
public class SettingsPane extends PaneElement {

    /**
     * Constructor for the SettingsPane.
     */
    public SettingsPane() {
        final ResourceBundle languageBundle = ResourceBundle
                .getBundle("languages.languages", new Locale("en", "US"));

        final TitledPane settingsPane = new TitledPane();
        final HBox settingsHeader = new HBox();
        final ImageView settingsIcon = ImageViewProvider
                .createImageView("/icons/adjustment.png", Constants.EXTRA_SMALL_ICON);
        final Label settingsLbl = new Label(languageBundle.getString("settings"));
        settingsHeader.getChildren().addAll(settingsIcon, settingsLbl);

        final VBox verticalLayout = new VBox();
        final ObservableList<String> availableLanguages = FXCollections.observableArrayList(
                "English", "Deutsch");
        final ChoiceBox<String> languageChoice = new ChoiceBox<>(availableLanguages);

        final ObservableList<String> availableThemes = FXCollections.observableArrayList(
                languageBundle.getString(Constants.LIGHT_THEME_CSS_NAME), languageBundle.getString(
                        Constants.DARK_THEME_CSS_NAME));
        final ChoiceBox<String> themeChoice = new ChoiceBox<>(availableThemes);
        themeChoice.setValue(availableThemes.get(0));
        themeChoice.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(final ObservableValue<? extends Number> observableValue, final Number number,
                                final Number number2) {
                if (availableThemes.get(number2.intValue())
                        .equals(languageBundle.getString(Constants.LIGHT_THEME_CSS_NAME))) {
                    BCozy.changeTheme(Constants.LIGHT_THEME_CSS);
                } else if (availableThemes.get(number2.intValue())
                        .equals(languageBundle.getString(Constants.DARK_THEME_CSS_NAME))) {
                    BCozy.changeTheme(Constants.DARK_THEME_CSS);
                }
            }
        });
        verticalLayout.setFillWidth(true);
        verticalLayout.getChildren().addAll(languageChoice, themeChoice);

        settingsPane.setGraphic(settingsHeader);
        settingsPane.setContent(verticalLayout);
        //settingsPane.setExpanded(false);

        this.getChildren().addAll(settingsPane);

    }
}
