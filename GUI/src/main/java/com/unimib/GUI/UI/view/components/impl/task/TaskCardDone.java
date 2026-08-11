package com.unimib.GUI.UI.view.components.impl.task;

import com.unimib.GUI.model.dto.TaskDTO;
import com.unimib.GUI.UI.view.components.abstr.TaskCardBase;
import com.unimib.GUI.UI.view.controller.impl.card.TaskCardDoneController;
import com.unimib.GUI.utils.UserSession;

public class TaskCardDone extends TaskCardBase {
    public TaskCardDone(TaskDTO taskDTO, UserSession userSession) {
        super("/components/TaskCardDone.fxml", new TaskCardDoneController(taskDTO, userSession));
    }
}
