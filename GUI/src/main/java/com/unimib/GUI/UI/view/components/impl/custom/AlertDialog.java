package com.unimib.GUI.UI.view.components.impl.custom;

import com.unimib.GUI.UI.state.ApplicationStateManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.Locale;

public class AlertDialog {

    public static void showAlert(String title, String message) {
        Runnable showDialog = () -> {
            StackPane overlay = new StackPane();
            overlay.getStyleClass().add("task-creation-overlay");
            overlay.setPickOnBounds(true);
            overlay.sceneProperty().addListener((observable, oldScene, newScene) -> {
                if (oldScene != null) {
                    overlay.prefWidthProperty().unbind();
                    overlay.prefHeightProperty().unbind();
                }
                if (newScene != null) {
                    overlay.prefWidthProperty().bind(newScene.widthProperty());
                    overlay.prefHeightProperty().bind(newScene.heightProperty());
                }
            });

            Label titleLbl = new Label(title);
            String normalizedTitle = title == null ? "" : title.toLowerCase(Locale.ROOT);
            if (normalizedTitle.contains("success")) {
                titleLbl.getStyleClass().add("dialog-title-success");
            } else {
                titleLbl.getStyleClass().add("dialog-title-failure");
            }

            Label messageLbl = new Label(message);
            messageLbl.setWrapText(true);
            messageLbl.getStyleClass().add("dialog-message");

            Button okBtn = new Button("OK");
            okBtn.getStyleClass().add("styled-btn");

            VBox dialogBox = new VBox(12, titleLbl, messageLbl, okBtn);
            dialogBox.getStyleClass().add("semi-transparent-bg");
            dialogBox.setAlignment(Pos.CENTER);
            dialogBox.setMaxWidth(420);
            dialogBox.setPadding(new Insets(22));
            dialogBox.setOnMouseClicked(event -> event.consume());

            okBtn.setOnAction(event ->
                    ApplicationStateManager.getInstance().removeWindow(overlay)
            );

            overlay.getChildren().add(dialogBox);
            StackPane.setAlignment(dialogBox, Pos.CENTER);

            ApplicationStateManager.getInstance().addPopUp(overlay);
        };

        if (Platform.isFxApplicationThread()) {
            showDialog.run();
            return;
        }
        Platform.runLater(showDialog);
    }
}