package com.unimib.GUI.UI.view.components.impl.layout;

import com.unimib.GUI.UI.view.controller.abstr.ChatController;
import com.unimib.GUI.UI.view.controller.impl.layout.chat_state.ClosedChatStateController;
import com.unimib.GUI.UI.view.utils.FXMLUtilLoader;
import com.unimib.GUI.utils.UserSession;
import javafx.scene.layout.StackPane;

public class Chat extends StackPane {

    private ChatController controller;

    public Chat(UserSession userSession) {
        FXMLUtilLoader.load(
                this,
                new ClosedChatStateController(this, userSession),
                "/components/Chat.fxml",
                "app.css"
        );
    }

    public void setController(ChatController controller) {
        if (this.controller != null) {
            this.controller.disposeListeners();
        }
        this.controller = controller;

        getChildren().clear();

        FXMLUtilLoader.load(
                this,
                controller,
                "/components/Chat.fxml",
                "app.css"
        );
    }

    public void destroy() {
        if (controller != null) {
            controller.destroy();
        }
    }
}