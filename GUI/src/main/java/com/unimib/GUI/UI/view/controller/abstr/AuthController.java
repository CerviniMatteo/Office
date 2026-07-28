package com.unimib.GUI.UI.view.controller.abstr;

import com.unimib.GUI.UI.state.ApplicationStateManager;
import com.unimib.GUI.UI.view.components.impl.layout.TaskContainer;
import com.unimib.GUI.utils.SessionManagerSingleton;

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