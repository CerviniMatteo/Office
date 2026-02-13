package com.unimib.assignment3.UI.components;

import com.unimib.assignment3.UI.web_socket_client.TaskWebSocketClientApp;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;

import static com.unimib.assignment3.UI.components.AlertDialog.showAlert;

public class Home extends StackPane {

    public Home() {

        TaskLayout tasksLayout = new TaskLayout();
        getChildren().add(tasksLayout);

        // Create Add Task button
        Button addTaskButton = new Button();
        addTaskButton.getStyleClass().add("add-task-btn");

        // Create the SVG icon
        String svgPathContent = "M440-440H200v-80h240v-240h80v240h240v80H520v240h-80v-240Z"; // Your SVG path
        SVGPath addIcon = new SVGPath();
        addIcon.setContent(svgPathContent);
        addIcon.getStyleClass().add("add-icon");

        // Wrap the icon in a StackPane for alignment
        StackPane iconWrapper = new StackPane(addIcon);
        iconWrapper.setPrefSize(24, 24); // Size of the SVG icon
        addTaskButton.setGraphic(iconWrapper);

        // Position the button at bottom-right
        getChildren().add(addTaskButton);
        StackPane.setAlignment(addTaskButton, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(addTaskButton, new Insets(20));
        addTaskButton.toFront();

        // Initialize WebSocket client
        TaskWebSocketClientApp webSocketClientApp = new TaskWebSocketClientApp(tasksLayout);
        try {
            webSocketClientApp.start();
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }
}
