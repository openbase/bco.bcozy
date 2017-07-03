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
import org.openbase.bco.bcozy.jp.JPLanguage;
import org.openbase.jps.core.JPService;
import org.openbase.jps.preset.JPDebugMode;
import org.openbase.jul.exception.FatalImplementationErrorException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
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
    
     private static void showError(Thread t, Throwable ex) {
        if (Platform.isFxApplicationThread()) {
            ExceptionPrinter.printHistory(new FatalImplementationErrorException("Uncauchted exception has been occurt!", "FxApplicationThread", ex), LOGGER);
        } else {
            ExceptionPrinter.printHistory(new FatalImplementationErrorException("Uncauchted exception has been occurt!", t.getName(), ex), LOGGER);
        }
    }
    
}