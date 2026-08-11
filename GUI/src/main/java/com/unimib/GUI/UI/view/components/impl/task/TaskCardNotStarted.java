package com.unimib.GUI.UI.view.components.impl.task;

import com.unimib.GUI.model.dto.TaskDTO;
import com.unimib.GUI.UI.view.components.abstr.TaskCardBase;
import com.unimib.GUI.UI.view.controller.impl.card.TaskCardNotStartedController;
import com.unimib.GUI.utils.UserSession;

public class TaskCardNotStarted extends TaskCardBase {
    public TaskCardNotStarted(TaskDTO taskDTO, UserSession userSession) {
        super("/components/TaskCardNotStarted.fxml", new TaskCardNotStartedController(taskDTO, userSession));
    }
}
