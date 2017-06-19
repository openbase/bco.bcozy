package org.openbase.bco.bcozy.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import org.openbase.bco.bcozy.view.mainmenupanes.SettingsPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public void addSettingsTab() {

        settings = new SettingsPane();


        if (settingsTab != null) {
            System.out.println("NICHT NULL");
        } else {
            System.out.println("DOCH NULL!!");
        }
        settingsTab.setContent(settings);
    }


}
