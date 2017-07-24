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
package org.openbase.bco.bcozy.jp;

import java.io.File;
import org.openbase.bco.authentication.lib.jp.JPInitializeCredentials;
import org.openbase.bco.authentication.lib.jp.JPResetCredentials;
import org.openbase.jps.core.JPService;
import org.openbase.jps.exception.JPNotAvailableException;
import org.openbase.jps.exception.JPServiceException;
import org.openbase.jps.exception.JPValidationException;
import org.openbase.jps.preset.AbstractJPDirectory;
import org.openbase.jps.preset.JPHelp;
import org.openbase.jps.preset.JPShareDirectory;
import org.openbase.jps.preset.JPVarDirectory;
import org.openbase.jps.tools.FileHandler;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.de">Tamino Huxohl</a>
 */
public class JPBcozyCredentialsDirectory extends AbstractJPDirectory {

    public static FileHandler.ExistenceHandling existenceHandling = FileHandler.ExistenceHandling.Must;
    public static FileHandler.AutoMode autoMode = FileHandler.AutoMode.Off;

    public static final String DEFAULT_CREDENTIALS_PATH = "bco/bcozy/credentials";

    public static final String[] COMMAND_IDENTIFIERS = {"--cr", "--credentials"};

    public JPBcozyCredentialsDirectory() {
        super(COMMAND_IDENTIFIERS, existenceHandling, autoMode);
    }

    @Override
    public File getParentDirectory() throws JPServiceException {
        try {
            if (JPService.getProperty(JPVarDirectory.class).getValue().exists() || JPService.testMode()) {
                return JPService.getProperty(JPVarDirectory.class).getValue();
            }
        } catch (JPNotAvailableException ex) {
            JPService.printError("Could not detect global var directory!", ex);
        }

        try {
            if (JPService.getProperty(JPShareDirectory.class).getValue().exists()) {
                return JPService.getProperty(JPShareDirectory.class).getValue();
            }
        } catch (JPNotAvailableException ex) {
            JPService.printError("Could not detect global share directory!", ex);
        }

        throw new JPServiceException("Could not detect db location!");
    }

    @Override
    protected File getPropertyDefaultValue() {
        return new File(DEFAULT_CREDENTIALS_PATH);
    }

    @Override
    public String getDescription() {
        return "Specifies the credential database directory. Use  " + JPInitializeCredentials.COMMAND_IDENTIFIERS[0] + " to auto create a credential directory.";
    }

    @Override
    public void validate() throws JPValidationException {
        boolean reinitDetected = false;

        try {
            if (JPService.getProperty(JPInitializeCredentials.class).getValue()) {
                setAutoCreateMode(FileHandler.AutoMode.On);
                setExistenceHandling(FileHandler.ExistenceHandling.Must);
                reinitDetected = true;
            }
        } catch (JPServiceException ex) {
            ExceptionPrinter.printHistory(new CouldNotPerformException("Could not access java property!", ex), logger);
        }

        try {
            if (JPService.getProperty(JPResetCredentials.class).getValue()) {
                setAutoCreateMode(FileHandler.AutoMode.On);
                setExistenceHandling(FileHandler.ExistenceHandling.MustBeNew);
                reinitDetected = true;
            }
        } catch (JPServiceException ex) {
            ExceptionPrinter.printHistory(new CouldNotPerformException("Could not access java property!", ex), logger);
        }

        if (!getValue().exists() && !reinitDetected) {
            throw new JPValidationException("Could not detect Credentials[" + getValue().getAbsolutePath() + "]! You can use the argument " + JPInitializeCredentials.COMMAND_IDENTIFIERS[0] + " to initialize a new credential enviroment. Use " + JPHelp.COMMAND_IDENTIFIERS[0] + " to get more options.");
        }

        super.validate();
    }
    
}
