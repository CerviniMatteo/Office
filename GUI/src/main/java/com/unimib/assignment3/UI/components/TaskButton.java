package com.unimib.assignment3.UI.components;

import com.unimib.assignment3.UI.controller.TaskController;
import com.unimib.assignment3.UI.dto.AcceptTaskRequestDTO;
import com.unimib.assignment3.UI.dto.StartTaskRequestDTO;
import com.unimib.assignment3.UI.dto.ChangeTaskStateRequestDTO;
import com.unimib.assignment3.UI.dto.TaskDTO;
import com.unimib.assignment3.UI.enums.TaskState;
import com.unimib.assignment3.UI.utils.ImageHelper;
import com.unimib.assignment3.UI.utils.SessionManagerSingleton;
import jakarta.annotation.Nonnull;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.image.ImageView;

import java.util.*;

import static com.unimib.assignment3.UI.utils.AlertDialog.showAlert;

public class TaskButton extends Button {

    public static final int STROKE_WIDTH = 3;
    private final Long taskId;

    public static final int SIZE = 40;
    TaskController taskController;

    private final Label descriptionLabel;
    private final Label stateLabel;
    private final Label startDateLabel;
    private final Label endDateLabel;
    private final StyledButton changeStateButton;
    private final StyledButton resetTaskButton;
    private final GridPane workersGrid;
    private final StyledButton acceptTaskButton;
    private final List<Long> acceptedBy;
    private TaskState taskState;
    private int row = 0;
    private int col = 0;

    public TaskButton(TaskDTO taskDTO) {

        taskController = new TaskController();

        this.getStyleClass().add("task-button");

        descriptionLabel = new Label(taskDTO.description());
        stateLabel = new Label();
        startDateLabel = new Label();
        endDateLabel = new Label();
        changeStateButton = new StyledButton();
        resetTaskButton = new StyledButton();
        workersGrid = new GridPane();
        acceptTaskButton = new StyledButton();
        acceptedBy = new ArrayList<>();
        HBox bottomBox = new HBox();

        taskId = taskDTO.taskId();

        setUpTaskLabels(taskDTO);

        HBox topDates = new HBox();
        topDates.getStyleClass().add("hbox-top-task-dates");
        Region datesSpacer = new Region();
        HBox.setHgrow(datesSpacer, Priority.ALWAYS);
        topDates.getChildren().addAll(startDateLabel, datesSpacer, endDateLabel);

        VBox centerBox = new VBox();
        Region descriptionSpacer = new Region();
        descriptionSpacer.setMinHeight(10);

        centerBox.getStyleClass().add("hbox-center-task-description");
        centerBox.getChildren().addAll(descriptionLabel, descriptionSpacer, stateLabel);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(topDates);
        borderPane.setCenter(centerBox);

        setGraphic(borderPane);
        setMaxWidth(Double.MAX_VALUE);
        setMaxHeight(Double.MAX_VALUE);

        GridPane.setHgrow(this, Priority.ALWAYS);
        GridPane.setVgrow(this, Priority.ALWAYS);

        bottomBox.getStyleClass().add("hbox-bottom-right-task-state");
        Region changeTaskStateSpacer = new Region();
        VBox vBox = new VBox();
        HBox workersBox = new HBox();
        Region workersSpacer = new Region();
        HBox.setHgrow(workersSpacer, Priority.ALWAYS);
        HBox.setMargin(workersBox, new Insets(10, 10, 0, 10));
        acceptTaskButton.setText("ACCEPT TASK");
        workersBox.getChildren().addAll(acceptTaskButton, workersSpacer, workersGrid);
        workersGrid.setHgap(10);
        workersGrid.setVgap(10);
        workersGrid.setPadding(new Insets(5));
        workersBox.setAlignment(Pos.CENTER_RIGHT);
        workersBox.setPadding(new Insets(10, 10, 0, 10));
        HBox.setHgrow(changeTaskStateSpacer, Priority.ALWAYS);
        bottomBox.getChildren().addAll(resetTaskButton, changeTaskStateSpacer, changeStateButton);
        vBox.getChildren().addAll(workersBox, bottomBox);
        borderPane.setBottom(vBox);
        setUpChangeStateButtonAction();
        setUpResetStateButtonAction();
    }

    public Long getTaskId() {
        return taskId;
    }

