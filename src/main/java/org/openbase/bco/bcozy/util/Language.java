package org.openbase.bco.bcozy.util;

import java.util.Locale;
import java.util.Objects;

/**
 * A language with given locale and name, eg "de_DE - Deutsch"
 *
 * @author vdasilva
 */
public class Language {

    private final Locale locale;
    private final String name;

    /**
     * Creates a Language form the given Locale, with it's localized Display-Language as {@link #name}.
     *
     * @see Locale#getDisplayLanguage()
     */
    public Language(Locale locale) {
        this(locale, locale.getDisplayLanguage(locale));
    }

    /**
     * Creates a Language with the given Locale and Name.
     */
    public Language(Locale locale, String name) {
        this.locale = Objects.requireNonNull(locale);
        this.name = Objects.requireNonNull(name);
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
