package com.unimib.assignment3.UI.components;

import com.unimib.assignment3.UI.web_socket_client.TaskWebSocketClientApp;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import static com.unimib.assignment3.UI.components.AlertDialog.showAlert;

public class Home extends HBox {

    public Home(ReadOnlyDoubleProperty widthProperty) {

        TaskLayout tasksLayout = new TaskLayout(5, 2);
        DashboardManager dashboardManager =
                new DashboardManager(widthProperty.multiply(0.20));
        HBox dashboardManagerContainer = dashboardManager.getDashboardContainer();
        TaskWebSocketClientApp webSocketClientApp = new TaskWebSocketClientApp(tasksLayout);
        try {
            webSocketClientApp.start();
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }

        getChildren().addAll(dashboardManagerContainer, tasksLayout);
        HBox.setHgrow(tasksLayout, Priority.ALWAYS);
    }
}

