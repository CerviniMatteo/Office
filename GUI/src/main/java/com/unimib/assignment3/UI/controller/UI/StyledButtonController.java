package com.unimib.assignment3.UI.controller.UI;

import com.unimib.assignment3.UI.components.StyledButton;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Controller class that contains the UI logic for the StyledButton FXML.
 * The controller is instantiated programmatically and attached to the StyledButton instance.
 */
public class StyledButtonController {

    @FXML
    private HBox content;

    @FXML
    private StackPane iconWrapper;

    @FXML
    private Label label;

    private final StyledButton root;

    private static final double ICON_SCALE = 0.03;

    public StyledButtonController(StyledButton root) {
        this.root = root;
    }

    @FXML
    private void initialize() {
        VBox.setMargin(root, new Insets(10));
    }

    public HBox getContent() {
        return content;
    }
}

