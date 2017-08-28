package org.openbase.bco.bcozy.util;

import java.util.Locale;

/**
 * A language with given locale and name, eg "de_DE - Deutsch"
 *
 * @author vdasilva
 */
public class Language {

    private final Locale locale;
    private final String name;

    public Language(Locale locale, String name) {
        this.locale = locale;
        this.name = name;
    }

    public Locale getLocale() {
        return locale;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return locale + " - " + name;
    }
}
