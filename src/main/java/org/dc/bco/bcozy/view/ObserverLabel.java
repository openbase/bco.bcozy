/**
 * ==================================================================
 *
 * This file is part of org.dc.bco.bcozy.
 *
 * org.dc.bco.bcozy is free software: you can redistribute it and modify
 * it under the terms of the GNU General Public License (Version 3)
 * as published by the Free Software Foundation.
 *
 * org.dc.bco.bcozy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with org.dc.bco.bcozy. If not, see <http://www.gnu.org/licenses/>.
 * ==================================================================
 */
package org.dc.bco.bcozy.view;

import javafx.scene.Node;
import javafx.scene.control.Label;
import org.dc.bco.bcozy.model.LanguageSelection;

import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

/**
 * Created by hoestreich on 1/2/16.
 */
public class ObserverLabel extends Label implements Observer {

    private String identifier;
    private ResourceBundle languageBundle = ResourceBundle
            .getBundle(Constants.LANGUAGE_RESOURCE_BUNDLE, Locale.getDefault());

    /**
     * Constructor to create a label which is capable of observing language changes in the application.
     * @param languageString The language string which combined with the actual language selection determines the
     *                       actual label
     */
    public ObserverLabel(final String languageString) {
        super();
        this.identifier = languageString;
        super.setText(languageBundle.getString(this.identifier));
        LanguageSelection.getInstance().addObserver(this);
    }

    /**
     * Constructor to create a label which is capable of observing language changes in the application.
     * @param languageString The language string which combined with the actual language selection determines the
     *                       actual label
     * @param graphic the graphic which should be displayed next to the label
     */
    public ObserverLabel(final String languageString, final Node graphic) {
        super();
        this.identifier = languageString;
        super.setText(languageBundle.getString(this.identifier));
        super.setGraphic(graphic);
        LanguageSelection.getInstance().addObserver(this);
    }

    @Override
    public void update(final Observable observable, final Object arg) {
        languageBundle = ResourceBundle.getBundle(Constants.LANGUAGE_RESOURCE_BUNDLE, Locale.getDefault());
        super.setText(languageBundle.getString(this.identifier));
    }

    /**
     * Sets the new identifier for this ObserverLabel.
     * @param identifier identifier
     */
    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
        languageBundle = ResourceBundle.getBundle(Constants.LANGUAGE_RESOURCE_BUNDLE, Locale.getDefault());
        super.setText(languageBundle.getString(this.identifier));
    }
}
