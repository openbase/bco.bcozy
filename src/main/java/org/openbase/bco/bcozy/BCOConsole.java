package org.openbase.bco.bcozy;

import org.openbase.bco.app.cloud.connector.CloudConnectorAppRemote;
import org.openbase.bco.authentication.lib.SessionManager;
import org.openbase.bco.dal.remote.DALRemote;
import org.openbase.bco.registry.lib.BCO;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jps.core.JPService;
import org.openbase.jps.preset.JPDebugMode;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InvalidStateException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Console;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class BCOConsole {

    public static final String APP_NAME = DALRemote.class.getSimpleName();
    private static final Logger LOGGER = LoggerFactory.getLogger(DALRemote.class);


    private BCOConsole() throws CouldNotPerformException, InterruptedException {

        Console console = System.console();
        if (console == null) {
            System.out.println("Couldn't get Console instance");
            System.exit(0);
        }

        System.out.println("");
        System.out.println("Welcome to the bco console, connect to bco... ");
        Registries.waitForData();
        Registries.waitUntilReady();
        System.out.println("connected");
        System.out.println("");
        System.out.println("Login required");
        try {
            SessionManager.getInstance().login(Registries.getUnitRegistry().getUserUnitIdByUserName(console.readLine("user: ")), new String(console.readPassword("password: ")));
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory("Login not possible!", ex, LOGGER);
        }
        System.out.println("");
        System.out.println("available commands:");
        System.out.println("");
        System.out.println("passwd - change the password of a given user");
        System.out.println("cloud connect - connect a user with the bco cloud service");
        System.out.println("cloud disconnect - disconnect a user from the bco cloud service");
        System.out.println("exit, quit, logout - close this console");
        System.out.println("register user - creates a new user account");
        System.out.println("");
        System.out.println("");

        mainloop:
        while (!Thread.interrupted()) {
            System.out.println("please type a command and press enter...");
            try {
                switch (console.readLine()) {
                    case "logout":
                    case "quit":
                    case "exit":
                        SessionManager.getInstance().logout();
                        break mainloop;
                    case "register user":
                        String newUser = console.readLine("user: ");
                        String newUserPwd = new String(console.readPassword("new password: "));
                        String newUserPwdConfirm = new String(console.readPassword("confirm new password: "));
                        System.out.println("");
                        if (!newUserPwd.equals(newUserPwdConfirm)) {
                            System.err.println("match failed!");
                            continue;
                        }
                        SessionManager.getInstance().registerUser(Registries.getUnitRegistry().getUserUnitIdByUserName(newUser), newUserPwd,false);
                        break;
                    case "passwd":
                        String user = console.readLine("user:");
                        String oldPwd = new String(console.readPassword("old password: "));
                        String newPwd = new String(console.readPassword("new password: "));
                        String newPwdConfirm = new String(console.readPassword("confirm new password:"));
                        System.out.println("");

                        if (!newPwd.equals(newPwdConfirm)) {
                            System.err.println("match failed!");
                            continue;
                        }
                        SessionManager.getInstance().changeCredentials(Registries.getUnitRegistry().getUserUnitIdByUserName(user), oldPwd, newPwd);
                        break;
                    case "cloud connect":
                        final CloudConnectorAppRemote cloudConnectorRemote = new CloudConnectorAppRemote();
                        System.out.println("For connecting your accound with the bco cloud connector a new cloud user password is needed.");
                        System.out.println("You need this password for example again to pair the google cloud with the bco cloud service.");
                        System.out.println("Please choose a strong password to protect the remote access of your home!");
                        String cloudPwd = new String(console.readPassword("your new cloud password:"));
                        String cloudPwdConfirm = new String(console.readPassword("confirm your new cloud password:"));
                        System.out.println("");

                        if (!cloudPwd.equals(cloudPwdConfirm)) {
                            throw new InvalidStateException("match failed!");
                        }
                        cloudConnectorRemote.register(cloudPwd).get(1, TimeUnit.MINUTES);
                        break;
                    case "cloud disconnect":
                        cloudDisconnect(console);
                        break;
                }
                System.out.println("successful");
            } catch (CouldNotPerformException | ExecutionException | TimeoutException ex) {
                ExceptionPrinter.printHistory(ex, LOGGER);
            }
        }
        System.exit(0);
    }

    private void cloudDisconnect(Console console) throws CouldNotPerformException, InterruptedException, TimeoutException, ExecutionException {
        final CloudConnectorAppRemote cloudConnectorRemote = new CloudConnectorAppRemote();
        cloudConnectorRemote.remove();
    }


    public static void main(String[] args) {
        BCO.printLogo();

        /* Setup JPService */
        JPService.setApplicationName(APP_NAME);
        JPService.registerProperty(JPDebugMode.class);
        JPService.parseAndExitOnError(args);

        try {
            new BCOConsole();
        } catch (CouldNotPerformException ex) {
            // just exit
        } catch (InterruptedException ex) {
            ExceptionPrinter.printHistoryAndReturnThrowable(ex, LOGGER, LogLevel.ERROR);
        }
    }
}
