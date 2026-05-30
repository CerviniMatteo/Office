package com.unimib.GUI.view.components.impl.task;

import com.unimib.GUI.model.dto.TaskDTO;
import com.unimib.GUI.view.components.abstr.TaskCardBase;
import com.unimib.GUI.view.controller.impl.card.TaskCardNotStartedController;

public class TaskCardNotStarted extends TaskCardBase {
    public TaskCardNotStarted(TaskDTO taskDTO) {
        super("/components/TaskCardNotStarted.fxml", new TaskCardNotStartedController(taskDTO));
    }
}

