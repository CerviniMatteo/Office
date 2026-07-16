package com.unimib.GUI.UI.view.controller.impl.card;

import com.unimib.GUI.model.dto.TaskDTO;
import com.unimib.GUI.UI.view.controller.abstr.TaskCardBaseWithWorkersImgController;
import com.unimib.GUI.UI.view.utils.StringHelper;
import com.unimib.GUI.UI.view.utils.WorkerImageUtils;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import static com.unimib.GUI.UI.view.components.impl.custom.AlertDialog.showAlert;

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

        resetButton.setOnAction(_ -> resetTask());
    }

    public TaskCardDoneController(TaskDTO task) {
        super(task);
    }

    private void resetTask() {
        try {
            Task<String> task = getTaskController().resetTaskState(getCurrentTask().taskId());
            task.setOnFailed(_ -> showAlert("Error", task.getException().getMessage()));
            new Thread(task).start();
        } catch (Exception ex) {
            showAlert("Error", "Failed to create request payload");
        }
    }
}
