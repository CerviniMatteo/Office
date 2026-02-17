package com.unimib.assignment3.UI.components;

import com.unimib.assignment3.UI.controller.UI.TaskCreationFormController;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Objects;

public class TaskCreationForm extends VBox {

    private final TaskCreationFormController controller;

    public TaskCreationForm() {
        super(8);

        Node root;
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/components/TaskCreationForm.fxml")));
            root = loader.load();
            controller = loader.getController();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load TaskCreationForm.fxml", e);
        }

        this.getChildren().add(root);

        setAlignment(Pos.CENTER);

        // ensure css is available on scene
        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                String css = Objects.requireNonNull(getClass().getResource("/styles/app.css")).toExternalForm();
                if (!newScene.getStylesheets().contains(css)) {
                    newScene.getStylesheets().add(css);
                }
            }
        });
    }

    public void setOnSuccess(Runnable onSuccess) {
        if (controller != null) controller.setOnSuccess(onSuccess);
    }
}
