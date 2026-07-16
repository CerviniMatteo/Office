package com.unimib.GUI.UI.viewmodel.impl;

import com.unimib.GUI.UI.state.UIState;
import com.unimib.GUI.UI.viewmodel.BaseViewModel;
import com.unimib.GUI.model.dto.AcceptTaskRequestDTO;
import com.unimib.GUI.model.dto.ChangeTaskStateRequestDTO;
import com.unimib.GUI.model.dto.StartTaskRequestDTO;
import com.unimib.GUI.model.dto.TaskDTO;
import com.unimib.GUI.repository.TaskCardRepository;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;

import java.util.List;

public class TaskCardViewModel extends BaseViewModel {

    private final TaskCardRepository repository = new TaskCardRepository();

    private final ObjectProperty<UIState<List<TaskDTO>>> tasksState =
            new SimpleObjectProperty<>();

    private final ObjectProperty<UIState<TaskDTO>> taskState =
            new SimpleObjectProperty<>();

    private final ObjectProperty<UIState<String>> createTaskState =
            new SimpleObjectProperty<>();

    private final ObjectProperty<UIState<String>> deleteTaskState =
            new SimpleObjectProperty<>();

    private final ObjectProperty<UIState<String>> acceptTaskState =
            new SimpleObjectProperty<>();

    private final ObjectProperty<UIState<String>> startTaskState =
            new SimpleObjectProperty<>();

    private final ObjectProperty<UIState<String>> changeTaskState =
            new SimpleObjectProperty<>();

    private final ObjectProperty<UIState<String>> resetTaskState =
            new SimpleObjectProperty<>();

    // =======================
    // ReadOnly properties
    // =======================

    public ReadOnlyObjectProperty<UIState<List<TaskDTO>>> getTasksStateProperty() {
        return tasksState;
    }

    public ReadOnlyObjectProperty<UIState<TaskDTO>> getTaskStateProperty() {
        return taskState;
    }

    public ReadOnlyObjectProperty<UIState<String>> createTaskStateProperty() {
        return createTaskState;
    }

    public ReadOnlyObjectProperty<UIState<String>> deleteTaskStateProperty() {
        return deleteTaskState;
    }

    public ReadOnlyObjectProperty<UIState<String>> acceptTaskStateProperty() {
        return acceptTaskState;
    }

    public ReadOnlyObjectProperty<UIState<String>> startTaskStateProperty() {
        return startTaskState;
    }

    public ReadOnlyObjectProperty<UIState<String>> changeTaskStateProperty() {
        return changeTaskState;
    }

    public ReadOnlyObjectProperty<UIState<String>> resetTaskStateProperty() {
        return resetTaskState;
    }

    // =======================
    // Actions
    // =======================

    public void fetchTasks() {

        Task<List<TaskDTO>> task = new Task<>() {
            @Override
            protected List<TaskDTO> call() {
                return repository.fetchTasks();
            }
        };

        execute(task, tasksState);
    }

    public void fetchTask(Long id) {

        Task<TaskDTO> task = new Task<>() {
            @Override
            protected TaskDTO call() {
                return repository.fetchTask(id);
            }
        };

        execute(task, taskState);
    }

    public void createTask(TaskDTO dto) {
        execute(repository.createTask(dto), createTaskState);
    }

    public void deleteTask(Long id) {
        execute(repository.deleteTask(id), deleteTaskState);
    }

    public void acceptTask(AcceptTaskRequestDTO dto) {
        execute(repository.acceptTask(dto), acceptTaskState);
    }

    public void startTask(StartTaskRequestDTO dto) {
        execute(repository.startTask(dto), startTaskState);
    }

    public void changeTaskState(ChangeTaskStateRequestDTO dto) {
        execute(repository.changeTaskState(dto), changeTaskState);
    }

    public void resetTaskState(Long id) {
        execute(repository.resetTaskState(id), resetTaskState);
    }
}