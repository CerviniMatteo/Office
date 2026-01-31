package com.unimib.assignment3.UI.components;

import com.unimib.assignment3.UI.controller.TaskController;
import com.unimib.assignment3.UI.dto.ChangeTaskStateRequestDTO;
import com.unimib.assignment3.UI.dto.TaskDTO;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import static com.unimib.assignment3.UI.utils.AlertDialog.showAlert;

public class TaskButton extends Button {

    TaskController taskController;

    private final Label descriptionLabel;
    private final Label stateLabel;
    private final Label startDateLabel;
    private final Label endDateLabel;
    private final HBox bottomBox;
    private final StyledButton changeStateButton;
    private final StyledButton resetTaskButton;

    public TaskButton(TaskDTO taskDTO){

        taskController = new TaskController();

        this.getStyleClass().add("task-button");

        descriptionLabel = new Label(taskDTO.getDescription());
        stateLabel = new Label();
        startDateLabel = new Label();
        endDateLabel = new Label();
        changeStateButton = new StyledButton();
        resetTaskButton = new StyledButton();
        bottomBox = new HBox();

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
        HBox.setHgrow(changeTaskStateSpacer, Priority.ALWAYS);
        bottomBox.getChildren().addAll(changeTaskStateSpacer, changeStateButton);
        borderPane.setBottom(bottomBox);

        setUpChangeStateButtonAction();
        setUpResetStateButtonAction();
    }

    private void setUpTaskLabels(TaskDTO taskDTO){
        replaceUnderscores(taskDTO);
        setId(taskDTO.getTaskId().toString());
        descriptionLabel.getStyleClass().add("task-description-lbl");

        stateLabel.getStyleClass().add("task-state-lbl");
        stateLabel.setText(taskDTO.getTaskState());

        String startDateStr = "START DATE\n";
        startDateStr += taskDTO.getStartDate() != null
                ? taskDTO.getStartDate().toString()
                : "N/A";
        startDateLabel.setText(startDateStr);
        startDateLabel.getStyleClass().add("task-date-lbl");

        String endDateStr = "END DATE\n";
        endDateStr+=  taskDTO.getEndDate() != null
                ? taskDTO.getEndDate().toString()
                : "N/A";
        endDateLabel.setText(endDateStr);
        endDateLabel.getStyleClass().add("task-date-lbl");

        changeStateButton.setText(changeStateButton(taskDTO.getTaskState()));

        resetTaskButton.setText("RESET TASK");

        setButtonOnTaskStateChangeStyle();
    }

    private void replaceUnderscores(TaskDTO taskDTO) {
        taskDTO.setTaskState(taskDTO.getTaskState().replace("_", " "));
    }

    private String changeStateButton(String taskState) {
        return switch (taskState) {
            case "TO BE STARTED" -> "START TASK";
            case "STARTED"      -> "COMPLETE TASK";
            case "DONE"         -> "TASK COMPLETED";
            default             -> "UNKNOWN STATE";
        };
    }

    private void setButtonOnTaskStateChangeStyle() {
        getStyleClass().setAll("task-button");
        switch (stateLabel.getText()) {
            case "TO BE STARTED" -> {
                                        getStyleClass().add("task-to-start");
                                        if(bottomBox.getChildren().contains(resetTaskButton)) {
                                            bottomBox.getChildren().removeFirst();
                                        }
                                        changeStateButton.setDisable(false);
                                    }
            case "STARTED"      -> getStyleClass().add("task-started");
            case "DONE"         ->{
                                        getStyleClass().add("task-done");
                                        changeStateButton.setDisable(true);
                                        bottomBox.getChildren().addFirst(resetTaskButton);
                                    }
            default             -> getStyleClass().add("task-unknown");
        }
    }

    private String mapTaskState(String taskState) {
        return switch (taskState) {
            case "TO BE STARTED" -> "STARTED";
            case "STARTED" -> "DONE";
            default -> "UNKNOWN_STATE";
        };
    }

    private void setUpChangeStateButtonAction() {
        changeStateButton.setOnAction(e -> {
            try {
                ChangeTaskStateRequestDTO payload =
                        new ChangeTaskStateRequestDTO(
                                Long.valueOf(getId()),
                                mapTaskState(stateLabel.getText())
                );

                Task<String> task = taskController.changeTaskState(payload);

                task.setOnSucceeded(ev -> System.out.println("Success"));


                task.setOnFailed(ev -> showAlert("Error", task.getException().getMessage()));

                new Thread(task).start();
            } catch (Exception ex) {
            showAlert("Error", "Failed to create request payload");}
        });
    }

    private void setUpResetStateButtonAction() {
        resetTaskButton.setOnAction(e -> {
            try {
                Task<String> task = taskController.resetTaskState(Long.valueOf(getId()));

                task.setOnSucceeded(ev -> System.out.println("Success"));

                task.setOnFailed(ev -> showAlert("Error", task.getException().getMessage()));

                new Thread(task).start();
            } catch (Exception ex) {
                showAlert("Error", "Failed to create request payload");}
        });
    }

    public void updateTask(TaskDTO taskDTO){
        setUpTaskLabels(taskDTO);
        setButtonOnTaskStateChangeStyle();
    }
}
