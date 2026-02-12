package com.unimib.assignment3.UI.components;

import com.unimib.assignment3.UI.web_socket_client.TaskWebSocketClientApp;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.awt.*;

import static com.unimib.assignment3.UI.components.AlertDialog.showAlert;

public class Home extends HBox {

    public Home() {

        TaskLayout tasksLayout = new TaskLayout();
        getChildren().add(tasksLayout);
        HBox.setHgrow(tasksLayout, Priority.ALWAYS);

        TaskWebSocketClientApp webSocketClientApp = new TaskWebSocketClientApp(tasksLayout);
        try {
            webSocketClientApp.start();
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }
}

