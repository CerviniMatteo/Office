package com.unimib.GUI.view.components.abstr;

import com.unimib.GUI.view.controller.abstr.TaskCardBaseController;
import com.unimib.GUI.view.utils.FXMLUtilLoader;
import javafx.scene.layout.BorderPane;

/**
 * Base class for task cards. Subclasses load a specific FXML layout but
 * share the same controller behavior (TaskCardController).
 */
public abstract class TaskCardBase extends BorderPane {

    protected TaskCardBase(String fxmlResource, TaskCardBaseController controller) {
        super();
        FXMLUtilLoader.load(this, controller, fxmlResource, "task-card");
    }


}