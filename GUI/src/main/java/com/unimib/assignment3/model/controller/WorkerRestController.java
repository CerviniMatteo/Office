
package com.unimib.assignment3.model.controller;

import com.unimib.assignment3.model.controller.base.BaseRestController;
import com.unimib.assignment3.model.dto.WorkerDTO;
import javafx.concurrent.Task;


import static com.unimib.assignment3.constants.Rest.BASE_EMPLOYEE_ENDPOINT;

public class WorkerRestController extends BaseRestController {

    public Task<WorkerDTO> fetchWorker(Long workerId) {
        return new Task<>() {
            @Override
            protected WorkerDTO call() {
                try {
                    return getOne(BASE_EMPLOYEE_ENDPOINT + "/" + workerId, WorkerDTO.class);
                } catch (Exception e) {
                    com.unimib.assignment3.view.components.impl.custom.AlertDialog.showAlert("Error", e.getMessage());
                    return null;
                }
            }
        };
    }
}
