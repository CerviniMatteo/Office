package com.unimib.GUI.view.controller.impl.layout.chat_state;

import com.unimib.GUI.model.dto.MessageDTO;
import com.unimib.GUI.view.components.impl.layout.Chat;
import com.unimib.GUI.view.controller.abstr.ChatController;
import com.unimib.GUI.view.utils.FileUtils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controller for the "chat open" state.
 *
 * FIX 1 (listener leak / message duplication on re-login):
 *   The ChangeListener is stored in a field so it can be removed in closeChat().
 *   Without removal, every login adds another listener to the singleton's
 *   receivedMessage property, causing 1 incoming message to fire N times.
 *
 * FIX 2 (re-open shows only last message):
 *   displayChat() now seeds getChatCache() from disk when there is no cached data,
 *   so the cache is always the authoritative, unified source of truth.
 *   Subsequent opens hit the cache (which includes both persisted and live messages).
 *
 * FIX 3 (chatPath overwritten by unrelated messages):
 *   chatPath is derived inline inside the listener from msg.chatId() instead of
 *   being stored as a mutable instance field that any arriving message could clobber.
 */
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

        chats.setVisible(false);
        chats.setManaged(false);
        chatArea.setVisible(true);
        chatArea.setManaged(true);

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

    // =========================
    // CLOSE CHAT
    // =========================

    private void closeChat() {
        chatWebSocketClientApp.removeReceiveListener(msgListener);

        ClosedChatStateController closed = new ClosedChatStateController(this.chat, this.chatCache);
        closed.adoptStateFrom(this);
        chat.setController(closed);
    }
}