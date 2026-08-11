package com.unimib.GUI.UI.view.factory;


import com.unimib.GUI.model.dto.TaskDTO;
import com.unimib.GUI.model.enums.TaskState;
import com.unimib.GUI.UI.view.components.abstr.TaskCardBase;
import com.unimib.GUI.UI.view.components.impl.task.TaskCardDone;
import com.unimib.GUI.UI.view.components.impl.task.TaskCardNotStarted;
import com.unimib.GUI.UI.view.components.impl.task.TaskCardStarted;
import com.unimib.GUI.utils.UserSession;

/**
 * Factory to create the correct TaskCard subclass based on TaskState.
 */
public final class TaskCardFactory {

    TaskCardFactory() {}

    public static TaskCardBase create(TaskDTO task, UserSession userSession) {
        if (task == null) return null;
        TaskState state = task.taskState();
        return switch (state) {
            case TO_BE_STARTED -> new TaskCardNotStarted(task, userSession);
            case STARTED -> new TaskCardStarted(task, userSession);
            case DONE -> new TaskCardDone(task, userSession);
        };
    }
}
