package com.unimib.GUI.UI.view.controller.abstr;

import com.unimib.GUI.UI.viewmodel.impl.ChatViewModel;
import com.unimib.GUI.model.dto.MessageDTO;
import com.unimib.GUI.UI.view.components.impl.custom.ChatEntry;
import com.unimib.GUI.UI.view.components.impl.layout.Chat;
import com.unimib.GUI.utils.UserSession;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;


public abstract class ChatController extends DefaultController {

    protected Chat chat;

    @FXML
    protected VBox chats;

    @FXML
    protected VBox chatContainer;

    @FXML
    protected VBox chatArea;

    @FXML
    protected TextField inputForm;

    @FXML
    protected Button sendButton;

    @FXML
    protected Button backButton;

    @FXML
    protected ScrollPane scrollPane;

    @FXML
    protected VBox closedChatArea;

    @FXML
    protected VBox unmatchedEmployeeContainer;

    protected long employeeId;

    protected Long selectedChatId = null;

    protected ChatViewModel viewModel;

    protected final Map<Long, List<MessageDTO>> chatCache;

    protected final Path baseDir = Paths.get(
            System.getProperty("user.home"),
            "chat-app",
            "chats"
    );


    public ChatController(Chat chat, Map<Long, List<MessageDTO>> chatCache, UserSession userSession) {
        super(userSession);
        this.chat = chat;
        this.chatCache = chatCache;
        employeeId = userSession.getEmployeeId();
        viewModel = new ChatViewModel(employeeId);
    }


    @FXML
    private void initialize() {
    }


    protected void baseInitialize() {
        initialize();
    }


    public void adoptStateFrom(ChatController other) {
        this.selectedChatId = other.selectedChatId;
        this.employeeId = other.employeeId;
        this.viewModel = other.viewModel;
    }

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
        label.setWrapText(true);

        return label;
    }

    /**
     * Called only when leaving the chat feature entirely (e.g. logout),
     * NOT on Open<->Closed state switches, since the viewModel is shared
     * and reused by the next controller via adoptStateFrom().
     */
    public void destroy() {
        disposeListeners();
        viewModel.destroy();
    }

    public void detach() {
        disposeListeners();
    }
}