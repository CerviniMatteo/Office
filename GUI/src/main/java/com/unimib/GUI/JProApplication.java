package com.unimib.GUI;

import com.unimib.GUI.UI.view.components.impl.layout.Auth;
import com.unimib.GUI.UI.state.ApplicationStateManager;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Objects;

public class JProApplication extends Application {

    private StackPane root;
    private StackPane contentRoot;
    private StackPane overlayRoot;


    @Override
    public void start(Stage stage) {

        root = new StackPane();
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root");


        contentRoot = new StackPane();
        contentRoot.setPickOnBounds(true);

        contentRoot.prefWidthProperty()
                .bind(root.widthProperty());

        contentRoot.prefHeightProperty()
                .bind(root.heightProperty());


        overlayRoot = new StackPane();
        overlayRoot.setPickOnBounds(false);
        overlayRoot.setMouseTransparent(true);

        overlayRoot.prefWidthProperty()
                .bind(root.widthProperty());

        overlayRoot.prefHeightProperty()
                .bind(root.heightProperty());


        root.getChildren()
                .addAll(contentRoot, overlayRoot);



        Scene scene = new Scene(root, 1920, 1080);


        String css = Objects.requireNonNull(
                getClass().getResource("/styles/app.css")
        ).toExternalForm();

        scene.getStylesheets().add(css);



        ApplicationStateManager stateManager =
                ApplicationStateManager.getInstance(
                        contentRoot,
                        overlayRoot
                );

        stateManager.replaceWindow(new Auth());



        stage.setTitle("Office");
        stage.setScene(scene);
        stage.show();
    }


    public StackPane getRoot() {
        return root;
    }


    public StackPane getContentRoot() {
        return contentRoot;
    }


    public StackPane getOverlayRoot() {
        return overlayRoot;
    }
}