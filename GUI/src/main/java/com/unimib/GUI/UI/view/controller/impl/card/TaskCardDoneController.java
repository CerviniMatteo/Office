package com.unimib.GUI.UI.view.controller.impl.card;

import com.unimib.GUI.model.dto.TaskDTO;
import com.unimib.GUI.UI.view.controller.abstr.TaskCardBaseWithWorkersImgController;
import com.unimib.GUI.UI.view.utils.StringHelper;
import com.unimib.GUI.UI.view.utils.WorkerImageUtils;
import com.unimib.GUI.utils.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class TaskCardDoneController extends TaskCardBaseWithWorkersImgController {
    @FXML
    private Button resetButton;

    @FXML
    protected void initialize() {
        super.initialize();

        getDateLabel().setText(
                "TASK STARTED ON: " + StringHelper.localDateTimeFormatter(getCurrentTask().startDate()) + "\n" +
                "TASK COMPLETED ON: " + StringHelper.localDateTimeFormatter(getCurrentTask().endDate())
        );

        WorkerImageUtils.populateWorkerImages(
                getWorkersGrid(),
                getCurrentTask().assignedWorkers(),
                getCurrentWorkerId(),
                getImgSize(),
                getStrokeWidth()
        );

        getStateLabel().getStyleClass().add("task-done");

        observeState(
                getViewModel().getResetTaskStateProperty(),
                () -> resetButton.setDisable(true),
                _ -> {
                    resetButton.setDisable(false);
                    showSuccess("Task reset successfully!");
                },
                this::showError
        );

        resetButton.setOnAction(_ -> resetTask());
    }

    public TaskCardDoneController(TaskDTO task, UserSession userSession) {
        super(task, userSession);
    }

    private void resetTask() {
        try {
            getViewModel().resetTaskState(getCurrentTask().taskId());
        } catch (Exception ex) {
            showError("Failed to create request payload");
        }
    }
}
