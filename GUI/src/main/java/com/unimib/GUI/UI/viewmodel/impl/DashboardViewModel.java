package com.unimib.GUI.UI.viewmodel.impl;

import com.unimib.GUI.UI.state.UIState;
import com.unimib.GUI.UI.viewmodel.BaseViewModel;
import com.unimib.GUI.model.dto.ChatInfoDTO;
import com.unimib.GUI.model.dto.MessageDTO;
import com.unimib.GUI.model.dto.WorkerInfoDTO;
import com.unimib.GUI.model.repository.ChatRepository;
import com.unimib.GUI.model.repository.DashboardRepository;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.List;
import java.util.function.Consumer;

public class DashboardViewModel extends BaseViewModel {

    private final DashboardRepository repository;

    private final ObjectProperty<UIState<Object>> chatsState =
            new SimpleObjectProperty<>();

    private final Long employeeId;

    public DashboardViewModel(Long employeeId) {
        this.employeeId = employeeId;
        this.repository = new  DashboardRepository(employeeId);
    }
}