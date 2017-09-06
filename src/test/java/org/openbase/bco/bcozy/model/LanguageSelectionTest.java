package org.openbase.bco.bcozy.model;

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
        String message = LanguageSelection.getLocalized("deleteError", "");

        Assert.assertEquals("Es ist leider ein Fehler aufgetreten: ", message);
    }


}