package com.example.app.Localisation;

import javafx.beans.value.ChangeListener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class I18nTest {
    private i18n.Lang originalLanguage;

    @BeforeEach
    void setUp() {
        originalLanguage = i18n.getCurrentLang();
        i18n.setLanguage(i18n.FI);
    }

    @AfterEach
    void tearDown() {
        i18n.setLanguage(originalLanguage);
    }

    @ParameterizedTest
    @CsvSource({
            "fi, fi-FI, Kirjaudu ulos",
            "en, en-EN, Log out",
            "ru, ru-RU, Выйти",
            "ja, ja-JP, ログアウト",
            "ar, ar-AR, تسجيل الخروج"
    })
    void supportedLanguageLoadsItsTranslationAndLocale(String code, String tag, String logout) {
        i18n.Lang language = i18n.SUPPORTED.stream()
                .filter(lang -> lang.code().equals(code)).findFirst().orElseThrow();

        i18n.setLanguage(language);

        assertEquals(language, i18n.getCurrentLang());
        assertEquals(language, i18n.currentLangProperty().get());
        assertEquals(Locale.forLanguageTag(tag), i18n.getLocale());
        assertEquals(logout, i18n.t("app.logout"));
    }

    @Test
    void translationFormatsArgumentsAfterLanguageChange() {
        assertEquals("Sinulla on 3 kurssia tällä lukukaudella.", i18n.t("student.courseCount", 3));
        i18n.setLanguage(i18n.EN);
        assertEquals("You have 2 courses this semester.", i18n.t("student.courseCount", 2));
    }

    @Test
    void missingTranslationReturnsKeyMarker() {
        assertEquals("!test.missing.key!", i18n.t("test.missing.key"));
    }

    @Test
    void nullLanguageKeepsCurrentLanguageAndBundle() {
        i18n.setLanguage(i18n.EN);
        i18n.setLanguage(null);
        assertEquals(i18n.EN, i18n.getCurrentLang());
        assertEquals("Log out", i18n.t("app.logout"));
    }

    @Test
    void equalLanguageDoesNotNotifyListeners() {
        AtomicInteger changes = new AtomicInteger();
        ChangeListener<i18n.Lang> listener = (property, oldValue, newValue) -> changes.incrementAndGet();
        i18n.currentLangProperty().addListener(listener);
        try {
            i18n.setLanguage(new i18n.Lang("fi", "Suomi"));
            assertEquals(0, changes.get());
            assertEquals("Kirjaudu ulos", i18n.t("app.logout"));
        } finally {
            i18n.currentLangProperty().removeListener(listener);
        }
    }

    @Test
    void listenerSeesNewTranslationWhenLanguageChanges() {
        AtomicReference<String> observedTranslation = new AtomicReference<>();
        ChangeListener<i18n.Lang> listener = (property, oldValue, newValue) ->
                observedTranslation.set(i18n.t("app.logout"));
        i18n.currentLangProperty().addListener(listener);
        try {
            i18n.setLanguage(i18n.EN);
            assertEquals("Log out", observedTranslation.get());
        } finally {
            i18n.currentLangProperty().removeListener(listener);
        }
    }

    @Test
    void unsupportedLanguageThrowsWithoutChangingState() {
        assertThrows(IllegalArgumentException.class,
                () -> i18n.setLanguage(new i18n.Lang("de", "Deutsch")));
        assertEquals(i18n.FI, i18n.getCurrentLang());
        assertEquals(Locale.forLanguageTag("fi-FI"), i18n.getLocale());
        assertEquals("Kirjaudu ulos", i18n.t("app.logout"));
    }
}