    private void setUpTaskLabels(TaskDTO taskDTO) {
        descriptionLabel.getStyleClass().add("task-description-lbl");

        stateLabel.getStyleClass().add("task-state-lbl");
        taskState = taskDTO.taskState();
        stateLabel.setText(replaceUnderscores(taskState));

        String startDateStr = "START DATE\n";
        startDateStr += taskDTO.startDate() != null
                ? taskDTO.startDate().toString()
                : "N/A";
        startDateLabel.setText(startDateStr);
        startDateLabel.getStyleClass().add("task-date-lbl");

        String endDateStr = "END DATE\n";
        endDateStr += taskDTO.endDate() != null
                ? taskDTO.endDate().toString()
                : "N/A";
        endDateLabel.setText(endDateStr);
        endDateLabel.getStyleClass().add("task-date-lbl");


        acceptedBy.addAll(taskDTO.assignedWorkers().keySet());
        setButtonOnAcceptTask(taskDTO.assignedWorkers().keySet());
        Long currentWorkerId =
                (Long) SessionManagerSingleton.getInstance().getAttribute("employeeId");
        ImageHelper imageHelper = new ImageHelper();
        for (Map.Entry<Long, String> entry : taskDTO.assignedWorkers().entrySet()) {
            if (entry.getKey().equals(currentWorkerId)) {
                String updatedImage = imageHelper.addGoldStrokeAndReturnBase64(entry.getValue(), SIZE, STROKE_WIDTH);
                entry.setValue(updatedImage);
            }
            addWorkerImageToGrid(entry.getValue());
        }

        changeStateButton.setText(getChangeStateButtonText(taskDTO.taskState()));

        resetTaskButton.setText("RESET TASK");

        setButtonOnTaskStateChangeStyle();
    }

    private void addWorkerImageToGrid(String base64Image) {
        ImageHelper imageHelper = new ImageHelper();
        ImageView imageView = imageHelper.createCircularImageView(
                imageHelper.createImageFromBase64(base64Image),
                SIZE
        );

        workersGrid.add(imageView, col, row);

        col++;
        int MAX_COLS = 3;
        if (col == MAX_COLS) {
            col = 0;
            row++;
        }
    }

    private String replaceUnderscores(TaskState taskState) {
       return taskState.toString().trim()
                .replace("_", " ")
                .replaceAll("\\s+", " ")
                .toUpperCase();
    }

    private String getChangeStateButtonText(TaskState state) {
        return switch (state) {
            case TO_BE_STARTED -> "START TASK";
            case STARTED -> "COMPLETE TASK";
            case DONE -> "TASK COMPLETED";
        };
    }

    private void setButtonOnTaskStateChangeStyle() {
        getStyleClass().setAll("task-button");
        switch (taskState) {
            case TO_BE_STARTED -> {
                getStyleClass().add("task-to-start");
                resetTaskButton.setDisable(true);
                changeStateButton.setDisable(false);
                acceptTaskButton.setDisable(true);
            }
            case STARTED -> {
                getStyleClass().add("task-started");
                Long currentWorkerId =
                        (Long) SessionManagerSingleton.getInstance().getAttribute("employeeId");
                acceptTaskButton.setDisable(acceptedBy.contains(currentWorkerId));
                resetTaskButton.setDisable(true);
            }
            case DONE -> {
                getStyleClass().add("task-done");
                changeStateButton.setDisable(true);
                resetTaskButton.setDisable(false);
                acceptTaskButton.setDisable(true);
            }
        }
    }

    private void setUpChangeStateButtonAction() {
        changeStateButton.setOnAction(e -> {
            try{
                Task<String> task;
                if(taskState.equals(TaskState.TO_BE_STARTED)) {
                    StartTaskRequestDTO payload = startTaskRequest();
                    task = taskController.startTask(payload);
                }
                else {
                    ChangeTaskStateRequestDTO payload =
                            new ChangeTaskStateRequestDTO(
                                    getTaskId(),
                                    taskState
                            );

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
                getTaskId(),
                workerId
        );
    }

    private void setUpResetStateButtonAction() {
        resetTaskButton.setOnAction(e -> {
            try {
                Task<String> task = taskController.resetTaskState(getTaskId());

                task.setOnSucceeded(ev -> acceptTaskButton.setDisable(true));

                task.setOnFailed(ev -> showAlert("Error", task.getException().getMessage()));

                new Thread(task).start();
            } catch (Exception ex) {
                showAlert("Error", "Failed to create request payload");
            }
        });
    }

    private void setButtonOnAcceptTask(Set<Long> workerIds) {
        acceptTaskButton.setOnAction(e -> {
            SessionManagerSingleton session = SessionManagerSingleton.getInstance();
            Long currentWorkerId = (Long) session.getAttribute("employeeId");
            if (!workerIds.contains(currentWorkerId)) {
                try {
                    Task<String> task = taskController.acceptTask(new AcceptTaskRequestDTO(
                            getTaskId(),
                            currentWorkerId
                    ));
                    task.setOnSucceeded(ev -> acceptTaskButton.setDisable(false));
                    task.setOnFailed(ev -> showAlert("Error", task.getException().getMessage()));
                    new Thread(task).start();
                } catch (Exception ex) {
                    showAlert("Error", "Failed to create request payload");
                }
            } else {
                acceptTaskButton.setDisable(true);
                showAlert("Info", "You have already accepted this task.");
            }
        });
    }
}
