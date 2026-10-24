package com.unimib.GUI.UI.view.controller.impl.layout;

import com.unimib.GUI.UI.state.ApplicationStateManager;
import com.unimib.GUI.UI.view.components.impl.layout.Chat;
import com.unimib.GUI.UI.view.components.impl.layout.Login;
import com.unimib.GUI.UI.view.components.impl.layout.Sidebar;
import com.unimib.GUI.UI.view.components.impl.layout.TaskContainer;
import com.unimib.GUI.UI.view.controller.abstr.DefaultController;
import com.unimib.GUI.UI.view.utils.ComponentVisibilityUtils;
import com.unimib.GUI.UI.viewmodel.impl.DashboardViewModel;
import com.unimib.GUI.model.dto.TaskStatsDTO;
import com.unimib.GUI.model.dto.WorkerDTO;
import com.unimib.GUI.utils.ImageHelper;
import com.unimib.GUI.utils.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Arc;

public class DashboardContainerController extends DefaultController {

    @FXML private VBox activeTaskContainer;
    @FXML private Button toggleMainWindowButton;
    @FXML private Button createTaskButton;
    @FXML private Button chatButton;
    @FXML private Button logOutButton;

    @FXML private BorderPane workerAvatarContainer;
    @FXML private Label workerGenerabilityLabel;
    @FXML private Label workerEmailLabel;

    @FXML private Arc progressCompletedTaskArc;
    @FXML private Arc progressStartedTaskArc;

    @FXML private Label progressCompletedTaskLabel;
    @FXML private Label progressStartedTaskLabel;

    @FXML private Label progressCompletedTaskFractionLabel;
    @FXML private Label progressStartedTaskFractionLabel;

    private Sidebar sidebar;
    private BorderPane root;
    private Chat chat;

    private final DashboardViewModel viewModel;

    public DashboardContainerController(UserSession userSession) {
        super(userSession);
        viewModel = new DashboardViewModel(userSession);
    }

    @FXML
    public void initialize() {
        // Start both rings empty so they never show stale values
        setTaskProgress(progressCompletedTaskArc, progressCompletedTaskLabel, 0);
        setTaskProgress(progressStartedTaskArc, progressStartedTaskLabel, 0);
        setTaskFraction(progressCompletedTaskFractionLabel, 0, 0);
        setTaskFraction(progressStartedTaskFractionLabel, 0, 0);

        configureSidebarActions();
        observeViewModel();
        showUserInfo();
    }

    public void setSidebar(Sidebar sidebar) {
        this.sidebar = sidebar;
        this.activeTaskContainer = sidebar.getActiveTaskContainer();
        this.toggleMainWindowButton = sidebar.getToggleMainWindowButton();
        this.createTaskButton = sidebar.getCreateTaskButton();
        this.chatButton = sidebar.getChatButton();
        this.logOutButton = sidebar.getLogOutButton();
        this.toggleMainWindowButton.setText("OPEN TASKS");
        configureSidebarActions();
    }

    private void showUserInfo() {
        viewModel.getWorker();
    }

    private void observeViewModel() {
        observeState(
                viewModel.workerStateProperty(),
                this::renderWorker,
                this::showError
        );

        observeState(
                viewModel.taskStatsStateProperty(),
                this::renderTaskStats,
                this::showError
        );
    }

    private void renderWorker(WorkerDTO worker) {
        if (worker == null) {
            return;
        }

        ImageHelper imageHelper = new ImageHelper();

        workerAvatarContainer.setCenter(
                imageHelper.createCircularImageViewWithGoldStroke(
                        imageHelper.createImageFromBase64(worker.encodedImage()),
                        70,
                        5
                )
        );

        workerGenerabilityLabel.setText(
                worker.name() + " " + worker.surname() + "\n" + worker.role()
        );

        workerEmailLabel.setText(worker.email());

        viewModel.getTaskStats(worker.workerId());
    }

    private void renderTaskStats(TaskStatsDTO taskStatsDTO) {
        if (taskStatsDTO == null || taskStatsDTO.totalTasks() <= 0) {
            setTaskProgress(progressCompletedTaskArc, progressCompletedTaskLabel, 0);
            setTaskProgress(progressStartedTaskArc, progressStartedTaskLabel, 0);
            setTaskFraction(progressCompletedTaskFractionLabel, 0, 0);
            setTaskFraction(progressStartedTaskFractionLabel, 0, 0);
            return;
        }

        long total = (long) taskStatsDTO.totalTasks();
        long completed = (long) taskStatsDTO.completedTasks();
        long started = (long) taskStatsDTO.inProgressTasks();

        setTaskProgress(progressCompletedTaskArc, progressCompletedTaskLabel, (double) completed / total);
        setTaskProgress(progressStartedTaskArc, progressStartedTaskLabel, (double) started / total);

        setTaskFraction(progressCompletedTaskFractionLabel, completed, total);
        setTaskFraction(progressStartedTaskFractionLabel, started, total);
    }

    private void setTaskProgress(Arc arc, Label label, double progress) {
        if (arc == null || label == null) {
            return;
        }

        double value = Double.isFinite(progress)
                ? Math.clamp(progress, 0.0, 1.0)
                : 0.0;

        // Negative length = clockwise from the start angle (12 o'clock)
        arc.setLength(-360.0 * value);
        label.setText(String.format("%.0f%%", value * 100.0));
    }

    private void setTaskFraction(Label label, long count, long total) {
        if (label == null) {
            return;
        }

        label.setText(count + " / " + total);
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
                ApplicationStateManager manager =
                        userSession.applicationStateManager();

                manager.replaceWindow(new Login(userSession));
            });
        }

        if (toggleMainWindowButton != null) {
            toggleMainWindowButton.setOnAction(_ -> {
                sidebar.setChatActionsVisible(true);

                sidebar.getChildren().forEach(node ->
                        ComponentVisibilityUtils.setVisible(true, node)
                );

                ApplicationStateManager manager =
                        userSession.applicationStateManager();

                manager.replaceWindow(new TaskContainer(userSession));
            });
        }
    }
}