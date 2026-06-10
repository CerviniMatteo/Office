package com.unimib.GUI.view.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StringHelper {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static String localDateTimeFormatter(LocalDateTime dateTime) {
        return dateTime.format(FORMATTER);
    }

    public static String hashString(String str){
        return DigestUtils.sha256Hex(str);
    }
}