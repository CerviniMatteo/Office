package com.unimib.assignment3.UI;

import com.unimib.assignment3.UI.view.components.impl.layout.Login;
import com.unimib.assignment3.UI.view.state.ApplicationStateManager;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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
        String sheetUrl = Objects.requireNonNull(getClass().getResource("/styles/app.css")).toExternalForm();
        scene.getStylesheets().add(sheetUrl);

        // Debug: print resolved stylesheet and current stylesheets
        System.out.println("[DEBUG] Loaded stylesheet URL: " + sheetUrl);
        System.out.println("[DEBUG] Scene stylesheets: " + scene.getStylesheets());

        ApplicationStateManager stateManager = ApplicationStateManager.getInstance(this);
        stateManager.replaceWindow(new Login());

        // Add mouse back/forward buttons handler: map mouse BACK/FORWARD to app navigation
        scene.addEventHandler(MouseEvent.MOUSE_PRESSED, ev -> {
            if (ev.getButton() == MouseButton.BACK) {
                stateManager.goBack();
                ev.consume();
            } else if (ev.getButton() == MouseButton.FORWARD) {
                stateManager.goForward();
                ev.consume();
            }
        });

        stage.setTitle("JavaFX App");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

        Image icon = new Image(Objects.requireNonNull(getClass().getResource("/images/icon.png")).toExternalForm());
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
