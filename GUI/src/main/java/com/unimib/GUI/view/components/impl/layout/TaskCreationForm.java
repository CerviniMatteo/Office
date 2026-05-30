package com.unimib.GUI.view.components.impl.layout;

import com.unimib.GUI.model.dto.TaskDTO;
import com.unimib.GUI.view.components.abstr.BasePopUpCard;
import com.unimib.GUI.view.controller.impl.layout.TaskCreationFormController;
import com.unimib.GUI.view.state.ApplicationStateManager;

import java.util.function.Consumer;

public class TaskCreationForm extends BasePopUpCard{

    public TaskCreationForm(TaskCreationFormController controller) {
        super("/components/TaskCreationForm.fxml", controller, "task-creation-overlay", Double.MAX_VALUE, Double.MAX_VALUE);
        controller.getCloseButton().setOnAction(_ -> ApplicationStateManager.getInstance().removeWindow(this));
    }

    public void setOnSuccess(Consumer<TaskDTO> onSuccess) {
        if (controller != null) ((TaskCreationFormController) controller).setOnSuccess(onSuccess);
    }

    public void setOnClose(Runnable onClose) {

        if (controller != null) ((TaskCreationFormController) controller).setOnClose(onClose);
    }
}
