package com.unimib.assignment3.UI.utils;

import java.net.http.HttpClient;

public class RestHelper {
    public static void get() {
        HttpClient httpClient = HttpClient.newBuilder().build();
    }

    public static void post() {}
}
