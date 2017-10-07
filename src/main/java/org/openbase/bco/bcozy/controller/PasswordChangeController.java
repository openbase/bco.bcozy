package org.openbase.bco.bcozy.controller;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.openbase.bco.authentication.lib.SessionManager;
import org.openbase.bco.bcozy.view.InfoPane;
import org.openbase.bco.bcozy.view.ObserverButton;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.RejectedException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author vdasilva
 */
public class PasswordChangeController extends AbstractCurrentUserAwareController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordChangeController.class);


    private static final String PASSWORD_FIELD_WRONG_CLASS = "password-field-wrong";


    @FXML
    private PasswordField oldPassword;
    @FXML
    private PasswordField newPassword;
    @FXML
    private PasswordField repeatedPassword;
    @FXML
    private ObserverButton savePassword;

    private Consumer<Boolean> onPasswordChange = success -> {
    };


    public void initialize() {
        savePassword.setApplyOnNewText(String::toUpperCase);
    }


    @FXML
    private void saveNewPassword() throws InterruptedException {

        clearHints();

        if (!verifyNewPassword()) {
            InfoPane.warn("passwordsNotEqual").hideAfter(Duration.seconds(5));
            clearPasswordFields();

            newPassword.getStyleClass().add(PASSWORD_FIELD_WRONG_CLASS);
            repeatedPassword.getStyleClass().add(PASSWORD_FIELD_WRONG_CLASS);

            onPasswordChange.accept(false);
            return;
        }

        try {
            SessionManager.getInstance().changeCredentials(getUserId(), oldPassword.getText(), newPassword.getText());
            onPasswordChange.accept(true);
            showSuccessMessage();
        } catch (RejectedException rex) {
            oldPassword.getStyleClass().add(PASSWORD_FIELD_WRONG_CLASS);
            onPasswordChange.accept(false);
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER);
            onPasswordChange.accept(false);
        }

        clearPasswordFields();

    }

    private void clearPasswordFields() {
        oldPassword.clear();
        newPassword.clear();
        repeatedPassword.clear();
    }

    private void clearHints() {
        oldPassword.getStyleClass().remove(PASSWORD_FIELD_WRONG_CLASS);
        newPassword.getStyleClass().remove(PASSWORD_FIELD_WRONG_CLASS);
        repeatedPassword.getStyleClass().remove(PASSWORD_FIELD_WRONG_CLASS);
    }

    private boolean verifyNewPassword() {
        return newPassword.getText().equals(repeatedPassword.getText())
                //&& !newPassword.getText().equals("admin")
                ;
    }

    private void showSuccessMessage() {
        InfoPane.info("saveSuccess")
                .backgroundColor(Color.GREEN)
                .hideAfter(Duration.seconds(5));
    }


    public void setOnPasswordChange(Consumer<Boolean> onPasswordChange) {
        this.onPasswordChange = Objects.requireNonNull(onPasswordChange);
    }
}
