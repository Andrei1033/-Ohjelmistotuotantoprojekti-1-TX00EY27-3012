package com.example.app.Localisation;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public final class FontSupport {

    private FontSupport() {}

    /**
     * Pick a font that can render the current language.
     * Falls back through a chain of common system fonts.
     */
    public static Font font(double size, FontWeight weight) {
        return Font.font(pickFamily(), weight, size);
    }

    public static String pickFamily() {
        return switch (i18n.getCurrentLang().code()) {
            case "ja" -> firstAvailable(
                    "Noto Sans JP", "Yu Gothic UI", "Meiryo", "Hiragino Sans", "System");
            case "ar" -> firstAvailable(
                    "Noto Naskh Arabic", "Segoe UI", "Tahoma", "Geeza Pro", "System");
            case "ru" -> firstAvailable(
                    "Noto Sans", "Segoe UI", "Arial", "System");
            default -> "System"; // fi, en
        };
    }

    private static String firstAvailable(String... families) {
        List<String> installed = Font.getFamilies();
        for (String f : families) {
            if (installed.contains(f)) return f;
        }
        return "System";
    }

    /** Right-to-left scripts need this flag; Layout mirrors itself. */
    public static boolean isRtl() {
        return "ar".equals(i18n.getCurrentLang().code());
    }
}