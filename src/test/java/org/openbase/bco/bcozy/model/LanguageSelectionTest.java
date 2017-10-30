package org.openbase.bco.bcozy.model;

import javafx.beans.property.ReadOnlyStringProperty;
import org.junit.Assert;
import org.junit.Test;

import java.util.Locale;

/**
 * @author vdasilva
 */
public class LanguageSelectionTest {
    @Test
    public void getLocalized() throws Exception {
        LanguageSelection.getInstance().setSelectedLocale(Locale.GERMANY);
        String message = LanguageSelection.getLocalized("deleteErrorWithMessage", "");

        Assert.assertEquals("Es ist leider ein Fehler aufgetreten: ", message);
    }

    @Test
    public void getLocalizedProperty() throws Exception {
        LanguageSelection.getInstance().setSelectedLocale(Locale.GERMANY);
        ReadOnlyStringProperty message = LanguageSelection.getProperty("login");

        Assert.assertEquals("ANMELDEN", message.get());

        LanguageSelection.getInstance().setSelectedLocale(new Locale("en", "US"));

        Assert.assertEquals("LOGIN", message.get());

    }

    //startLogin=ANMELDUNG ÖFFNEN
    @Test
    public void getLocalizedWithRightEncoding() throws Exception {
        LanguageSelection.getInstance().setSelectedLocale(Locale.GERMANY);
        String message = LanguageSelection.getLocalized("startLogin");

        Assert.assertEquals("ANMELDUNG ÖFFNEN", message);
    }
}
