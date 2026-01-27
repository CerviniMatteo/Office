package com.unimib.assignment3.UI.components;

import com.unimib.assignment3.UI.dto.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.control.Label;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import javafx.scene.paint.Paint;


public class TaskLayout {
    private GridPane tasksLayout;
    private int columns;
    private int rows;

    public TaskLayout(int rows, int columns) {
        tasksLayout = new GridPane(rows, columns);
        setColumns(columns);
        setRows(rows);
        tasksLayout.setHgap(2);
        tasksLayout.setVgap(2);
        HBox.setMargin(tasksLayout, new Insets(10));
        setTasksButtons();

        // Set columns to grow evenly
        setColumnConstraint();

        // Set rows to grow evenly
        setRowsConstraints();

        HBox.setHgrow(getTasksLayout(), Priority.ALWAYS);
    }

    public GridPane getTasksLayout() {
        return tasksLayout;
    }

    public void setTasksLayout(GridPane tasksLayout) {
        this.tasksLayout = tasksLayout;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    private void setColumnConstraint(){
        for (int i = 0; i < columns; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / columns); // percent of total width
            cc.setFillWidth(true);
            tasksLayout.getColumnConstraints().add(cc);
        }
    }

    private void setRowsConstraints(){
        for (int i = 0; i < rows; i++) {
            RowConstraints rc = new RowConstraints();
            rc.setPercentHeight(100.0 / rows); // percent of total height
            rc.setFillHeight(true);
            tasksLayout.getRowConstraints().add(rc);
        }
    }

    // Add buttons dynamically
    private void setTasksButtons(){
        List<Task> tasks = fetchTasksFromBackend();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                Label descriptionLabel = new Label(tasks.get(i+j).getDescription());
                descriptionLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-font-size: 24px;");

                Label stateLabel = new Label(tasks.get(i+j).getTaskState());
                stateLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-font-size: 20px;");

                String startDateStr = "START DATE\n";
                startDateStr += tasks.get(i+j).getStartDate() != null
                        ? tasks.get(i+j).getStartDate().toString()
                        : "N/A";
                Label startDateLabel = new Label(startDateStr);
                startDateLabel.setStyle("-fx-text-fill: yellow; -fx-font-size: 18px;");

                String endDateStr = "END DATE\n";
                endDateStr+=  tasks.get(i+j).getEndDate() != null
                        ? tasks.get(i+j).getEndDate().toString()
                        : "N/A";
                Label endDateLabel = new Label(endDateStr);
                endDateLabel.setStyle("-fx-text-fill: yellow; -fx-font-size: 18px;");

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

                Button button = new Button();
                button.setGraphic(borderPane);
                button.setMaxWidth(Double.MAX_VALUE);
                button.setMaxHeight(Double.MAX_VALUE);

                button.setBorder(new Border(
                        new BorderStroke(
                                Paint.valueOf("#4d067B"),
                                BorderStrokeStyle.SOLID,
                                new CornerRadii(10),
                                new BorderWidths(2)
                        )
                ));

                setButtonColor(button, tasks.get(i+j).getTaskState());

                getTasksLayout().add(button, j, i);
            }
        }
    }

    private List<Task> fetchTasksFromBackend() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/tasks"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            return mapper.readValue(response.body(), new TypeReference<List<Task>>(){});
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return List.of();
        }
    }

    private void setButtonColor(Button button, String taskState) {
        switch (taskState) {
            case "TO BE STARTED":
                button.setStyle("-fx-background-color: #2F2139;"); // niente text-fill qui
                break;
            case "STARTED":
                button.setStyle("-fx-background-color: #C38CC3;");
                break;
            case "DONE":
                button.setStyle("-fx-background-color: #086466;");
                break;
        }
    }

}
