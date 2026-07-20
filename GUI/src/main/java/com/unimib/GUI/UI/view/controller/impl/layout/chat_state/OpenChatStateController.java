package com.unimib.GUI.UI.view.controller.impl.layout.chat_state;

import com.unimib.GUI.UI.view.components.impl.layout.Chat;
import com.unimib.GUI.UI.view.controller.abstr.ChatController;
import com.unimib.GUI.UI.view.utils.FileUtils;
import com.unimib.GUI.model.dto.MessageDTO;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OpenChatStateController extends ChatController {

    protected OpenChatStateController(
            Chat chat,
            Map<Long, List<MessageDTO>> chatCache
    ) {
        super(chat, chatCache);
    }

    @FXML
    private void initialize() {

        super.baseInitialize();

        observeState(
                viewModel.getSendMessageStateProperty(),
                () -> sendButton.setDisable(true),
                unused -> {
                    sendButton.setDisable(false);
                    inputForm.clear();
                },
                error -> {
                    sendButton.setDisable(false);
                    showError(error);
                }
        );

        viewModel.getMessagesProperty().addListener((obs, oldMessages, messages) -> {
            if (messages == null || selectedChatId == null)
                return;
            chatContainer.getChildren().clear();
            messages.forEach(message -> {
                System.out.printf("Rendering message: %s%n", message);
                renderMessage(message);
            });
        });

        sendButton.setOnAction(_ -> {

            String text = inputForm.getText();

            if (text == null || text.isBlank() || selectedChatId == null)
                return;

            viewModel.sendMessage(text);
        });

        backButton.setOnAction(_ -> exitChat());

        chatContainer.heightProperty().addListener((_, _, _) ->
                scrollPane.setVvalue(1.0));

        scrollPane.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );
    }

    // =========================
    // DISPLAY CHAT
    // =========================

    public void displayChat(Long chatId) {

        selectedChatId = chatId;

        setChatVisible(false, true);

        chatContainer.getChildren().clear();

        viewModel.openChat(chatId);
    }

    private void setChatVisible(boolean chatsVisible, boolean areaVisible) {

        chats.setVisible(chatsVisible);
        chats.setManaged(chatsVisible);

        chatArea.setVisible(areaVisible);
        chatArea.setManaged(areaVisible);
    }

    // =========================
    // EXIT CHAT
    // =========================

    private void exitChat() {

        viewModel.closeChat();
        viewModel.disconnect();

        setChatVisible(true, false);

        ClosedChatStateController closed =
                new ClosedChatStateController(chat, chatCache);

        closed.adoptStateFrom(this);

        chat.setController(closed);
    }
}