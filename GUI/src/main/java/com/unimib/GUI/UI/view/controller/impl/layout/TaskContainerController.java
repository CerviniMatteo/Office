
package com.unimib.GUI.UI.view.controller.impl.layout;

import com.unimib.GUI.UI.state.ApplicationStateManager;
import com.unimib.GUI.UI.view.components.abstr.TaskCardBase;
import com.unimib.GUI.UI.view.components.impl.layout.*;
import com.unimib.GUI.UI.view.components.impl.task.TaskCardSkeleton;
import com.unimib.GUI.UI.view.controller.abstr.DefaultController;
import com.unimib.GUI.UI.view.factory.TaskCardFactory;
import com.unimib.GUI.UI.viewmodel.impl.TaskViewModel;
import com.unimib.GUI.model.dto.TaskDTO;
import com.unimib.GUI.model.enums.TaskState;
import com.unimib.GUI.utils.UserSession;
import com.unimib.GUI.web_socket_client.TaskWebSocketClientApp;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TaskContainerController extends DefaultController {

    @FXML private VBox notStartedTaskBox;
    @FXML private VBox startedTaskBox;
    @FXML private VBox doneTaskBox;
    @FXML private HBox centerContainer;
    @FXML private VBox activeTaskContainer;

    @FXML private Button createTaskButton;
    @FXML private Button chatButton;
    @FXML private Button logOutButton;
    @FXML private Button toggleMainWindowButton;

    private final Map<Long, TaskCardBase> tasks = new HashMap<>();
    private final Map<Long, TaskCardBase> skeletonTasks = new HashMap<>();

    private Sidebar sidebar;
    private BorderPane root;
    private Chat chat;
    private TaskViewModel viewModel;
    private TaskCreationForm form;
    private TaskWebSocketClientApp webSocketClient;

    public TaskContainerController(UserSession userSession) {
        super(userSession);
    }

    public void setSidebar(Sidebar sidebar) {
        this.sidebar = sidebar;
        activeTaskContainer = sidebar.getActiveTaskContainer();
        createTaskButton = sidebar.getCreateTaskButton();
        chatButton = sidebar.getChatButton();
        logOutButton = sidebar.getLogOutButton();
        toggleMainWindowButton = sidebar.getToggleMainWindowButton();

        toggleMainWindowButton.setText("OPEN DASHBOARD");
        sidebar.setChatActionsVisible(true);

        configureSidebarActions();
    }

    public void setRoot(BorderPane root) {
        this.root = root;
    }

    @FXML
    public void initialize() {
        centerContainer.setFillHeight(true);

        setupCenterComponents();
        configureSidebarActions();
        setupButtons();

        viewModel = new TaskViewModel();
        observeTasks();
        observeTask();
        viewModel.fetchTasks();

        startWebSocketClient();
    }

    private void setupCenterComponents() {
        form = new TaskCreationForm();
        centerContainer.getChildren().add(form);
    }

    private void configureSidebarActions() {
        if (createTaskButton != null) {
            createTaskButton.setVisible(true);
            createTaskButton.setManaged(true);
            createTaskButton.setOnAction(_ -> toggleTaskForm());
        }

        if (chatButton != null) {
            chatButton.setOnAction(_ -> toggleChat());
        }

        if (toggleMainWindowButton != null) {
            toggleMainWindowButton.setOnAction(_ -> openDashboard());
        }
    }

    private void toggleTaskForm() {
        boolean isFormVisible = centerContainer.getChildren().contains(form);

        closeChat();

        if (isFormVisible) {
            centerContainer.getChildren().remove(form);
        } else {
            form.clear();
            centerContainer.getChildren().remove(form);
            centerContainer.getChildren().add(form);
        }
    }

    private void toggleChat() {
        boolean isChatVisible = chat != null
                && centerContainer.getChildren().contains(chat);

        if (isChatVisible) {
            closeChat();
            return;
        }

        if (chat == null) {
            chat = new Chat(userSession);
        }

        centerContainer.getChildren().remove(form);
        centerContainer.getChildren().remove(chat);
        centerContainer.getChildren().add(chat);

        chatButton.setText("CLOSE CHAT");
    }

    private void closeChat() {
        if (chat != null) {
            centerContainer.getChildren().remove(chat);
        }

        if (chatButton != null) {
            chatButton.setText("OPEN CHAT");
        }
    }

    private void openDashboard() {
        stopResources();

        ApplicationStateManager manager =
                userSession.applicationStateManager();

        manager.replaceWindow(new DashboardContainer(userSession));
    }

    private void setupButtons() {
        if (logOutButton != null) {
            logOutButton.setOnAction(_ -> {
                stopResources();

                ApplicationStateManager manager =
                        userSession.applicationStateManager();

                manager.replaceWindow(new Login(userSession));
            });
        }
    }

    private void startWebSocketClient() {
        webSocketClient = new TaskWebSocketClientApp();
        webSocketClient.start();

        webSocketClient.getProperty().addListener((_, _, message) -> {
            if (message != null && !message.isEmpty()) {
                handleTaskChange(message);
            }
        });
    }

    private void stopResources() {
        if (chat != null) {
            chat.destroy();
            chat = null;
        }

        if (webSocketClient != null) {
            webSocketClient.stop();
            webSocketClient = null;
        }
    }

    private void observeTasks() {
        showInitialSkeleton();

        observeState(
                viewModel.getTasksStateProperty(),
                () -> {},
                fetchedTasks -> {
                    removeInitialSkeleton();

                    if (fetchedTasks == null) {
                        return;
                    }

                    clearContainers();
                    tasks.clear();
                    skeletonTasks.clear();

                    for (TaskDTO taskDTO : fetchedTasks) {
                        TaskCardBase card =
                                TaskCardFactory.create(taskDTO, userSession);

                        if (card != null) {
                            tasks.put(taskDTO.taskId(), card);
                            addTask(taskDTO, card);
                        }
                    }
                },
                error -> {
                    removeInitialSkeleton();
                    showError(error);
                }
        );
    }

    private void observeTask() {
        observeState(
                viewModel.getTaskStateProperty(),
                () -> {},
                updatedTask -> {
                    if (updatedTask == null) {
                        return;
                    }

                    removeSkeleton(updatedTask.taskId());
                    removeTask(updatedTask.taskId());

                    TaskCardBase card =
                            TaskCardFactory.create(updatedTask, userSession);

                    if (card != null) {
                        tasks.put(updatedTask.taskId(), card);
                        addTask(updatedTask, card);
                    }
                },
                this::showError
        );
    }

    private void showInitialSkeleton() {
        clearContainers();

        for (int i = 0; i < 3; i++) {
            notStartedTaskBox.getChildren()
                    .add(newSkeletonPlaceholder(-1L));
            startedTaskBox.getChildren()
                    .add(newSkeletonPlaceholder(-1L));
            doneTaskBox.getChildren()
                    .add(newSkeletonPlaceholder(-1L));
        }
    }

    private TaskCardBase newSkeletonPlaceholder(Long taskId) {
        TaskDTO placeholder = new TaskDTO(
                taskId,
                "",
                TaskState.LOADING,
                null,
                null,
                Collections.emptyMap()
        );

        return TaskCardFactory.create(placeholder, userSession);
    }

    private void removeInitialSkeleton() {
        notStartedTaskBox.getChildren().removeIf(
                node -> node instanceof TaskCardSkeleton
        );
        startedTaskBox.getChildren().removeIf(
                node -> node instanceof TaskCardSkeleton
        );
        doneTaskBox.getChildren().removeIf(
                node -> node instanceof TaskCardSkeleton
        );
    }

    private void showTaskSkeleton(Long taskId) {
        removeSkeleton(taskId);

        TaskCardBase skeleton = newSkeletonPlaceholder(taskId);
        skeleton.setId("skeleton-" + taskId);

        skeletonTasks.put(taskId, skeleton);
        doneTaskBox.getChildren().add(skeleton);
    }

    private void removeSkeleton(Long taskId) {
        TaskCardBase skeleton = skeletonTasks.remove(taskId);

        if (skeleton == null) {
            return;
        }

        notStartedTaskBox.getChildren().remove(skeleton);
        startedTaskBox.getChildren().remove(skeleton);
        doneTaskBox.getChildren().remove(skeleton);
    }

    private void clearContainers() {
        notStartedTaskBox.getChildren().clear();
        startedTaskBox.getChildren().clear();
        doneTaskBox.getChildren().clear();

        if (activeTaskContainer != null) {
            activeTaskContainer.getChildren().clear();
        }
    }

    private void handleTaskChange(String message) {
        if (message.startsWith("FETCH_TASK:")) {
            Long taskId = Long.valueOf(
                    message.substring("FETCH_TASK:".length())
            );

            Platform.runLater(() -> updateTaskCard(taskId));

        } else if (message.startsWith("DELETE_TASK:")) {
            Long taskId = Long.valueOf(
                    message.substring("DELETE_TASK:".length())
            );

            Platform.runLater(() -> deleteEntry(taskId));
        }
    }

    private void updateTaskCard(Long taskId) {
        showTaskSkeleton(taskId);
        viewModel.fetchTask(taskId);
    }

    private void deleteEntry(Long taskId) {
        tasks.remove(taskId);
        removeSkeleton(taskId);
        removeTask(taskId);
    }

    private void removeTask(Long taskId) {
        String id = taskId.toString();

        notStartedTaskBox.getChildren().removeIf(
                node -> id.equals(node.getId())
        );
        startedTaskBox.getChildren().removeIf(
                node -> id.equals(node.getId())
        );
        doneTaskBox.getChildren().removeIf(
                node -> id.equals(node.getId())
        );

        if (activeTaskContainer != null) {
            activeTaskContainer.getChildren().removeIf(
                    node -> id.equals(node.getId())
            );
        }
    }

    private void addTask(TaskDTO taskDTO, TaskCardBase taskCard) {
        taskCard.setId(taskDTO.taskId().toString());
        taskCard.getStyleClass().add("task-card-compact");

        switch (taskDTO.taskState()) {
            case TO_BE_STARTED ->
                    notStartedTaskBox.getChildren().add(taskCard);

            case STARTED -> {
                startedTaskBox.getChildren().add(taskCard);

                Label label = new Label(taskDTO.description());
                label.setId(taskDTO.taskId().toString());
                label.getStyleClass().add("active-task-entry-lbl");
                label.setMaxWidth(Double.MAX_VALUE);
                label.setWrapText(true);

                activeTaskContainer.getChildren().add(label);
            }

            case DONE ->
                    doneTaskBox.getChildren().add(taskCard);
        }
    }
}
