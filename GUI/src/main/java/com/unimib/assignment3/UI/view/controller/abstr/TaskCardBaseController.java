package com.unimib.assignment3.UI.view.controller.abstr;

import com.unimib.assignment3.UI.model.controller.TaskRestController;
import com.unimib.assignment3.UI.model.dto.TaskDTO;
import com.unimib.assignment3.UI.utils.SessionManagerSingleton;
import com.unimib.assignment3.UI.view.components.impl.custom.AlertDialog;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import static com.unimib.assignment3.UI.utils.StringHelper.replaceUnderscores;

public abstract class TaskCardBaseController implements DefaultController {

    private final TaskDTO currentTask;
    private final TaskRestController taskRestController;
    private final Long currentWorkerId;

    @FXML private Label titleLabel;
    @FXML private Label stateLabel;
    @FXML private Button closeButton;
    @FXML private Button deleteButton;

    public TaskCardBaseController(TaskDTO task) {
        this.currentTask = task;
        this.taskRestController = new TaskRestController();
        this.currentWorkerId =
                (Long) SessionManagerSingleton
                        .getInstance()
                        .getAttribute("employeeId");
    }

    @FXML
    protected void initialize() {
        titleLabel.setText(currentTask.description());
        stateLabel.setText(
                replaceUnderscores(currentTask.taskState().toString())
        );
        deleteButton.setOnAction(event -> {
            Task<String> task = taskRestController.deleteTask(currentTask.taskId());
            task.setOnSucceeded(taskEvent -> {
                AlertDialog.showAlert("Success", "Task successfully deleted");
            });

            new Thread(task).start();
        });
    }

    public TaskDTO getCurrentTask() { return currentTask; }
    public TaskRestController getTaskController() { return taskRestController; }
    public Long getCurrentWorkerId() { return currentWorkerId; }
    public Label getTitleLabel() { return titleLabel; }
    public Label getStateLabel() { return stateLabel; }
    public Button getCloseButton() { return closeButton; }
}