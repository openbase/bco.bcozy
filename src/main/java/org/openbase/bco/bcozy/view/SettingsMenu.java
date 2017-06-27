package org.openbase.bco.bcozy.view;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.openbase.bco.bcozy.BCozy;
import org.openbase.bco.bcozy.controller.SettingsController;
import org.openbase.bco.bcozy.view.mainmenupanes.SettingsPane;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

/**
 * @author vdasilva
 */
public class SettingsMenu extends StackPane {

    /**
     * Application logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsMenu.class);


    @FXML
    TabPane settingsPane;
    //    @FXML Tab settingsTab;
    @FXML
    SettingsPane settings;


    public SettingsMenu() {
        settings = new SettingsPane();
        AnchorPane test;
//        settingsPane = new TabPane();
//        settingsTab = new Tab("Settings");
//        settingsPane.getTabs().addAll(settingsTab);

        try {

            URL url = getClass().getClassLoader().getResource("SettingsMenu.fxml");
            if (url == null) {
                throw new RuntimeException("SettingsMenu.fxml not found");
            }

            FXMLLoader loader = new FXMLLoader(url);
            test = loader.load();
            this.getChildren().addAll(test);

            SettingsController settingsController = (SettingsController) loader.getController();

            settingsController.setSettingsPane(settings);

            settingsController.addSettingsTab();


        } catch (Exception ex) {
            ex.printStackTrace();
            ExceptionPrinter.printHistory("Content could not be loaded", ex, LOGGER);
        }


//        settingsTab.setClosable(false);
//        this.getChildren()‚.addAll(settingsPane);
    }

    public SettingsPane getSettingsPane() {
        return settings;
    }

}
