package com.unimib.GUI.UI.view.factory;


import com.unimib.GUI.model.dto.TaskDTO;
import com.unimib.GUI.model.enums.TaskState;
import com.unimib.GUI.UI.view.components.abstr.TaskCardBase;
import com.unimib.GUI.UI.view.components.impl.task.TaskCardDone;
import com.unimib.GUI.UI.view.components.impl.task.TaskCardNotStarted;
import com.unimib.GUI.UI.view.components.impl.task.TaskCardStarted;

/**
 * Factory to create the correct TaskCard subclass based on TaskState.
 */
public final class TaskCardFactory {

    TaskCardFactory() {}

    public static TaskCardBase create(TaskDTO task) {
        if (task == null) return null;
        TaskState state = task.taskState();
        return switch (state) {
            case TO_BE_STARTED -> new TaskCardNotStarted(task);
            case STARTED -> new TaskCardStarted(task);
            case DONE -> new TaskCardDone(task);
        };
    }
}

