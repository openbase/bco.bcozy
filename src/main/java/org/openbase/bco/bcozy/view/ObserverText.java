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
package org.openbase.bco.bcozy.view;

import javafx.scene.text.Text;
import org.openbase.bco.bcozy.model.LanguageSelection;

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
 * Created by agatting on 13.04.16.
 */
public class ObserverText extends Text implements Observer {

    protected final Logger LOGGER = LoggerFactory.getLogger(ObserverText.class);

    private String identifier;
    private ResourceBundle languageBundle = ResourceBundle.getBundle(Constants.LANGUAGE_RESOURCE_BUNDLE, Locale.getDefault());

    /**
     * Constructor to create a text which is capable of observing language changes in the application.
     *
     * @param languageString The language string which combined with the actual language selection determines the
     * actual text
     */
    public ObserverText(final String identifier) {
        super();
        setIdentifier(identifier);
        LanguageSelection.getInstance().addObserver(this);
    }

    /**
     * Sets the new identifier for this ObserverText.
     *
     * @param identifier identifier
     */
    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
        languageBundle = ResourceBundle.getBundle(Constants.LANGUAGE_RESOURCE_BUNDLE, Locale.getDefault());

        try {
            super.setText(languageBundle.getString(identifier));
        } catch (MissingResourceException ex) {
            ExceptionPrinter.printHistory("Could not resolve Identifier ["+identifier+"]!", ex, LOGGER, LogLevel.WARN);
            super.setText(identifier);
        }
    }

    @Override
    public void update(final Observable observable, final Object arg) {
        setIdentifier(identifier);
    }
}
