package com.unimib.assignment3.UI.view.controller.impl.layout;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimib.assignment3.UI.model.controller.ChatRestController;
import com.unimib.assignment3.UI.model.dto.MessageDTO;
import com.unimib.assignment3.UI.utils.SessionManagerSingleton;
import com.unimib.assignment3.UI.view.components.impl.custom.AlertDialog;
import com.unimib.assignment3.UI.view.components.impl.custom.ChatEntry;
import com.unimib.assignment3.UI.view.components.impl.custom.StyledButton;
import com.unimib.assignment3.UI.view.controller.abstr.DefaultController;
import com.unimib.assignment3.UI.web_socket_client.ChatWebSocketClientApp;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

public class ChatViewController implements DefaultController {

    @FXML private VBox chats;
    @FXML private VBox chatContainer;
    @FXML private TextField inputForm;
    @FXML private Button addChat;
    @FXML private Button sendButton;
    @FXML private ScrollPane scrollPane;

    private long employeeId;

    ChatWebSocketClientApp chatWebSocketClientApp;

    @FXML
    private void initialize() {

        employeeId = (long) SessionManagerSingleton.getInstance()
                .getAttribute("employeeId");

        var task = ChatRestController.getChats(employeeId);

        task.setOnSucceeded(event -> {
            List<Long> chatIds = task.getValue();
            if (chatIds == null) return;

            chatIds.forEach(chatId -> {
                StyledButton btn = new StyledButton();
                btn.setText("Chat " + chatId);

                chats.getChildren().add(btn);
            });
        });

        ChatWebSocketClientApp webSocketClientApp = new ChatWebSocketClientApp();
        try {
            webSocketClientApp.start();
        } catch (Exception e) {
            System.out.println("Could not connect to GanttCalendar");
            AlertDialog.showAlert("Error", "Could not connect to GanttCalendar: " + e.getMessage());
        }


        webSocketClientApp.receiveMessage().addListener((obs, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) return;

            Platform.runLater(() -> {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    MessageDTO message = mapper.readValue(newValue, MessageDTO.class);
                    Label messageLabel = createMessage(message.message());
                    ChatEntry chatEntry;
                    if(message.senderId().equals(employeeId)) {
                        chatEntry = new ChatEntry(messageLabel, new Label());
                    } else {
                        chatEntry = new ChatEntry(new Label(), messageLabel);
                    }
                    Region spacer = new Region();
                    spacer.setPrefHeight(10);
                    chatContainer.getChildren().addAll(chatEntry, spacer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });

        sendButton.setOnAction(event -> {
            String text = inputForm.getText();
            if (text == null || text.isEmpty()) return;

            try {
                MessageDTO messageDTO = new MessageDTO(1L, employeeId, text);

                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(messageDTO);

                webSocketClientApp.sendMessage( json);

                inputForm.clear();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.vvalueProperty().bind(chatContainer.heightProperty());

        new Thread(task).start();
    }

    // =========================
    // UI HELPER
    // =========================
    private Label createMessage(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("insert-text-lbl");
        return label;
    }
}