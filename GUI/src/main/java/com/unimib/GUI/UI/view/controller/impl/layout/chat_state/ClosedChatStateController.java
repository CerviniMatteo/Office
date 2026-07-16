package com.unimib.GUI.UI.view.controller.impl.layout.chat_state;

import com.unimib.GUI.model.controller.ChatRestController;
import com.unimib.GUI.model.dto.MessageDTO;
import com.unimib.GUI.UI.view.components.impl.custom.StyledButton;
import com.unimib.GUI.UI.view.components.impl.layout.Chat;
import com.unimib.GUI.UI.view.controller.abstr.ChatController;
import javafx.concurrent.Task;
import javafx.fxml.FXML;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ClosedChatStateController extends ChatController {

    public ClosedChatStateController(Chat chat) {
        super(chat, new HashMap<>());
    }

    protected ClosedChatStateController(Chat chat, Map<Long, List<MessageDTO>> chatCache) {
        super(chat, chatCache);
    }

    @FXML
    private void initialize() {
        super.baseInitialize();
        chatArea.setVisible(false);
        chatArea.setManaged(false);
        loadChatList();
    }

    // ======================================================
    // LOAD CHAT LIST
    // ======================================================

    private void loadChatList() {
        Task<List<Long>> task = new ChatRestController().getChats(employeeId);

        task.setOnSucceeded(_ -> {
            List<Long> chatIds = task.getValue();

            if (chatIds == null) return;

            chats.getChildren().clear();

            for (Long chatId : chatIds) {
                StyledButton btn = new StyledButton();
                btn.setText("Chat " + chatId);
                btn.setMaxWidth(Double.MAX_VALUE);
                btn.setOnAction(_ -> openChat(chatId));
                chats.getChildren().add(btn);
            }
        });

        task.setOnFailed(_ -> task.getException().printStackTrace());

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    // ======================================================
    // OPEN CHAT
    // ======================================================

    private void openChat(Long chatId) {
        OpenChatStateController open = new OpenChatStateController(this.chat, this.chatCache);
        open.adoptStateFrom(this);
        chat.setController(open);
        open.displayChat(chatId);
    }
}