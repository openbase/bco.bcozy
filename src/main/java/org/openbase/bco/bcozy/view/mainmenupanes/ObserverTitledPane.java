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
package org.openbase.bco.bcozy.view.mainmenupanes;

import javafx.scene.control.TitledPane;
import org.openbase.bco.bcozy.model.LanguageSelection;
import org.openbase.bco.bcozy.view.Constants;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hoestreich
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>.
 */
public class ObserverTitledPane extends TitledPane implements Observer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObserverTitledPane.class);

    private final String identifier;

    private ResourceBundle languageBundle = ResourceBundle.getBundle(Constants.LANGUAGE_RESOURCE_BUNDLE, Locale.getDefault());

    /**
     * Constructor to create a button which is capable of observing language changes in the application.
     *
     * @param languageString The language string which combined with the actual language selection determines the
     * buttons label
     */
    public ObserverTitledPane(final String languageString) {
        super();
        this.identifier = languageString;
        setTitle(languageString);
        LanguageSelection.getInstance().addObserver(this);
    }

    @Override
    public void update(final Observable observable, final Object arg) {
        languageBundle = ResourceBundle.getBundle(Constants.LANGUAGE_RESOURCE_BUNDLE, Locale.getDefault());
        super.setText(languageBundle.getString(this.identifier));
    }

    public void setTitle(final String languageString) {
        try {
            super.setText(languageBundle.getString(identifier));
        } catch (MissingResourceException ex) {
            ExceptionPrinter.printHistory("Could resolve LanguageString[" + languageString + "]!", ex, LOGGER, LogLevel.WARN);
            super.setText(identifier);
        }
    }
}
