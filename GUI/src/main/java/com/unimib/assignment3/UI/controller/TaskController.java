package com.unimib.assignment3.UI.controller;

import com.unimib.assignment3.UI.dto.AcceptTaskRequestDTO;
import com.unimib.assignment3.UI.dto.ChangeTaskStateRequestDTO;
import javafx.concurrent.Task;
import java.net.http.HttpResponse;
import static com.unimib.assignment3.UI.rest.TaskRest.*;
import static com.unimib.assignment3.UI.utils.AlertDialog.showAlert;

public class TaskController {

    public Task<String> changeTaskState(ChangeTaskStateRequestDTO payload) {
        return new Task<>() {
            @Override
            protected String call() throws Exception {

                HttpResponse<String> response = postChangeTaskState(payload);

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

                HttpResponse<String> response = postResetTaskState(taskId);

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

                HttpResponse<String> response = postAcceptTask(payload);
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
