package com.unimib.assignment3.UI.utils;

import javafx.scene.control.Alert;

public class AlertDialog {

    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
