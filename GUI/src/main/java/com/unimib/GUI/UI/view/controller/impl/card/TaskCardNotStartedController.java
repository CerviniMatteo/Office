package com.unimib.GUI.UI.view.controller.impl.card;

import com.unimib.GUI.model.dto.StartTaskRequestDTO;
import com.unimib.GUI.model.dto.TaskDTO;
import com.unimib.GUI.UI.view.controller.abstr.TaskCardBaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import static com.unimib.GUI.UI.view.components.impl.custom.AlertDialog.showAlert;

public class TaskCardNotStartedController extends TaskCardBaseController {

    @FXML
    private Button changeStateButton;

    @FXML
    protected void initialize() {
        super.initialize();
        getStateLabel().getStyleClass().add("task-to-start");

        addListener(
                getViewModel().startTaskStateProperty(),
                changeStateButton,
                "Task started!"
        );

        changeStateButton.setOnAction(_ -> startTask());
    }

    public TaskCardNotStartedController(TaskDTO task) {
        super(task);
    }

    private void startTask() {
        try {
            StartTaskRequestDTO payload = new StartTaskRequestDTO(
                    getCurrentTask().taskId(),
                    getCurrentWorkerId()
            );

            payload.validate();

            getViewModel().startTask(payload);

        } catch (Exception ex) {
            showAlert("Error", "Failed to create request payload");
        }
    }
}

