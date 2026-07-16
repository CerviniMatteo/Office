package com.unimib.GUI.UI.view.controller.abstr;

import com.unimib.GUI.UI.state.UIState;
import com.unimib.GUI.UI.view.components.impl.custom.AlertDialog;
import com.unimib.GUI.UI.viewmodel.impl.TaskCardViewModel;
import com.unimib.GUI.model.dto.TaskDTO;
import com.unimib.GUI.utils.SessionManagerSingleton;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import static com.unimib.GUI.UI.view.components.impl.custom.AlertDialog.showAlert;
import static com.unimib.GUI.utils.StringHelper.replaceUnderscores;

public abstract class TaskCardBaseController implements DefaultController {

    private final TaskCardViewModel viewModel;
    private final TaskDTO currentTask;
    private final Long currentWorkerId;

    @FXML private Label titleLabel;
    @FXML private Label stateLabel;
    @FXML private Button deleteButton;

    public TaskCardBaseController(TaskDTO task) {
        this.currentTask = task;
        this.currentWorkerId = (Long) SessionManagerSingleton
                .getInstance()
                .getAttribute("employeeId");

        this.viewModel = new TaskCardViewModel();
    }

    @FXML
    protected void initialize() {

        titleLabel.setText(currentTask.description());
        stateLabel.setText(
                replaceUnderscores(currentTask.taskState().toString())
        );

        observeViewModel();

        deleteButton.setOnAction(e ->
                viewModel.deleteTask(currentTask.taskId())
        );
    }

    private void observeViewModel() {

        viewModel.deleteTaskStateProperty().addListener((obs, oldState, state) -> {

            if (state == null)
                return;

            if (state.isLoading()) {
                deleteButton.setDisable(true);
                return;
            }

            deleteButton.setDisable(false);

            if (state.getError() != null) {
                AlertDialog.showAlert("Error", state.getError());
            } else {
                AlertDialog.showAlert(
                        "Success",
                        "Task successfully deleted"
                );
            }
        });
    }

    public TaskDTO getCurrentTask() {
        return currentTask;
    }

    public TaskCardViewModel getViewModel() {
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

    public <T> void addListener(
            ReadOnlyObjectProperty<UIState<T>> property,
            Button button,
            String message
    ) {

        property.addListener((_, _, state) -> {

            if (state == null)
                return;

            if (state.isLoading()) {
                button.setDisable(true);
                return;
            }

            button.setDisable(false);

            if (state.getError() != null) {
                showAlert(
                        "Error",
                        state.getError()
                );
            } else {
                System.out.println(message);
            }
        });
    }
}