package com.unimib.assignment3.model.controller.base;

import javafx.concurrent.Task;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestTemplate;

import static com.unimib.assignment3.view.components.impl.custom.AlertDialog.showAlert;

public abstract class BaseRestController {

    protected static final RestTemplate rest = createRestTemplate();

    private static RestTemplate createRestTemplate() {

        return new RestTemplate();
    }

    // --- GET ---
    @Nullable
    protected <T> T getOne(String url, Class<T> type) {
        try {
            return rest.getForObject(url, type);
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
            return null;
        }
    }

    @Nullable
    protected <T> T getMany(String url, ParameterizedTypeReference<T> type) {
        try {
            ResponseEntity<T> response = rest.exchange(url, HttpMethod.GET, null, type);
            return response.getBody();
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
            return null;
        }
    }

    // --- POST ---
    protected <T> Task<T> postTask(String url, Object payload) {
        return new Task<>() {
            @Override
            protected T call() {
                try {
                    HttpEntity<Object> entity = new HttpEntity<>(payload);
                    ResponseEntity<T> response = rest.exchange(url, HttpMethod.POST, entity, (Class<T>) String.class);
                    return response.getBody();
                } catch (Exception e) {
                    showAlert("Error", e.getMessage());
                    return null;
                }
            }
        };
    }

}