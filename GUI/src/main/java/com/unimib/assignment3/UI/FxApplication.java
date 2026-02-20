package com.unimib.assignment3.UI;

import com.unimib.assignment3.UI.components.Login;
import com.unimib.assignment3.UI.state.ApplicationStateManager;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Objects;

public class FxApplication extends Application {

    private StackPane root;
    private StackPane contentRoot; // holds main app views
    private StackPane overlayRoot; // holds overlays like banners

    @Override
    public void start(Stage stage) {
        root = new StackPane();
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root");

        contentRoot = new StackPane();
        contentRoot.setPickOnBounds(true);
        contentRoot.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        contentRoot.prefWidthProperty().bind(root.widthProperty());
        contentRoot.prefHeightProperty().bind(root.heightProperty());
        overlayRoot = new StackPane();
        overlayRoot.setPickOnBounds(false);
        overlayRoot.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        overlayRoot.setStyle("-fx-background-color: transparent;");
        overlayRoot.prefWidthProperty().bind(root.widthProperty());
        overlayRoot.prefHeightProperty().bind(root.heightProperty());
        overlayRoot.setMouseTransparent(true);

        root.getChildren().addAll(contentRoot, overlayRoot);

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource("/styles/app.css")
                ).toExternalForm()
        );

        ApplicationStateManager stateManager = ApplicationStateManager.getInstance(this);
        stateManager.replaceWindow(new Login(this));

        stage.setTitle("JavaFX App");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

        Image icon = new Image(Objects.requireNonNull(getClass().getResource("/icon.png")).toExternalForm());
        stage.getIcons().add(icon);
    }

    /**
     * Root StackPane (content + overlay)
     */
    public StackPane getRoot() {
        return root;
    }

    /**
     * Pane that holds main application content (Login, Home, etc.)
     */
    public StackPane getContentRoot() {
        return contentRoot;
    }

    /**
     * Pane that holds overlays (banners, modals, etc.) on top of contentRoot
     */
    public StackPane getOverlayRoot() {
        return overlayRoot;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
