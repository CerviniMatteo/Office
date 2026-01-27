package com.unimib.assignment3.UI;

import com.unimib.assignment3.UI.components.DashboardManager;
import com.unimib.assignment3.UI.components.TaskLayout;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class FxApplication extends Application {

    @Override
    public void start(Stage stage) {
        // root
        HBox root = new HBox();
        Scene scene = new Scene(root, 800, 600);

        // tasks
        TaskLayout tasksLayout = new TaskLayout(5, 2);
        DashboardManager dashboardManager = new DashboardManager(root.widthProperty());
        tasksLayout.getTasksLayout().prefWidthProperty().bind(root.widthProperty().multiply(0.95));
        root.getChildren().addAll(dashboardManager.getDashboard().getDashboard(), dashboardManager.getDashboardButton().getButton(), tasksLayout.getTasksLayout());

        stage.setTitle("JavaFX App");
        stage.setScene(scene);
        stage.setMaximized(true); // maximized with title bar
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
