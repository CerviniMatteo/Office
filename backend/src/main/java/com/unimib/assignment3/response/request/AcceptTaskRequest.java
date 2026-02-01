package com.unimib.assignment3.response.request;

import com.unimib.assignment3.enums.TaskState;

public class AcceptTaskRequest {
    private Long taskId;
    private Long employeeId;

    public AcceptTaskRequest(Long taskId, Long employeeId) {
        this.taskId = taskId;
        this.employeeId = employeeId;
    }

    // Getters and setters
    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }
}
