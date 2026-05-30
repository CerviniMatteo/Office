package com.unimib.GUI.view.controller.abstr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimib.GUI.model.dto.MessageDTO;
import com.unimib.GUI.utils.SessionManagerSingleton;
import com.unimib.GUI.view.components.impl.custom.AlertDialog;
import com.unimib.GUI.view.components.impl.custom.ChatEntry;
import com.unimib.GUI.view.components.impl.layout.Chat;
import com.unimib.GUI.web_socket_client.ChatWebSocketClientApp;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import java.nio.file.*;
import java.util.*;

public abstract class ChatController implements DefaultController {

    protected Chat chat;

    @FXML protected VBox chats;
    @FXML protected VBox chatContainer;
    @FXML protected VBox chatArea;
    @FXML protected TextField inputForm;
    @FXML protected Button sendButton;
    @FXML protected Button backButton;
    @FXML protected ScrollPane scrollPane;

    protected long employeeId;
    protected Long selectedChatId = null;

    protected ChatWebSocketClientApp chatWebSocketClientApp;

    protected final Map<Long, List<MessageDTO>> chatCache = new HashMap<>();
    protected final ObjectMapper mapper = new ObjectMapper();

    protected final Path baseDir = Paths.get(
            System.getProperty("user.home"),
            "chat-app",
            "chats"
    );

    public ChatController(Chat chat) {
        this.chat = chat;
    }


    @FXML
    private void initialize() {
        employeeId = (long) SessionManagerSingleton.getInstance()
                .getAttribute("employeeId");

        try {
            Files.createDirectories(baseDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        chatWebSocketClientApp = ChatWebSocketClientApp.getInstance();
        try {
            chatWebSocketClientApp.start();
        } catch (Exception e) {
            AlertDialog.showAlert("Error", "Could not connect: " + e.getMessage());
        }
    }

    protected void baseInitialize(){
            initialize();
    }

    // =========================
    // STATE TRANSFER
    // =========================
    /**
     * Copy shared state from another controller instance.
     * Called when switching between Closed and Open states to preserve cache and identifiers.
     */
    public void adoptStateFrom(ChatController other) {
        this.chatCache.putAll(other.chatCache);
        this.selectedChatId = other.selectedChatId;
        this.employeeId = other.employeeId;
        this.chatWebSocketClientApp = other.chatWebSocketClientApp;
    }

    // =========================
    // RENDER MESSAGE
    // =========================
    protected void renderMessage(MessageDTO msg) {
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

    protected Label createMessage(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("insert-text-lbl");
        return label;
    }
}