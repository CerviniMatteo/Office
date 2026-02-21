package com.unimib.assignment3.UI.view.components;

import com.unimib.assignment3.UI.view.controller.StyledButtonController;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import java.io.IOException;

public class StyledButton extends Button {

    private final StyledButtonController controller;

    public StyledButton() {
        controller = new StyledButtonController(this);
        loadFXML();

        this.setFocusTraversable(false);
        this.setFocused(false);

        this.getStyleClass().add("styled-btn");

    }

    private void loadFXML() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/StyledButton.fxml"));
        loader.setRoot(this);
        loader.setController(controller);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML for StyledButton", e);
        }
    }
}