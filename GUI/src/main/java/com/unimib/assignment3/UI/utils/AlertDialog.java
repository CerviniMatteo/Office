package com.unimib.assignment3.UI.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class AlertDialog {
    public static void showAlert(String title, String message) {
        Runnable task = () -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.show();
        };

        if (Platform.isFxApplicationThread()) {
            task.run();
        } else {
            Platform.runLater(task);
        }
    }

}
