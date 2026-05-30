package com.unimib.GUI.view.components.impl.task;

import com.unimib.GUI.model.dto.TaskDTO;
import com.unimib.GUI.view.components.abstr.TaskCardBase;
import com.unimib.GUI.view.controller.impl.card.TaskCardStartedController;

public class TaskCardStarted extends TaskCardBase {
    public TaskCardStarted(TaskDTO taskDTO) {
        super("/components/TaskCardStarted.fxml", new TaskCardStartedController(taskDTO));
    }
}

