package com.unimib.assignment3.view.controller.impl.layout;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimib.assignment3.model.controller.ChatRestController;
import com.unimib.assignment3.model.dto.MessageDTO;
import com.unimib.assignment3.utils.SessionManagerSingleton;
import com.unimib.assignment3.view.components.impl.custom.AlertDialog;
import com.unimib.assignment3.view.components.impl.custom.ChatEntry;
import com.unimib.assignment3.view.components.impl.custom.StyledButton;
import com.unimib.assignment3.view.controller.abstr.DefaultController;
import com.unimib.assignment3.web_socket_client.ChatWebSocketClientApp;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.nio.file.*;
import java.util.*;

public class ChatViewController implements DefaultController {

    @FXML private VBox chats;
    @FXML private VBox chatContainer;
    @FXML private VBox chatArea;
    @FXML private TextField inputForm;
    @FXML private Button sendButton;
    @FXML private Button backButton;
    @FXML private ScrollPane scrollPane;

    private long employeeId;
    private Long selectedChatId = null;

    private ChatWebSocketClientApp chatWebSocketClientApp;

    private final Map<Long, List<MessageDTO>> chatCache = new HashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    private final Path baseDir = Paths.get(
            System.getProperty("user.home"),
            "chat-app",
            "chats"
    );

    @FXML
    private void initialize() {

        employeeId = (long) SessionManagerSingleton.getInstance()
                .getAttribute("employeeId");

        try {
            Files.createDirectories(baseDir);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // INITIAL UI
        chatArea.setVisible(false);
        chatArea.setManaged(false);

        // LOAD CHAT LIST
        var task = getListTask();
        new Thread(task).start();

        backButton.setOnAction(e -> closeChat());

        // WEBSOCKET
        chatWebSocketClientApp = new ChatWebSocketClientApp();

        try {
            chatWebSocketClientApp.start();
        } catch (Exception e) {
            AlertDialog.showAlert("Error", "Could not connect: " + e.getMessage());
        }

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

        chatContainer.heightProperty().addListener((_, _, _) ->
                scrollPane.setVvalue(1.0)
        );

        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }

    // =========================
    // LOAD CHAT LIST
    // =========================
    private Task<List<Long>> getListTask() {

        var task = new ChatRestController().getChats(employeeId);

        task.setOnSucceeded(_ -> {
            List<Long> chatIds = task.getValue();
            if (chatIds == null) return;

            for (Long chatId : chatIds) {
                StyledButton btn = new StyledButton();
                btn.setText("Chat " + chatId);
                btn.setMaxWidth(Double.MAX_VALUE);

                btn.setOnAction(_ -> openChat(chatId));

                chats.getChildren().add(btn);
            }
        });

        return task;
    }

    // =========================
    // OPEN CHAT
    // =========================
    private void openChat(Long chatId) {
        selectedChatId = chatId;

        chats.setVisible(false);
        chats.setManaged(false);

        chatArea.setVisible(true);
        chatArea.setManaged(true);

        chatContainer.getChildren().clear();

        // If we already have messages cached for this chat, render them instead
        // of reloading from file (which may contain the same messages). This
        // prevents duplicate rendering when messages were received while the
        // user was offline and later saved to disk.
        List<MessageDTO> cached = chatCache.get(chatId);
        if (cached != null && !cached.isEmpty()) {
            for (MessageDTO m : cached) {
                renderMessage(m);
            }
        } else {
            loadFromFile(chatId);
        }
    }

    // =========================
    // CLOSE CHAT
    // =========================
    private void closeChat() {
        selectedChatId = null;

        chatArea.setVisible(false);
        chatArea.setManaged(false);

        chats.setVisible(true);
        chats.setManaged(true);

        chatContainer.getChildren().clear();
    }

    // =========================
    // SAVE FILE
    // =========================
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

    // =========================
    // LOAD FILE
    // =========================
    private void loadFromFile(Long chatId) {
        try {
            Path file = baseDir.resolve(chatId + ".txt");

            if (!Files.exists(file)) return;

            Files.lines(file)
                    .filter(l -> !l.isBlank())
                    .forEach(line -> {
                        try {
                            MessageDTO msg = mapper.readValue(line, MessageDTO.class);

                            // Avoid adding/rendering duplicates: the same message
                            // may already be present in memory because it was
                            // received over the websocket and cached. Use
                            // List.contains (MessageDTO is a record so equals
                            // is field-based) to skip duplicates.
                            var list = chatCache.computeIfAbsent(chatId, _ -> new ArrayList<>());
                            if (!list.contains(msg)) {
                                list.add(msg);
                                renderMessage(msg);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // RENDER MESSAGE
    // =========================
    private void renderMessage(MessageDTO msg) {
        Label label = createMessage(msg.message());

        ChatEntry entry;
        if (msg.senderId().equals(employeeId)) {
            entry = new ChatEntry(label, new Label());
        } else {
            entry = new ChatEntry(new Label(), label);
        }

        Region spacer = new Region();
        spacer.setPrefHeight(10);

        chatContainer.getChildren().addAll(entry, spacer);
    }

    private Label createMessage(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("insert-text-lbl");
        return label;
    }
}