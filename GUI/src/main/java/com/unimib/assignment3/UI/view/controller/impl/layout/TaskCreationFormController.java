package com.unimib.assignment3.UI.view.controller.impl.layout;

import com.unimib.assignment3.UI.model.controller.TaskRestController;
import com.unimib.assignment3.UI.model.dto.DescriptionTaskDTO;
import com.unimib.assignment3.UI.view.controller.abstr.DefaultController;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import static com.unimib.assignment3.UI.view.components.impl.custom.AlertDialog.showAlert;

public class TaskCreationFormController implements DefaultController{

    @FXML
    private TextField descriptionField;

    @FXML
    private Button submitButton;

    private final TaskRestController taskRestController = new TaskRestController();

    // Callback invoked on successful creation
    private Runnable onSuccess;

    @FXML
    private void initialize() {
        submitButton.setOnAction(e -> handleSubmit());
    }

    private void handleSubmit() {
        String description = descriptionField.getText();

        if (description == null || description.trim().isEmpty()) {
            showAlert("Validation", "Description cannot be empty");
            return;
        }

        DescriptionTaskDTO payload = new DescriptionTaskDTO(description.trim());

        Task<String> pushTask = taskRestController.createTask(payload);

        pushTask.setOnSucceeded(ev -> {
            showAlert("Success", "Task created");
            if (onSuccess != null) {
                onSuccess.run();
            }
        });

        pushTask.setOnFailed(ev -> showAlert("Error",
                pushTask.getException() == null ? "Unknown error" : pushTask.getException().getMessage()));

        new Thread(pushTask).start();
    }

    public void setOnSuccess(Runnable onSuccess) {
        this.onSuccess = onSuccess;
    }
}