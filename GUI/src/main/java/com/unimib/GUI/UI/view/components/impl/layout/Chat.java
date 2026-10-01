package com.unimib.GUI.UI.view.components.impl.layout;

import com.unimib.GUI.UI.view.controller.abstr.ChatController;
import com.unimib.GUI.UI.view.controller.impl.layout.chat_state.ClosedChatStateController;
import com.unimib.GUI.UI.view.utils.FXMLUtilLoader;
import com.unimib.GUI.UI.viewmodel.impl.ChatViewModel;
import com.unimib.GUI.model.dto.MessageDTO;
import com.unimib.GUI.utils.UserSession;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chat extends StackPane {

    private ChatController controller;

    public Chat(UserSession userSession) {
        // created once and shared by every state controller:
        // one ViewModel = one STOMP connection
        ChatViewModel viewModel = new ChatViewModel(userSession.getEmployeeId());

        // assign it, otherwise the first controller is never disposed/destroyed
        Map<Long, List<MessageDTO>> chatCache = new HashMap<>();
        this.controller = new ClosedChatStateController(
                this, chatCache, userSession, viewModel
        );

        FXMLUtilLoader.load(
                this,
                controller,
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