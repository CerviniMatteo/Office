package com.unimib.assignment3.UI;

import com.unimib.assignment3.UI.components.DashboardManager;
import com.unimib.assignment3.UI.components.TaskLayout;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.util.Objects;

public class FxApplication extends Application {

    @Override
    public void start(Stage stage) {
        HBox root = new HBox();
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource("/styles/app.css")
                ).toExternalForm()
        );


        DashboardManager dashboardManager =
                new DashboardManager(root.widthProperty().multiply(0.15));

        TaskLayout tasksLayout = new TaskLayout(5, 2);
        HBox.setHgrow(tasksLayout, Priority.ALWAYS);

        root.getChildren().addAll(
                dashboardManager.getHbox(),
                tasksLayout
        );

        stage.setTitle("JavaFX App");
        stage.setScene(scene);
        root.setBackground(Background.fill(Paint.valueOf("#140D19")));
        stage.setMaximized(true);
        stage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
