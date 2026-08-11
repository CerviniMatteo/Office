package com.unimib.GUI.utils;

import com.unimib.GUI.UI.state.ApplicationStateManager;

public record UserSession(
        ApplicationStateManager applicationStateManager,
        SessionManager sessionManager
) {

    public Long getEmployeeId() {
        Long employeeId =
                sessionManager.getAttribute(
                        "employeeId",
                        Long.class
                );

        if (employeeId == null) {
            throw new IllegalStateException(
                    "Missing employeeId in session"
            );
        }

        return employeeId;
    }

    public void clear() {
        sessionManager.clear();
    }
}