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

import javafx.scene.Node;
import javafx.scene.control.Label;
import org.openbase.bco.bcozy.model.LanguageSelection;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import org.openbase.bco.bcozy.view.mainmenupanes.UserPane;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by hoestreich on 1/2/16.
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class ObserverLabel extends Label implements Observer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObserverLabel.class);

    private String identifier;
    private ResourceBundle languageBundle = ResourceBundle
            .getBundle(Constants.LANGUAGE_RESOURCE_BUNDLE, Locale.getDefault());

    /**
     * Constructor to create a label which is capable of observing language changes in the application.
     *
     * @param languageString The language string which combined with the actual language selection determines the
     * actual label
     */
    public ObserverLabel(final String identifier) {
        super();
        LanguageSelection.getInstance().addObserver(this);
        setIdentifier(identifier);
    }

    /**
     * Constructor to create a label which is capable of observing language changes in the application.
     *
     * @param languageString The language string which combined with the actual language selection determines the
     * actual label
     * @param graphic the graphic which should be displayed next to the label
     */
    public ObserverLabel(final String identifier, final Node graphic) {
        super();
        super.setGraphic(graphic);
        LanguageSelection.getInstance().addObserver(this);
        setIdentifier(identifier);
    }

    @Override
    public void update(final Observable observable, final Object arg) {
        setIdentifier(identifier);
    }

    /**
     * Sets the new identifier for this ObserverLabel.
     *
     * @param identifier identifier
     */
    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
        languageBundle = ResourceBundle.getBundle(Constants.LANGUAGE_RESOURCE_BUNDLE, Locale.getDefault());
        try {
            super.setText(languageBundle.getString(this.identifier));
        } catch (MissingResourceException ex) {
            ExceptionPrinter.printHistory("Could not resolve Identifier["+identifier+"]", ex, LOGGER);
            super.setText(this.identifier);
        }
    }
}
