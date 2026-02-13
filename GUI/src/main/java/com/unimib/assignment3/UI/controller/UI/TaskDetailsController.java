package com.unimib.assignment3.UI.controller.UI;

import com.unimib.assignment3.UI.controller.rest.TaskController;
import com.unimib.assignment3.UI.dto.AcceptTaskRequestDTO;
import com.unimib.assignment3.UI.dto.ChangeTaskStateRequestDTO;
import com.unimib.assignment3.UI.dto.StartTaskRequestDTO;
import com.unimib.assignment3.UI.dto.TaskDTO;
import com.unimib.assignment3.UI.enums.TaskState;
import com.unimib.assignment3.UI.utils.ImageHelper;
import com.unimib.assignment3.UI.utils.SessionManagerSingleton;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.unimib.assignment3.UI.components.AlertDialog.showAlert;

@SuppressWarnings("unused")
public class TaskDetailsController {

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
        stateLabel.setText(replaceUnderscores(taskState));

        acceptedBy.clear();
        if (task.assignedWorkers() != null) {
            acceptedBy.addAll(task.assignedWorkers().keySet());
        }

        setStateLabelStyle();
        setupButtons();
        setWorkersPictures();
        setDateLabel();
    }

    private String replaceUnderscores(TaskState taskState) {
        return taskState.toString().trim()
                .replace("_", " ")
                .replaceAll("\\s+", " ")
                .toUpperCase();
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
        acceptButton.setOnAction(null); // rimuove handler precedente
        Long currentWorkerId = (Long) SessionManagerSingleton.getInstance().getAttribute("employeeId");

        switch (taskState) {
            case TO_BE_STARTED -> acceptButton.setVisible(false);
            case STARTED -> {
                if (!acceptedBy.contains(currentWorkerId)) {
                    acceptButton.setVisible(true);
                    acceptButton.setDisable(false);
                    acceptButton.setText("ACCEPT TASK");
                    acceptButton.setOnAction(e -> acceptCurrentTask(currentWorkerId));
                } else {
                    acceptButton.setVisible(false);
                }
            }
            case DONE -> {
                acceptButton.setVisible(true);
                acceptButton.setDisable(false);
                acceptButton.setText("RESET TASK");
                acceptButton.setOnAction(e -> resetTask());
            }
        }
    }

    private void setupChangeStateButton() {
        changeStateButton.setOnAction(null);

        switch (taskState) {
            case TO_BE_STARTED -> {
                changeStateButton.setVisible(true);
                changeStateButton.setDisable(false);
                changeStateButton.setText("START TASK");
                changeStateButton.setOnAction(e -> startTask());
            }
            case STARTED -> {
                changeStateButton.setVisible(true);
                changeStateButton.setDisable(false);
                changeStateButton.setText("COMPLETE TASK");
                changeStateButton.setOnAction(e -> completeTask());
            }
            case DONE -> changeStateButton.setVisible(false);
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
                acceptButton.setDisable(true);
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
            task.setOnSucceeded(ev -> acceptButton.setDisable(true));
            task.setOnFailed(ev -> showAlert("Error", task.getException().getMessage()));
            new Thread(task).start();
        } catch (Exception ex) {
            showAlert("Error", "Failed to create request payload");
        }
    }

    public void setWorkersPictures() {
        if (currentTask.assignedWorkers() == null || currentTask.assignedWorkers().isEmpty()) {
            workersGrid.setVisible(false);
            workersGrid.setManaged(false);
            return;
        }
        workersGrid.setVisible(true);
        Long currentWorkerId = (Long) SessionManagerSingleton.getInstance().getAttribute("employeeId");
        ImageHelper imageHelper = new ImageHelper();
        for (Map.Entry<Long, String> entry : currentTask.assignedWorkers().entrySet()) {
            if (entry.getKey().equals(currentWorkerId)) {
                String updatedImage = imageHelper.addGoldStrokeAndReturnBase64(entry.getValue(), SIZE, STROKE_WIDTH);
                entry.setValue(updatedImage);
            }
            addWorkerImageToGrid(entry.getValue());
        }
    }

    private void addWorkerImageToGrid(String base64Image) {
        ImageHelper imageHelper = new ImageHelper();
        ImageView imageView = imageHelper.createCircularImageView(
                imageHelper.createImageFromBase64(base64Image),
                SIZE
        );
        int cols = 3;
        int col = workersGrid.getChildren().size() % cols;
        int row = workersGrid.getChildren().size() / cols;
        workersGrid.add(imageView, col, row);
    }

    private void setDateLabel() {
        dateLabel.setVisible(false);
        dateLabel.setManaged(false);
        if (taskState == TaskState.STARTED && currentTask.startDate() != null) {
            dateLabel.setVisible(true);
            dateLabel.setManaged(true);
            dateLabel.setText("TASK STARTED ON: " + currentTask.startDate());
        } else if (taskState == TaskState.DONE && currentTask.endDate() != null) {
            dateLabel.setVisible(true);
            dateLabel.setManaged(true);
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
