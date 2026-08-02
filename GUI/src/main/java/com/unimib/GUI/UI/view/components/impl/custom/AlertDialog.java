package com.unimib.GUI.UI.view.components.impl.custom;

import com.unimib.GUI.UI.state.ApplicationStateManager;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;

public class AlertDialog {

    public static void showAlert(String title, String message) {
        Runnable showDialog = () -> {
            StackPane overlay = new StackPane();
            overlay.getStyleClass().add("task-creation-overlay");
            overlay.setPickOnBounds(true);
            overlay.sceneProperty().addListener((_, oldScene, newScene) -> {
                if (oldScene != null) {
                    overlay.prefWidthProperty().unbind();
                    overlay.prefHeightProperty().unbind();
                }
                if (newScene != null) {
                    overlay.prefWidthProperty().bind(newScene.widthProperty());
                    overlay.prefHeightProperty().bind(newScene.heightProperty());
                }
            });

            AlertDialogBox dialogBox = new AlertDialogBox(
                    title,
                    message,
                    () -> ApplicationStateManager.getInstance().removeWindow(overlay)
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