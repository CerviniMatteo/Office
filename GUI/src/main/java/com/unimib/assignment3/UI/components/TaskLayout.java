package com.unimib.assignment3.UI.components;

import com.unimib.assignment3.UI.dto.Task;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;


public class TaskLayout {
    private GridPane tasksLayout;
    private int columns;
    private int rows;

    public TaskLayout(int rows, int columns) {
        tasksLayout = new GridPane(rows, columns);
        setColumns(columns);
        setRows(rows);
        tasksLayout.setHgap(10);
        tasksLayout.setVgap(10);
        HBox.setMargin(tasksLayout, new Insets(15));
        setButtons();

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
    private void setButtons(){
        List<Task> tasks = fetchTasksFromBackend();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                Button button = new Button(tasks.get(i+j).getDescription() + "\n" + tasks.get(i+j).getTaskState());
                button.setId(tasks.get(i+j).getTaskId().toString());
                button.setMaxWidth(Double.MAX_VALUE);
                button.setMaxHeight(Double.MAX_VALUE);
                tasksLayout.add(button, j, i);
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
            return mapper.readValue(response.body(), new TypeReference<List<Task>>(){});
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

}
