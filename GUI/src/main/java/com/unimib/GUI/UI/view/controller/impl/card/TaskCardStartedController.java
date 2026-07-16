package com.unimib.GUI.UI.view.controller.impl.card;

import com.unimib.GUI.model.dto.AcceptTaskRequestDTO;
import com.unimib.GUI.model.dto.ChangeTaskStateRequestDTO;
import com.unimib.GUI.model.dto.TaskDTO;
import com.unimib.GUI.UI.view.controller.abstr.TaskCardBaseWithWorkersImgController;
import com.unimib.GUI.UI.view.utils.StringHelper;
import com.unimib.GUI.UI.view.utils.WorkerImageUtils;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import static com.unimib.GUI.UI.view.components.impl.custom.AlertDialog.showAlert;
import static com.unimib.GUI.UI.view.utils.ComponentVisibilityUtils.*;

public class TaskCardStartedController extends TaskCardBaseWithWorkersImgController {

    @FXML
    private Button acceptButton;
    @FXML
    private Button changeStateButton;

    public TaskCardStartedController(TaskDTO task) {
        super(task);
    }

    @FXML
    protected void initialize() {
        super.initialize();

        getTitleLabel().getStyleClass().add("task-started");

        getDateLabel().setText(
                "TASK STARTED ON: " + StringHelper.localDateTimeFormatter(getCurrentTask().startDate())
        );

        WorkerImageUtils.populateWorkerImages(
                getWorkersGrid(),
                getCurrentTask().assignedWorkers(),
                getCurrentWorkerId(),
                getImgSize(),
                getStrokeWidth()
        );

        getStateLabel().getStyleClass().add("task-started");

        changeStateButton.setOnAction(_ -> completeTask());
        setupButtons();
    }

    private void setupButtons() {
        if (isCurrentWorkerAssigned()) {
            setVisible(false, acceptButton);
            setEnabled(changeStateButton);
            changeStateButton.setOnAction(_ -> completeTask());

        } else {
            setEnabled(acceptButton);
            acceptButton.setOnAction(_ -> acceptCurrentTask(getCurrentWorkerId()));
            setVisible(false, changeStateButton);
        }
    }


    private void acceptCurrentTask(Long workerId) {
        try {
            AcceptTaskRequestDTO payload = new AcceptTaskRequestDTO(getCurrentTask().taskId(), workerId);
            payload.validate();
            Task<String> task = getTaskController().acceptTask(payload);
            task.setOnSucceeded(_ -> setDisabled(acceptButton));
            task.setOnFailed(_ -> showAlert("Error", task.getException().getMessage()));
            new Thread(task).start();
        } catch (Exception ex) {
            showAlert("Error", "Failed to create request payload");
        }
    }


    private void completeTask() {
        try {
            ChangeTaskStateRequestDTO payload = new ChangeTaskStateRequestDTO(getCurrentTask().taskId(), getCurrentTask().taskState());
            payload.validate();
            Task<String> task = getTaskController().changeTaskState(payload);
            task.setOnSucceeded(_ -> System.out.println("Task completed!"));
            task.setOnFailed(_ -> showAlert("Error", task.getException().getMessage()));
            new Thread(task).start();
        } catch (Exception ex) {
            showAlert("Error", "Failed to create request payload");
        }
    }
}
