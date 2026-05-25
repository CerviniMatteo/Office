package com.unimib.assignment3.view.components.impl.task;

import com.unimib.assignment3.model.dto.TaskDTO;
import com.unimib.assignment3.view.components.abstr.TaskCardBase;
import com.unimib.assignment3.view.controller.impl.card.TaskCardNotStartedController;

public class TaskCardNotStarted extends TaskCardBase {
    public TaskCardNotStarted(TaskDTO taskDTO) {
        super("/components/TaskCardNotStarted.fxml", new TaskCardNotStartedController(taskDTO));
    }
}

