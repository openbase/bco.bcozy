package org.openbase.bco.bcozy.util;

import org.openbase.jul.exception.printer.ExceptionPrinter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Loads available languages from {@code languages/available_languages}.
 *
 * @author vdasilva
 */
public class FileLanguagesDetector implements LanguageLoader {
    private static final org.slf4j.Logger LOGGER = getLogger(FileLanguagesDetector.class);

    @Override
    public List<Language> getLanguages() {
        try {
            List<String> lines = loadLines();
            List<Locale> locales = toLocales(lines);
            List<Language> languages = new ArrayList<>();
            for (Locale locale : locales) {
                languages.add(new Language(locale));
            }
            return languages;
        } catch (URISyntaxException | IOException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER);
        }
        return Collections.emptyList();
    }


    private List<String> loadLines() throws URISyntaxException, IOException {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("languages/available_languages");
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            reader.lines().forEach(lines::add);
        }

        return lines;
    }

    private List<Locale> toLocales(List<String> lines) {
        List<Locale> locales = new ArrayList<>(lines.size());
        for (String line : lines) {
            if (line.contains("_")) {
                locales.add(new Locale(line.split("_")[0], line.split("_")[1]));
            } else {
                locales.add(new Locale(line));
            }
        }
        return locales;
    }
}
