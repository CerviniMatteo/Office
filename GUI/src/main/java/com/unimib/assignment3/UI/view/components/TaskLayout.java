package com.unimib.assignment3.UI.view.components;

import com.unimib.assignment3.UI.model.controller.TaskController;
import com.unimib.assignment3.UI.model.enums.TaskState;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.unimib.assignment3.UI.model.dto.TaskDTO;
import static com.unimib.assignment3.UI.view.components.AlertDialog.showAlert;

public class TaskLayout extends ScrollPane {

    private final VBox toBeStartedColumn;
    private final VBox startedColumn;
    private final VBox doneColumn;
    private Map<Long, TaskDetailsWindow> taskDetails;

    public TaskLayout() {
        this.getStyleClass().add("task-layout");

        setHbarPolicy(ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollBarPolicy.NEVER);
        setFitToWidth(true);
        setPannable(true);

        toBeStartedColumn = new VBox(20);
        startedColumn = new VBox(20);
        doneColumn = new VBox(20);

        HBox mainLayout = new HBox(20, toBeStartedColumn, startedColumn, doneColumn);
        mainLayout.setPadding(new Insets(10, 10, 10, 10));
        setContent(mainLayout);

        setContent(mainLayout);

        HBox.setHgrow(toBeStartedColumn, Priority.ALWAYS);
        HBox.setHgrow(startedColumn, Priority.ALWAYS);
        HBox.setHgrow(doneColumn, Priority.ALWAYS);

        loadTasks();
    }

    public void updateTaskDetails(Long taskId) {
        new Thread(() -> {
            TaskController controller = new TaskController();
            TaskDTO taskDTO = controller.fetchTask(taskId);

            Platform.runLater(() -> {
                if (taskDTO == null) {
                    showAlert("Error", "Server is currently down");
                    return;
                }

                TaskDetailsWindow oldNode = taskDetails.get(taskId);
                if (oldNode != null) {
                    removeTaskFromColumns(oldNode);
                }

                TaskDetailsWindow newNode = new TaskDetailsWindow();
                newNode.setTask(taskDTO);
                addTaskToColumn(taskDTO, newNode);
                taskDetails.put(taskId, newNode);
            });
        }).start();
    }

    private void loadTasks() {
        new Thread(() -> {
            TaskController controller = new TaskController();
            List<TaskDTO> taskDTOS = controller.fetchTasks();
            taskDetails = new HashMap<>();

            Platform.runLater(() -> {
                toBeStartedColumn.getChildren().clear();
                startedColumn.getChildren().clear();
                doneColumn.getChildren().clear();

                if (taskDTOS == null) {
                    showAlert("Error", "Server is currently down");
                    return;
                }

                for (TaskDTO taskDTO : taskDTOS) {
                    TaskDetailsWindow node = new TaskDetailsWindow();
                    node.setTask(taskDTO);
                    addTaskToColumn(taskDTO, node);
                    taskDetails.put(taskDTO.taskId(), node);
                }
            });
        }).start();
    }

    private void addTaskToColumn(TaskDTO taskDTO, TaskDetailsWindow node) {
        TaskState state = taskDTO.taskState();
        if (state == null) return;

        switch (state) {
            case TaskState.TO_BE_STARTED -> toBeStartedColumn.getChildren().add(node);
            case TaskState.STARTED -> startedColumn.getChildren().add(node);
            case TaskState.DONE -> doneColumn.getChildren().add(node);
        }
    }

    private void removeTaskFromColumns(TaskDetailsWindow node) {
        toBeStartedColumn.getChildren().remove(node);
        startedColumn.getChildren().remove(node);
        doneColumn.getChildren().remove(node);
    }
}
