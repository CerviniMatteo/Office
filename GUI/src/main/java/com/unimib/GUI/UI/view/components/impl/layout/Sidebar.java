package com.unimib.GUI.UI.view.components.impl.layout;

import com.unimib.GUI.UI.view.controller.impl.layout.SidebarController;
import com.unimib.GUI.UI.view.utils.FXMLUtilLoader;
import com.unimib.GUI.utils.UserSession;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Sidebar extends HBox {

    private final SidebarController controller;

    public Sidebar(UserSession userSession) {
        super();
        this.controller = new SidebarController(userSession);
        FXMLUtilLoader.load(this, controller, "/components/Sidebar.fxml", null);
    }

    public VBox getActiveTaskContainer() {
        return controller.getActiveTaskContainer();
    }

    public Button getToggleMainWindowButton() {
        return controller.getToggleMainWindowdButton();
    }

    public Button getCreateTaskButton() {
        return controller.getCreateTaskButton();
    }

    public Button getChatButton() {
        return controller.getChatButton();
    }

    public Button getLogOutButton() {
        return controller.getLogOutButton();
    }

    public VBox getChatActionsContainer() {
        return controller.getChatActionsContainer();
    }

    public void setChatActionsVisible(boolean visible) {
        controller.setChatActionsVisible(visible);
    }
}
