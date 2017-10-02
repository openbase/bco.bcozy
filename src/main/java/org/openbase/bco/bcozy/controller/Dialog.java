package org.openbase.bco.bcozy.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.openbase.bco.bcozy.model.LanguageSelection;

import java.util.Optional;

/**
 * @author vdasilva
 */
public class Dialog {

    public static boolean getConfirmation(String message) {
        Optional<ButtonType> selection = new Alert(Alert.AlertType.CONFIRMATION, message).showAndWait();

        return selection.isPresent() && selection.get().equals(ButtonType.OK);
    }

    public static boolean getConfirmation(String identifier, Object... args) {
        String message = LanguageSelection.getLocalized(identifier, args);

        return getConfirmation(message);
    }


}
