package com.unimib.assignment3.UI.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.unimib.assignment3.UI.dto.AcceptTaskRequestDTO;
import com.unimib.assignment3.UI.dto.ChangeTaskStateRequestDTO;
import com.unimib.assignment3.UI.dto.TaskDTO;
import com.unimib.assignment3.UI.utils.RestHelper;

import java.net.http.HttpResponse;
import java.util.List;
import static com.unimib.assignment3.UI.utils.AlertDialog.showAlert;

public class TaskRest {
    private static final String BASE_ENDPOINT = "http://localhost:8080/tasks";
    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public static List<TaskDTO> fetchTasks() {
        try {
            HttpResponse<String> response = RestHelper.createGetRequest(BASE_ENDPOINT);
            return mapper.readValue(response.body(), new TypeReference<>() {});
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
            return List.of(); // Return empty list instead of null
        }
    }

    public static TaskDTO fetchTask(Long taskId) {
        try {
            HttpResponse<String> response = RestHelper.createGetRequest(BASE_ENDPOINT + "/" + taskId);
            return mapper.readValue(response.body(), TaskDTO.class);
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
            return null;
        }
    }

    public static HttpResponse<String> postChangeTaskState(ChangeTaskStateRequestDTO dto) {
        return RestHelper.createPostRequest(BASE_ENDPOINT + "/changeState", dto);
    }

    public static HttpResponse<String> postResetTaskState(Long taskId) {
        return RestHelper.createPostRequest(BASE_ENDPOINT + "/resetState", taskId);
    }

    public static HttpResponse<String> postAcceptTask(AcceptTaskRequestDTO dto) {
        return RestHelper.createPostRequest(BASE_ENDPOINT + "/acceptTask", dto);
    }
}
