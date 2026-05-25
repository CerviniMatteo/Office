package com.unimib.assignment3.view.components.impl.layout;

import com.unimib.assignment3.view.controller.impl.layout.ChatViewController;
import com.unimib.assignment3.view.utils.FXMLUtilLoader;
import javafx.scene.layout.VBox;

public class Chat extends VBox {
    public Chat() {
        ChatViewController controller = new ChatViewController();
        FXMLUtilLoader.load(this, controller, "/components/Chat.fxml", "app.css");
    }
}