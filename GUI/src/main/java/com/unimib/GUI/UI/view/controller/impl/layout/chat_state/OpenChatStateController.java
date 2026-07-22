package com.unimib.GUI.UI.view.controller.impl.layout.chat_state;

import com.unimib.GUI.UI.view.components.impl.layout.Chat;
import com.unimib.GUI.UI.view.controller.abstr.ChatController;
import com.unimib.GUI.model.dto.MessageDTO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;

import java.util.List;
import java.util.Map;

import static com.unimib.GUI.UI.view.utils.ComponentVisibilityUtils.setVisible;

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

        observeState(
                viewModel.getMessagesStateProperty(),
                () -> Platform.runLater(() -> chatContainer.getChildren().clear()),
                messages -> {

                    if (messages == null || selectedChatId == null)
                        return;

                    chatContainer.getChildren().clear();

                    for (MessageDTO message : messages) {
                        System.out.printf("Rendering message: %s%n", message);
                        renderMessage(message);
                    }
                },
                this::showError
        );

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

        setChatVisible(true,  false);

        chatContainer.getChildren().clear();

        viewModel.openChat(chatId);
    }

    private void setChatVisible(boolean chatsVisible, boolean areaVisible) {
        setVisible(chatsVisible, chatArea);
        setVisible(areaVisible, closedChatArea);

    }

    // =========================
    // EXIT CHAT
    // =========================

    private void exitChat() {
        viewModel.closeChat();

        setChatVisible(false, true);

        ClosedChatStateController closed =
                new ClosedChatStateController(chat, chatCache);

        closed.adoptStateFrom(this);

        chat.setController(closed);
    }
}