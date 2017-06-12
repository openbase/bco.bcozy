package org.openbase.bco.bcozy.view;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import org.openbase.bco.bcozy.view.mainmenupanes.SettingsPane;

/**
 * @author vdasilva
 */
public class SettingsMenu extends StackPane {

    TabPane settingsPane;
    Tab settingsTab;
    SettingsPane settings;

    public SettingsMenu() {
        settings = new SettingsPane();

        settingsPane = new TabPane();
        settingsTab = new Tab("Settings");
        settingsPane.getTabs().addAll(settingsTab);
        settingsTab.setContent(settings);


        settingsTab.setClosable(false);
        this.getChildren().addAll(settingsPane);
    }

    public SettingsPane getSettingsPane() {
        return settings;
    }
}
