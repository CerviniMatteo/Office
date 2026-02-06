package com.unimib.assignment3.UI.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.unimib.assignment3.UI.utils.RestHelper;
import javafx.concurrent.Task;

import java.net.http.HttpResponse;
import static com.unimib.assignment3.UI.components.AlertDialog.showAlert;

public class LoginController {
    private static final String BASE_ENDPOINT = "http://localhost:8080/login";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static Task<String> login(String email) {
        return new Task<>(){
            @Override
            protected String call() {
                try {
                    HttpResponse <String> response = RestHelper.createGetRequest(BASE_ENDPOINT+ "/" + email);
                    mapper.registerModule(new JavaTimeModule());
                    return mapper.readValue(response.body(), String.class);
                } catch (Exception e) {
                    showAlert("Error", e.getMessage());
                    return null;
                }
            }
        };
    }
}
