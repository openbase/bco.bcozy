package org.openbase.bco.bcozy.util;

import org.openbase.jul.exception.printer.ExceptionPrinter;

import javax.annotation.Nonnull;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 * Doesn't work, if packaged as jar.
 * Should be reworked.
 *
 * @author vdasilva
 */
public class BundleLanguagesDetector implements LanguagesDetector {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(BundleLanguagesDetector.class);

    private static final String BUNDLE_FOLDER = "languages";

    @Override
    public List<Language> getLanguages() {
        List<Locale> bundles = getLocalesFromBundles();
        return bundles.stream().map(Language::new).collect(Collectors.toList());
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

        final List<String> files = getResourceFolderFiles(BUNDLE_FOLDER);
        if (files.isEmpty()) {
            files.addAll(getResourceFolderFilesWithStream(BUNDLE_FOLDER));
        }

        for (String file : files) {
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
        ClassLoader loader = this.getClass().getClassLoader();
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

}
