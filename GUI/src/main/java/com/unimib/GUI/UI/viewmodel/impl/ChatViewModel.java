package com.unimib.GUI.UI.viewmodel.impl;

import com.unimib.GUI.UI.state.UIState;
import com.unimib.GUI.UI.viewmodel.BaseViewModel;
import com.unimib.GUI.model.dto.ChatInfoDTO;
import com.unimib.GUI.model.dto.MessageDTO;
import com.unimib.GUI.model.dto.WorkerInfoDTO;
import com.unimib.GUI.UI.repository.ChatRepository;
import javafx.beans.property.*;

import java.util.List;
import java.util.function.Consumer;

public class ChatViewModel extends BaseViewModel {

    private final ChatRepository repository = new ChatRepository();

    private final ObjectProperty<UIState<List<ChatInfoDTO>>> chatsState =
            new SimpleObjectProperty<>();

    private final ObjectProperty<UIState<List<WorkerInfoDTO>>> newChatsState =
            new SimpleObjectProperty<>();

    private final ObjectProperty<UIState<Void>> connectionState =
            new SimpleObjectProperty<>();

    private final ObjectProperty<UIState<Void>> sendMessageState =
            new SimpleObjectProperty<>();

    private final ObjectProperty<UIState<List<MessageDTO>>> messagesState =
            new SimpleObjectProperty<>();

    private final ObjectProperty<Long> selectedChat =
            new SimpleObjectProperty<>();

    private final ObjectProperty<UIState<Void>> createChatState =
            new SimpleObjectProperty<>();

    private final Long employeeId;

    private final Consumer<Long> cacheListener;

    public ChatViewModel(Long employeeId) {

        this.employeeId = employeeId;

        cacheListener = chatId -> {

            Long currentChat = selectedChat.get();

            if (currentChat == null || !currentChat.equals(chatId))
                return;

            // Can fire from the socket's receive thread. loadMessages()
            // reads from the on-disk cache, so route it through execute()
            // like every other action instead of calling it inline.
            execute(() -> repository.loadMessages(chatId), messagesState);
        };

        repository.addCacheListener(cacheListener);

        connect();
    }

    public void loadChats() {
        execute(
                ()->repository.getChats(employeeId),
                chatsState);
    }

    public void getUnMatchedEmployeeInfos() {
        execute(
                ()->repository.getUnMatchedEmployeeInfos(employeeId),
                newChatsState);
    }

    private void connect() {
        execute(repository.connect(), connectionState);
    }

    public void openChat(Long chatId) {

        selectedChat.set(chatId);

        // Previously called repository.loadMessages(chatId) directly,
        // blocking the FX Application Thread on a disk read for chats
        // not yet in memory. Now goes through the shared executor.
        execute(() -> repository.loadMessages(chatId), messagesState);
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

    public void createChat(Long targetEmployeeId) {
        execute(repository.createChat(this.employeeId, targetEmployeeId), createChatState);
    }

    public void closeChat() {
        selectedChat.set(null);
        messagesState.set(null);
    }

    public void destroy() {
        closeChat();
        repository.removeCacheListener(cacheListener);
        execute(repository.disconnect(), connectionState);
    }

    public ReadOnlyObjectProperty<UIState<List<ChatInfoDTO>>> getChatsStateProperty() {
        return chatsState;
    }

    public ReadOnlyObjectProperty<UIState<List<WorkerInfoDTO>>> getNewChatsStateProperty() {
        return newChatsState;
    }

    public ReadOnlyObjectProperty<UIState<Void>> getConnectionStateProperty() {
        return connectionState;
    }

    public ReadOnlyObjectProperty<UIState<Void>> getSendMessageStateProperty() {
        return sendMessageState;
    }

    public ReadOnlyObjectProperty<UIState<List<MessageDTO>>> getMessagesStateProperty() {
        return messagesState;
    }

    public ReadOnlyObjectProperty<UIState<Void>> getCreateChatStateProperty() {
        return createChatState;
    }

    public ReadOnlyObjectProperty<Long> getSelectedChatProperty() {
        return selectedChat;
    }
}