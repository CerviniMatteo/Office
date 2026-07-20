package com.unimib.GUI.UI.view.controller.impl.card;

import com.unimib.GUI.model.dto.StartTaskRequestDTO;
import com.unimib.GUI.model.dto.TaskDTO;
import com.unimib.GUI.UI.view.controller.abstr.TaskCardBaseController;

import javafx.fxml.FXML;
import javafx.scene.control.Button;


public class TaskCardNotStartedController extends TaskCardBaseController {


    @FXML
    private Button changeStateButton;


    public TaskCardNotStartedController(TaskDTO task) {
        super(task);
    }


    @FXML
    protected void initialize() {

        super.initialize();


        getStateLabel()
                .getStyleClass()
                .add("task-to-start");


        observeState(
                getViewModel().getStartTaskStateProperty(),


                () -> changeStateButton.setDisable(true),

                _ -> {
                    changeStateButton.setDisable(false);
                    showSuccess("Task started successfully!");
                },

                this::showError
        );


        changeStateButton.setOnAction(
                _ -> startTask()
        );
    }



    private void startTask() {

        try {

            StartTaskRequestDTO payload =
                    new StartTaskRequestDTO(
                            getCurrentTask().taskId(),
                            getCurrentWorkerId()
                    );


            payload.validate();


            getViewModel()
                    .startTask(payload);


        } catch (Exception ex) {

            showError("Failed to create request payload");
        }
    }
}