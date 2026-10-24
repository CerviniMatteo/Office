package com.unimib.GUI.model.controller.impl;

import com.unimib.GUI.model.controller.BaseRestController;
import com.unimib.GUI.model.dto.WorkerDTO;
import javafx.concurrent.Task;

import static com.unimib.GUI.constants.Rest.*;

public class AuthRestController extends BaseRestController {

    public WorkerDTO login(String email) {
        return getOne(BASE_LOGIN_ENDPOINT + "/" + email, WorkerDTO.class);
    }

    public Task<WorkerDTO> signup(WorkerDTO worker) {
        return post(BASE_REGISTRATION_ENDPOINT, worker, WorkerDTO.class);
    }
}
