package com.unimib.GUI.model.repository;

import com.unimib.GUI.model.controller.impl.AuthRestController;
import com.unimib.GUI.model.dto.WorkerDTO;
import com.unimib.GUI.utils.UserSession;
import javafx.concurrent.Task;

import static com.unimib.GUI.constants.Rest.EMPLOYEE;

public class AuthRepository {

    private final AuthRestController dataSource;
    private final UserSession userSession;

    public AuthRepository(UserSession userSession) {
        this.dataSource = new AuthRestController();
        this.userSession = userSession;
    }

    public WorkerDTO login(String email) {
        WorkerDTO workerDTO = dataSource.login(email);
        writeToCache(workerDTO);

        return workerDTO;
    }

    public Task<WorkerDTO> signup(WorkerDTO worker) {
        Task<WorkerDTO> task =  dataSource.signup(worker);
        task.setOnSucceeded(event -> {
            WorkerDTO workerDTO = task.getValue();
            writeToCache(workerDTO);
        });

        return task;
    }

    private void writeToCache(WorkerDTO workerDTO) {
        userSession.sessionManager().setAttribute(EMPLOYEE, workerDTO);
    }
}