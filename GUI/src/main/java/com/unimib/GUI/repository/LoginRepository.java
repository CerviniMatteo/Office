package com.unimib.GUI.repository;

import com.unimib.GUI.model.controller.LoginRestController;
import javafx.concurrent.Task;

public class LoginRepository {

    private final LoginRestController dataSource;

    public LoginRepository() {
        this.dataSource = new LoginRestController();
    }

    public Task<String> login(String email) {
        return dataSource.login(email);
    }
}