package com.unimib.assignment3.UI.model.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.unimib.assignment3.UI.model.dto.*;
import com.unimib.assignment3.UI.utils.RestHelper;
import javafx.concurrent.Task;
import org.springframework.lang.Nullable;

import java.net.http.HttpResponse;
import java.util.List;
import static com.unimib.assignment3.UI.constants.Rest.BASE_ENDPOINT;
import static com.unimib.assignment3.UI.view.components.impl.custom.AlertDialog.showAlert;
import static com.unimib.assignment3.UI.utils.RestHelper.createPostRequest;

public class TaskRestController {
    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());


    public List<TaskDTO> fetchTasks() {
        try {
            HttpResponse<String> response = RestHelper.createGetRequest(BASE_ENDPOINT + "/all");
            return mapper.readValue(response.body(), new TypeReference<>() {});
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
            return List.of();
        }
    }

    public TaskDTO fetchTask(Long taskId) {
        try {
            HttpResponse<String> response = RestHelper.createGetRequest(BASE_ENDPOINT + "/" + taskId);
            return mapper.readValue(response.body(), TaskDTO.class);
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
            return null;
        }
    }

    public Task<String> changeTaskState(ChangeTaskStateRequestDTO payload) {
        return new Task<>() {
            @Override
            protected String call() {
                HttpResponse<String> response = createPostRequest(BASE_ENDPOINT + "/changeState", payload);
                return checkResponse(response);
            }
        };
    }

    public Task<String> startTask(StartTaskRequestDTO payload) {
        return new Task<>() {
            @Override
            protected String call() {
                HttpResponse<String> response = createPostRequest(BASE_ENDPOINT + "/startTask", payload);
                return checkResponse(response);
            }
        };
    }

    public Task<String> resetTaskState(Long taskId) {
        return new Task<>() {
            @Override
            protected String call() {
                HttpResponse<String> response = createPostRequest(BASE_ENDPOINT + "/resetState", taskId);
                return checkResponse(response);
            }
        };
    }

    public Task<String> acceptTask(AcceptTaskRequestDTO payload) {
        return new Task<>() {
            @Override
            protected String call(){
                HttpResponse<String> response = createPostRequest(BASE_ENDPOINT + "/acceptTask", payload);
                return checkResponse(response);
            }
        };
    }

    public Task<String> createTask(TaskDTO payload) {
        return new Task<>() {
            @Override
            protected String call(){
                HttpResponse<String> response = createPostRequest(BASE_ENDPOINT + "/createTask", payload);
                return checkResponse(response);
            }
        };
    }

    @Nullable
    private String checkResponse(HttpResponse<String> response) {
        if (response == null) {
            showAlert("Error", "No response from server");
            return null;
        }

        int status = response.statusCode();

        // Check if status is not 2xx
        if (status < 200 || status >= 300) {
            showAlert("Error", status + " - " + response.body());
            return null;
        }

        // Success
        return response.body();
    }
}
