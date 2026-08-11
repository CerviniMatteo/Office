
package com.unimib.GUI.model.controller.impl;

import com.unimib.GUI.model.controller.BaseRestController;
import com.unimib.GUI.model.dto.WorkerDTO;
import javafx.concurrent.Task;


import static com.unimib.GUI.constants.Rest.BASE_EMPLOYEE_ENDPOINT;

public class WorkerRestController extends BaseRestController {

    public Task<WorkerDTO> fetchWorker(Long workerId) {
        return new Task<>() {
            @Override
            protected WorkerDTO call() throws Exception {
                return getOne(BASE_EMPLOYEE_ENDPOINT + "/" + workerId, WorkerDTO.class);
            }
        };
    }
}
