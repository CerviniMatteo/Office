package com.unimib.assignment3.UI.components;

import javafx.geometry.Insets;
import javafx.scene.layout.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import com.unimib.assignment3.UI.dto.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;


public class TaskLayout extends GridPane{
    private int columns;
    private int rows;
    private List<TaskButton> taskButtons;

    public TaskLayout(int rows, int columns) {
        super(rows, columns);
        setColumns(columns);
        setRows(rows);
        setHgap(10);
        setVgap(10);
        HBox.setMargin(this, new Insets(10));

        taskButtons = new ArrayList<>();
        setTasksButtons();

        // Set columns to grow evenly
        setColumnConstraint();

        // Set rows to grow evenly
        setRowsConstraints();

        HBox.setHgrow(this, Priority.ALWAYS);
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
            getColumnConstraints().add(cc);
        }
    }

    private void setRowsConstraints(){
        for (int i = 0; i < rows; i++) {
            RowConstraints rc = new RowConstraints();
            rc.setPercentHeight(100.0 / rows); // percent of total height
            rc.setFillHeight(true);
            getRowConstraints().add(rc);
        }
    }

    // Add buttons dynamically
    private void setTasksButtons(){
        List<Task> tasks = fetchTasksFromBackend();
        int counter = 0;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {

                if (counter >= tasks.size()) return;

                TaskButton taskButton = new TaskButton(tasks.get(counter++));
                taskButtons.add(taskButton);

                add(taskButton, col, row);
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
            return mapper.readValue(response.body(), new TypeReference<>() {
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return List.of();
        }
    }
}
