
package com.unimib.GUI.model.controller.impl;

import com.unimib.GUI.model.controller.BaseRestController;
import com.unimib.GUI.model.dto.WorkerDTO;
import javafx.concurrent.Task;


import static com.unimib.GUI.constants.Rest.BASE_EMPLOYEE_ENDPOINT;

public class WorkerRestController extends BaseRestController {

    public Task<WorkerDTO> fetchWorker(Long workerId) {
        return new Task<>() {
            @Override
            protected WorkerDTO call() {
                try {
                    return getOne(BASE_EMPLOYEE_ENDPOINT + "/" + workerId, WorkerDTO.class);
                } catch (Exception e) {
                    com.unimib.GUI.UI.view.components.impl.custom.AlertDialog.showAlert("Error", e.getMessage());
                    return null;
                }
            }
        };
    }
}
