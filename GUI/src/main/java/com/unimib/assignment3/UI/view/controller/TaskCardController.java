package com.unimib.assignment3.UI.view.controller;

import com.unimib.assignment3.UI.model.controller.TaskController;
import com.unimib.assignment3.UI.model.dto.AcceptTaskRequestDTO;
import com.unimib.assignment3.UI.model.dto.ChangeTaskStateRequestDTO;
import com.unimib.assignment3.UI.model.dto.StartTaskRequestDTO;
import com.unimib.assignment3.UI.model.dto.TaskDTO;
import com.unimib.assignment3.UI.model.enums.TaskState;
import com.unimib.assignment3.UI.utils.ImageHelper;
import com.unimib.assignment3.UI.utils.SessionManagerSingleton;
import com.unimib.assignment3.UI.view.utils.GridHelper;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.unimib.assignment3.UI.utils.StringHelper.replaceUnderscores;
import static com.unimib.assignment3.UI.view.components.AlertDialog.showAlert;
import static com.unimib.assignment3.UI.view.utils.ComponentVisibilityUtils.*;

@SuppressWarnings("unused")
public class TaskCardController {

    private static final int SIZE = 50;
    public static final double STROKE_WIDTH = 3;

    @FXML
    private Label titleLabel;
    @FXML
    private Label stateLabel;
    @FXML
    private Button changeStateButton;
    @FXML
    private Button acceptButton;
    @FXML
    private GridPane workersGrid;
    @FXML
    private Label dateLabel;

    private final TaskController taskController = new TaskController();
    private final List<Long> acceptedBy = new ArrayList<>();
    private TaskState taskState;
    private TaskDTO currentTask;

    public void setTask(TaskDTO task) {
        currentTask = task;
        taskState = currentTask.taskState();

        titleLabel.setText(task.description());
        stateLabel.setText(replaceUnderscores(taskState.toString()));

        acceptedBy.clear();
        if (task.assignedWorkers() != null) {
            acceptedBy.addAll(task.assignedWorkers().keySet());
        }

        setStateLabelStyle();
        setupButtons();
        setWorkersPictures();
        setDateLabel();
    }

    private void setStateLabelStyle() {
        switch (taskState) {
            case TO_BE_STARTED -> stateLabel.getStyleClass().add("task-to-start");
            case STARTED -> stateLabel.getStyleClass().add("task-started");
            case DONE -> stateLabel.getStyleClass().add("task-done");
        }
    }

    private void setupButtons() {
        setupAcceptButton();
        setupChangeStateButton();
    }

    private void setupAcceptButton() {
        acceptButton.setOnAction(null);
        Long currentWorkerId = (Long) SessionManagerSingleton.getInstance().getAttribute("employeeId");

        switch (taskState) {
            case TO_BE_STARTED -> setVisible(false, acceptButton);
            case STARTED -> {
                if (acceptedBy.contains(currentWorkerId)) {
                    setVisible(false, acceptButton);
                    acceptButton.setText("ACCEPT TASK");
                    acceptButton.setOnAction(e -> acceptCurrentTask(currentWorkerId));
                } else {
                    setEnabled(acceptButton);
                }
            }
            case DONE -> {
                setEnabled(acceptButton);
                acceptButton.setText("RESET TASK");
                acceptButton.setOnAction(e -> resetTask());
            }
        }
    }

    private void setupChangeStateButton() {
        changeStateButton.setOnAction(null);
        switch (taskState) {
            case TO_BE_STARTED -> {
                setEnabled(changeStateButton);
                changeStateButton.setText("START TASK");
                changeStateButton.setOnAction(e -> startTask());
            }
            case STARTED -> {
                setEnabled(changeStateButton);
                changeStateButton.setText("COMPLETE TASK");
                changeStateButton.setOnAction(e -> completeTask());
            }
            case DONE -> setVisible(false, changeStateButton);
        }
    }

    private void startTask() {
        try {
            StartTaskRequestDTO payload = new StartTaskRequestDTO(
                    currentTask.taskId(),
                    (Long) SessionManagerSingleton.getInstance().getAttribute("employeeId")
            );
            payload.validate();
            Task<String> task = taskController.startTask(payload);
            task.setOnSucceeded(ev -> System.out.println("Task started!"));
            task.setOnFailed(ev -> showAlert("Error", task.getException().getMessage()));
            new Thread(task).start();
        } catch (Exception ex) {
            showAlert("Error", "Failed to create request payload");
        }
    }

    private void acceptCurrentTask(Long workerId) {
        try {
            AcceptTaskRequestDTO payload = new AcceptTaskRequestDTO(currentTask.taskId(), workerId);
            payload.validate();
            Task<String> task = taskController.acceptTask(payload);
            task.setOnSucceeded(ev -> {
                setDisabled(acceptButton);
                acceptedBy.add(workerId);
            });
            task.setOnFailed(ev -> showAlert("Error", task.getException().getMessage()));
            new Thread(task).start();
        } catch (Exception ex) {
            showAlert("Error", "Failed to create request payload");
        }
    }

    private void completeTask() {
        try {
            ChangeTaskStateRequestDTO payload = new ChangeTaskStateRequestDTO(currentTask.taskId(), taskState);
            payload.validate();
            Task<String> task = taskController.changeTaskState(payload);
            task.setOnSucceeded(ev -> System.out.println("Task completed!"));
            task.setOnFailed(ev -> showAlert("Error", task.getException().getMessage()));
            new Thread(task).start();
        } catch (Exception ex) {
            showAlert("Error", "Failed to create request payload");
        }
    }

    private void resetTask() {
        try {
            Task<String> task = taskController.resetTaskState(currentTask.taskId());
            task.setOnSucceeded(ev -> setDisabled(acceptButton));
            task.setOnFailed(ev -> showAlert("Error", task.getException().getMessage()));
            new Thread(task).start();
        } catch (Exception ex) {
            showAlert("Error", "Failed to create request payload");
        }
    }

    public void setWorkersPictures() {
        if (currentTask.assignedWorkers() == null || currentTask.assignedWorkers().isEmpty()) {
            GridHelper.clearGrid(workersGrid);
            return;
        }
        GridHelper.ensureVisible(workersGrid, true);
        Long currentWorkerId = (Long) SessionManagerSingleton.getInstance().getAttribute("employeeId");
        ImageHelper imageHelper = new ImageHelper();
        for (Map.Entry<Long, String> entry : currentTask.assignedWorkers().entrySet()) {
            if (entry.getKey().equals(currentWorkerId)) {
                String updatedImage = imageHelper.addGoldStrokeAndReturnBase64(entry.getValue(), SIZE, STROKE_WIDTH);
                entry.setValue(updatedImage);
            }
            GridHelper.addImageBase64(workersGrid, entry.getValue(), imageHelper, SIZE, 3);
        }
    }

    private void setDateLabel() {
        setVisible(false, dateLabel);
        if (taskState == TaskState.STARTED && currentTask.startDate() != null) {
            setVisible(true, dateLabel);
            dateLabel.setText("TASK STARTED ON: " + currentTask.startDate());
        } else if (taskState == TaskState.DONE && currentTask.endDate() != null) {
            setVisible(true, dateLabel);
            dateLabel.setText("TASK COMPLETED ON: " + currentTask.endDate());
        }
    }

    public Label getStateLabel() {
        return stateLabel;
    }

    public TaskDTO getCurrentTask() {
        return currentTask;
    }
}
