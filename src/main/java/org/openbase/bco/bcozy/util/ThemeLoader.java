package org.openbase.bco.bcozy.util;

import org.openbase.jul.exception.printer.ExceptionPrinter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Loads available languages from {@code languages/available_languages}.
 *
 * @author vdasilva
 */
public class ThemeLoader {

    public static final String CSS_SUFFIX = ".css";
    public static final String CSS_FOLDER = "css";
    public static final String THEME_REGISTRY = CSS_FOLDER + "/themes";

    private static final org.slf4j.Logger LOGGER = getLogger(ThemeLoader.class);

    public static List<String> loadThemes() {
        final List<String> lines = new ArrayList<>();
        final InputStream stream = ThemeLoader.class.getClassLoader().getResourceAsStream(THEME_REGISTRY);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            reader.lines().forEach(lines::add);
        } catch (final IOException ex) {
            ExceptionPrinter.printHistory("Could not load themes!", ex, LOGGER);
        }
        return lines;
    }

    public static String getCssUri(final String theme) {
        return CSS_FOLDER + "/" + theme.toLowerCase() + CSS_SUFFIX;
    }
}
