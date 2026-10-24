package com.unimib.GUI.model.repository;

import com.unimib.GUI.model.dto.WorkerDTO;
import com.unimib.GUI.utils.UserSession;

import static com.unimib.GUI.constants.Rest.EMPLOYEE;

public class DashboardRepository {
    private UserSession userSession;

    public DashboardRepository(UserSession userSession) {
        this.userSession = userSession;
    }

    public WorkerDTO getWorker() {
        return userSession.sessionManager().getAttribute(EMPLOYEE, WorkerDTO.class);
    }
}
