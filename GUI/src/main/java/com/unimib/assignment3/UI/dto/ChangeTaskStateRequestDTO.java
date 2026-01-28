package com.unimib.assignment3.UI.dto;

public class ChangeTaskStateRequestDTO {
    private Long taskId;
    private String taskState;

    public ChangeTaskStateRequestDTO(Long taskId, String taskState) {
        this.taskId = taskId;
        this.taskState = taskState;
    }

    public Long getTaskId() {
        return taskId;
    }

    public String getTaskState() {
        return taskState;
    }
}
