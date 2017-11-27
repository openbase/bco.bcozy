package org.openbase.bco.bcozy.util;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to hold available languages.
 *
 * @author vdasilva
 */
public final class Languages {

    /**
     * Application Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Languages.class);

    /**
     * Holder-Class for Threadsafe-Singleton.
     */
    private static final class InstanceHolder {
        static final Languages INSTANCE = new Languages();
    }

    public static Languages getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * Fallback-Language, defaults to {@link Locale#US}.
     */
    private final Language fallback = new Language(Locale.US);

    /**
     * Loaded Languages.
     */
    private final List<Language> languages;

    /**
     * Initializes Languages from available property-files.
     */
    Languages() {
        this.languages = new FileLanguagesDetector().getLanguages();
    }

    /**
     * Returns the available Language with the locale or current {@link #fallback}.
     */
    @Nonnull
    public Language get(Locale locale) {
        return languages.stream().filter(l -> Objects.equals(l.getLocale(), locale))
                .findAny().orElse(fallback);
    }

    /**
     * Returns the available Language with the name or current {@link #fallback}.
     */
    @Nonnull
    public Language get(String name) {
        return languages.stream().filter(l -> Objects.equals(l.getName(), name))
                .findAny().orElse(fallback);
    }

    /**
     * Returns all available Languages.
     */
    @Nonnull
    public List<Language> get() {
        return languages;
    }
}
