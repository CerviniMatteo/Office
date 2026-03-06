package com.unimib.assignment3.UI.view.controller.impl.card;

import com.unimib.assignment3.UI.model.dto.TaskDTO;
import com.unimib.assignment3.UI.view.controller.abstr.TaskCardBaseWithWorkersImgController;
import com.unimib.assignment3.UI.view.utils.StringFormatter;
import com.unimib.assignment3.UI.view.utils.WorkerImageUtils;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import static com.unimib.assignment3.UI.view.components.impl.custom.AlertDialog.showAlert;

public class TaskCardDoneController extends TaskCardBaseWithWorkersImgController {
    @FXML
    private Button resetButton;

    @FXML
    protected void initialize() {
        super.initialize();

        getDateLabel().setText(
                "TASK STARTED ON: " + StringFormatter.localDateTimeFormatter(getCurrentTask().startDate()) + "\n" +
                "TASK COMPLETED ON: " + StringFormatter.localDateTimeFormatter(getCurrentTask().endDate())
        );

        WorkerImageUtils.populateWorkerImages(
                getWorkersGrid(),
                getCurrentTask().assignedWorkers(),
                getCurrentWorkerId(),
                getImgSize(),
                getStrokeWidth()
        );

        getStateLabel().getStyleClass().add("task-done");

        resetButton.setOnAction(e -> resetTask());
    }

    public TaskCardDoneController(TaskDTO task) {
        super(task);
    }

    private void resetTask() {
        try {
            Task<String> task = getTaskController().resetTaskState(getCurrentTask().taskId());
            task.setOnFailed(ev -> showAlert("Error", task.getException().getMessage()));
            new Thread(task).start();
        } catch (Exception ex) {
            showAlert("Error", "Failed to create request payload");
        }
    }
}
