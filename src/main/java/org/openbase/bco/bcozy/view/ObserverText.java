/**
 * ==================================================================
 * <p>
 * This file is part of org.openbase.bco.bcozy.
 * <p>
 * org.openbase.bco.bcozy is free software: you can redistribute it and modify
 * it under the terms of the GNU General Public License (Version 3)
 * as published by the Free Software Foundation.
 * <p>
 * org.openbase.bco.bcozy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with org.openbase.bco.bcozy. If not, see <http://www.gnu.org/licenses/>.
 * ==================================================================
 */
package org.openbase.bco.bcozy.view;

import javafx.beans.DefaultProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import org.openbase.bco.bcozy.model.LanguageSelection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Observable;
import java.util.Observer;
import java.util.function.Function;

/**
 * Created by agatting on 13.04.16.
 * @author vdasilva
 */
@DefaultProperty("identifier")
public class ObserverText extends Text implements Observer {

    protected final Logger LOGGER = LoggerFactory.getLogger(ObserverText.class);

    @FXML
    private SimpleStringProperty identifier = new SimpleStringProperty();

    /**
     * Is applied to new text when text is changed.
     */
    private Function<String, String> applyOnNewText = Function.identity();

    public ObserverText() {
        super();
        identifier.addListener((observable, oldValue, newValue) -> update(null, null));
        LanguageSelection.getInstance().addObserver(this);
    }

    /**
     * Constructor to create a text which is capable of observing language changes in the application.
     *
     * @param identifier The language string which combined with the actual language selection determines the
     * actual text
     */
    public ObserverText(final String identifier) {
        this();
        setIdentifier(identifier);
    }

    @Override
    public void update(final Observable observable, final Object arg) {
        update();
    }

    public void update() {
        if (getIdentifier() == null) {
            return;
        }

        String text = LanguageSelection.getLocalized(getIdentifier());

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
        this.update();
    }
}
