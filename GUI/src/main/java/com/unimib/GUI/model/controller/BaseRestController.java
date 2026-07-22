package com.unimib.GUI.model.controller;

import com.unimib.GUI.UI.view.components.impl.custom.AlertDialog;
import javafx.concurrent.Task;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public abstract class BaseRestController {

    protected final RestTemplate rest = new RestTemplate();

    protected <T> T getOne(String url, Class<T> responseType) {
        try {
            return rest.getForObject(url, responseType);
        } catch (Exception e) {
            AlertDialog.showAlert("Error", e.getMessage());
            throw new RuntimeException("GET request failed for " + url, e);
        }
    }

    protected <T> T getMany(String url, ParameterizedTypeReference<T> responseType) {
        try {
            ResponseEntity<T> response = rest.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    responseType
            );

            return response.getBody();

        } catch (Exception e) {
            AlertDialog.showAlert("Error", e.getMessage());
            throw new RuntimeException("GET request failed for " + url, e);
        }
    }

    protected <T, R> Task<R> post(String url, T payload, Class<R> responseType) {

        return new Task<>() {
            @Override
            protected R call() {

                try {
                    HttpEntity<T> entity = new HttpEntity<>(payload);

                    ResponseEntity<R> response = rest.exchange(
                            url,
                            HttpMethod.POST,
                            entity,
                            responseType
                    );

                    return response.getBody();

                } catch (Exception e) {
                    AlertDialog.showAlert("Error", e.getMessage());
                    throw new RuntimeException("POST request failed for " + url, e);
                }
            }
        };
    }
}