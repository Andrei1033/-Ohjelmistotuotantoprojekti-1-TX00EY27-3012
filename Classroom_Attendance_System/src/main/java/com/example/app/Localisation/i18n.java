
package com.example.app.Localisation;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class i18n {

    public record Lang(String code, String label) {}

    public static final Lang FI = new Lang("fi", "Suomi");
    public static final Lang EN = new Lang("en", "English");
    public static final Lang RU = new Lang("ru", "Русский");
    public static final Lang JA = new Lang("ja", "日本語");
    public static final Lang AR = new Lang("ar", "العربية");

    public static final List<Lang> SUPPORTED =
            List.of(FI, EN, RU, JA, AR);

    // Files are located directly inside src/main/resources
    private static final String BASE = "MessagesBundle";

    // Finnish is always the initial language
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

        if (lang == null) return;

        if (lang.equals(current.get())) return;

        ResourceBundle newBundle = load(lang);

        bundle = newBundle;
        current.set(lang);

        System.out.println(
                "[i18n] Language changed to: " + lang.code()
        );
    }

    public static String t(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            System.err.println(
                    "[i18n] Missing translation: " + key
            );
            return "!" + key + "!";
        }
    }

    public static String t(String key, Object... args) {
        return java.text.MessageFormat.format(t(key), args);
    }

    private static ResourceBundle load(Lang lang) {

        String localeTag = switch (lang.code()) {
            case "fi" -> "fi-FI";
            case "en" -> "en-EN";
            case "ru" -> "ru-RU";
            case "ja" -> "ja-JP";
            case "ar" -> "ar-AR";
            default -> throw new IllegalArgumentException(
                    "Unsupported language: " + lang.code()
            );
        };

        Locale locale = Locale.forLanguageTag(localeTag);

        try {
            ResourceBundle result = ResourceBundle.getBundle(
                    BASE,
                    locale,
                    Thread.currentThread().getContextClassLoader(),
                    ResourceBundle.Control.getNoFallbackControl(
                            ResourceBundle.Control.FORMAT_PROPERTIES
                    )
            );

            System.out.println(
                    "[i18n] Loaded bundle: " + result.getLocale()
            );

            return result;

        } catch (MissingResourceException e) {

            throw new IllegalStateException(
                    "Cannot load translation for: " + localeTag
                            + ". Check src/main/resources/",
                    e
            );
        }
    }
}
