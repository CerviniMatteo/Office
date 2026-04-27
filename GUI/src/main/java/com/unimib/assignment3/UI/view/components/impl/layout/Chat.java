package com.unimib.assignment3.UI.view.components.impl.layout;

import com.unimib.assignment3.UI.view.controller.impl.layout.ChatViewController;
import com.unimib.assignment3.UI.view.utils.FXMLUtilLoader;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class Chat extends VBox {
    private ChatViewController controller;
    public Chat() {
        controller = new ChatViewController();
        FXMLUtilLoader.load(this, controller, "/components/Chat.fxml", "app.css");
    }
}