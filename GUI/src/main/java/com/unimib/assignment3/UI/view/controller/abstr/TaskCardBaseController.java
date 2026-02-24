package com.unimib.assignment3.UI.view.controller.abstr;

import com.unimib.assignment3.UI.model.controller.TaskRestController;
import com.unimib.assignment3.UI.model.dto.TaskDTO;
import com.unimib.assignment3.UI.utils.SessionManagerSingleton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import static com.unimib.assignment3.UI.utils.StringHelper.replaceUnderscores;

public abstract class TaskCardBaseController implements DefaultController {

    private final TaskDTO currentTask;
    private final TaskRestController taskRestController;
    private final Long currentWorkerId;

    @FXML private Label titleLabel;
    @FXML private Label stateLabel;

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
    }

    protected TaskDTO getCurrentTask() { return currentTask; }
    protected TaskRestController getTaskController() { return taskRestController; }
    protected Long getCurrentWorkerId() { return currentWorkerId; }
    protected Label getTitleLabel() { return titleLabel; }
    protected Label getStateLabel() { return stateLabel; }
}