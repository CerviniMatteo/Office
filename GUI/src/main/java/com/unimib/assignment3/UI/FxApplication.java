package com.unimib.assignment3.UI;

import com.unimib.assignment3.UI.components.DashboardManager;
import com.unimib.assignment3.UI.components.TaskLayout;
import com.unimib.assignment3.UI.web_socket_client.TaskWebSocketClientApp;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Objects;

import static com.unimib.assignment3.UI.utils.AlertDialog.showAlert;

public class FxApplication extends Application {

    @Override
    public void start(Stage stage) {
        HBox root = new HBox();
        root.getStyleClass().add("root");
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource("/styles/app.css")
                ).toExternalForm()
        );


        DashboardManager dashboardManager =
                new DashboardManager(root.widthProperty().multiply(0.15));

        TaskLayout tasksLayout = new TaskLayout(5, 2);
        TaskWebSocketClientApp webSocketClientApp = new TaskWebSocketClientApp(tasksLayout);
        try {
            webSocketClientApp.start();
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
        HBox.setHgrow(tasksLayout, Priority.ALWAYS);
        root.getChildren().addAll(
                dashboardManager.getHbox(),
                tasksLayout
        );

        stage.setTitle("JavaFX App");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

        Image icon = new Image(Objects.requireNonNull(getClass().getResource("/icon.png")).toExternalForm());
        stage.getIcons().add(icon);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
