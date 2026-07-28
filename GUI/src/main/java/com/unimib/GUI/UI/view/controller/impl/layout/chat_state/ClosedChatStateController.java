package com.unimib.GUI.UI.view.controller.impl.layout.chat_state;

import com.unimib.GUI.UI.view.components.impl.custom.UnmatchedEmployeeBar;
import com.unimib.GUI.model.dto.ChatInfoDTO;
import com.unimib.GUI.model.dto.MessageDTO;
import com.unimib.GUI.model.dto.WorkerInfoDTO;
import com.unimib.GUI.UI.view.components.impl.custom.StyledButton;
import com.unimib.GUI.UI.view.components.impl.layout.Chat;
import com.unimib.GUI.UI.view.controller.abstr.ChatController;
import javafx.fxml.FXML;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.unimib.GUI.UI.view.utils.ComponentVisibilityUtils.setVisible;

public class ClosedChatStateController extends ChatController {

    public ClosedChatStateController(Chat chat) {
        super(chat, new HashMap<>());
    }

    protected ClosedChatStateController(Chat chat, Map<Long, List<MessageDTO>> chatCache) {
        super(chat, chatCache);
    }

    @FXML
    private void initialize() {
        super.baseInitialize();

        setVisible(false, chatArea);

        observeState(
                viewModel.getNewChatsStateProperty(),
                this::showUnmatchedEmployees,
                this::showError
        );


        observeState(
                viewModel.getChatsStateProperty(),
                null,
                this::showChatList,
                this::showError
        );

        observeState(
                viewModel.getCreateChatStateProperty(),
                _ -> {
                    viewModel.loadChats();
                    viewModel.getUnMatchedEmployeeInfos();
                },
                this::showError
        );

        viewModel.loadChats();
        viewModel.getUnMatchedEmployeeInfos();
    }

    private void showUnmatchedEmployees(List<WorkerInfoDTO> employees) {
        if (unmatchedEmployeeContainer == null) {
            return;
        }

        unmatchedEmployeeContainer.getChildren().clear();

        if (employees == null || employees.isEmpty()) {
            return;
        }

        for (WorkerInfoDTO employee : employees) {
            UnmatchedEmployeeBar bar = new UnmatchedEmployeeBar(employee);

            bar.getController().setOnCreateClick(targetEmployeeId ->
                    viewModel.createChat(targetEmployeeId)
            );

            unmatchedEmployeeContainer.getChildren().add(bar);
        }
    }

    private void showChatList(List<ChatInfoDTO> chatInfos) {
        if (chatInfos == null) return;

        chats.getChildren().clear();

        for (ChatInfoDTO chatInfo : chatInfos) {
            StyledButton btn = new StyledButton();
            btn.setText(chatInfo.receiverInfo());
            btn.setMaxWidth(Double.MAX_VALUE);

            btn.setOnAction(_ -> openChat(chatInfo.chatId()));

            chats.getChildren().add(btn);
        }
    }

    private void openChat(Long chatId) {
        viewModel.openChat(chatId);

        OpenChatStateController open = new OpenChatStateController(this.chat, this.chatCache);
        open.adoptStateFrom(this);
        chat.setController(open);
        open.displayChat(chatId);
    }
}