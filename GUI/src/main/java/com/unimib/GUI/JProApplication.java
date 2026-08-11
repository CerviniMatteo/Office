package com.unimib.GUI;

import com.jpro.webapi.WebAPI;
import com.unimib.GUI.UI.state.ApplicationStateManager;
import com.unimib.GUI.UI.view.components.impl.layout.Auth;
import com.unimib.GUI.utils.SessionManager;
import com.unimib.GUI.utils.UserSession;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Objects;

public class JProApplication extends Application {

    @Override
    public void start(Stage stage) {

        StackPane root = new StackPane();
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root");

        StackPane contentRoot = new StackPane();
        contentRoot.setPickOnBounds(true);
        contentRoot.prefWidthProperty().bind(root.widthProperty());
        contentRoot.prefHeightProperty().bind(root.heightProperty());

        StackPane overlayRoot = new StackPane();
        overlayRoot.setPickOnBounds(false);
        overlayRoot.setMouseTransparent(true);
        overlayRoot.prefWidthProperty().bind(root.widthProperty());
        overlayRoot.prefHeightProperty().bind(root.heightProperty());

        root.getChildren().addAll(contentRoot, overlayRoot);

        Scene scene = new Scene(root, 1920, 1080);

        String css = Objects.requireNonNull(
                getClass().getResource("/styles/app.css")
        ).toExternalForm();

        scene.getStylesheets().add(css);

        stage.setTitle("Office");
        stage.setScene(scene);
        stage.show();

        WebAPI webAPI = WebAPI.getWebAPI(scene);

        System.out.println("JPro session: " + webAPI.getInstanceID());

        UserSession userSession = new UserSession(
                new ApplicationStateManager(
                        contentRoot,
                        overlayRoot
                ),
                new SessionManager()
        );

        userSession.applicationStateManager()
                .replaceWindow(new Auth(userSession));
    }
}