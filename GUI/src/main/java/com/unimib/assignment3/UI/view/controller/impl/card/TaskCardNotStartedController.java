package com.unimib.assignment3.UI.view.controller.impl.card;

import com.unimib.assignment3.UI.model.controller.TaskRestController;
import com.unimib.assignment3.UI.model.dto.StartTaskRequestDTO;
import com.unimib.assignment3.UI.model.dto.TaskDTO;
import com.unimib.assignment3.UI.utils.SessionManagerSingleton;
import com.unimib.assignment3.UI.view.controller.abstr.TaskCardBaseController;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import static com.unimib.assignment3.UI.view.components.impl.custom.AlertDialog.showAlert;

public class TaskCardNotStartedController extends TaskCardBaseController {

        @FXML
        private Button changeStateButton;

        private final TaskRestController taskRestController = new TaskRestController();

    @FXML
    protected void initialize() {
        super.initialize();

        getStateLabel().getStyleClass().add("task-to-start");
        changeStateButton.setOnAction(e -> startTask());
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
            task.setOnSucceeded(ev -> System.out.println("Task started!"));
            task.setOnFailed(ev -> showAlert("Error", task.getException().getMessage()));
            new Thread(task).start();
        } catch (Exception ex) {
            showAlert("Error", "Failed to create request payload");
        }
    }
}
