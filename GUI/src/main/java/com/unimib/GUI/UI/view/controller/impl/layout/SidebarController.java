package com.unimib.GUI.UI.view.controller.impl.layout;

import com.unimib.GUI.UI.view.controller.abstr.DefaultController;
import com.unimib.GUI.UI.view.utils.ComponentVisibilityUtils;
import com.unimib.GUI.utils.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class SidebarController extends DefaultController {

    @FXML private VBox activeTaskContainer;
    @FXML private VBox chatActionsContainer;
    @FXML private Button toggleMainWindowButton;
    @FXML private Button createTaskButton;
    @FXML private Button chatButton;
    @FXML private Button logOutButton;

    public SidebarController(UserSession userSession) {
        super(userSession);
    }

    public Button getToggleMainWindowButton() {
        return toggleMainWindowButton;
    }

    public void setToggleMainWindowAction(Runnable action) {
        if (toggleMainWindowButton != null) {
            toggleMainWindowButton.setOnAction(_ -> action.run());
        }
    }

    public void setCreateTaskAction(Runnable action) {
        if (createTaskButton != null) {
            createTaskButton.setOnAction(_ -> action.run());
        }
    }

    public void setOpenChatAction(Runnable action) {
        if (chatButton != null) {
            chatButton.setOnAction(_ -> action.run());
        }
    }

    public void setLogOutAction(Runnable action) {
        if (logOutButton != null) {
            logOutButton.setOnAction(_ -> action.run());
        }
    }

    public void setChatActionsVisible(boolean visible) {
        if (chatActionsContainer != null) {
            ComponentVisibilityUtils.setVisible(visible, chatActionsContainer);
        }
    }

    public VBox getActiveTaskContainer() {
        return activeTaskContainer;
    }

    public VBox getChatActionsContainer() {
        return chatActionsContainer;
    }

    public Button getToggleMainWindowdButton() {
        return toggleMainWindowButton;
    }

    public Button getCreateTaskButton() {
        return createTaskButton;
    }

    public Button getChatButton() {
        return chatButton;
    }

    public Button getLogOutButton() {
        return logOutButton;
    }
}
