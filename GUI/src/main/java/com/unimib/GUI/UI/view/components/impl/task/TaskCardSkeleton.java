package com.unimib.GUI.UI.view.components.impl.task;

import com.unimib.GUI.UI.view.components.abstr.TaskCardBase;
import com.unimib.GUI.UI.view.controller.impl.card.TaskCardSkeletonController;
import com.unimib.GUI.model.dto.TaskDTO;
import com.unimib.GUI.utils.UserSession;

public class TaskCardSkeleton extends TaskCardBase {
    public TaskCardSkeleton(TaskDTO taskDTO, UserSession userSession) {
        super("/components/TaskCardSkeleton.fxml", new TaskCardSkeletonController(taskDTO, userSession));
    }
}
