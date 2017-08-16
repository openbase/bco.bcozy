package org.openbase.bco.bcozy.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

/**
 * @author vdasilva
 */
public class FxmlController {

    /**
     * Default Controller necessary for loading fxml files.
     */
    public FxmlController() {

    }

    @FXML
    private Text actiontarget;

    @FXML
    protected void handleSubmitButtonAction(ActionEvent event) {

        actiontarget.setText("Sign in button pressed");
        System.out.println("KLICK REGISTRIERT!");
    }
}
