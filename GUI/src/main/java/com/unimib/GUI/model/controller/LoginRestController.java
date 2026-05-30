package com.unimib.GUI.model.controller;

import javafx.concurrent.Task;

import static com.unimib.GUI.constants.Rest.BASE_LOGIN_ENDPOINT;

public class LoginRestController extends com.unimib.GUI.model.controller.base.BaseRestController {

    public Task<String> login(String email) {
        return new Task<>() {
            @Override
            protected String call() {
                return getOne(BASE_LOGIN_ENDPOINT + "/" + email, String.class);
            }
        };
    }
}
