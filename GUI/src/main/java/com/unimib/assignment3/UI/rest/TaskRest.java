package com.unimib.assignment3.UI.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.unimib.assignment3.UI.dto.ChangeTaskStateRequestDTO;
import com.unimib.assignment3.UI.dto.TaskDTO;
import java.net.http.HttpResponse;
import java.util.List;
import static com.unimib.assignment3.UI.utils.AlertDialog.showAlert;
import static com.unimib.assignment3.UI.utils.RestHelper.createGetRequest;
import static com.unimib.assignment3.UI.utils.RestHelper.createPostRequest;

public class TaskRest {
    private static final String BASE_ENDPOINT = "http://localhost:8080/tasks";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<TaskDTO> fetchTasks() {
        try {
            HttpResponse <String> response = createGetRequest(BASE_ENDPOINT);
            mapper.registerModule(new JavaTimeModule());
            return mapper.readValue(response.body(), new TypeReference<>() {
            });
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
            return null;
        }
    }

    public static TaskDTO fetchTask(Long taskId) {
        try {
            HttpResponse<String> response = createGetRequest(BASE_ENDPOINT + "/" + taskId);
            mapper.registerModule(new JavaTimeModule());
            return mapper.readValue(response.body(), TaskDTO.class);

        } catch (Exception e) {
            showAlert("Error", e.getMessage());
            return null;
        }
    }
    public static HttpResponse<String> postChangeTaskState(ChangeTaskStateRequestDTO changeTaskStateRequestDTO) {
        try {
            return createPostRequest(BASE_ENDPOINT + "/changeState", changeTaskStateRequestDTO);
        } catch (Exception e) {
            return null;
        }
    }

    public static HttpResponse<String> postResetTaskState(Long taskId){
        try {
            return createPostRequest(BASE_ENDPOINT + "/resetState", taskId);
        } catch (Exception e) {
            return null;
        }
    }
}
