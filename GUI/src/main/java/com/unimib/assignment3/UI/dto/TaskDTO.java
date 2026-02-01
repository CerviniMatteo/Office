package com.unimib.assignment3.UI.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskDTO {
    private Long taskId;
    private String description;
    private String taskState;
    private LocalDate startDate;
    private LocalDate endDate;
    private Map<Long, String> assignedWorkers;

    public Long getTaskId() {
        return taskId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTaskState() {
        return taskState;
    }

    public void setTaskState(String taskState) {
        this.taskState = taskState;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Map<Long, String> getAssignedWorkers() {
        return assignedWorkers;
    }

    public void setAssignedWorkers(Map<Long, String> assignedWorkers) {
        this.assignedWorkers = assignedWorkers;
    }
}
