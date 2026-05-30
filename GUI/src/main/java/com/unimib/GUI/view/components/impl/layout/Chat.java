package com.unimib.GUI.view.components.impl.layout;

import com.unimib.GUI.view.controller.abstr.ChatController;
import com.unimib.GUI.view.controller.impl.layout.chat_state.ClosedChatStateController;
import com.unimib.GUI.view.utils.FXMLUtilLoader;
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