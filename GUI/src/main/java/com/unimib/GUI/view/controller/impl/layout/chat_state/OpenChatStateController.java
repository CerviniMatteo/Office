package com.unimib.GUI.view.controller.impl.layout.chat_state;

import com.unimib.GUI.model.dto.MessageDTO;
import com.unimib.GUI.view.components.impl.layout.Chat;
import com.unimib.GUI.view.controller.abstr.ChatController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class OpenChatStateController extends ChatController {

    public OpenChatStateController(Chat chat) {
        super(chat);
    }

    @FXML
    private void initialize() {
        super.baseInitialize();

        sendButton.setOnAction(_ -> {
            String text = inputForm.getText();
            if (text == null || text.isEmpty()) return;
            if (selectedChatId == null) return;

            try {
                MessageDTO msg = new MessageDTO(selectedChatId, employeeId, text);

                chatWebSocketClientApp.sendMessage(
                        mapper.writeValueAsString(msg)
                );

                inputForm.clear();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        chatWebSocketClientApp.receiveMessage().addListener((_, _, newV) -> {
            if (newV == null || newV.isEmpty()) return;

            Platform.runLater(() -> {
                try {
                    MessageDTO msg = mapper.readValue(newV, MessageDTO.class);

                    // SAVE
                    saveToFile(msg);

                    // CACHE
                    chatCache
                            .computeIfAbsent(msg.chatId(), _ -> new ArrayList<>())
                            .add(msg);

                    // RENDER ONLY IF OPEN
                    if (selectedChatId != null && selectedChatId.equals(msg.chatId())) {
                        renderMessage(msg);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });

        backButton.setOnAction(_ -> closeChat());

        chatContainer.heightProperty().addListener((_, _, _) ->
                scrollPane.setVvalue(1.0)
        );

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
            loadFromFile(chatId);
        }
    }

    // =========================
    // CLOSE CHAT
    // =========================
    private void closeChat() {
        ClosedChatStateController closed = new ClosedChatStateController(this.chat);
        // Preserve chat cache and shared state when switching back
        closed.adoptStateFrom(this);
        chat.setController(closed);
    }


    // =========================
    // LOAD FROM FILE
    // =========================
    protected void loadFromFile(Long chatId) {
        try {
            Path file = baseDir.resolve(chatId + ".txt");

            if (!Files.exists(file)) {
                return;
            }

            try (var lines = Files.lines(file)) {
                lines.filter(line -> !line.isBlank()).forEach(line -> {
                    try {
                        MessageDTO msg = mapper.readValue(line, MessageDTO.class);

                        var list = chatCache.computeIfAbsent(chatId, _ -> new ArrayList<>());

                        if (!list.contains(msg)) {
                            list.add(msg);
                            renderMessage(msg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveToFile(MessageDTO msg) {
        try {
            Path file = baseDir.resolve(msg.chatId() + ".txt");

            String json = mapper.writeValueAsString(msg);

            Files.writeString(
                    file,
                    json + System.lineSeparator(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}