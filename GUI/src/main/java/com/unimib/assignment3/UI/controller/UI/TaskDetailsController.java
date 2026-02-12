package com.unimib.assignment3.UI.controller.UI;

import com.unimib.assignment3.UI.controller.rest.TaskController;
import com.unimib.assignment3.UI.dto.AcceptTaskRequestDTO;
import com.unimib.assignment3.UI.dto.ChangeTaskStateRequestDTO;
import com.unimib.assignment3.UI.dto.StartTaskRequestDTO;
import com.unimib.assignment3.UI.dto.TaskDTO;
import com.unimib.assignment3.UI.enums.TaskState;
import com.unimib.assignment3.UI.utils.ImageHelper;
import com.unimib.assignment3.UI.utils.SessionManagerSingleton;
import jakarta.annotation.Nonnull;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private Button acceptButton;
    @FXML
    private Button changeStateButton;
    @FXML
    private Button resetButton;
    @FXML
    private GridPane workersGrid;

    private final TaskController taskController = new TaskController();
    private final List<Long> acceptedBy = new ArrayList<>();
    private TaskState taskState;
    private TaskDTO currentTask;

    public void setTask(TaskDTO task) {
        currentTask = task;

        this.taskState = currentTask.taskState();

        titleLabel.setText(task.description());
        stateLabel.setText(replaceUnderscores(taskState));
        acceptedBy.clear();
        acceptedBy.addAll(task.assignedWorkers().keySet());

        switch (task.taskState()) {
            case TO_BE_STARTED -> stateLabel.getStyleClass().add("task-to-start");
            case STARTED -> stateLabel.getStyleClass().add("task-started");
            case DONE -> stateLabel.getStyleClass().add("task-done");
            default -> {}
        }

        setButtonOnTaskStateChangeStyle();
        setAcceptButtonAction(currentTask.assignedWorkers().keySet());
        setChangeStateButtonAction();
        setResetButtonAction();

        setWorkersPictures();
    }

    private String replaceUnderscores(TaskState taskState) {
        return taskState.toString().trim()
                .replace("_", " ")
                .replaceAll("\\s+", " ")
                .toUpperCase();
    }


    public void setWorkersPictures(){
        Long currentWorkerId =
                (Long) SessionManagerSingleton.getInstance().getAttribute("employeeId");
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
        int rows = (int) Math.ceil((double) workersGrid.getChildren().size() / cols);

        int col = workersGrid.getChildren().size() % cols;
        int row = workersGrid.getChildren().size() / cols;

        workersGrid.add(imageView, col, row);
    }

    private void setButtonOnTaskStateChangeStyle() {
        switch (taskState) {
            case TO_BE_STARTED -> {
                stateLabel.getStyleClass().add("task-to-start");
                resetButton.setVisible(false);
                resetButton.setDisable(true);
                changeStateButton.setVisible(true);
                changeStateButton.setDisable(false);
                acceptButton.setVisible(false);
                acceptButton.setDisable(true);



                changeStateButton.setText("START TASK");
            }
            case STARTED -> {
                stateLabel.getStyleClass().add("task-started");
                Long currentWorkerId =
                        (Long) SessionManagerSingleton.getInstance().getAttribute("employeeId");
                if(acceptedBy.contains(currentWorkerId)){
                    acceptButton.setVisible(false);
                    acceptButton.setDisable(true);
                }else{
                    acceptButton.setVisible(true);
                    acceptButton.setDisable(false);
                }
                resetButton.setVisible(false);
                resetButton.setDisable(true);
                changeStateButton.setText("COMPLETE TASK");
            }
            case DONE -> {
                stateLabel.getStyleClass().add("task-done");
                changeStateButton.setVisible(false);
                changeStateButton.setDisable(true);
                resetButton.setVisible(true);
                resetButton.setDisable(false);
                acceptButton.setVisible(false);
                acceptButton.setDisable(true);
                changeStateButton.setText("TASK COMPLETED");
            }
        }
    }

    private void setChangeStateButtonAction() {
        changeStateButton.setOnAction(e -> {
            try{
                Task<String> task;
                if(taskState.equals(TaskState.TO_BE_STARTED)) {
                    StartTaskRequestDTO payload = startTaskRequest();
                    payload.validate();
                    task = taskController.startTask(payload);
                }
                else {
                    ChangeTaskStateRequestDTO payload =
                            new ChangeTaskStateRequestDTO(
                                    currentTask.taskId(),
                                    taskState
                            );
                    payload.validate();
                    task = taskController.changeTaskState(payload);
                }

                task.setOnSucceeded(ev -> System.out.println("Success"));


                task.setOnFailed(ev -> showAlert("Error", task.getException().getMessage()));

                new Thread(task).start();
            } catch (Exception ex) {
                showAlert("Error", "Failed to create request payload");
            }
        });
    }

    @Nonnull
    private StartTaskRequestDTO startTaskRequest() {
        SessionManagerSingleton sessionManagerSingleton = SessionManagerSingleton.getInstance();
        Long workerId =  (Long) sessionManagerSingleton.getAttribute("employeeId");
        return new StartTaskRequestDTO(
                currentTask.taskId(),
                workerId
        );
    }

    private void setResetButtonAction() {
        resetButton.setOnAction(e -> {
            try {
                Task<String> task = taskController.resetTaskState(currentTask.taskId());

                task.setOnSucceeded(ev -> acceptButton.setDisable(true));

                task.setOnFailed(ev -> showAlert("Error", task.getException().getMessage()));

                new Thread(task).start();
            } catch (Exception ex) {
                showAlert("Error", "Failed to create request payload");
            }
        });
    }

    private void setAcceptButtonAction(Set<Long> workerIds) {
        acceptButton.setOnAction(e -> {
            SessionManagerSingleton session = SessionManagerSingleton.getInstance();
            Long currentWorkerId = (Long) session.getAttribute("employeeId");
            if (!workerIds.contains(currentWorkerId)) {
                try {
                    AcceptTaskRequestDTO payload = new AcceptTaskRequestDTO(currentTask.taskId(),
                            currentWorkerId);
                    payload.validate();
                    Task<String> task = taskController.acceptTask(payload);
                    task.setOnSucceeded(ev -> acceptButton.setDisable(false));
                    task.setOnFailed(ev -> showAlert("Error", task.getException().getMessage()));
                    new Thread(task).start();
                } catch (Exception ex) {
                    showAlert("Error", "Failed to create request payload");
                }
            } else {
                acceptButton.setDisable(true);
                showAlert("Info", "You have already accepted this task.");
            }
        });
    }


    public Label getStateLabel() {
        return stateLabel;
    }

    public TaskDTO getCurrentTask() {
        return currentTask;
    }
}
