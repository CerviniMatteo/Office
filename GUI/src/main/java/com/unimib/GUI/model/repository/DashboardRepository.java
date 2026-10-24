package com.unimib.GUI.model.repository;

import com.unimib.GUI.model.controller.impl.TaskRestController;
import com.unimib.GUI.model.dto.TaskStatsDTO;
import com.unimib.GUI.model.dto.WorkerDTO;
import com.unimib.GUI.utils.UserSession;
import javafx.concurrent.Task;

import static com.unimib.GUI.constants.Rest.EMPLOYEE;

public class DashboardRepository {
    private UserSession userSession;
    private TaskRestController taskRestController;

    public DashboardRepository(UserSession userSession) {
        this.userSession = userSession;
        this.taskRestController = new TaskRestController();
    }

    public Task<WorkerDTO> getWorker() {
        return  new Task<WorkerDTO>() {
            @Override
            protected WorkerDTO call() throws Exception {
                return userSession.sessionManager().getAttribute(EMPLOYEE, WorkerDTO.class);
            }
        };
    }

    public TaskStatsDTO getTaskStats(Long workerId) {
        return taskRestController.getTaskStats(workerId);
    }
}
