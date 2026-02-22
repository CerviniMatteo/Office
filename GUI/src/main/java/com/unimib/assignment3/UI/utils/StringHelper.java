package com.unimib.assignment3.UI.utils;


public class StringHelper {
    public static String replaceUnderscores(String string) {
        return string.trim()
                .replace("_", " ")
                .replaceAll("\\s+", " ")
                .toUpperCase();
    }
}
