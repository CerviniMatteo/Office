package com.unimib.assignment3.UI.model.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.unimib.assignment3.UI.model.dto.AcceptTaskRequestDTO;
import com.unimib.assignment3.UI.model.dto.StartTaskRequestDTO;
import com.unimib.assignment3.UI.model.dto.ChangeTaskStateRequestDTO;
import com.unimib.assignment3.UI.model.dto.TaskDTO;
import com.unimib.assignment3.UI.utils.RestHelper;
import javafx.concurrent.Task;
import java.net.http.HttpResponse;
import java.util.List;
import static com.unimib.assignment3.UI.constants.Rest.BASE_ENDPOINT;
import static com.unimib.assignment3.UI.view.components.AlertDialog.showAlert;
import static com.unimib.assignment3.UI.utils.RestHelper.createPostRequest;

public class TaskController {
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

    public Task<String> createTask(TaskDTO payload) {
        return new Task<>() {
            @Override
            protected String call() {
                HttpResponse<String> response = createPostRequest(BASE_ENDPOINT + "/createTask", payload);

                if (response == null) {
                    showAlert("Error", "No response from server");
                    return null;
                }

                if (response.statusCode() != 200) {
                    showAlert("Error", response.statusCode() + " - " + response.body());
                    return null;
                }
                return response.body();
            }
        };
    }

    public Task<String> changeTaskState(ChangeTaskStateRequestDTO payload) {
        return new Task<>() {
            @Override
            protected String call() {

                HttpResponse<String> response = createPostRequest(BASE_ENDPOINT + "/changeState", payload);

                if (response == null) {
                    showAlert("Error", "No response from server");
                    return null;
                }

                if (response.statusCode() != 200) {
                    showAlert("Error", response.statusCode() + " - " + response.body());
                    return null;
                }
                return response.body();
            }
        };
    }

    public Task<String> startTask(StartTaskRequestDTO payload) {
        return new Task<>() {
            @Override
            protected String call() {

                HttpResponse<String> response = createPostRequest(BASE_ENDPOINT + "/startTask", payload);

                if (response == null) {
                    showAlert("Error", "No response from server");
                    return null;
                }

                if (response.statusCode() != 200) {
                    showAlert("Error", response.statusCode() + " - " + response.body());
                    return null;
                }
                return response.body();
            }
        };
    }

    public Task<String> resetTaskState(Long taskId) {
        return new Task<>() {
            @Override
            protected String call() {

                HttpResponse<String> response = createPostRequest(BASE_ENDPOINT + "/resetState", taskId);

                if (response == null) {
                    showAlert("Error", "No response from server");
                    return null;
                }

                if (response.statusCode() != 200) {
                    showAlert("Error", response.statusCode() + " - " + response.body());
                    return null;
                }

                return response.body();
            }
        };
    }

    public Task<String> acceptTask(AcceptTaskRequestDTO payload) {
        return new Task<>() {
            @Override
            protected String call(){

                HttpResponse<String> response = createPostRequest(BASE_ENDPOINT + "/acceptTask", payload);
                if (response == null) {
                    showAlert("Error", "No response from server");
                    return null;
                }

                if (response.statusCode() != 200) {
                    showAlert("Error", response.statusCode() + " - " + response.body());
                    return null;
                }

                return response.body();
            }
        };
    }
}
