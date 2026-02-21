package com.unimib.assignment3.UI.view.components;

import com.unimib.assignment3.UI.view.controller.TaskDetailsController;
import com.unimib.assignment3.UI.model.dto.TaskDTO;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.Objects;

public class TaskDetailsWindow extends BorderPane {

    private final TaskDetailsController controller;

    public TaskDetailsWindow() {
        controller = new TaskDetailsController();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/TaskDetails.fxml"));
        loader.setRoot(this);
        loader.setController(controller);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load TaskDetails.fxml", e);
        }

        this.getStyleClass().addAll("task-card");

        this.setFocusTraversable(false);
        this.setPickOnBounds(false);

        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                String css = Objects.requireNonNull(getClass().getResource("/styles/app.css")).toExternalForm();
                if (!newScene.getStylesheets().contains(css)) {
                    newScene.getStylesheets().add(css);
                }
            }
        });
    }

    public void setTask(TaskDTO task) {
        if (task == null) return;
        controller.setTask(task);
    }
}
