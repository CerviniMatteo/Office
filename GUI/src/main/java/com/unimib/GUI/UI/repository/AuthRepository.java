package com.unimib.GUI.UI.repository;

import com.unimib.GUI.model.controller.impl.AuthRestController;
import com.unimib.GUI.model.dto.WorkerDTO;
import javafx.concurrent.Task;

public class AuthRepository {

    private final AuthRestController dataSource;

    public AuthRepository() {
        this.dataSource = new AuthRestController();
    }

    public Long login(String email) {
        return dataSource.login(email);
    }

    public Task<WorkerDTO> signup(WorkerDTO worker) {
        return dataSource.signup(worker);
    }
}