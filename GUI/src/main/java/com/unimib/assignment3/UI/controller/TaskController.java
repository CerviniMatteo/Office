package com.unimib.assignment3.UI.controller;

import com.unimib.assignment3.UI.dto.ChangeTaskStateRequestDTO;
import javafx.concurrent.Task;
import java.net.http.HttpResponse;
import static com.unimib.assignment3.UI.rest.TaskRest.*;

public class TaskController {

    public Task<String> changeTaskState(ChangeTaskStateRequestDTO payload) {
        return new Task<>() {
            @Override
            protected String call() {

                HttpResponse<String> response = postChangeTaskState(payload);

                if (response == null) {
                    throw new RuntimeException("No response from server");
                }

                if (response.statusCode() != 200) {
                    throw new RuntimeException(
                            "HTTP error " + response.statusCode()
                    );
                }
                return response.body();
            }
        };
    }

    public Task<String> resetTaskState(Long taskId) {
        return new Task<>() {
            @Override
            protected String call() {

                HttpResponse<String> response = postResetTaskState(taskId);

                if (response == null) {
                    throw new RuntimeException("No response from server");
                }

                if (response.statusCode() != 200) {
                    throw new RuntimeException(
                            "HTTP error " + response.statusCode()
                    );
                }

                return response.body();
            }
        };
    }
}
