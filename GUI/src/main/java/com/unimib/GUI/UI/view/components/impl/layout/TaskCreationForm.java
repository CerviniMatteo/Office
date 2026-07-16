package com.unimib.GUI.UI.view.components.impl.layout;

import com.unimib.GUI.UI.view.controller.impl.layout.TaskCreationFormController;
import com.unimib.GUI.UI.view.utils.FXMLUtilLoader;
import javafx.scene.layout.BorderPane;

public class TaskCreationForm extends BorderPane {

    private final TaskCreationFormController controller;

    public TaskCreationForm(TaskCreationFormController controller) {
        this.controller = controller;
        FXMLUtilLoader.load(this, controller, "/components/TaskCreationForm.fxml", "app.css");
    }

    /**
     * Resets this form to its default state by delegating to the
     * {@link TaskCreationFormController} supplied by the creator
     * (e.g. when the creator wants to reopen/reuse this form without
     * re-instantiating it).
     */
    public void clear() {
        controller.clear();
    }
}