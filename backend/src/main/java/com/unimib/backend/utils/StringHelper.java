package com.unimib.backend.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class StringHelper {
    public static String hashString(String str){
        return DigestUtils.sha256Hex(str);
    }
}
