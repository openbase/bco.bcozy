package org.openbase.bco.bcozy.controller.powerTerminal;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebErrorEvent;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InvalidStateException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;


public class PowerDrawVisualizationController implements Initializable {

    @FXML
    WebView webView;
    private WebEngine webEngine;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void initialize (URL url, ResourceBundle rb) {
        webEngine = webView.getEngine();
        webEngine.setOnAlert((WebEvent<String> event) -> {
            ExceptionPrinter.printHistory(new InvalidStateException("Webengine alert detected!", new CouldNotPerformException(event.toString())), logger);
        });
        webEngine.setOnError((WebErrorEvent event) -> {
            ExceptionPrinter.printHistory(new InvalidStateException("Webengine error detected!", new CouldNotPerformException(event.toString())), logger);
        });
        webEngine.load("http://192.168.75.100:9999");
    }
}
