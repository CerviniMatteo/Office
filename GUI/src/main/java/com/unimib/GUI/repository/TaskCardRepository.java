package com.unimib.GUI.repository;

import com.unimib.GUI.model.controller.impl.TaskRestController;
import com.unimib.GUI.model.dto.AcceptTaskRequestDTO;
import com.unimib.GUI.model.dto.ChangeTaskStateRequestDTO;
import com.unimib.GUI.model.dto.StartTaskRequestDTO;
import com.unimib.GUI.model.dto.TaskDTO;
import javafx.concurrent.Task;

import java.util.List;

public class TaskCardRepository {

    private final TaskRestController remote;

    public TaskCardRepository() {
        remote = new TaskRestController();
    }

    public List<TaskDTO> fetchTasks() {
        return remote.fetchTasks();
    }

    public TaskDTO fetchTask(Long id) {
        return remote.fetchTask(id);
    }

    public Task<String> deleteTask(Long id) {
        return remote.deleteTask(id);
    }

    public Task<String> createTask(TaskDTO dto) {
        return remote.createTask(dto);
    }

    public Task<String> acceptTask(AcceptTaskRequestDTO dto) {
        return remote.acceptTask(dto);
    }

    public Task<String> startTask(StartTaskRequestDTO dto) {
        return remote.startTask(dto);
    }

    public Task<String> changeTaskState(ChangeTaskStateRequestDTO dto) {
        return remote.changeTaskState(dto);
    }

    public Task<String> resetTaskState(Long id) {
        return remote.resetTaskState(id);
    }
}