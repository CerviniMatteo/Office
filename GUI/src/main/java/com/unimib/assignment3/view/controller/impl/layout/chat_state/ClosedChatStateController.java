package com.unimib.assignment3.view.controller.impl.layout.chat_state;

import com.unimib.assignment3.model.controller.ChatRestController;
import com.unimib.assignment3.view.components.impl.custom.StyledButton;
import com.unimib.assignment3.view.components.impl.layout.Chat;
import com.unimib.assignment3.view.controller.abstr.ChatController;
import javafx.concurrent.Task;
import javafx.fxml.FXML;

import java.util.List;

public class ClosedChatStateController extends ChatController {

    public ClosedChatStateController(Chat chat) {
        super(chat);
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

        Task<List<Long>> task =
                new ChatRestController().getChats(employeeId);

        task.setOnSucceeded(_ -> {

            List<Long> chatIds = task.getValue();

            if (chatIds == null) {
                return;
            }

            chats.getChildren().clear();

            for (Long chatId : chatIds) {

                StyledButton btn = new StyledButton();

                btn.setText("Chat " + chatId);

                btn.setMaxWidth(Double.MAX_VALUE);

                btn.setOnAction(_ -> openChat(chatId));

                chats.getChildren().add(btn);
            }
        });

        task.setOnFailed(_ ->
                task.getException().printStackTrace());

        Thread thread = new Thread(task);

        thread.setDaemon(true);

        thread.start();
    }

    // ======================================================
    // OPEN CHAT
    // ======================================================

    private void openChat(Long chatId) {
        // Switch to OpenChatStateController which handles all the button listeners
        OpenChatStateController open = new OpenChatStateController(this.chat);
        // Preserve chat cache and shared state when switching controllers
        open.adoptStateFrom(this);
        chat.setController(open);

        // Now that the new controller is loaded with its listeners attached via initialize(),
        // display the requested chat
        open.displayChat(chatId);
    }


}