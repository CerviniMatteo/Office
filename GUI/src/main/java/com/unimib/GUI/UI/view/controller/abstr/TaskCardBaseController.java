package com.unimib.GUI.UI.view.controller.abstr;

import com.unimib.GUI.UI.view.components.impl.custom.HoldButton;
import com.unimib.GUI.UI.viewmodel.impl.TaskViewModel;
import com.unimib.GUI.model.dto.TaskDTO;
import com.unimib.GUI.utils.SessionManagerSingleton;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import static com.unimib.GUI.utils.StringHelper.replaceUnderscores;

public abstract class TaskCardBaseController implements DefaultController {

    private final TaskViewModel viewModel;
    private final TaskDTO currentTask;
    private final Long currentWorkerId;

    @FXML
    private Label titleLabel;

    @FXML
    private Label stateLabel;

    @FXML
    private HBox bottomButtonBox;

    private HoldButton deleteButton;

    public TaskCardBaseController(TaskDTO task) {
        this.currentTask = task;
        this.currentWorkerId = (Long) SessionManagerSingleton
                .getInstance()
                .getAttribute("employeeId");
        this.viewModel = new TaskViewModel();
    }

    @FXML
    protected void initialize() {

        titleLabel.setText(currentTask.description());
        stateLabel.setText(replaceUnderscores(currentTask.taskState().toString()));

        observeState(
                viewModel.getDeleteTaskStateProperty(),
                () -> deleteButton.setDisable(true),
                _ -> {
                    deleteButton.setDisable(false);
                    showSuccess("Task deleted successfully");
                },
                error -> {
                    deleteButton.setDisable(false);
                    showError(error);
                }
        );

        deleteButton = new HoldButton();
        deleteButton.setOnHoldFinished(() -> viewModel.deleteTask(currentTask.taskId()));

        bottomButtonBox.getChildren().addLast(deleteButton);
    }

    public TaskDTO getCurrentTask() {
        return currentTask;
    }

    public TaskViewModel getViewModel() {
        return viewModel;
    }

    public Long getCurrentWorkerId() {
        return currentWorkerId;
    }

    public Label getTitleLabel() {
        return titleLabel;
    }

    public Label getStateLabel() {
        return stateLabel;
    }
}