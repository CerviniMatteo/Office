package com.unimib.assignment3.UI.view.components.abstr;

import com.unimib.assignment3.UI.view.controller.abstr.TaskCardBaseController;
/**
 * Base class for task cards. Subclasses load a specific FXML layout but
 * share the same controller behavior (TaskCardController).
 */
public abstract class TaskCardBase extends BasePopUpCard {

    protected TaskCardBase(String fxmlResource, TaskCardBaseController controller) {
        super(fxmlResource, controller, "task-card", 700, 400);
        controller.getCloseButton().setOnAction(e -> removeTaskPopup());
    }


}