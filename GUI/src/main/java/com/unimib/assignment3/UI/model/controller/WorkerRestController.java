package com.unimib.assignment3.UI.model.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.unimib.assignment3.UI.model.dto.WorkerDTO;
import com.unimib.assignment3.UI.utils.RestHelper;
import javafx.concurrent.Task;

import java.net.http.HttpResponse;

import static com.unimib.assignment3.UI.view.components.impl.custom.AlertDialog.showAlert;

public class WorkerRestController {
    private static final String BASE_ENDPOINT = "http://localhost:8080/employee";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static Task<WorkerDTO> fetchWorker(Long workerId) {
        return new Task<>(){
            @Override
            protected WorkerDTO call() {
                try {
                    HttpResponse<String> response = RestHelper.createGetRequest(BASE_ENDPOINT+ "/" + workerId);
                    mapper.registerModule(new JavaTimeModule());
                    return mapper.readValue(response.body(), WorkerDTO.class);
                } catch (Exception e) {
                    showAlert("Error", e.getMessage());
                    return null;
                }
            }
        };
    }
}
