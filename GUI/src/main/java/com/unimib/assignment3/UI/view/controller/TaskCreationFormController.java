package com.unimib.assignment3.UI.view.controller;

import com.unimib.assignment3.UI.model.controller.TaskController;
import com.unimib.assignment3.UI.model.dto.DescriptionTaskDTO;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import static com.unimib.assignment3.UI.view.components.AlertDialog.showAlert;
import static com.unimib.assignment3.UI.view.utils.BlurHelper.applyBlur;
import static com.unimib.assignment3.UI.view.utils.BlurHelper.removeBlur;

public class TaskCreationFormController {

    @FXML
    private StackPane root;

    @FXML
    private Rectangle backgroundLayer;

    @FXML
    private TextField descriptionField;

    @FXML
    private Button submitButton;

    private final TaskController taskController = new TaskController();

    // Callback invoked on successful creation
    private Runnable onSuccess;

    @FXML
    private void initialize() {
        backgroundLayer.widthProperty().bind(root.widthProperty());
        backgroundLayer.heightProperty().bind(root.heightProperty());

        applyBlur(backgroundLayer, 5);

        submitButton.setOnAction(e -> handleSubmit());
    }

    private void handleSubmit() {
        String description = descriptionField.getText();

        if (description == null || description.trim().isEmpty()) {
            showAlert("Validation", "Description cannot be empty");
            return;
        }

        DescriptionTaskDTO payload = new DescriptionTaskDTO(description.trim());

        Task<String> pushTask = taskController.createTask(payload);

        pushTask.setOnSucceeded(ev -> {
            showAlert("Success", "Task created");
            removeBlur(backgroundLayer);
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