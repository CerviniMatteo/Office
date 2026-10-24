package com.unimib.GUI.UI.view.controller.abstr;

import com.unimib.GUI.UI.state.ApplicationStateManager;
import com.unimib.GUI.UI.view.components.impl.layout.DashboardContainer;
import com.unimib.GUI.UI.view.components.impl.layout.TaskContainer;
import com.unimib.GUI.utils.UserSession;

public abstract class AuthController extends DefaultController {

    protected AuthController(UserSession userSession) {
        super(userSession);
    }

    protected void saveEmployeeId(Long employeeId) {
        userSession.sessionManager().setAttribute("employeeId", employeeId);
    }

    protected void goToTaskContainer() {
        ApplicationStateManager manager = userSession.applicationStateManager();
        manager.replaceWindow(new TaskContainer(userSession));
        showSuccess("Login successful, Welcome back!");
    }
}