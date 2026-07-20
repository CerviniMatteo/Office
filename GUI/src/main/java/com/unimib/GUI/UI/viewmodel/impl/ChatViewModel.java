package com.unimib.GUI.UI.viewmodel.impl;

import com.unimib.GUI.UI.state.UIState;
import com.unimib.GUI.UI.viewmodel.BaseViewModel;
import com.unimib.GUI.model.dto.ChatInfoDTO;
import com.unimib.GUI.model.dto.MessageDTO;
import com.unimib.GUI.repository.ChatRepository;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;

import java.util.List;
import java.util.function.Consumer;

public class ChatViewModel extends BaseViewModel {

    private final ChatRepository repository = new ChatRepository();

    private final ObjectProperty<UIState<List<ChatInfoDTO>>> chatsState =
            new SimpleObjectProperty<>();

    private final ObjectProperty<UIState<Void>> connectionState =
            new SimpleObjectProperty<>();

    private final ObjectProperty<UIState<Void>> sendMessageState =
            new SimpleObjectProperty<>();

    private final ListProperty<MessageDTO> messages =
            new SimpleListProperty<>(FXCollections.observableArrayList());

    private final ObjectProperty<Long> selectedChat =
            new SimpleObjectProperty<>();

    private final Long employeeId;

    private final Consumer<Long> cacheListener;

    public ChatViewModel(Long employeeId) {

        this.employeeId = employeeId;

        cacheListener = chatId -> {

            Long currentChat = selectedChat.get();

            if (currentChat == null || !currentChat.equals(chatId))
                return;

            Platform.runLater(() ->
                    messages.setAll(repository.loadMessages(chatId))
            );
        };

        repository.addCacheListener(cacheListener);

        connect();
    }

    // =========================
    // Actions
    // =========================

    public void loadChats() {
        execute(repository.getChats(employeeId), chatsState);
    }

    private void connect() {
        execute(repository.connect(), connectionState);
    }

    public void disconnect() {
        execute(repository.disconnect(), connectionState);
    }

    public void openChat(Long chatId) {

        selectedChat.set(chatId);

        messages.setAll(repository.loadMessages(chatId));
    }

    public void sendMessage(String text) {

        if (text == null || text.isBlank())
            return;

        Long currentChat = selectedChat.get();

        if (currentChat == null)
            return;

        execute(
                repository.sendMessage(
                        new MessageDTO(
                                currentChat,
                                employeeId,
                                text
                        )
                ),
                sendMessageState
        );
    }

    public void closeChat() {

        selectedChat.set(null);

        messages.clear();
    }

    public void destroy() {

        repository.removeCacheListener(cacheListener);

        disconnect();
    }

    public ReadOnlyObjectProperty<UIState<List<ChatInfoDTO>>> getChatsStateProperty() {
        return chatsState;
    }

    public ReadOnlyObjectProperty<UIState<Void>> getConnectionStateProperty() {
        return connectionState;
    }

    public ReadOnlyObjectProperty<UIState<Void>> getSendMessageStateProperty() {
        return sendMessageState;
    }

    public ReadOnlyListProperty<MessageDTO> getMessagesProperty() {
        return messages;
    }

    public ReadOnlyObjectProperty<Long> getSelectedChatProperty() {
        return selectedChat;
    }
}