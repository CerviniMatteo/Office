package com.unimib.assignment3.UI.controller;

import com.unimib.assignment3.UI.dto.ChangeTaskStateRequestDTO;
import com.unimib.assignment3.UI.dto.TaskDTO;
import javafx.concurrent.Task;
import java.net.http.HttpResponse;
import static com.unimib.assignment3.UI.rest.TaskRest.*;

public class TaskController {

    public Task<TaskDTO> changeTaskState(ChangeTaskStateRequestDTO payload) {
        return new Task<>() {
            @Override
            protected TaskDTO call() {

                HttpResponse<String> response = postChangeTaskState(payload);

                if (response == null) {
                    throw new RuntimeException("No response from server");
                }

                if (response.statusCode() != 200) {
                    throw new RuntimeException(
                            "HTTP error " + response.statusCode()
                    );
                }

                TaskDTO taskDTO = fetchTask(payload.getTaskId());

                if (taskDTO == null) {
                    throw new RuntimeException("Failed to fetch updated task");
                }

                return taskDTO;
            }
        };
    }

    public Task<TaskDTO> resetTaskState(Long taskId) {
        return new Task<>() {
            @Override
            protected TaskDTO call() {

                HttpResponse<String> response = postResetTaskState(taskId);

                if (response == null) {
                    throw new RuntimeException("No response from server");
                }

                if (response.statusCode() != 200) {
                    throw new RuntimeException(
                            "HTTP error " + response.statusCode()
                    );
                }

                TaskDTO taskDTO = fetchTask(taskId);

                if (taskDTO == null) {
                    throw new RuntimeException("Failed to fetch updated task");
                }

                return taskDTO;
            }
        };
    }
}
