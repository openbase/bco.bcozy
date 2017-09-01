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
package org.openbase.bco.bcozy.model;

import org.openbase.bco.bcozy.view.Constants;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;

/**
 * Created by hoestreich on 1/2/16.
 *
 * @author vdasilva
 */
public final class LanguageSelection extends Observable {

    private static final Logger LOGGER = LoggerFactory.getLogger(LanguageSelection.class);


    /**
     * Singleton instance.
     */
    private static LanguageSelection instance;

    /**
     * Private constructor to deny manual instantiation.
     */
    private LanguageSelection() {
    }

    /**
     * Singleton Pattern.
     *
     * @return the singleton instance of the language selection observable
     */
    public static LanguageSelection getInstance() {
        synchronized (LanguageSelection.class) {
            if (LanguageSelection.instance == null) {
                LanguageSelection.instance = new LanguageSelection();
            }
        }
        return LanguageSelection.instance;
    }

    /**
     * Setter method to allow changing the language and notifying all gui elements to adapt afterwards.
     *
     * @param selectedLocale the new locale which should be set as default.
     */
    public void setSelectedLocale(final Locale selectedLocale) {
        this.setChanged();
        Locale.setDefault(selectedLocale);
        notifyObservers(Locale.getDefault());
    }

    /**
     * Retunrns the localized text for the given identifier.
     *
     * @param identifier the identifier
     * @return the localized string
     */
    public static String getLocalized(String identifier) {
        Objects.requireNonNull(identifier);

        String text;
        try {
            text = ResourceBundle.getBundle(Constants.LANGUAGE_RESOURCE_BUNDLE, Locale.getDefault()).getString(identifier);
        } catch (MissingResourceException ex) {
            ExceptionPrinter.printHistory("Could not resolve Identifier[" + identifier + "]", ex, LOGGER, LogLevel.WARN);
            text = identifier;
        }

        return text;
    }

    /**
     * Adds an Listener to the given identifier.
     * The Listener is called, each time the language changed and on attach.
     *
     * @param identifier               the identifier
     * @param onLanguageChangeListener the listener for this identifier
     */
    public static void addObserverFor(String identifier, OnLanguageChangeListener onLanguageChangeListener) {
        getInstance().addObserver((o, arg) -> onLanguageChangeListener.onLanguageChange(Locale.getDefault(),
                getLocalized(identifier)));
        onLanguageChangeListener.onLanguageChange(Locale.getDefault(), getLocalized(identifier));
    }

    /**
     * Adds an Listener to the given identifier.
     * The Listener is called, each time the language changed and on attach.
     *
     * @param identifier      the identifier
     * @param newTextConsumer the listener for this identifier
     */
    public static void addObserverFor(String identifier, Consumer<String> newTextConsumer) {
        getInstance().addObserver((o, arg) -> newTextConsumer.accept(getLocalized(identifier)));
        newTextConsumer.accept(getLocalized(identifier));
    }

    public interface OnLanguageChangeListener {
        void onLanguageChange(Locale locale, String text);
    }

}
