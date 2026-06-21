package com.unimib.GUI.view.components.impl.layout;

import com.unimib.GUI.view.controller.impl.layout.TaskCreationFormController;
import com.unimib.GUI.view.utils.FXMLUtilLoader;
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