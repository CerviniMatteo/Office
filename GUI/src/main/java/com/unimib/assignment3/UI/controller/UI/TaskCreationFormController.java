package com.unimib.assignment3.UI.controller.UI;

import com.unimib.assignment3.UI.controller.rest.TaskController;
import com.unimib.assignment3.UI.dto.TaskDTO;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import static com.unimib.assignment3.UI.components.AlertDialog.showAlert;

public class TaskCreationFormController {

    @FXML
    private TextField descriptionField;

    @FXML
    private Button submitButton;

    private final TaskController taskController = new TaskController();

    // Callback invoked on successful creation
    private Runnable onSuccess;

    @FXML
    private void initialize() {
        if (submitButton != null) {
            submitButton.setOnAction(e -> handleSubmit());
        }
    }

    private void handleSubmit() {
        if (descriptionField == null) return;
        String description = descriptionField.getText();
        if (description == null || description.trim().isEmpty()) {
            showAlert("Validation", "Description cannot be empty");
            return;
        }

        TaskDTO payload = new TaskDTO(null, description.trim(), null, null, null, null);
        System.out.println("[TaskCreationForm] Submitting task: " + payload.description());

        Task<String> pushTask = taskController.createTask(payload);
        pushTask.setOnSucceeded(ev -> {
            String response = pushTask.getValue();
            System.out.println("[TaskCreationForm] createTask succeeded, response=" + response);
            // Treat any successful POST (task succeeded) as success for UI flow, even if response body is empty
            showAlert("Success", "Task created");
            if (onSuccess != null) onSuccess.run();
        });
        pushTask.setOnFailed(ev -> {
            System.out.println("[TaskCreationForm] createTask failed: " + pushTask.getException());
            showAlert("Error", pushTask.getException() == null ? "Unknown error" : pushTask.getException().getMessage());
        });
        new Thread(pushTask).start();
    }

    public void setOnSuccess(Runnable onSuccess) {
        this.onSuccess = onSuccess;
    }
}
