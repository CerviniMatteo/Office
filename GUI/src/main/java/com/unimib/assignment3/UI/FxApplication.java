package com.unimib.assignment3.UI;

import com.unimib.assignment3.UI.components.Home;
import com.unimib.assignment3.UI.components.InformationBanner;
import com.unimib.assignment3.UI.components.Login;
import com.unimib.assignment3.UI.enums.BannerType;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;
import java.util.Objects;
import java.util.Stack;

import static com.unimib.assignment3.UI.components.InformationBanner.timeInSeconds;

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

    private VBox createBannerBox(BannerType type, String message) {
        InformationBanner banner = new InformationBanner(type, message);
        banner.setPrefHeight(60);
        banner.setMaxWidth(100);
        banner.setPrefWidth(240);
        banner.setAlignment(Pos.CENTER);
        banner.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(banner, new Insets(10, 10, 20, 20));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(banner, spacer);
        return vBox;
    }

    public void afterLogin(String message){
        Home home = new Home();
        pushWindow(home);
        HBox.setHgrow(home, Priority.ALWAYS);
        home.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        VBox bannerBox = createBannerBox(BannerType.SUCCESS, message);
        root.getChildren().add(bannerBox);
        PauseTransition pause = new PauseTransition(Duration.seconds(timeInSeconds));
        pause.setOnFinished(event -> root.getChildren().remove(bannerBox));
        pause.play();
    }

    public void failedLogin(String message){
        VBox bannerBox = createBannerBox(BannerType.FAILURE, message);
        root.getChildren().add(bannerBox);
        PauseTransition pause = new PauseTransition(Duration.seconds(timeInSeconds));
        pause.setOnFinished(event -> root.getChildren().remove(bannerBox));
        pause.play();
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
