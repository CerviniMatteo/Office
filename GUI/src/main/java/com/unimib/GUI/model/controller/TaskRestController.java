package com.unimib.GUI.model.controller;

import com.unimib.GUI.model.controller.base.BaseRestController;
import com.unimib.GUI.model.dto.AcceptTaskRequestDTO;
import com.unimib.GUI.model.dto.ChangeTaskStateRequestDTO;
import com.unimib.GUI.model.dto.StartTaskRequestDTO;
import com.unimib.GUI.model.dto.TaskDTO;
import javafx.concurrent.Task;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;

import static com.unimib.GUI.constants.Rest.*;

public class TaskRestController extends BaseRestController {

    public List<TaskDTO> fetchTasks() {
        return getMany(BASE_TASK_ENDPOINT + "/all", new ParameterizedTypeReference<>() {});
    }

    public TaskDTO fetchTask(Long taskId) {
        return getOne(BASE_TASK_ENDPOINT + "/" + taskId, TaskDTO.class);
    }

    public Task<String> changeTaskState(ChangeTaskStateRequestDTO payload) {
        return postTask(BASE_TASK_ENDPOINT + "/changeState", payload);
    }

    public Task<String> startTask(StartTaskRequestDTO payload) {
        return postTask(BASE_TASK_ENDPOINT + "/startTask", payload);
    }

    public Task<String> resetTaskState(Long taskId) {
        return postTask(BASE_TASK_ENDPOINT + "/resetState", taskId);
    }

    public Task<String> acceptTask(AcceptTaskRequestDTO payload) {
        return postTask(BASE_TASK_ENDPOINT + "/acceptTask", payload);
    }

    public Task<String> createTask(TaskDTO payload) {
        return postTask(BASE_TASK_ENDPOINT + "/createTask", payload);
    }

    public Task<String> deleteTask(Long payload) {
        return postTask(BASE_TASK_ENDPOINT + "/deleteTask", payload);
    }
}