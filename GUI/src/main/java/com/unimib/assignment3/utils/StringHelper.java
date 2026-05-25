package com.unimib.assignment3.utils;


public class StringHelper {
    public static String replaceUnderscores(String string) {
        return string.trim()
                .replace("_", " ")
                .replaceAll("\\s+", " ")
                .toUpperCase();
    }

    public static String replaceSpaces(String string) {
        return string.replaceAll("\\n", "").replace(" ", "").trim();
    }
}
