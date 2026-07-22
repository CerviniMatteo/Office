package com.unimib.GUI.model.controller.impl;

import com.unimib.GUI.model.controller.BaseRestController;
import com.unimib.GUI.model.dto.AcceptTaskRequestDTO;
import com.unimib.GUI.model.dto.ChangeTaskStateRequestDTO;
import com.unimib.GUI.model.dto.StartTaskRequestDTO;
import com.unimib.GUI.model.dto.TaskDTO;
import javafx.concurrent.Task;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;

import static com.unimib.GUI.constants.Rest.BASE_TASK_ENDPOINT;

public class TaskRestController extends BaseRestController {

    public List<TaskDTO> fetchTasks() {
        return getMany(BASE_TASK_ENDPOINT + "/all", new ParameterizedTypeReference<>() {});
    }

    public TaskDTO fetchTask(Long taskId) {
        return getOne(BASE_TASK_ENDPOINT + "/" + taskId, TaskDTO.class);
    }

    public Task<String> changeTaskState(ChangeTaskStateRequestDTO payload) {
        return post(BASE_TASK_ENDPOINT + "/changeState", payload, String.class);
    }

    public Task<String> startTask(StartTaskRequestDTO payload) {
        return post(BASE_TASK_ENDPOINT + "/startTask", payload, String.class);
    }

    public Task<String> resetTaskState(Long taskId) {
        return post(BASE_TASK_ENDPOINT + "/resetState", taskId, String.class);
    }

    public Task<String> acceptTask(AcceptTaskRequestDTO payload) {
        return post(BASE_TASK_ENDPOINT + "/acceptTask", payload, String.class);
    }

    public Task<String> createTask(TaskDTO payload) {
        return post(BASE_TASK_ENDPOINT + "/createTask", payload, String.class);
    }

    public Task<String> deleteTask(Long taskId) {
        return post(BASE_TASK_ENDPOINT + "/deleteTask", taskId, String.class);
    }
}