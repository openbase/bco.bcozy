package org.openbase.bco.bcozy.util;

import java.io.File;
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
public class Languages {


    /**
     * Holder-Class for Threadsafe-Singleton.
     */
    private static final class InstanceHolder {
        static final Languages INSTANCE = new Languages();
    }

    public static Languages getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private final Language english = fromLocale(Locale.US);

    private final List<Language> languages;

    /**
     * Initializes Languages from available property-files.
     */
    private Languages() {
        List<Locale> bundles = getLocalesFromBundles();
        this.languages = bundles.stream().map(this::fromLocale).collect(Collectors.toList());
    }

    /**
     * Creates a Language form the given Locale.
     *
     * @param locale
     * @return
     */
    private Language fromLocale(Locale locale) {
        return new Language(locale, locale.getDisplayLanguage(locale));
    }

    /**
     * Creates Locales from each property-file available in "/languages".
     *
     * @return
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
     *
     * @return
     */
    private List<String> getFileMatches() {
        List<String> matches = new LinkedList<>();
        Pattern pattern = Pattern.compile("languages_(\\w+[_\\w+])\\.properties");
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
     *
     * @param folder
     * @return
     */
    private String[] getResourceFolderFiles(String folder) {
        ClassLoader loader = Languages.class.getClassLoader();
        URL url = loader.getResource(folder);

        File[] files = Optional.ofNullable(url)
                .map(URL::getPath)
                .map(File::new)
                .map(File::listFiles)
                .orElse(new File[0]);

        String[] names = new String[files.length];

        for (int i = 0; i < files.length; i++) {
            names[i] = files[i].getName();
        }
        return names;
    }

    /**
     * Returns the available Language with the locale or defaults to "en_US"
     *
     * @param locale
     * @return
     */
    public Language get(Locale locale) {
        return languages.stream().filter(l -> Objects.equals(l.getLocale(), locale))
                .findAny().orElse(english);
    }

    /**
     * Returns the available Language with the name or defaults to "en_US"
     *
     * @param name
     * @return
     */
    public Language get(String name) {
        return languages.stream().filter(l -> Objects.equals(l.getName(), name))
                .findAny().orElse(english);
    }

    public List<Language> get() {
        return languages;
    }
}
