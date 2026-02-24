package com.unimib.assignment3.UI.view.components.abstr;

import com.unimib.assignment3.UI.view.controller.abstr.TaskCardBaseController;
import com.unimib.assignment3.UI.view.utils.FXMLUtilLoader;
import javafx.scene.layout.BorderPane;

/**
 * Base class for task cards. Subclasses load a specific FXML layout but
 * share the same controller behavior (TaskCardController).
 */
public abstract class TaskCardBase extends BorderPane{

    protected final TaskCardBaseController controller;

    protected TaskCardBase(String fxmlResource, TaskCardBaseController taskCardBaseController) {
        controller = taskCardBaseController;
        FXMLUtilLoader.load(this, taskCardBaseController, fxmlResource, "task-card");
    }

    public TaskCardBaseController getController() {
        return controller;
    }
}

