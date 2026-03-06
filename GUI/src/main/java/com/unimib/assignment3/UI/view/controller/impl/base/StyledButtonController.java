package com.unimib.assignment3.UI.view.controller.impl.base;

import com.unimib.assignment3.UI.view.components.impl.custom.StyledButton;
import com.unimib.assignment3.UI.view.controller.abstr.DefaultController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Controller class that contains the UI logic for the StyledButton FXML.
 * The controller is instantiated programmatically and attached to the StyledButton instance.
 */
public class StyledButtonController implements DefaultController {

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

        // Debug: print style classes and scene stylesheets once the node is attached to a scene
        Platform.runLater(() -> {
            System.out.println("[DEBUG] StyledButton styleClasses: " + root.getStyleClass());
            Scene s = root.getScene();
            if (s != null) {
                System.out.println("[DEBUG] StyledButton scene stylesheets: " + s.getStylesheets());
            } else {
                System.out.println("[DEBUG] StyledButton has no scene yet");
            }
        });
    }

    public HBox getContent() {
        return content;
    }
}
