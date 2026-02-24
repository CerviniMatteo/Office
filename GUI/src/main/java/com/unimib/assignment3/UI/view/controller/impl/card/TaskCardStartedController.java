package com.unimib.assignment3.UI.view.controller.impl.card;

import com.unimib.assignment3.UI.model.dto.AcceptTaskRequestDTO;
import com.unimib.assignment3.UI.model.dto.ChangeTaskStateRequestDTO;
import com.unimib.assignment3.UI.model.dto.TaskDTO;
import com.unimib.assignment3.UI.view.controller.abstr.TaskCardBaseWithWorkersImgController;
import com.unimib.assignment3.UI.view.utils.WorkerImageUtils;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import static com.unimib.assignment3.UI.view.components.impl.custom.AlertDialog.showAlert;
import static com.unimib.assignment3.UI.view.utils.ComponentVisibilityUtils.*;

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
                "TASK STARTED ON: " + getCurrentTask().startDate()
        );

        WorkerImageUtils.populateWorkerImages(
                getWorkersGrid(),
                getCurrentTask().assignedWorkers(),
                getCurrentWorkerId(),
                getImgSize(),
                getStrokeWidth()
        );

        getStateLabel().getStyleClass().add("task-started");

        changeStateButton.setOnAction(e -> completeTask());
        setupButtons();
    }

    private void setupButtons() {
        if (isCurrentWorkerAssigned()) {
            setVisible(false, acceptButton);
            setEnabled(changeStateButton);
            changeStateButton.setOnAction(e -> completeTask());

        } else {
            setEnabled(acceptButton);
            acceptButton.setOnAction(e -> acceptCurrentTask(getCurrentWorkerId()));
            setVisible(false, changeStateButton);
        }
    }


    private void acceptCurrentTask(Long workerId) {
        try {
            AcceptTaskRequestDTO payload = new AcceptTaskRequestDTO(getCurrentTask().taskId(), workerId);
            payload.validate();
            Task<String> task = getTaskController().acceptTask(payload);
            task.setOnSucceeded(ev -> setDisabled(acceptButton));
            task.setOnFailed(ev -> showAlert("Error", task.getException().getMessage()));
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
            task.setOnSucceeded(ev -> System.out.println("Task completed!"));
            task.setOnFailed(ev -> showAlert("Error", task.getException().getMessage()));
            new Thread(task).start();
        } catch (Exception ex) {
            showAlert("Error", "Failed to create request payload");
        }
    }
}
