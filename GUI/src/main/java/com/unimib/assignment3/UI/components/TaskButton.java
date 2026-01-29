package com.unimib.assignment3.UI.components;

import com.unimib.assignment3.UI.dto.ChangeTaskStateRequestDTO;
import com.unimib.assignment3.UI.dto.TaskDTO;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import java.net.http.HttpResponse;
import static com.unimib.assignment3.UI.rest.TaskRest.fetchTask;
import static com.unimib.assignment3.UI.rest.TaskRest.postChangeTaskState;
import static com.unimib.assignment3.UI.utils.AlertDialog.showAlert;

public class TaskButton extends Button {
    private final Label descriptionLabel;
    private final Label stateLabel;
    private final Label startDateLabel;
    private final Label endDateLabel;
    private final StyledButton changeStateButton;

    public TaskButton(TaskDTO taskDTO){
        super();

        descriptionLabel = new Label(taskDTO.getDescription());
        stateLabel = new Label();
        startDateLabel = new Label();
        endDateLabel = new Label();
        changeStateButton = new StyledButton();

        setUpTaskLabels(taskDTO);

        HBox topDates = new HBox();
        topDates.setPadding(new Insets(2));
        topDates.setAlignment(Pos.TOP_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topDates.getChildren().addAll(startDateLabel, spacer, endDateLabel);


        VBox centerBox = new VBox(2);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.getChildren().addAll(descriptionLabel, stateLabel);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(topDates);
        borderPane.setCenter(centerBox);

        setGraphic(borderPane);
        setMaxWidth(Double.MAX_VALUE);
        setMaxHeight(Double.MAX_VALUE);

        GridPane.setHgrow(this, Priority.ALWAYS);
        GridPane.setVgrow(this, Priority.ALWAYS);

        HBox bottomBox = new HBox();
        bottomBox.setPadding(new Insets(10));
        bottomBox.setAlignment(Pos.BOTTOM_RIGHT);
        bottomBox.getChildren().add(changeStateButton);
        borderPane.setBottom(bottomBox);

        setUpChangeStateButtonAction();
    }

    private void setUpTaskLabels(TaskDTO taskDTO){
        replaceUnderscores(taskDTO);
        setId(taskDTO.getTaskId().toString());
        descriptionLabel.getStyleClass().add("task-lbl");

        stateLabel.getStyleClass().add("task-lbl");
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
        boolean isDone = "DONE".equals(taskDTO.getTaskState());
        changeStateButton.setDisable(isDone);


        setButtonOnTaskStateChangeStyle(stateLabel.getText());
        taskDTO.setTaskState(taskDTO.getTaskState().replace("_", " "));
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

    private void setButtonOnTaskStateChangeStyle(String taskState) {

        getStyleClass().setAll("task-button");

        switch (taskState) {
            case "TO BE STARTED" -> getStyleClass().add("task-to-start");
            case "STARTED"      -> getStyleClass().add("task-started");
            case "DONE"         -> {
                                    getStyleClass().add("task-done");
                                    changeStateButton.setDisable(true);
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

                Task<TaskDTO> task = new Task<>() {
                    @Override
                    protected TaskDTO call() {

                        HttpResponse<String> response = postChangeTaskState(payload);

                        if (response == null) {
                            throw new RuntimeException("No response from server");
                        }

                        if (response.statusCode() != 200) {
                            throw new RuntimeException(
                                    "HTTP error " + response.statusCode()
                            );
                        }

                        TaskDTO taskDTO = fetchTask(Long.valueOf(getId()));

                        if (taskDTO == null) {
                            throw new RuntimeException("Failed to fetch updated task");
                        }

                        return taskDTO;
                    }
                };

                task.setOnSucceeded(ev -> {
                    setUpTaskLabels(task.getValue());
                });


                task.setOnFailed(ev -> {
                    showAlert("Error", task.getException().getMessage());
                });

                new Thread(task).start();

            } catch (Exception ex) {
                showAlert("Error", "Failed to create request payload");
            }
        });
    }
}
