package com.unimib.GUI.UI.view.components.impl.layout;

import com.unimib.GUI.UI.view.controller.impl.layout.TaskContainerController;
import com.unimib.GUI.UI.view.utils.FXMLUtilLoader;
import com.unimib.GUI.utils.UserSession;
import javafx.scene.layout.BorderPane;

import java.util.Objects;

public class TaskContainer extends BorderPane {

    private final TaskContainerController controller;

    public TaskContainer(UserSession userSession) {
        super();
        controller = new TaskContainerController(userSession);
        FXMLUtilLoader.load(this, controller, "/components/TaskContainer.fxml", null);
        getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/styles/app.css")).toExternalForm()
        );
    }

    public TaskContainerController getController() {
        return controller;
    }
}
