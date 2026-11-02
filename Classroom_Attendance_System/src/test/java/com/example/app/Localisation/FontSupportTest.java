package com.example.app.Localisation;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

class FontSupportTest {
    private i18n.Lang originalLanguage;

    @BeforeEach
    void setUp() {
        originalLanguage = i18n.getCurrentLang();
    }

    @AfterEach
    void tearDown() {
        i18n.setLanguage(originalLanguage);
    }

    static Stream<i18n.Lang> languages() {
        return i18n.SUPPORTED.stream();
    }

    static Stream<i18n.Lang> systemFontLanguages() {
        return Stream.of(i18n.FI, i18n.EN);
    }

    static Stream<Arguments> fontPreferences() {
        return Stream.of(
                Arguments.of(i18n.JA, "Noto Sans JP", "Yu Gothic UI"),
                Arguments.of(i18n.AR, "Noto Naskh Arabic", "Segoe UI"),
                Arguments.of(i18n.RU, "Noto Sans", "Segoe UI")
        );
    }

    @ParameterizedTest
    @MethodSource("languages")
    void onlyArabicUsesRightToLeftLayout(i18n.Lang language) {
        i18n.setLanguage(language);
        assertEquals(i18n.AR.equals(language), FontSupport.isRtl());
    }

    @ParameterizedTest
    @MethodSource("systemFontLanguages")
    void finnishAndEnglishUseSystemFont(i18n.Lang language) {
        i18n.setLanguage(language);
        assertEquals("System", FontSupport.pickFamily());
    }

    @ParameterizedTest
    @MethodSource("fontPreferences")
    void preferredFontWinsRegardlessOfInstalledOrder(i18n.Lang language, String preferred, String fallback) {
        i18n.setLanguage(language);
        try (MockedStatic<Font> fonts = mockStatic(Font.class)) {
            fonts.when(Font::getFamilies).thenReturn(List.of(fallback, preferred));
            assertEquals(preferred, FontSupport.pickFamily());
        }
    }

    @ParameterizedTest
    @MethodSource("fontPreferences")
    void usesNextAvailableFontWhenPreferredIsMissing(i18n.Lang language, String preferred, String fallback) {
        i18n.setLanguage(language);
        try (MockedStatic<Font> fonts = mockStatic(Font.class)) {
            fonts.when(Font::getFamilies).thenReturn(List.of("Unrelated font", fallback));
            assertEquals(fallback, FontSupport.pickFamily());
        }
    }

    @ParameterizedTest
    @MethodSource("fontPreferences")
    void usesSystemWhenNoCandidateIsInstalled(i18n.Lang language, String preferred, String fallback) {
        i18n.setLanguage(language);
        try (MockedStatic<Font> fonts = mockStatic(Font.class)) {
            fonts.when(Font::getFamilies).thenReturn(List.of("Unrelated font"));
            assertEquals("System", FontSupport.pickFamily());
        }
    }

    @ParameterizedTest
    @MethodSource("languages")
    void createsFontWithRequestedSizeAndWeight(i18n.Lang language) {
        i18n.setLanguage(language);
        Font expected = Font.font(FontSupport.pickFamily(), FontWeight.BOLD, 18);
        Font actual = FontSupport.font(18, FontWeight.BOLD);
        assertEquals(18, actual.getSize(), 0.001);
        assertEquals(expected.getFamily(), actual.getFamily());
        assertEquals(expected.getStyle(), actual.getStyle());
    }
}
