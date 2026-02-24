package com.unimib.assignment3.UI.view.components.impl.task;

import com.unimib.assignment3.UI.model.dto.TaskDTO;
import com.unimib.assignment3.UI.view.components.abstr.TaskCardBase;
import com.unimib.assignment3.UI.view.controller.impl.card.TaskCardNotStartedController;

public class TaskCardNotStarted extends TaskCardBase {
    public TaskCardNotStarted(TaskDTO taskDTO) {
        super("/components/TaskCardNotStarted.fxml", new TaskCardNotStartedController(taskDTO));
    }
}

