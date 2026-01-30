package com.unimib.assignment3.UI.components;

import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import java.util.List;
import com.unimib.assignment3.UI.dto.TaskDTO;

import static com.unimib.assignment3.UI.rest.TaskRest.fetchTasks;
import static com.unimib.assignment3.UI.utils.AlertDialog.showAlert;

public class TaskLayout extends ScrollPane{

    private final GridPane grid;
    private final int rows;
    private final int columns;

    public TaskLayout(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;

        setHbarPolicy(ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollBarPolicy.NEVER);
        setFitToWidth(true);
        setPannable(true);

        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        setContent(grid);

        getStyleClass().add("task-layout");
        grid.getStyleClass().add("task-grid-layout");

        setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        HBox.setHgrow(this, Priority.ALWAYS);

        setColumnConstraints();
        setRowConstraints();
        loadTasks();
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

    public void loadTasks() {
        new Thread(() -> {
            List<TaskDTO> taskDTOS = fetchTasks();

            Platform.runLater(() -> {
                grid.getChildren().clear();

                if (taskDTOS == null) {
                    showAlert("Error", "Server is currently down");
                    return;
                }

                int col = 0;
                int row = 0;
                for (TaskDTO taskDTO : taskDTOS) {
                    TaskButton btn = new TaskButton(taskDTO);
                    grid.add(btn, col, row);

                    col++;
                    if (col == columns) {
                        col = 0;
                        row++;
                    }
                }
            });
        }).start();
    }

}
