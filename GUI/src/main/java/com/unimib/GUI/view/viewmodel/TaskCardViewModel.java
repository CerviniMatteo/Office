package com.unimib.GUI.view.viewmodel;

import com.unimib.GUI.repository.TaskCardRepository;
import javafx.concurrent.Task;

public class TaskCardViewModel {

    private final TaskCardRepository repository;

    public TaskCardViewModel() {
        this.repository = new TaskCardRepository();
    }

    public Task<String> deleteTask(Long taskId) {
        return repository.deleteTask(taskId);

    }

}