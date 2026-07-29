package com.unimib.GUI.UI.view.controller.abstr;

import com.unimib.GUI.UI.state.ApplicationStateManager;
import com.unimib.GUI.UI.view.components.impl.layout.Login;
import com.unimib.GUI.UI.view.components.impl.layout.Registration;
import com.unimib.GUI.UI.view.components.impl.layout.TaskContainer;
import com.unimib.GUI.utils.SessionManagerSingleton;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

public abstract class AuthController implements DefaultController {

    protected void saveEmployeeId(Long employeeId) {
        SessionManagerSingleton
                .getInstance()
                .setAttribute("employeeId", employeeId);
    }

    protected void goToTaskContainer() {
        ApplicationStateManager
                .getInstance()
                .replaceWindow(new TaskContainer());
    }
}