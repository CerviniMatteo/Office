package com.unimib.GUI.UI.viewmodel.impl;

import com.unimib.GUI.UI.viewmodel.BaseViewModel;
import com.unimib.GUI.model.dto.WorkerDTO;
import com.unimib.GUI.model.repository.DashboardRepository;
import com.unimib.GUI.utils.UserSession;

public class DashboardViewModel extends BaseViewModel {

    private final DashboardRepository repository;

    public DashboardViewModel(UserSession userSession) {
        this.repository = new DashboardRepository(userSession);
    }

    public WorkerDTO getWorker() {
        return repository.getWorker();
    }
}