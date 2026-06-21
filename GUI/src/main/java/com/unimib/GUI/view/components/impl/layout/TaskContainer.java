package com.unimib.GUI.view.components.impl.layout;

import com.unimib.GUI.view.controller.impl.layout.TaskContainerController;
import com.unimib.GUI.view.utils.FXMLUtilLoader;
import javafx.scene.layout.BorderPane;

import java.util.Objects;

public class TaskContainer extends BorderPane {

    private final TaskContainerController controller;

    public TaskContainer() {
        super();
        controller = new TaskContainerController();
        FXMLUtilLoader.load(this, controller, "/components/TaskContainer.fxml", null);
        getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/styles/app.css")).toExternalForm()
        );
    }

    public TaskContainerController getController() {
        return controller;
    }
}
