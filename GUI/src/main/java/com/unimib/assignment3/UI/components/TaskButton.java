package com.unimib.assignment3.UI.components;

import com.unimib.assignment3.UI.dto.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class TaskButton extends Button {
    private static final String BASE_URL = "http://localhost:8080/api/tasks";
    private Label descriptionLabel;
    private Label stateLabel;
    private Label startDateLabel;
    private Label endDateLabel;
    private StyledButton changeStateButton;

    public TaskButton(Task task){
        super();
        setId("" + task.getTaskId());
        descriptionLabel = new Label(task.getDescription());
        descriptionLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-font-size: 24px;");

        stateLabel = new Label(task.getTaskState());
        stateLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-font-size: 20px;");

        String startDateStr = "START DATE\n";
        startDateStr += task.getStartDate() != null
                ? task.getStartDate().toString()
                : "N/A";
        startDateLabel = new Label(startDateStr);
        startDateLabel.setStyle("-fx-text-fill: yellow; -fx-font-size: 18px;");
        startDateLabel.setPadding(new Insets(10));

        String endDateStr = "END DATE\n";
        endDateStr+=  task.getEndDate() != null
                ? task.getEndDate().toString()
                : "N/A";
        endDateLabel = new Label(endDateStr);
        endDateLabel.setStyle("-fx-text-fill: yellow; -fx-font-size: 18px;");
        endDateLabel.setPadding(new Insets(10));

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

        changeStateButton = new StyledButton(changeStateButton(), "#F8E2D4", "#F8E2D4");
        HBox bottomBox = new HBox();
        bottomBox.setPadding(new Insets(10));
        bottomBox.setAlignment(Pos.BOTTOM_RIGHT);
        bottomBox.getChildren().add(changeStateButton);
        borderPane.setBottom(bottomBox);

        setButtonColor(this, task.getTaskState(), "#F8E2D4");
    }

    private String changeStateButton(){
        return switch (stateLabel.getText()) {
            case "TO BE STARTED" -> "START TASK";
            case "STARTED"      -> "COMPLETE TASK";
            case "DONE"         -> "TASK COMPLETED";
            default             -> "UNKNOWN STATE";
        };
    }

    private void setButtonColor(Button button, String taskState, String borderColor) {
        String color;
        switch (taskState) {
            case "TO BE STARTED" -> color = "#2F2139";
            case "STARTED"      -> color = "#6A2E3D";
            case "DONE"         -> color = "#086466";
            default             -> color = "#444";
        }

        button.setStyle("""
        -fx-background-color: %s;
        -fx-background-radius: 30;
        -fx-border-radius: 30;
        -fx-border-width: 2;
        -fx-border-color: %s;
    """.formatted(color, borderColor));
    }

}
