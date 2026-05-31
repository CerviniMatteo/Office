package com.unimib.GUI.view.controller.impl.layout.chat_state;

import com.unimib.GUI.model.controller.ChatRestController;
import com.unimib.GUI.model.dto.MessageDTO;
import com.unimib.GUI.view.components.impl.custom.StyledButton;
import com.unimib.GUI.view.components.impl.layout.Chat;
import com.unimib.GUI.view.controller.abstr.ChatController;
import javafx.concurrent.Task;
import javafx.fxml.FXML;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for the "chat list" state (no chat currently open).
 *
 * No changes to logic from the original. Included here so the full picture
 * is clear: this controller never touches the WebSocket listener, so it has
 * no listener-leak responsibility. The cache it carries via Chat is already
 * persistent and shared — no adoption of cache needed.
 */
public class ClosedChatStateController extends ChatController {

    public ClosedChatStateController(Chat chat) {
        super(chat, new HashMap<Long, List<MessageDTO>>());
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