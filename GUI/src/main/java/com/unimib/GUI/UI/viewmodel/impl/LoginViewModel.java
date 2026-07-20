package com.unimib.GUI.UI.viewmodel.impl;

import com.unimib.GUI.UI.viewmodel.BaseViewModel;
import com.unimib.GUI.repository.LoginRepository;
import javafx.concurrent.Task;

import static com.unimib.GUI.UI.view.utils.StringHelper.hashString;

public class LoginViewModel extends BaseViewModel {

    private final LoginRepository repository;

    public LoginViewModel() {
        this.repository = new LoginRepository();
    }

    public Task<String> login(String email) {
        return repository.login(hashString(email));
    }
}