package com.unimib.GUI.UI.view.controller.impl.layout;

import com.unimib.GUI.UI.state.ApplicationStateManager;
import com.unimib.GUI.UI.view.components.impl.layout.Chat;
import com.unimib.GUI.UI.view.components.impl.layout.Login;
import com.unimib.GUI.UI.view.components.impl.layout.Sidebar;
import com.unimib.GUI.UI.view.components.impl.layout.TaskContainer;
import com.unimib.GUI.UI.view.controller.abstr.DefaultController;
import com.unimib.GUI.UI.view.utils.ComponentVisibilityUtils;
import com.unimib.GUI.UI.viewmodel.impl.DashboardViewModel;
import com.unimib.GUI.model.dto.WorkerDTO;
import com.unimib.GUI.utils.ImageHelper;
import com.unimib.GUI.utils.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class DashboardContainerController extends DefaultController {

    @FXML private VBox activeTaskContainer;
    @FXML private Button toggleMainWindowButton;
    @FXML private Button createTaskButton;
    @FXML private Button chatButton;
    @FXML private Button logOutButton;

    @FXML private BorderPane workerAvatarContainer;
    @FXML private Label workerNameLabel;
    @FXML private Label workerSurnameLabel;
    @FXML private Label workerEmailLabel;


    private Sidebar sidebar;
    private BorderPane root;
    private Chat chat;

    private DashboardViewModel viewModel;

    public DashboardContainerController(UserSession userSession) {
        super(userSession);
        viewModel = new DashboardViewModel(userSession);
    }

    public void setSidebar(Sidebar sidebar) {
        this.sidebar = sidebar;
        this.activeTaskContainer = sidebar.getActiveTaskContainer();
        this.toggleMainWindowButton = sidebar.getToggleMainWindowButton();
        this.createTaskButton = sidebar.getCreateTaskButton();
        this.chatButton = sidebar.getChatButton();
        this.logOutButton = sidebar.getLogOutButton();

        configureSidebarActions();
    }

    private void showUserInfo() {
        WorkerDTO worker = viewModel.getWorker();
        if (worker != null) {
            ImageHelper imageHelper = new ImageHelper();
            workerAvatarContainer.setCenter(imageHelper.createCircularImageViewWithGoldStroke(imageHelper.createImageFromBase64(worker.encodedImage()), 100, 7));
            workerNameLabel.setText(worker.name());
            workerSurnameLabel.setText(worker.surname());
            workerEmailLabel.setText(worker.email());
        }
    }

    public void setRoot(BorderPane root) {
        this.root = root;
    }

    private void configureSidebarActions() {
        if (createTaskButton != null) {
            createTaskButton.setVisible(false);
            createTaskButton.setManaged(false);
        }
        if (sidebar != null) {
            sidebar.setChatActionsVisible(false);
        }
        if (logOutButton != null) {
            logOutButton.setOnAction(_ -> {
                ApplicationStateManager manager = userSession.applicationStateManager();
                manager.replaceWindow(new Login(userSession));
            });
        }

        if (toggleMainWindowButton != null) {
            toggleMainWindowButton.setOnAction(_ -> {
                toggleMainWindowButton.setText("OPEN DASHBOARD");
                sidebar.setChatActionsVisible(true);
                sidebar.getChildren().forEach(node -> {
                    ComponentVisibilityUtils.setVisible(true, node);
                });
                ApplicationStateManager manager = userSession.applicationStateManager();
                manager.replaceWindow(new TaskContainer(userSession));
            });
        }
    }

    @FXML
    public void initialize() {
        configureSidebarActions();
        showUserInfo();
    }
}