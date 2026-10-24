package com.unimib.GUI.UI.viewmodel.impl;

import com.unimib.GUI.UI.state.UIState;
import com.unimib.GUI.UI.viewmodel.BaseViewModel;
import com.unimib.GUI.model.dto.TaskStatsDTO;
import com.unimib.GUI.model.dto.WorkerDTO;
import com.unimib.GUI.model.repository.DashboardRepository;
import com.unimib.GUI.utils.UserSession;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class DashboardViewModel extends BaseViewModel {

    private final DashboardRepository repository;
    private final ObjectProperty<UIState<WorkerDTO>> workerState =
            new SimpleObjectProperty<>();

    private final ObjectProperty<UIState<TaskStatsDTO>> taskStatsState =
            new SimpleObjectProperty<>();

    public DashboardViewModel(UserSession userSession) {
        this.repository = new DashboardRepository(userSession);
    }

    public void getWorker() {
        execute(
                repository.getWorker(),
                workerState
        );
    }

    public void getTaskStats(Long workerId) {
        execute(
                () -> repository.getTaskStats(workerId),
                taskStatsState
        );
    }

    public ReadOnlyObjectProperty<UIState<WorkerDTO>> workerStateProperty() {
        return workerState;
    }

    public ReadOnlyObjectProperty<UIState<TaskStatsDTO>> taskStatsStateProperty() {
        return taskStatsState;
    }
}