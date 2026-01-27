package com.unimib.assignment3.UI.components;

import com.unimib.assignment3.UI.dto.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class TaskButton extends Button {
    public TaskButton(Task task){
        super();
        setId("" + task.getTaskId());
        Label descriptionLabel = new Label(task.getDescription());
        descriptionLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-font-size: 24px;");

        Label stateLabel = new Label(task.getTaskState());
        stateLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-font-size: 20px;");

        String startDateStr = "START DATE\n";
        startDateStr += task.getStartDate() != null
                ? task.getStartDate().toString()
                : "N/A";
        Label startDateLabel = new Label(startDateStr);
        startDateLabel.setStyle("-fx-text-fill: yellow; -fx-font-size: 18px;");
        startDateLabel.setPadding(new Insets(10));

        String endDateStr = "END DATE\n";
        endDateStr+=  task.getEndDate() != null
                ? task.getEndDate().toString()
                : "N/A";
        Label endDateLabel = new Label(endDateStr);
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

        setButtonColor(this, task.getTaskState());
    }

    private void setButtonColor(Button button, String taskState) {
        String color;
        switch (taskState) {
            case "TO BE STARTED" -> color = "#2F2139";
            case "STARTED"      -> color = "#C38CC3";
            case "DONE"         -> color = "#086466";
            default             -> color = "#444";
        }

        button.setStyle("""
        -fx-background-color: %s;
        -fx-background-radius: 30;
        -fx-border-radius: 30;
        -fx-border-width: 3;
    """.formatted(color));
    }

}
