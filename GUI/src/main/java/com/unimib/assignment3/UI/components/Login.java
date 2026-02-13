package com.unimib.assignment3.UI.components;

import com.unimib.assignment3.UI.FxApplication;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Objects;

public class Login extends VBox {

    FxApplication fxApplication;

    public Login(FxApplication fxApplication) {
        super(8);
        this.fxApplication = fxApplication;

        // Load FXML and controller
        com.unimib.assignment3.UI.controller.UI.LoginViewController controller = new com.unimib.assignment3.UI.controller.UI.LoginViewController();
        controller.setFxApplication(fxApplication);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/Login.fxml"));
        loader.setRoot(this);
        loader.setController(controller);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load Login.fxml", e);
        }

        setAlignment(Pos.CENTER);

        // ensure css is available on scene
        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                String css = Objects.requireNonNull(getClass().getResource("/styles/app.css")).toExternalForm();
                if (!newScene.getStylesheets().contains(css)) {
                    newScene.getStylesheets().add(css);
                }
            }
        });
    }
}