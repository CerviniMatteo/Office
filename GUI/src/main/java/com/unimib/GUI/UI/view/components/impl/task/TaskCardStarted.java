package com.unimib.GUI.UI.view.components.impl.task;

import com.unimib.GUI.model.dto.TaskDTO;
import com.unimib.GUI.UI.view.components.abstr.TaskCardBase;
import com.unimib.GUI.UI.view.controller.impl.card.TaskCardStartedController;

public class TaskCardStarted extends TaskCardBase {
    public TaskCardStarted(TaskDTO taskDTO) {
        super("/components/TaskCardStarted.fxml", new TaskCardStartedController(taskDTO));
    }
}

