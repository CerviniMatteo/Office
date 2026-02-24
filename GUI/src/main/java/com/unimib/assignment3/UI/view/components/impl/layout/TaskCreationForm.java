package com.unimib.assignment3.UI.view.components.impl.layout;

import com.unimib.assignment3.UI.view.controller.impl.layout.TaskCreationFormController;
import com.unimib.assignment3.UI.view.utils.FXMLUtilLoader;
import javafx.geometry.Pos;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class TaskCreationForm extends VBox {

    private final TaskCreationFormController controller;

    public TaskCreationForm() {
        super(8);
        controller = new TaskCreationFormController();
        FXMLUtilLoader.load(this, controller, "/components/TaskCreationForm.fxml", "");

        setAlignment(Pos.CENTER);
        this.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        this.setFillWidth(true);
        VBox.setVgrow(this, Priority.ALWAYS);
    }

    public void setOnSuccess(Runnable onSuccess) {
        if (controller != null) controller.setOnSuccess(onSuccess);
    }
}
