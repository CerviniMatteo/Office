package com.unimib.assignment3.response.request;

import com.unimib.assignment3.enums.TaskState;

public class ChangeTaskStateRequest {
    private Long taskId;
    private TaskState taskState;

    public ChangeTaskStateRequest() {}

    public ChangeTaskStateRequest(Long taskId, TaskState taskState) {
        this.taskId = taskId;
        this.taskState = taskState;
    }

    // Getters and setters
    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public TaskState getTaskState() {
        return taskState;
    }

    public void setTaskState(TaskState taskState) {
        this.taskState = taskState;
    }
}
