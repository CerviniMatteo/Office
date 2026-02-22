package com.unimib.assignment3.UI.view.components;

import com.unimib.assignment3.UI.view.controller.TaskCreationFormController;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Priority;
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

        // Make this component expand to fill the available overlay/content area so
        // the inner StackPane (loaded FXML) gets full width/height and the background
        // Rectangle bound to it will cover the whole screen.
        this.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        this.setFillWidth(true);
        VBox.setVgrow(root, Priority.ALWAYS);

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
