package com.unimib.assignment3.UI.components;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;
import com.unimib.assignment3.UI.dto.TaskDTO;

import static com.unimib.assignment3.UI.rest.TaskRest.fetchTasks;

public class TaskLayout extends ScrollPane{

    private final GridPane grid;
    private final int rows;
    private final int columns;
    private final List<TaskButton> taskButtons = new ArrayList<>();

    public TaskLayout(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        setPadding(new Insets(10));
        setHbarPolicy(ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollBarPolicy.NEVER);
        setFitToWidth(true);
        setPannable(true);

        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        setContent(grid);

        // Scroll behavior
        setStyle("""
        -fx-background-color: transparent;
        -fx-background: transparent;
        """);
        grid.setStyle("""
        -fx-border-radius: 30;
        -fx-border-width: 2;
        -fx-border-color: %s;""".formatted("#F8E2D4"));

        setHbarPolicy(ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollBarPolicy.AS_NEEDED);

        HBox.setHgrow(this, Priority.ALWAYS);

        setColumnConstraints();
        setRowConstraints();
        loadTasks();
    }


    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    private void setColumnConstraints() {
        for (int i = 0; i < columns; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / columns);
            cc.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(cc);
        }
    }

    private void setRowConstraints() {
        for (int i = 0; i < rows; i++) {
            RowConstraints rc = new RowConstraints();
            rc.setVgrow(Priority.ALWAYS);
            grid.getRowConstraints().add(rc);
        }
    }

    // Add buttons dynamically
    private void setTasksButtons(){
        List<TaskDTO> taskDTOS = fetchTasks();
        int counter = 0;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {

                if (counter >= taskDTOS.size()) return;

                TaskButton taskButton = new TaskButton(taskDTOS.get(counter++));
                taskButtons.add(taskButton);

                GridPane.setHgrow(taskButton, Priority.ALWAYS);
                GridPane.setVgrow(taskButton, Priority.ALWAYS);
                taskButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

                grid.add(taskButton, col, row);
            }
        }
    }

    private void loadTasks() {
        List<TaskDTO> taskDTOS = fetchTasks();

        int col = 0;
        int row = 0;

        for (TaskDTO taskDTO : taskDTOS) {
            TaskButton btn = new TaskButton(taskDTO);
            taskButtons.add(btn);

            grid.add(btn, col, row);

            col++;
            if (col == columns) {
                col = 0;
                row++;
            }
        }
    }
}
