package com.example.app.Localisation;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class i18n {

    public record Lang(String code, String label) {}

    public static final Lang FI = new Lang("fi", "Suomi");
    public static final Lang EN = new Lang("en", "English");
    public static final Lang RU = new Lang("ru", "Русский");
    public static final Lang JA = new Lang("ja", "日本語");
    public static final Lang AR = new Lang("ar", "العربية");

    public static final List<Lang> SUPPORTED = List.of(FI, EN, RU, JA, AR);

    private static final String BASE = "MessagesBundle";
    private static final String PATH = "com/example/app/Localisation/";

    private static final ObjectProperty<Lang> current =
            new SimpleObjectProperty<>(FI);

    private static ResourceBundle bundle = load(FI);

    private i18n() {}

    public static ObjectProperty<Lang> currentLangProperty() {
        return current;
    }

    public static Lang getCurrentLang() {
        return current.get();
    }

    public static void setLanguage(Lang lang) {
        if (lang == null || lang.equals(current.get())) return;
        bundle = load(lang);
        current.set(lang);
    }

    /** Get localized string, or a fallback marker if missing. */
    public static String t(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return "!" + key + "!";
        }
    }

    public static String t(String key, Object... args) {
        return java.text.MessageFormat.format(t(key), args);
    }

    private static ResourceBundle load(Lang lang) {
        Locale locale = Locale.forLanguageTag(lang.code());
        // Prefer no fallback-to-default noise; but do use default bundle as safety net.
        return ResourceBundle.getBundle(
                BASE, locale,
                Thread.currentThread().getContextClassLoader(),
                new Utf8Control());
    }

    /** Custom control so .properties files are read as UTF-8 (Java 8 compat). */
    private static final class Utf8Control extends ResourceBundle.Control {
        @Override
        public ResourceBundle newBundle(String baseName, Locale locale,
                                        String format, ClassLoader loader,
                                        boolean reload) throws IOException {
            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, "properties");
            URL url = loader.getResource(resourceName);
            if (url == null) return null;

            URLConnection conn = url.openConnection();
            if (reload) conn.setUseCaches(false);

            try (InputStream in = conn.getInputStream();
                 InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                return new PropertyResourceBundle(reader);
            }
        }
    }
}