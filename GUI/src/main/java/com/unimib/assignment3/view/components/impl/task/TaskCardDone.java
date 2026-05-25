package com.unimib.assignment3.view.components.impl.task;

import com.unimib.assignment3.model.dto.TaskDTO;
import com.unimib.assignment3.view.components.abstr.TaskCardBase;
import com.unimib.assignment3.view.controller.impl.card.TaskCardDoneController;

public class TaskCardDone extends TaskCardBase {
    public TaskCardDone(TaskDTO taskDTO) {
        super("/components/TaskCardDone.fxml", new TaskCardDoneController(taskDTO));
    }
}

