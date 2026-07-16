package com.unimib.GUI.UI.view.controller.impl.card;

import com.unimib.GUI.model.dto.AcceptTaskRequestDTO;
import com.unimib.GUI.model.dto.ChangeTaskStateRequestDTO;
import com.unimib.GUI.model.dto.TaskDTO;
import com.unimib.GUI.UI.view.controller.abstr.TaskCardBaseWithWorkersImgController;
import com.unimib.GUI.UI.view.utils.StringHelper;
import com.unimib.GUI.UI.view.utils.WorkerImageUtils;

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

        getTitleLabel()
                .getStyleClass()
                .add("task-started");


        getDateLabel().setText(
                "TASK STARTED ON: " +
                        StringHelper.localDateTimeFormatter(
                                getCurrentTask().startDate()
                        )
        );


        WorkerImageUtils.populateWorkerImages(
                getWorkersGrid(),
                getCurrentTask().assignedWorkers(),
                getCurrentWorkerId(),
                getImgSize(),
                getStrokeWidth()
        );


        getStateLabel()
                .getStyleClass()
                .add("task-started");


        addListener(
                getViewModel().acceptTaskStateProperty(),
                acceptButton,
                "Task accepted!"
        );


        addListener(
                getViewModel().changeTaskStateProperty(),
                changeStateButton,
                "Task completed!"
        );


        setupButtons();
    }



    private void setupButtons() {

        if (isCurrentWorkerAssigned()) {

            setVisible(false, acceptButton);
            setEnabled(changeStateButton);

            changeStateButton.setOnAction(
                    _ -> completeTask()
            );

        } else {

            setEnabled(acceptButton);

            acceptButton.setOnAction(
                    _ -> acceptTask()
            );

            setVisible(false, changeStateButton);
        }
    }



    private void acceptTask() {

        try {

            AcceptTaskRequestDTO payload =
                    new AcceptTaskRequestDTO(
                            getCurrentTask().taskId(),
                            getCurrentWorkerId()
                    );


            payload.validate();

            getViewModel()
                    .acceptTask(payload);


        } catch (Exception ex) {

            showAlert(
                    "Error",
                    "Failed to create request payload"
            );
        }
    }



    private void completeTask() {

        try {

            ChangeTaskStateRequestDTO payload =
                    new ChangeTaskStateRequestDTO(
                            getCurrentTask().taskId(),
                            getCurrentTask().taskState()
                    );


            payload.validate();

            getViewModel()
                    .changeTaskState(payload);


        } catch (Exception ex) {

            showAlert(
                    "Error",
                    "Failed to create request payload"
            );
        }
    }
}