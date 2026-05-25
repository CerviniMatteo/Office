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
import com.unimib.assignment3.model.dto.ReadReceipt;
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

        // listeners and websocket setup
        setupBackButtonListener();
        setupSendButtonListener();
        setupChatContainerListener();

        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

    }

    // =========================
    // LISTENERS SETUP
    // =========================
    private void setupBackButtonListener() {
        backButton.setOnAction(e -> closeChat());
    }

    private void setupWebSocketClient() {
        chatWebSocketClientApp = new ChatWebSocketClientApp();

        try {
            chatWebSocketClientApp.start();
        } catch (Exception e) {
            AlertDialog.showAlert("Error", "Could not connect: " + e.getMessage());
            return;
        }

        chatWebSocketClientApp.receiveMessage().addListener((obs, oldV, newV) -> onWebSocketMessageReceived(newV));
    }

    private void onWebSocketMessageReceived(String newV) {
        if (newV == null || newV.isEmpty()) return;

        Platform.runLater(() -> {
            try {
                String trimmed = newV.trim();

                if (trimmed.startsWith("[")) {
                    // server sent an array of messages
                    MessageDTO[] msgs = mapper.readValue(trimmed, MessageDTO[].class);
                    for (MessageDTO msg : msgs) {
                        // SAVE
                        saveToFile(msg);

                        // CACHE
                        chatCache
                                .computeIfAbsent(msg.chatId(), _ -> new ArrayList<>())
                                .add(msg);

                        // RENDER ONLY IF OPEN
                        if (selectedChatId != null && selectedChatId.equals(msg.chatId())) {
                            renderMessage(msg);
                            sendReadReceipt(msg);
                        }
                    }
                } else {
                    MessageDTO msg = mapper.readValue(trimmed, MessageDTO.class);

                    // SAVE
                    saveToFile(msg);

                    // CACHE
                    chatCache
                            .computeIfAbsent(msg.chatId(), _ -> new ArrayList<>())
                            .add(msg);

                    // RENDER ONLY IF OPEN
                    if (selectedChatId != null && selectedChatId.equals(msg.chatId())) {
                        renderMessage(msg);
                        sendReadReceipt(msg);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void sendReadReceipt(MessageDTO msg) {
        try {
            if(msg.senderId().equals(employeeId)) return;
            ReadReceipt receipt = new ReadReceipt(
                    msg.chatId(),
                    msg.senderId(),
                    employeeId,
                    msg.message()
            );

            chatWebSocketClientApp.sendReadReceipt(
                    mapper.writeValueAsString(receipt)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupSendButtonListener() {
        sendButton.setOnAction(_ -> onSendButton());
    }

    private void onSendButton() {
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
    }

    private void setupChatContainerListener() {
        chatContainer.heightProperty().addListener((_, _, _) -> scrollPane.setVvalue(1.0));
    }

    // =========================
    // LOAD CHAT LIST
    // =========================
    private Task<List<Long>> getListTask() {

        var task = new ChatRestController().getChats(employeeId);

        task.setOnSucceeded(_ -> {
            List<Long> chatIds = task.getValue();
            if (chatIds == null) return;

            for (Long roomId : chatIds) {
                StyledButton btn = new StyledButton();
                btn.setText("Chat " + roomId);
                btn.setMaxWidth(Double.MAX_VALUE);

                btn.setOnAction(_ -> openChat(roomId));

                chats.getChildren().add(btn);
                getUnreadMessagesTask(roomId, employeeId);
            }

        });

        return task;
    }

    private void getUnreadMessagesTask(Long roomId, Long employeeId) {
        var task = new ChatRestController().getUnreadMessages(roomId, employeeId);
        task.setOnSucceeded(_ -> {
            List<MessageDTO>  messages = task.getValue();
            saveToFile(messages);
        });
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

        loadFromFile(chatId);

        setupWebSocketClient();
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
        saveToFile(List.of(msg));
    }

    private void saveToFile(List<MessageDTO> msg) {
        try {
            Path file = baseDir.resolve(msg.getFirst().chatId() + ".txt");

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
                            String trimmed = line.trim();
                            if (trimmed.startsWith("[")) {
                                MessageDTO[] msgs = mapper.readValue(trimmed, MessageDTO[].class);
                                for (MessageDTO msg : msgs) {
                                    chatCache
                                            .computeIfAbsent(chatId, _ -> new ArrayList<>())
                                            .add(msg);

                                    renderMessage(msg);
                                }
                            } else {
                                MessageDTO msg = mapper.readValue(trimmed, MessageDTO.class);

                                chatCache
                                        .computeIfAbsent(chatId, _ -> new ArrayList<>())
                                        .add(msg);

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