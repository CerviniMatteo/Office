package com.unimib.assignment3.UI;

import com.unimib.assignment3.UI.components.Home;
import com.unimib.assignment3.UI.components.Login;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

public class FxApplication extends Application {

    private HBox root;
    private List<Node> windowsStack;

    @Override
    public void start(Stage stage) {
        root = new HBox();
        root.getStyleClass().add("root");
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource("/styles/app.css")
                ).toExternalForm()
        );

        Login login = new Login(this);
        root.getChildren().add(login);
        root.setAlignment(Pos.CENTER);

        windowsStack = new Stack<>();
        windowsStack.add(login);

        stage.setTitle("JavaFX App");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

        Image icon = new Image(Objects.requireNonNull(getClass().getResource("/icon.png")).toExternalForm());
        stage.getIcons().add(icon);
    }

    public void afterLogin(){
        Home home = new Home(root.widthProperty());
        pushWindow(home);
        HBox.setHgrow(home, Priority.ALWAYS);
        home.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    }

    private void pushWindow(Node newWindow) {
        root.getChildren().clear();
        root.getChildren().add(newWindow);
        windowsStack.add(newWindow);
    }
    private void popWindow() {
        if (windowsStack.size() > 1) {
            windowsStack.removeLast();
            Node previousWindow = windowsStack.getLast();
            root.getChildren().clear();
            root.getChildren().add(previousWindow);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
