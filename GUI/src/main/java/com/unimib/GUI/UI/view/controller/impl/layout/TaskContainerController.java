package com.unimib.GUI.UI.view.controller.impl.layout;

import com.unimib.GUI.model.controller.impl.TaskRestController;
import com.unimib.GUI.model.dto.TaskDTO;
import com.unimib.GUI.utils.SessionManagerSingleton;
import com.unimib.GUI.UI.view.components.abstr.TaskCardBase;
import com.unimib.GUI.UI.view.components.impl.custom.AlertDialog;
import com.unimib.GUI.UI.view.components.impl.layout.Chat;
import com.unimib.GUI.UI.view.components.impl.layout.TaskCreationForm;
import com.unimib.GUI.UI.view.controller.abstr.DefaultController;
import com.unimib.GUI.UI.view.factory.TaskCardFactory;
import com.unimib.GUI.UI.state.ApplicationStateManager;
import com.unimib.GUI.web_socket_client.ChatWebSocketClientApp;
import com.unimib.GUI.web_socket_client.TaskWebSocketClientApp;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskContainerController implements DefaultController {
    @FXML
    private VBox notStartedTaskBox;

     @FXML
     private VBox startedTaskBox;

    @FXML
    private VBox doneTaskBox;

    @FXML
    private HBox centerContainer;

    @FXML
    private VBox activeTaskContainer;

    private final Map<Long, TaskCardBase> tasks = new HashMap<>();
    private TaskRestController taskRestController;

    @FXML
    private Button createTaskButton;

    @FXML
    private Button chatButton;

    @FXML
    private Button logOutButton;

    private Chat chat;

     @FXML
     public void initialize() {
         centerContainer.setFillHeight(true);

        taskRestController = new TaskRestController();

        List<TaskDTO> fetchedTasks = taskRestController.fetchTasks();
        if (fetchedTasks != null) {
            fetchedTasks.forEach(taskDTO -> {
                TaskCardBase taskCard = TaskCardFactory.create(taskDTO);
                if (taskCard != null) {
                    tasks.put(taskDTO.taskId(), taskCard);
                    addTask(taskDTO, taskCard);
                }
            });
        }

        TaskWebSocketClientApp webSocketClientApp = new TaskWebSocketClientApp();
        try {
            webSocketClientApp.start();
        } catch (Exception e) {
            System.out.println("Could not connect to TaskContainer");
            e.printStackTrace();
            AlertDialog.showAlert("Error", "Could not connect to TaskContainer: " + e.getMessage());
        }

        webSocketClientApp.getProperty().addListener((_, _, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                handleTaskChange(newVal);
            }

        });


         TaskCreationForm form = new TaskCreationForm(new TaskCreationFormController());
         centerContainer.getChildren().add(form);

         createTaskButton.setOnAction(_ -> {
             centerContainer.getChildren().remove(chat);
             if(centerContainer.getChildren().contains(form)){
                 centerContainer.getChildren().remove(form);
             }else{
                 form.clear();
                 centerContainer.getChildren().add(form);
             }
         });



         chatButton.setOnAction(_ -> {
             if(chat == null){
                 chat = new Chat();
             }
            if(centerContainer.getChildren().contains(chat)){
                chatButton.setText("OPEN CHAT");
                centerContainer.getChildren().remove(form);
                centerContainer.getChildren().remove(chat);
            } else {
                chatButton.setText("CLOSE CHAT");
                centerContainer.getChildren().add(chat);
                centerContainer.getChildren().remove(form);
            }
        });

        logOutButton.setOnAction(_ -> {
            webSocketClientApp.stop();
            ChatWebSocketClientApp.resetInstance();
            SessionManagerSingleton sessionManagerSingleton = SessionManagerSingleton.getInstance();
            sessionManagerSingleton.removeAttribute("employeeId");
            ApplicationStateManager stateManager = ApplicationStateManager.getInstance();
            stateManager.goBack();
        });
    }

    private void handleTaskChange(String message) {
        System.out.println("GanttCalendarController received WS message: " + message);
        if (message.contains("FETCH_TASK:")) {
            String substring = message.substring(message.indexOf(":") + 1);
            Long taskId = Long.valueOf(substring);
            System.out.println(taskId);
            Platform.runLater(() -> updateTaskCard(taskId));
        }
        if (message.contains("DELETE_TASK:")) {
            String substring = message.substring(message.indexOf(":") + 1);
            Long taskId = Long.valueOf(substring);
            System.out.println(taskId);
            Platform.runLater(() -> deleteEntry(taskId));
        }
    }


        public void updateTaskCard(Long taskId) {
            Task<TaskDTO> task = new Task<>() {
                @Override protected TaskDTO call() { return taskRestController.fetchTask(taskId); }
            };

            task.setOnSucceeded(_ -> {
                TaskDTO updatedTask = task.getValue();
                if(updatedTask == null){
                    return;
                }

                removeTask(taskId);

                TaskCardBase updatedTaskCard = TaskCardFactory.create(updatedTask);
                if (updatedTaskCard != null) {
                    tasks.put(taskId, updatedTaskCard);
                    addTask(updatedTask, updatedTaskCard);
                }
            });

            new Thread(task).start();
        }

    public void deleteEntry(Long taskId) {
        TaskCardBase task = tasks.get(taskId);
        if(task == null){
            return;
        }
        removeTask(taskId);
        tasks.remove(taskId);
    }

    public void deleteActiveTaskFromDashboard(Long taskId) {
        activeTaskContainer.getChildren().removeIf(node -> taskId.toString().equals(node.getId()));
    }

    private void removeTask(Long taskId) {
        notStartedTaskBox.getChildren().removeIf(node -> taskId.toString().equals(node.getId()));
        startedTaskBox.getChildren().removeIf(node -> taskId.toString().equals(node.getId()));
        doneTaskBox.getChildren().removeIf(node -> taskId.toString().equals(node.getId()));
        deleteActiveTaskFromDashboard(taskId);
    }

    private void addTask(TaskDTO taskDTO, TaskCardBase taskCard) {
        taskCard.setId(taskDTO.taskId().toString());
        taskCard.getStyleClass().add("task-card-compact");

        switch (taskDTO.taskState()) {
            case TO_BE_STARTED -> {
                notStartedTaskBox.getChildren().add(taskCard);
            }

            case STARTED -> {
                startedTaskBox.getChildren().add(taskCard);
                Label taskLabel = new Label(taskDTO.description());
                taskLabel.setId(taskDTO.taskId().toString());
                taskLabel.getStyleClass().add("active-task-entry-lbl");
                taskLabel.setMaxWidth(Double.MAX_VALUE);
                taskLabel.setWrapText(true);
                activeTaskContainer.getChildren().add(taskLabel);
            }

            case DONE -> {
                doneTaskBox.getChildren().add(taskCard);
            }
        }
    }
}
