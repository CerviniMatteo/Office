package com.unimib.assignment3.UI.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.unimib.assignment3.UI.dto.ChangeTaskStateRequestDTO;
import com.unimib.assignment3.UI.dto.TaskDTO;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import static com.unimib.assignment3.UI.utils.AlertDialog.showAlert;

public class TaskRest {
    private static final String BASE_ENDPOINT = "http://localhost:8080/tasks";
    private static final ObjectMapper mapper = new ObjectMapper();
    private final static HttpClient client = HttpClient.newHttpClient();

    public static List<TaskDTO> fetchTasks() {
        try {
            HttpResponse <String> response = createGetRequest(BASE_ENDPOINT);
            mapper.registerModule(new JavaTimeModule());
            return mapper.readValue(response.body(), new TypeReference<List<TaskDTO>>() {
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

    private static HttpResponse<String> createGetRequest(String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(endpoint))
                .GET()
                .build();

        return  client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static HttpResponse<String> createPostRequest(String endpoint, Object bodyArgument){
        try {
            String payload = mapper.writeValueAsString(bodyArgument);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(endpoint))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            return null;
        }
    }
}
