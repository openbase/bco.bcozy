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

import javafx.beans.DefaultProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import org.openbase.bco.bcozy.model.LanguageSelection;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.function.Function;

import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hoestreich
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 * @author vdasilva
 */
@DefaultProperty("identifier")
public class ObserverLabel extends Label implements Observer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObserverLabel.class);

    @FXML
    private SimpleStringProperty identifier = new SimpleStringProperty();
    private ResourceBundle languageBundle = ResourceBundle.getBundle(Constants.LANGUAGE_RESOURCE_BUNDLE, Locale.getDefault());


    /**
     * Is applied to new text when text is changed.
     */
    private Function<String, String> applyOnNewText = Function.identity();


    public ObserverLabel() {
        super();
        identifier.addListener((observable, oldValue, newValue) -> update(null, null));
        LanguageSelection.getInstance().addObserver(this);
    }

    /**
     * Constructor to create a label which is capable of observing language changes in the application.
     *
     * @param identifier The language string which combined with the actual language selection determines the
     *                   actual label
     */
    public ObserverLabel(final String identifier) {
        this();
        setIdentifier(identifier);
    }

    /**
     * Constructor to create a label which is capable of observing language changes in the application.
     *
     * @param identifier The language string which combined with the actual language selection determines the
     *                   actual label
     * @param graphic    the graphic which should be displayed next to the label
     */
    public ObserverLabel(final String identifier, final Node graphic) {
        this(identifier);
        super.setGraphic(graphic);
    }

    @Override
    public void update(final Observable observable, final Object arg) {
        if (getIdentifier() == null || getIdentifier().isEmpty()) {
            return;
        }

        String text;
        try {
            languageBundle = ResourceBundle.getBundle(Constants.LANGUAGE_RESOURCE_BUNDLE, Locale.getDefault());

            text = languageBundle.getString(this.getIdentifier());
        } catch (MissingResourceException ex) {
            ExceptionPrinter.printHistory("Could not resolve Identifier[" + getIdentifier() + "]", ex, LOGGER, LogLevel.WARN);
            text = getIdentifier();
        }
        super.setText(applyOnNewText.apply(text));
    }

    /**
     * Sets the new identifier for this ObserverLabel.
     *
     * @param identifier identifier
     */
    public void setIdentifier(String identifier) {
        this.identifier.set(identifier);
    }

    public String getIdentifier() {
        return identifier.get();
    }

    public SimpleStringProperty identifierProperty() {
        return identifier;
    }

    public void setApplyOnNewText(Function<String, String> applyOnNewText) {
        this.applyOnNewText = applyOnNewText != null ? applyOnNewText : Function.identity();
        this.update(null,null);
    }
}
