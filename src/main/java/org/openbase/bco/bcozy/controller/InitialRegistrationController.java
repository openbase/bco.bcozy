package org.openbase.bco.bcozy.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.util.function.Consumer;

/**
 * @author vdasilva
 */
public class InitialRegistrationController {

    private final Consumer<Boolean> onFinish;
    public JFXTextField userNameInput;
    public JFXPasswordField repeatPasswordField;
    public JFXButton registrationButton;
    public JFXTextField SessionKeyInput;


    public InitialRegistrationController(Consumer<Boolean> onFinish) {
        this.onFinish = onFinish;
    }

    @FXML
    public void initialize() {
        registrationButton.setOnAction(e -> register());
    }

    private void register() {
        //TODO initial register

        //TODO return Success
        onFinish.accept(true);
    }
}
