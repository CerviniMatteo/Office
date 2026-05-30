package com.unimib.GUI.view.controller.impl.card;

import com.unimib.GUI.model.controller.TaskRestController;
import com.unimib.GUI.model.dto.StartTaskRequestDTO;
import com.unimib.GUI.model.dto.TaskDTO;
import com.unimib.GUI.utils.SessionManagerSingleton;
import com.unimib.GUI.view.controller.abstr.TaskCardBaseController;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import static com.unimib.GUI.view.components.impl.custom.AlertDialog.showAlert;

public class TaskCardNotStartedController extends TaskCardBaseController {

        @FXML
        private Button changeStateButton;

        private final TaskRestController taskRestController = new TaskRestController();

    @FXML
    protected void initialize() {
        super.initialize();

        getStateLabel().getStyleClass().add("task-to-start");
        changeStateButton.setOnAction(_ -> startTask());
    }

    public TaskCardNotStartedController(TaskDTO task) {
        super(task);
    }

    private void startTask() {
        try {
            StartTaskRequestDTO payload = new StartTaskRequestDTO(
                getCurrentTask().taskId(),
                (Long) SessionManagerSingleton.getInstance().getAttribute("employeeId")
            );
            payload.validate();
            Task<String> task = taskRestController.startTask(payload);
            task.setOnSucceeded(_ -> System.out.println("Task started!"));
            task.setOnFailed(_ -> showAlert("Error", task.getException().getMessage()));
            new Thread(task).start();
        } catch (Exception ex) {
            showAlert("Error", "Failed to create request payload");
        }
    }
}
