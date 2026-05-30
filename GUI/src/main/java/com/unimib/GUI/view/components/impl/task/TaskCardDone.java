package com.unimib.GUI.view.components.impl.task;

import com.unimib.GUI.model.dto.TaskDTO;
import com.unimib.GUI.view.components.abstr.TaskCardBase;
import com.unimib.GUI.view.controller.impl.card.TaskCardDoneController;

public class TaskCardDone extends TaskCardBase {
    public TaskCardDone(TaskDTO taskDTO) {
        super("/components/TaskCardDone.fxml", new TaskCardDoneController(taskDTO));
    }
}

