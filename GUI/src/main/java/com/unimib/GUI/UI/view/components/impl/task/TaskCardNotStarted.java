package com.unimib.GUI.UI.view.components.impl.task;

import com.unimib.GUI.model.dto.TaskDTO;
import com.unimib.GUI.UI.view.components.abstr.TaskCardBase;
import com.unimib.GUI.UI.view.controller.impl.card.TaskCardNotStartedController;

public class TaskCardNotStarted extends TaskCardBase {
    public TaskCardNotStarted(TaskDTO taskDTO) {
        super("/components/TaskCardNotStarted.fxml", new TaskCardNotStartedController(taskDTO));
    }
}

