package com.unimib.assignment3.UI.view.utils;

import com.unimib.assignment3.UI.view.controller.abstr.DefaultController;
import javafx.scene.Node;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.util.Arrays;

public class FXMLUtilLoader {
    public static void load(Node node, DefaultController controller, String fxmlResource, String style){
        FXMLLoader loader = new FXMLLoader(node.getClass().getResource(fxmlResource));
        loader.setController(controller);
        loader.setRoot(node);

        try {
            loader.load();
        } catch (IOException e) {
           e.printStackTrace();
            throw new RuntimeException("Failed to load " + fxmlResource, e);
        }

        // Safely add style classes only when provided (allow multiple classes separated by whitespace)
        if (style != null) {
            String trimmed = style.trim();
            if (!trimmed.isEmpty()) {
                node.getStyleClass().addAll(Arrays.asList(trimmed.split("\\s+")));
            }
        }
        node.setFocusTraversable(false);
        node.setPickOnBounds(false);
    }
}
