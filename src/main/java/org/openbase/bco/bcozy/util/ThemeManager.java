package org.openbase.bco.bcozy.util;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import org.openbase.bco.bcozy.BCozy;
import org.openbase.jul.visual.javafx.JFXConstants;

import java.util.List;

public class ThemeManager {

    public static boolean darkTheme = false;

    public SimpleStringProperty themeName = new SimpleStringProperty();

    private static ThemeManager instance = new ThemeManager();
    private List<String> themeList;

    private ThemeManager() {
        this.themeList = ThemeLoader.loadThemes();
        this.themeName.addListener((observable, oldValue, newValue) -> applyTheme());
    }

    public static ThemeManager getInstance() {
        return instance;
    }

    public List<String> getThemeList() {
        return themeList;
    }

    /**
     * Method to change application wide theme from other locations in the view.
     *
     * @param themeName the name of the theme to be set
     */
    public void changeTheme(final String themeName) {
        this.themeName.setValue(themeName);
    }

    private void applyTheme() {
        if (BCozy.primaryStage != null && BCozy.primaryStage.getScene() != null) {
            applyTheme(BCozy.primaryStage.getScene());
        }
    }

    public void applyTheme(final Scene scene) {
        if (BCozy.primaryStage != null && BCozy.primaryStage.getScene() != null) {
            scene.getStylesheets().clear();
            scene.getStylesheets().addAll(JFXConstants.CSS_DEFAULT, ThemeLoader.getCssUri(themeName.getValue()));
            darkTheme = themeName.getValue().toLowerCase().contains("dark");
        }
    }

    public static boolean isDarkThemeSelected() {
        return darkTheme;
    }

    public void loadDefaultTheme() {
        themeName.setValue(themeList.get(0));
    }
}
