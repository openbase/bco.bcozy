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

/**
 * @author vdasilva
 */
public class PasswordChangeController extends AbstractCurrentUserAwareController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordChangeController.class);


    @FXML
    private PasswordField oldPassword;
    @FXML
    private PasswordField newPassword;
    @FXML
    private PasswordField repeatedPassword;
    @FXML
    private ObserverButton savePassword;

    public void initialize() {
        savePassword.setApplyOnNewText(String::toUpperCase);
    }


    @FXML
    private void saveNewPassword() throws InterruptedException {

        if (!verifyNewPassword()) {
            InfoPane.warn("passwordsNotEqual").hideAfter(Duration.seconds(5));
            clearPasswordFields();
            return;
        }

        try {
            SessionManager.getInstance().changeCredentials(getUserId(), oldPassword.getText(), newPassword.getText());
            showSuccessMessage();

        } catch (RejectedException rex) {
            InfoPane.info("oldPasswordWrong").backgroundColor(Color.RED).hideAfter(Duration.seconds(5));
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER);
        }

        clearPasswordFields();

    }

    private void clearPasswordFields() {
        oldPassword.clear();
        newPassword.clear();
        repeatedPassword.clear();
    }

    private boolean verifyNewPassword() {
        return newPassword.getText().equals(repeatedPassword.getText());
    }

    private void showSuccessMessage() {
        InfoPane.info("saveSuccess")
                .backgroundColor(Color.GREEN)
                .hideAfter(Duration.seconds(5));
    }


}
