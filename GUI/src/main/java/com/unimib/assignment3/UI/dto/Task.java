package com.unimib.assignment3.UI.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Task {
    private Long taskId;
    private String description;
    private String taskState;
    private LocalDate startDate;
    private LocalDate endDate;

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
        if(taskState.equals("TO_BE_STARTED")) {
            taskState = "TO BE STARTED";
        }
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
}
