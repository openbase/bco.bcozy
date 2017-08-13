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
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import org.openbase.bco.bcozy.model.LanguageSelection;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by hoestreich on 1/2/16.
 *
 * @author vdasilva
 */
@DefaultProperty("identifier")
public class ObserverButton extends Button implements Observer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObserverButton.class);

    @FXML
    private SimpleStringProperty identifier = new SimpleStringProperty();
    private ResourceBundle languageBundle = ResourceBundle
            .getBundle(Constants.LANGUAGE_RESOURCE_BUNDLE, Locale.getDefault());

    /**
     * Constructor to create a button which is capable of observing language changes in the application.
     * The identifier needs to be set with {@link #setIdentifier(String)}.
     */
    public ObserverButton() {
        identifier.addListener((observable, oldValue, newValue) -> update(null, null));
        LanguageSelection.getInstance().addObserver(this);
    }

    /**
     * Constructor to create a button which is capable of observing language changes in the application.
     *
     * @param languageString The language string which combined with the actual language selection determines the
     *                       buttons label
     */
    public ObserverButton(final String languageString) {
        this();
        this.setIdentifier(languageString);
    }

    /**
     * Constructor to create a button which is capable of observing language changes in the application.
     *
     * @param languageString The language string which combined with the actual language selection determines the
     *                       buttons label
     * @param graphic        the graphic which should be displayed next to the label
     */
    public ObserverButton(final String languageString, final Node graphic) {
        this(languageString);
        super.setGraphic(graphic);
    }

    @Override
    public void update(final Observable observable, final Object arg) {
        if (getIdentifier() == null || getIdentifier().isEmpty()) {
            return;
        }

        try {
            languageBundle = ResourceBundle.getBundle(Constants.LANGUAGE_RESOURCE_BUNDLE, Locale.getDefault());
            super.setText(languageBundle.getString(this.getIdentifier()));
        } catch (MissingResourceException ex) {
            ExceptionPrinter.printHistory("Could not resolve Identifier[" + getIdentifier() + "]", ex, LOGGER,
                    LogLevel.WARN);
            super.setText(getIdentifier());
        }
    }

    public String getIdentifier() {
        return identifier.get();
    }

    public StringProperty identifierProperty() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier.set(identifier != null ? identifier.trim() : null);
    }
}
