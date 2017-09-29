/**
 * ==================================================================
 *
 * This file is part of org.openbase.bco.bcozy.
 *
 * org.openbase.bco.bcozy is free software: you can redistribute it and modify
 * it under the terms of the GNU General Public License (Version 3)
 * as published by the Free Software Foundation.
 *
 * org.openbase.bco.bcozy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with org.openbase.bco.bcozy. If not, see <http://www.gnu.org/licenses/>.
 * ==================================================================
 */
package org.openbase.bco.bcozy;

import javafx.application.Platform;

import static org.openbase.bco.bcozy.BCozy.APP_NAME;

import org.openbase.bco.authentication.lib.jp.JPCredentialsDirectory;
import org.openbase.bco.authentication.lib.jp.JPAuthentication;
import org.openbase.bco.authentication.lib.jp.JPInitializeCredentials;
import org.openbase.bco.bcozy.jp.JPLanguage;
import org.openbase.bco.bcozy.view.LoadingPane;
import org.openbase.jps.core.JPService;
import org.openbase.jps.preset.JPDebugMode;
import org.openbase.jul.exception.FatalImplementationErrorException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.openbase.jul.extension.rsb.com.jp.JPRSBHost;
import org.openbase.jul.extension.rsb.com.jp.JPRSBPort;
import org.openbase.jul.extension.rsb.com.jp.JPRSBThreadPooling;
import org.openbase.jul.extension.rsb.com.jp.JPRSBTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class BCozyLauncher {

    /**
     * Application launcher logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BCozyLauncher.class);

    /**
     * Main Method starting JavaFX Environment.
     *
     * @param args Arguments from command line.
     */
    public static void main(final String... args) {
        LOGGER.info("Start " + APP_NAME + "...");

        /* Setup JPService */
        JPService.setApplicationName(APP_NAME);
        JPService.registerProperty(JPDebugMode.class);
        JPService.registerProperty(JPLanguage.class);
        JPService.registerProperty(JPInitializeCredentials.class);
        JPService.registerProperty(JPCredentialsDirectory.class);
        JPService.registerProperty(JPRSBThreadPooling.class);
        JPService.registerProperty(JPRSBHost.class);
        JPService.registerProperty(JPRSBPort.class);
        JPService.registerProperty(JPRSBTransport.class);
        JPService.registerProperty(JPAuthentication.class);
        try {
            JPService.parseAndExitOnError(args);
            Thread.setDefaultUncaughtExceptionHandler(BCozyLauncher::showError);
            BCozy.launch(BCozy.class, args);
        } catch (IllegalStateException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER, LogLevel.ERROR);
            LOGGER.info(APP_NAME + " finished unexpected.");
        }
        LOGGER.info(APP_NAME + " finished.");
    }

    private static int errorCounter = 0;

    private static void showError(Thread t, Throwable ex) {
        errorCounter++;

        if (Platform.isFxApplicationThread()) {
            ExceptionPrinter.printHistory(new FatalImplementationErrorException("Uncaught exception has "
                    + "occured!", "FxApplicationThread", ex), LOGGER);
        } else {
            ExceptionPrinter.printHistory(new FatalImplementationErrorException("Uncaught exception has "
                    + "occured!", t.getName(), ex), LOGGER);
        }

        // check if error counter is to high
        if (errorCounter == 100) {

            if (Platform.isFxApplicationThread()) {
                printShutdownErrorMessage();
            } else {
                Platform.runLater(() -> {
                    printShutdownErrorMessage();
                });
            }

            new Thread() {
                @Override
                public void run() {
                    try {
                        // wait until user feedback is given.
                        Thread.sleep(1000);
                    } catch (InterruptedException ex1) {
                        // just continue because exit is called anyway.
                    }
                    System.exit(100);
                }
            }.start();
            return;

        }
    }

    private static void printShutdownErrorMessage() {
        LOGGER.error("To many fatal errors occured! Exit bcozy...");
        try {
            LoadingPane.getInstance().error("To many fatal errors occured!\nApplication is shutting down ");
        } catch (NotAvailableException ex1) {
            // could not inform user about shutdown
        }
    }
}
