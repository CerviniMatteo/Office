package com.unimib.GUI.view.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StringFormatter {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static String localDateTimeFormatter(LocalDateTime dateTime) {
        return dateTime.format(FORMATTER);
    }
}