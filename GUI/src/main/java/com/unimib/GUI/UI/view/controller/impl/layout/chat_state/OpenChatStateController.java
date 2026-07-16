package com.unimib.GUI.UI.view.controller.impl.layout.chat_state;

import com.unimib.GUI.model.dto.MessageDTO;
import com.unimib.GUI.UI.view.components.impl.layout.Chat;
import com.unimib.GUI.UI.view.controller.abstr.ChatController;
import com.unimib.GUI.UI.view.utils.FileUtils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OpenChatStateController extends ChatController {

    protected OpenChatStateController(Chat chat, Map<Long, List<MessageDTO>> chatCache) {
        super(chat, chatCache);
    }

    private ChangeListener<String> msgListener;

    @FXML
    private void initialize() {
        super.baseInitialize();

        // ---- Send button ----
        sendButton.setOnAction(_ -> {
            String text = inputForm.getText();
            if (text == null || text.isBlank()) return;   // FIX: isBlank() catches whitespace
            if (selectedChatId == null) return;

            try {
                MessageDTO msg = new MessageDTO(selectedChatId, employeeId, text);
                chatWebSocketClientApp.sendMessage(mapper.writeValueAsString(msg));
                inputForm.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        msgListener = (_, _, newV) -> {
            if (newV == null || newV.isEmpty()) return;

            Platform.runLater(() -> {
                try {
                    MessageDTO msg = mapper.readValue(newV, MessageDTO.class);

                    Path path = baseDir.resolve(msg.chatId() + ".txt");

                    FileUtils.appendObject(path, msg);

                    chatCache
                            .computeIfAbsent(msg.chatId(), _ -> new ArrayList<>())
                            .add(msg);

                    // RENDER only if this chat is currently open
                    if (selectedChatId != null && selectedChatId.equals(msg.chatId())) {
                        renderMessage(msg);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        };

        // Use the ChatWebSocketClientApp helper so listeners are tracked and can be
        // removed by stop()/reset if a controller fails to do so.
        chatWebSocketClientApp.addReceiveListener(msgListener);

        // ---- Back button ----
        backButton.setOnAction(_ -> closeChat());

        // ---- Auto-scroll ----
        chatContainer.heightProperty().addListener((_, _, _) ->
                scrollPane.setVvalue(1.0));

        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }

    // =========================
    // DISPLAY CHAT
    // =========================

    public void displayChat(Long chatId) {
        selectedChatId = chatId;

        closeChat(false, true);

        chatContainer.getChildren().clear();

        List<MessageDTO> cached = chatCache.get(chatId);

        if (cached != null && !cached.isEmpty()) {
            cached.forEach(this::renderMessage);
        } else {
            Path path = baseDir.resolve(chatId + ".txt");
            List<MessageDTO> messages = FileUtils.readObjects(path, MessageDTO.class);

           chatCache.put(chatId, new ArrayList<>(messages));


            messages.forEach(this::renderMessage);
        }
    }

    private void closeChat(boolean chatsIdOpen, boolean chatAreaOpen){
        chats.setVisible(chatsIdOpen);
        chats.setManaged(chatsIdOpen);
        chatArea.setVisible(chatAreaOpen);
        chatArea.setManaged(chatAreaOpen);
    }

    // =========================
    // CLOSE CHAT
    // =========================

    private void closeChat() {
        chatWebSocketClientApp.removeReceiveListener(msgListener);
        chatCache.clear();
        closeChat(true, false);
        ClosedChatStateController closed = new ClosedChatStateController(this.chat, this.chatCache);
        closed.adoptStateFrom(this);
        chat.setController(closed);
    }
}