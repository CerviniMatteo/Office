package com.unimib.GUI.UI.view.controller.abstr;

import com.unimib.GUI.model.controller.TaskRestController;
import com.unimib.GUI.model.dto.TaskDTO;
import com.unimib.GUI.utils.SessionManagerSingleton;
import com.unimib.GUI.UI.view.components.impl.custom.AlertDialog;
import com.unimib.GUI.UI.viewmodel.TaskCardViewModel;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import static com.unimib.GUI.utils.StringHelper.replaceUnderscores;

public abstract class TaskCardBaseController implements DefaultController {

    private final TaskCardViewModel viewModel;

    private final TaskDTO currentTask;
    private final TaskRestController taskRestController;
    private final Long currentWorkerId;

    @FXML private Label titleLabel;
    @FXML private Label stateLabel;
    @FXML private Button deleteButton;

    public TaskCardBaseController(TaskDTO task) {
        this.currentTask = task;
        this.taskRestController = new TaskRestController();
        this.currentWorkerId =
                (Long) SessionManagerSingleton
                        .getInstance()
                        .getAttribute("employeeId");

        viewModel = new TaskCardViewModel();
    }

    @FXML
    protected void initialize() {
        titleLabel.setText(currentTask.description());
        stateLabel.setText(
                replaceUnderscores(currentTask.taskState().toString())
        );
        deleteButton.setOnAction(_ -> {
            Task<String> task = viewModel.deleteTask(currentTask.taskId());
            task.setOnSucceeded(_ -> AlertDialog.showAlert("Success", "Task successfully deleted"));

            new Thread(task).start();
        });
    }

    public TaskDTO getCurrentTask() { return currentTask; }
    public TaskRestController getTaskController() { return taskRestController; }
    public Long getCurrentWorkerId() { return currentWorkerId; }
    public Label getTitleLabel() { return titleLabel; }
    public Label getStateLabel() { return stateLabel; }
}