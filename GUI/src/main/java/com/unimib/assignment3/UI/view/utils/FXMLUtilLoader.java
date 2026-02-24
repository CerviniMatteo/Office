package com.unimib.assignment3.UI.view.utils;

import com.unimib.assignment3.UI.view.controller.abstr.DefaultController;
import javafx.scene.Node;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class FXMLUtilLoader {
    public static void load(Node node, DefaultController controller, String fxmlResource, String style){
        FXMLLoader loader = new FXMLLoader(node.getClass().getResource(fxmlResource));
        loader.setController(controller);
        loader.setRoot(node);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load " + fxmlResource, e);
        }

        node.getStyleClass().addAll(style);
        node.setFocusTraversable(false);
        node.setPickOnBounds(false);
    }
}
