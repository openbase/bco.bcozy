package org.openbase.bco.bcozy.util;

import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Class to hold available languages.
 *
 * @author vdasilva
 */
public final class Languages {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(Languages.class);

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
        List<Locale> bundles = getLocalesFromBundles();
        this.languages = bundles.stream().map(Language::new).collect(Collectors.toList());
    }

    /**
     * Creates Locales from each property-file available in "/languages".
     */
    private List<Locale> getLocalesFromBundles() {
        List<String> matches = getFileMatches();
        List<Locale> locales = new ArrayList<>(matches.size());
        for (String match : matches) {
            if (match.contains("_")) {
                locales.add(new Locale(match.split("_")[0], match.split("_")[1]));
            } else {
                locales.add(new Locale(match));
            }
        }
        return locales;
    }

    /**
     * Gets all Language-Identifier (eg 'de', 'de_DE'), which have available property-files in "/languages".
     */
    @Nonnull
    private List<String> getFileMatches() {
        List<String> matches = new LinkedList<>();
        Pattern pattern = Pattern.compile("languages_(\\w+[_\\w+])\\.properties");

        final List<String> files = getResourceFolderFiles("languages");
        if (files.isEmpty()) {
            files.addAll(getResourceFolderFilesWithStream("languages"));
        }

        for (String file : getResourceFolderFiles("languages")) {
            Matcher matcher = pattern.matcher(file);
            if (matcher.find()) {
                matches.add(matcher.group(1));
            }
        }
        return matches;
    }

    /**
     * Gets all File-Names from an Resource-Directory.
     */
    @Nonnull
    private List<String> getResourceFolderFiles(String folder) {
        ClassLoader loader = Languages.class.getClassLoader();
        URL url = loader.getResource(folder);

        File[] files = Optional.ofNullable(url)
                .map(URL::getPath)
                .map(File::new)
                .map(File::listFiles)
                .orElse(new File[0]);

        List<String> names = new ArrayList<>();

        for (File file : files) {
            names.add(file.getName());
        }
        return names;
    }

    /**
     * Gets all File-Names from an Resource-Directory, alternative Way
     */
    private List<String> getResourceFolderFilesWithStream(String folder) {
        List<String> filenames = new ArrayList<>();

        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(folder);
             BufferedReader br = new BufferedReader(new InputStreamReader(in))) {

            for (String resource = br.readLine(); resource != null; resource = br.readLine()) {
                filenames.add(resource);
            }
        } catch (IOException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER);
        }

        return filenames;
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
