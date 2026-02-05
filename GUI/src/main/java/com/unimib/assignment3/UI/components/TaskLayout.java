package com.unimib.assignment3.UI.components;

import com.unimib.assignment3.UI.controller.TaskController;
import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.unimib.assignment3.UI.dto.TaskDTO;
import static com.unimib.assignment3.UI.utils.AlertDialog.showAlert;

public class TaskLayout extends ScrollPane{

    private final GridPane grid;
    private final int rows;
    private final int columns;
    private Map<Long, TaskButton> taskButtons;

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

    public void updateTaskButton(Long taskId) {
        new Thread(() -> {
            TaskController controller = new TaskController();
            TaskDTO taskDTO = controller.fetchTask(taskId);

            Platform.runLater(() -> {
                if (taskDTO == null) {
                    showAlert("Error", "Server is currently down");
                    return;
                }
                TaskButton oldButton = taskButtons.get(taskId);
                if (oldButton == null) return;
                int row = GridPane.getRowIndex(oldButton) == null ? 0 : GridPane.getRowIndex(oldButton);
                int col = GridPane.getColumnIndex(oldButton) == null ? 0 : GridPane.getColumnIndex(oldButton);
                grid.getChildren().remove(oldButton);
                TaskButton newButton = new TaskButton(taskDTO);
                grid.add(newButton, col, row);
                taskButtons.put(taskId, newButton);
            });
        }).start();
    }


    private void loadTasks() {
        new Thread(() -> {
            TaskController controller = new TaskController();
            List<TaskDTO> taskDTOS = controller.fetchTasks();
            taskButtons = new HashMap<>();

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
                    taskButtons.put(btn.getTaskId(), btn);
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
