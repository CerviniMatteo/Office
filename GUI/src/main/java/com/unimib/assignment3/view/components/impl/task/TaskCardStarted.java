package com.unimib.assignment3.view.components.impl.task;

import com.unimib.assignment3.model.dto.TaskDTO;
import com.unimib.assignment3.view.components.abstr.TaskCardBase;
import com.unimib.assignment3.view.controller.impl.card.TaskCardStartedController;

public class TaskCardStarted extends TaskCardBase {
    public TaskCardStarted(TaskDTO taskDTO) {
        super("/components/TaskCardStarted.fxml", new TaskCardStartedController(taskDTO));
    }
}

