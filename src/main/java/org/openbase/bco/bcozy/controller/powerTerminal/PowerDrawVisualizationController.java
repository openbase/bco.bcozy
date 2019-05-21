package org.openbase.bco.bcozy.controller.powerTerminal;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.ResourceBundle;

public class PowerDrawVisualizationController implements Initializable {

    @FXML
    WebView webView;
    private WebEngine webEngine;


    @Override
    public void initialize (URL url, ResourceBundle rb) {
        webEngine = webView.getEngine();
        webEngine.load("192.168.75.100:9999");
    }
}