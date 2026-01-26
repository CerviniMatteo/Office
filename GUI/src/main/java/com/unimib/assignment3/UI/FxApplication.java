package com.unimib.assignment3.UI;

import com.unimib.assignment3.UI.components.TaskLayout;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;

public class FxApplication extends Application {

    @Override
    public void start(Stage stage) {
        // root
        HBox root = new HBox();
        Scene scene = new Scene(root, 800, 600);

        // dashboard
        VBox dashboard = new VBox();
        HBox.setMargin(dashboard, new Insets(10));

        dashboard.setBorder(new Border(
                new BorderStroke(
                        Paint.valueOf("#4d0087"),
                        BorderStrokeStyle.SOLID,
                        new CornerRadii(10),
                        new BorderWidths(2)
                )
        ));

        // tasks
        TaskLayout tasksLayout = new TaskLayout(5, 5);

        //  25% / 75%
        dashboard.prefWidthProperty().bind(root.widthProperty().multiply(0.25));
        tasksLayout.getTasksLayout().prefWidthProperty().bind(root.widthProperty().multiply(0.75));

        HBox.setHgrow(dashboard, Priority.ALWAYS);

        root.getChildren().addAll(dashboard, tasksLayout.getTasksLayout());

        stage.setTitle("JavaFX App");
        stage.setScene(scene);
        stage.setMaximized(true); // maximized with title bar
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
