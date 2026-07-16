package com.unimib.GUI.repository;

import com.unimib.GUI.model.controller.TaskRestController;
import javafx.concurrent.Task;

public class TaskCardRepository {

    private final TaskRestController dataSource;

    public TaskCardRepository() {
        this.dataSource = new TaskRestController();
    }

    public Task<String> deleteTask(Long taskId) {
        return dataSource.deleteTask(taskId);
    }
}