package com.unimib.assignment3.view.components.impl.layout;

import com.unimib.assignment3.view.controller.abstr.ChatController;
import com.unimib.assignment3.view.controller.impl.layout.chat_state.ClosedChatStateController;
import com.unimib.assignment3.view.utils.FXMLUtilLoader;
import javafx.scene.layout.VBox;

public class Chat extends VBox {

    private ChatController controller;

    public Chat() {
        FXMLUtilLoader.load(
                this,
                new ClosedChatStateController(this),
                "/components/Chat.fxml",
                "app.css"
        );
    }

    public void setController(ChatController controller) {
        this.controller = controller;

        getChildren().clear();

        FXMLUtilLoader.load(
                this,
                controller,
                "/components/Chat.fxml",
                "app.css"
        );
    }

    public ChatController getController() {
        return this.controller;
    }
}